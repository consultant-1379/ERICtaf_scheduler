package com.ericsson.cifwk.taf.operators;

import com.ericsson.cifwk.taf.annotations.Operator;
import com.ericsson.cifwk.taf.annotations.Shared;
import com.ericsson.cifwk.taf.annotations.VUserScoped;
import com.ericsson.cifwk.taf.data.Host;
import com.ericsson.cifwk.taf.data.Ports;
import com.ericsson.cifwk.taf.operators.view.ApprovalPopupModel;
import com.ericsson.cifwk.taf.operators.view.BreadcrumbsView;
import com.ericsson.cifwk.taf.operators.view.DeleteSchedulePopup;
import com.ericsson.cifwk.taf.operators.view.DocsView;
import com.ericsson.cifwk.taf.operators.view.DropSelector;
import com.ericsson.cifwk.taf.operators.view.HeaderSection;
import com.ericsson.cifwk.taf.operators.view.IncludeScheduleModal;
import com.ericsson.cifwk.taf.operators.view.LoginPage;
import com.ericsson.cifwk.taf.operators.view.LoginPopupViewModel;
import com.ericsson.cifwk.taf.operators.view.ReviewCommentsPopup;
import com.ericsson.cifwk.taf.operators.view.ScheduleEditPage;
import com.ericsson.cifwk.taf.operators.view.ScheduleListPage;
import com.ericsson.cifwk.taf.operators.view.ScheduleListRow;
import com.ericsson.cifwk.taf.operators.view.ScheduleViewPage;
import com.ericsson.cifwk.taf.operators.view.TestwareSuitesPopupPage;
import com.ericsson.cifwk.taf.ui.Browser;
import com.ericsson.cifwk.taf.ui.BrowserTab;
import com.ericsson.cifwk.taf.ui.UI;
import com.ericsson.cifwk.taf.ui.core.GenericPredicate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

import static com.ericsson.cifwk.taf.ui.BrowserType.FIREFOX;
import static org.apache.commons.lang3.StringUtils.isEmpty;

@Operator
@Shared
@VUserScoped
public class ScheduleOperator {

    private static final Logger LOGGER = LoggerFactory.getLogger(ScheduleOperator.class);

    public static final String BASE_PATH = "/#";
    public static final String LOGIN_PATH = "/login";
    public static final int WAIT_TIMEOUT = 10000;
    public static final int WAIT_LONG_TIMEOUT = 20000;
    public static final int WAIT_EXTRA_LONG_TIMEOUT = 30000;
    public static final int WAIT_SHORT_TIMEOUT = 2000;
    public static final String APPROVE = "approve";
    public static final String REJECT = "reject";

    private Host host;
    private Browser browser;
    private BrowserTab browserTab;

    private ScheduleEditPage scheduleEditPage;
    private TestwareSuitesPopupPage suitesPopupPage;

    public void init(Host host) {
        this.host = host;
        browser = UI.newBrowser();
        String loginUrl = getLoginUrl();
        browserTab = browser.open(loginUrl);

        scheduleEditPage = null;
        suitesPopupPage = null;
    }

    protected BrowserTab getBrowserTab() {
        return browserTab;
    }

    protected Host getHost() {
        return host;
    }

    private String getBaseUrl() {
        String port = host.getPort().get(Ports.HTTP);
        String hostAddress = host.getIp();
        return "http://" + hostAddress + ":" + port + BASE_PATH;
    }

    public String getCurrentTabDescriptor() {
        return browserTab.getWindowDescriptor();
    }

    public void createNewTab() {
        String currentUrl = browserTab.getCurrentUrl();
        browserTab = browser.open(currentUrl);
    }

    @SuppressWarnings("deprecation")
    public void switchTab(String tabDescriptor) {
        LOGGER.info("Browser current tab '{}'", browser.getCurrentWindow().getCurrentUrl());
        browser.switchWindow(tabDescriptor);
        browserTab = browser.getCurrentWindow();
        LOGGER.info("Browser switched tab '{}'", browser.getCurrentWindow().getCurrentUrl());
    }

    private String getLoginUrl() {
        return getBaseUrl() + LOGIN_PATH;
    }

    public String getActiveTabUrl() {
        return browserTab.getCurrentUrl();
    }

    public void setActiveTabHash(String hashString) {
        browserTab.open(getBaseUrl() + hashString);
    }

    public void goToSchedulesList() {
        getBreadcrumbs().goToSchedules();
        GenericPredicate predicate = new GenericPredicate() {
            @Override
            public boolean apply() {
                return !isEmpty(getDropSelector().getDrop());
            }
        };
        browserTab.waitUntil(predicate, WAIT_TIMEOUT);
    }

    public void waitForScheduleListShown() {
        GenericPredicate predicate = new GenericPredicate() {
            @Override
            public boolean apply() {
                return isScheduleListPageOpened();
            }
        };
        browserTab.waitUntil(predicate, WAIT_LONG_TIMEOUT);
    }

    public void waitForScheduleEditPageShown() {
        GenericPredicate predicate = new GenericPredicate() {
            @Override
            public boolean apply() {
                return isScheduleEditPageOpened();
            }
        };
        browserTab.waitUntil(predicate, WAIT_TIMEOUT);
    }

    public void waitForScheduleViewPageShown() {
        GenericPredicate predicate = new GenericPredicate() {
            @Override
            public boolean apply() {
                return isScheduleViewPageOpened();
            }
        };
        browserTab.waitUntil(predicate, WAIT_TIMEOUT);
    }

    public void selectDrop(String dropName) {
        getDropSelector().selectDrop(dropName, getBrowserTab());
    }

    public void selectDrop(DropSelection iso) {
        DropSelector dropSelector = getDropSelector();
        browserTab.waitUntil(new GenericPredicate() {
            @Override
            public boolean apply() {
                return dropSelector.areDropOptionsPresent();
            }
        }, WAIT_TIMEOUT);
        dropSelector.selectDrop(iso, browserTab);
    }

    public DropSelection getDropSelection() {
        return getDropSelector().getSelection();
    }

    public void login() {
        login(host.getUser(), host.getPass());
    }

    public void login(String user, String password) {
        LoginPage loginPage = getLoginPage();
        loginPage.login(user, password);
        getHeaderSection();
    }

    public boolean isLoggedIn() {
        LoginPage loginPage = getLoginPage();
        return !loginPage.isOnLoginPage();
    }

    public void waitForToastsToClear() {
        if (getHeaderSection().isToastsHolderDisplayed()) {
            browserTab.waitUntil(new GenericPredicate() {
                @Override
                public boolean apply() {
                    return !getHeaderSection().isToastsHolderDisplayed();
                }
            }, WAIT_LONG_TIMEOUT);
        }
    }

    public void waitUntilToastShown() {
        browserTab.waitUntil(new GenericPredicate() {
            @Override
            public boolean apply() {
                return getHeaderSection().isToastsHolderDisplayed();
            }
        }, WAIT_EXTRA_LONG_TIMEOUT);
    }

    public void waitUntilAddTagsBlockIsDisplayed() {
        getScheduleEditPage().waitUntil(new GenericPredicate() {
            @Override
            public boolean apply() {
                return getScheduleEditPage().isAddTagsIconsBlockDisplayed(); }
        }, WAIT_TIMEOUT);
    }

    public void waitUntilIconBlocksAreHidden() {
        getScheduleEditPage().waitUntil(new GenericPredicate() {
            @Override
            public boolean apply() {
                return !getScheduleEditPage().isAddTagsIconsBlockDisplayed() &&
                        !getScheduleEditPage().isAddItemAttributesIconsBlockDisplayed() &&
                        !getScheduleEditPage().isAddItemGroupValuesIconsBlockDisplayed();
            }
        }, WAIT_TIMEOUT);
    }

    public void waitUntilCommentsPopupIsHidden() {
        getScheduleEditPage().waitUntil(new GenericPredicate() {
            @Override
            public boolean apply() {
                return !getReviewCommentsPopup().isCurrentView(); }
        }, WAIT_TIMEOUT);
    }

    public void waitUntilItemGroupBlockIsDisplayed() {
        getScheduleEditPage().waitUntil(new GenericPredicate() {
            @Override
            public boolean apply() {
                return getScheduleEditPage().isAddItemGroupValuesIconsBlockDisplayed() && !getScheduleEditPage().isAddTagsIconsBlockDisplayed();
            }
        }, WAIT_TIMEOUT);
    }

    public void waitUntilItemAttributesBlockIsDisplayed() {
        getScheduleEditPage().waitUntil(new GenericPredicate() {
            @Override
            public boolean apply() {
                return getScheduleEditPage().isAddItemAttributesIconsBlockDisplayed() && !getScheduleEditPage().isAddTagsIconsBlockDisplayed();
            }
        }, WAIT_TIMEOUT);
    }

    public void waitUntilPropertyAttributesBlockIsDisplayed() {
        getScheduleEditPage().waitUntil(new GenericPredicate() {
            @Override
            public boolean apply() {
                return getScheduleEditPage().isAddPropertyAttributesIconBlockDisplayed() && !getScheduleEditPage().isAddTagsIconsBlockDisplayed(); }
        }, WAIT_TIMEOUT);
    }

    public void waitUntilTestCampaignAttributesBlockIsDisplayed() {
        getScheduleEditPage().waitUntil(new GenericPredicate() {
            @Override
            public boolean apply() {
                return getScheduleEditPage().isAddTestCampaignAttributesIconBlockDisplayed() && !getScheduleEditPage().isAddTagsIconsBlockDisplayed();
            }
        }, WAIT_TIMEOUT);
    }

    public void logout() {
        waitForToastsToClear();
        getHeaderSection().logout();
        browserTab.waitUntilComponentIsDisplayed(getLoginPage().getPasswordInput(), WAIT_SHORT_TIMEOUT);
    }

    private LoginPage getLoginPage() {
        return browserTab.getView(LoginPage.class);
    }

    private LoginPopupViewModel getLoginPopup() {
        return browserTab.getView(LoginPopupViewModel.class);
    }

    public ScheduleListPage getScheduleListPage() {
        browserTab.waitUntil(new GenericPredicate() {
            @Override
            public boolean apply() {
                return browserTab.getView(ScheduleListPage.class).isCurrentView();
            }
        });
        return browserTab.getView(ScheduleListPage.class);
    }

    public boolean isLoginPopupShown() {
        return getLoginPopup().isCurrentView();
    }

    public boolean isScheduleListPageOpened() {
        return getScheduleListPage().isCurrentView();
    }

    public boolean isScheduleEditPageOpened() {
        return getScheduleEditPage().isCurrentView() && getScheduleEditPage().isSpinnerHidden();
    }

    public boolean isScheduleViewPageOpened() {
        return getScheduleViewPage().isCurrentView();
    }

    public boolean isCreateSchedulePageOpened() {
        return getScheduleEditPage().isCurrentView();
    }

    public void removeDefaultItemContentFromEditor() {
        getScheduleEditPage().removeDefaultItemContent();
    }

    public void selectTestwareForScheduleEdit(String testwareName) {
        getScheduleEditPage().selectTestware(testwareName);
    }

    public boolean isTestwareSelectedForScheduleEdit(String testwareName) {
        return getScheduleEditPage().isTestwareSelected(testwareName);
    }

    public void openSuitesPopupForScheduleEdit() {
        getScheduleEditPage().openSuitesPopup();
    }

    public boolean checkIfScheduleEditorContainsString(String string) {
        return getScheduleEditPage().scheduleContainsString(string);
    }

    public boolean isEditButtonDisplayed(String scheduleName) {
        return getScheduleListPage().isScheduleEditButtonDisplayed(scheduleName);
    }

    public boolean isReviewIconDisplayed() {
        return getScheduleViewPage().isReviewIconDisplayed();
    }

    public boolean checkIfLatestScheduleVersionContainsString(String version) {
        browserTab.waitUntilComponentIsDisplayed(ScheduleViewPage.SCHEDULE_VERSION, WAIT_SHORT_TIMEOUT);
        return getScheduleViewPage().latestVersionContainsString(version);
    }

    public boolean checkIfUnvalidatedWarningIsDisplayed() {
        return getApproveSchedulePopup().isWarningIconDisplayed();
    }

    public boolean checkIfUnvalidatedLabelIsDisplayed() {
        return getScheduleViewPage().isScheduleNotValidatedLabelDisplayed();
    }

    public boolean checkIfValidatedLabelIsDisplayed() {
        return getScheduleViewPage().isScheduleValidatedLabelDisplayed();
    }

    public ScheduleEditPage getScheduleEditPage() {
        if (scheduleEditPage != null && scheduleEditPage.isCurrentView()) {
            return scheduleEditPage;
        }

        scheduleEditPage = browserTab.getView(ScheduleEditPage.class);
        return scheduleEditPage;
    }

    public ScheduleViewPage getScheduleViewPage() {
        browserTab.waitUntilComponentIsDisplayed("#view-scheduleName", 5000);
        return browserTab.getView(ScheduleViewPage.class);
    }

    public ReviewCommentsPopup getReviewCommentsPopup() {
        browserTab.waitUntilComponentIsDisplayed("#view-scheduleName", 5000);
        return browserTab.getView(ReviewCommentsPopup.class);
    }

    public HeaderSection getHeaderSection() {
        browserTab.waitUntil(new GenericPredicate() {
            @Override
            public boolean apply() {
                return browserTab.getView(HeaderSection.class).isCurrentView();
            }
        });
        return browserTab.getView(HeaderSection.class);
    }

    public DropSelector getDropSelector() {
        return browserTab.getView(DropSelector.class);
    }

    public BreadcrumbsView getBreadcrumbs() {
        browserTab.waitUntil(new GenericPredicate() {
            @Override
            public boolean apply() {
                return browserTab.getView(BreadcrumbsView.class).isDisplayed();
            }
        });
        return browserTab.getView(BreadcrumbsView.class);
    }

    public DeleteSchedulePopup getDeleteSchedulePopup() {
        browserTab.waitUntilComponentIsDisplayed(DeleteSchedulePopup.MODAL_POPUP_HOLDER, WAIT_SHORT_TIMEOUT);
        return browserTab.getView(DeleteSchedulePopup.class);
    }

    public ApprovalPopupModel getApproveSchedulePopup() {
        browserTab.waitUntilComponentIsDisplayed(ApprovalPopupModel.MODAL_DIALOG, WAIT_EXTRA_LONG_TIMEOUT);
        return browserTab.getView(ApprovalPopupModel.class);
    }

    public void confirmDeleteSchedule() {
        DeleteSchedulePopup deleteSchedulePopup = getDeleteSchedulePopup();
        deleteSchedulePopup.clickDeleteButton();
        browserTab.waitUntilComponentIsHidden(deleteSchedulePopup.getModalPopupHolder(), WAIT_SHORT_TIMEOUT);
    }

    public void confirmSaveSchedule() {
        ApprovalPopupModel approvalPopupModel = getApproveSchedulePopup();
        approvalPopupModel.clickSaveButton();
        browserTab.waitUntilComponentIsHidden(approvalPopupModel.getModalPopupHolder(), WAIT_EXTRA_LONG_TIMEOUT);
    }

    protected TestwareSuitesPopupPage getTestwareSuitesPopup() {
        if (suitesPopupPage != null && suitesPopupPage.isCurrentView()) {
            return suitesPopupPage;
        }

        browserTab.waitUntilComponentIsDisplayed(TestwareSuitesPopupPage.MODAL_POPUP_HOLDER_CSS, WAIT_SHORT_TIMEOUT);
        suitesPopupPage = browserTab.getView(TestwareSuitesPopupPage.class);
        browserTab.waitUntilComponentIsHidden(suitesPopupPage.getSuitesLoadSpinner(), WAIT_TIMEOUT);
        return suitesPopupPage;
    }

    /* SCHEDULE METHODS */

    public void viewSchedule(String scheduleName) {
        getScheduleListPage().viewSchedule(scheduleName);
    }

    public void saveSchedule() {
        getScheduleEditPage().save();
    }

    public void validateSchedule() {
        getScheduleEditPage().validate();
    }

    public void goToCreateSchedule() {
        getScheduleListPage().clickCreateScheduleButton();
    }

    public int getNumberOfSchedulesDisplayed() {
        return getScheduleListPage().getNumberOfSchedules();
    }

    public int getNumberOfUniqueTeams() {
        return getScheduleListPage().getNumberOfUniqueTeams();
    }

    public String getFilteredTeamNameFromTable() {
        return getScheduleListPage().getUniqueTeamFromTable();
    }

    public List<ScheduleListRow> getSchedules() {
        return getScheduleListPage().getSchedules();
    }

    public void saveAndSendScheduleForApproval() {
        getApproveSchedulePopup().clickSendAndSaveButton();
    }

    public void enterApproverDetails(List<String> reviewers) {
        ApprovalPopupModel approvalPopupModel = getApproveSchedulePopup();
        for (String reviewer : reviewers) {
            approvalPopupModel.setApproverDetails(reviewer + ",");
        }
    }

    public int getNumberOfReviewersEntered() {
        ApprovalPopupModel approvalPopupModel = getApproveSchedulePopup();
        return approvalPopupModel.getNumberOfReviewers();
    }

    public void deleteInvalidReviewer(int index) {
        getApproveSchedulePopup().clickRemoveReviewerByIndex(index);
    }

    public void goToEditScheduleByXmlName(String scheduleName) {
        getScheduleListPage().clickEditScheduleButton(scheduleName);
    }

    public void goToViewScheduleByXmlName(String scheduleName) {
        getScheduleListPage().clickViewScheduleButton(scheduleName);
    }

    public void deleteScheduleByXmlName(String scheduleName) {
        getScheduleListPage().clickDeleteScheduleIcon(scheduleName);
        confirmDeleteSchedule();
    }

    public boolean isScheduleListShown() {
        return getScheduleListPage().isCurrentView();
    }

    public boolean isScheduleInList(String scheduleName) {
        return getScheduleListPage().isScheduleInList(scheduleName);
    }

    public boolean isScheduleViewPageShown() {
        return getScheduleViewPage().isCurrentView();
    }

    public String getScheduleName() {
        return getScheduleViewPage().getName();
    }

    public String getScheduleType() {
        return getScheduleViewPage().getType();
    }

    public String getScheduleTeam() {
        return getScheduleViewPage().getTeam();
    }

    public String getScheduleXml() {
        return getScheduleViewPage().getXml();
    }

    public String getScheduleFormNameText() {
        return getScheduleEditPage().getName();
    }

    public String getScheduleFormType() {
        return getScheduleEditPage().getType();
    }

    public String getScheduleFormTeam() {
        return getScheduleEditPage().getTeam();
    }

    public String getScheduleFormXml() {
        return getScheduleEditPage().getXml();
    }

    public void setScheduleFormNameText(String scheduleName) {
        getScheduleEditPage().setName(scheduleName);
    }

    public void setScheduleFormTypeDropdown(String type) {
        getScheduleEditPage().selectType(type);
    }

    public void setScheduleTeamNameDropdown(String team) {
        getScheduleEditPage().selectTeam(team);
    }

    public void moveEditorTextareaToVisibleArea() {
        browserTab.evaluate("document.querySelector('.ace_text-input').style.top = '70px';");
    }

    public void setScheduleFormXml(String xml) {
        // show hidden text input for editor
        browserTab.evaluate(
                "var input = document.querySelector('.scheduleForm-xml_hidden');" +
                        "if (input) {input.className=\"scheduleForm-xml\"}"
        );

        getScheduleEditPage().setXml(xml);

        // hide text input for editor
        browserTab.evaluate(
                "var input = document.querySelector('.scheduleForm-xml');" +
                        "if (input) {input.className=\"scheduleForm-xml scheduleForm-xml_hidden\"}"
        );
    }

    public void selectScheduleVersion(String scheduleVersion) {
        getScheduleViewPage().selectVersion(scheduleVersion);
    }

    public void showApprovalForm() {
        getScheduleViewPage().clickReviewIcon();
        getScheduleViewPage().waitForReviewBlock();
    }

    public void reviewSchedule(String approvalMsg, String reviewStatus) {
        ScheduleViewPage scheduleViewPage = getScheduleViewPage();
        scheduleViewPage.clickReviewIcon();
        scheduleViewPage.waitForReviewBlock();

        scheduleViewPage.enterApprovalMessage(approvalMsg);
        if (APPROVE.equals(reviewStatus)) {
            scheduleViewPage.clickApproveButton();
            scheduleViewPage.waitForReviewBlockIsHidden();
        } else if (REJECT.equals(reviewStatus)) {
            scheduleViewPage.clickRejectButton();
            scheduleViewPage.waitForReviewBlockIsHidden();
        }
    }

    public void revokePreviousStatus() {
        ScheduleViewPage scheduleViewPage = getScheduleViewPage();
        scheduleViewPage.clickReviewIcon();
        scheduleViewPage.waitForReviewBlock();
        scheduleViewPage.clickRevokeButton();
        scheduleViewPage.waitForReviewBlockIsHidden();
    }

    public boolean isScheduleApproved() {
        return getScheduleViewPage().isScheduleApproved();
    }

    public boolean approveAndRejectButtonsEnabled() {
        return getScheduleViewPage().isRejectButtonEnabled() && getScheduleViewPage().isApproveButtonEnabled();
    }

    public boolean isApproverCorrect(String approver) {
        browserTab.waitUntilComponentIsDisplayed(ScheduleViewPage.APPROVER_TEXT_SELECTOR, WAIT_SHORT_TIMEOUT);
        return getScheduleViewPage().isApproverCorrect(approver);
    }

    public boolean isApprovalMsgCorrect(String msg) {
        browserTab.waitUntilComponentIsDisplayed(ScheduleViewPage.APPROVAL_MSG_TEXT_SELECTOR, WAIT_SHORT_TIMEOUT);
        return getScheduleViewPage().isApprovalMsgCorrect(msg);
    }

    public boolean isSuitesPopupOpened() {
        return getTestwareSuitesPopup().isCurrentView();
    }

    public boolean areSuitesCheckedInPopup() {
        return getTestwareSuitesPopup().areSuitesChecked();
    }

    public boolean isSuiteAdded(String suiteName) {
        return getTestwareSuitesPopup().isSuiteAdded(suiteName);
    }

    public void deselectSuitesInPopup() {
        getTestwareSuitesPopup().deselectSuites();
    }

    public void deselectSuitesByNamesInPopup(String[] suitesNames) {
        getTestwareSuitesPopup().deselectSuitesByNames(suitesNames);
    }

    public boolean isConfirmButtonEnabledInPopup() {
        return getTestwareSuitesPopup().isConfirmButtonEnabled();
    }

    public void checkSuitesByOrderNrInPopup(int... orderNumbers) {
        getTestwareSuitesPopup().checkSuitesByOrderNr(orderNumbers);
    }

    public List<String> getCheckedSuitesNamesFromPopup() {
        return getTestwareSuitesPopup().getCheckedSuitesNames();
    }

    public void confirmSuitesSelectionInPopup() {
        getTestwareSuitesPopup().confirmSuitesSelection();
    }

    public boolean hasScheduleEditPageErrorMarkers() {
        return getScheduleEditPage().hasErrorMarkers();
    }

    public boolean isScheduleEditPageWarningDisplayed() {
        return getScheduleEditPage().isWarningDisplayed();
    }

    public void refreshPage() {
        browserTab.refreshPage();
    }

    public void openReviewComments() {
        getScheduleViewPage().openReviewComments();
    }

    public void closeReviewComments() {
        getReviewCommentsPopup().closePopup();
    }

    public boolean isTestwareIncluded(String testwareName) {
        return getScheduleEditPage().isTestwareIncluded(testwareName);
    }

    public boolean hasTestwareSuitesDistinctions(String testwareName) {
        return getScheduleEditPage().hasTestwareSuitesDistinctions(testwareName);
    }

    public void refreshTestwareList() {
        getScheduleEditPage().refreshTestwareList();
    }

    public IncludeScheduleModal getIncludeScheduleModal() {
        browserTab.waitUntilComponentIsDisplayed(IncludeScheduleModal.TABLE_HOLDER_SELECTOR, WAIT_LONG_TIMEOUT);
        return browserTab.getView(IncludeScheduleModal.class);
    }

    public void clickIncludeScheduleButton() {
        browserTab.waitUntilComponentIsHidden(suitesPopupPage.getSuitesBodyHolder(), WAIT_TIMEOUT);
        getScheduleEditPage().clickIncludeScheduleButton();
    }

    public void selectScheduleToInclude(String scheduleName) {
        IncludeScheduleModal modal = getIncludeScheduleModal();
        modal.selectSchedule(scheduleName);
    }

    public void confirmIncludeSchedule() {
        getIncludeScheduleModal().confirmSelection();
    }

    public boolean isScheduleTabDisplayed(String scheduleName) {
        browserTab.waitUntilComponentIsDisplayed(ScheduleEditPage.TAB_LABEL_SELECTOR);
        return getScheduleEditPage().isScheduleTabLabelDisplayed(scheduleName);
    }

    public void selectTabByName(String name) {
        getScheduleListPage().selectTabByName(name);
    }

    public boolean isDropSelectorDisplayed() {
        return getDropSelector().isDisplayed();
    }

    public boolean isKgbModeEnabledInForm() {
        return (getScheduleEditPage().isKgbModeEnabled() && !isDropSelectorDisplayed());
    }

    public void cancelEditSchedule() {
        getScheduleEditPage().cancel();
    }

    public void clickDocumentationLinkByName(String name) {
        DocsView docsView = getBrowserTab().getView(DocsView.class);
        docsView.clickLinkByName(name);
    }

    public boolean isCorrectHeadingDisplayed(String name) {
        DocsView docsView = getBrowserTab().getView(DocsView.class);
        return docsView.isCorrectHeadingDisplayed(name);
    }

    public void help() {
        getHeaderSection().help();
    }
}
