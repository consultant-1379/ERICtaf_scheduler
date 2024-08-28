package com.ericsson.cifwk.taf.operators.view;

import com.ericsson.cifwk.taf.ui.core.UiComponent;
import com.ericsson.cifwk.taf.ui.core.UiComponentMapping;
import com.ericsson.cifwk.taf.ui.sdk.Button;
import com.ericsson.cifwk.taf.ui.sdk.GenericViewModel;
import com.ericsson.cifwk.taf.ui.sdk.Label;
import com.ericsson.cifwk.taf.ui.sdk.Select;
import com.ericsson.cifwk.taf.ui.sdk.TextBox;

public class ScheduleViewPage extends GenericViewModel {

    public static final String APPROVER_TEXT_SELECTOR = ".scheduleView-approver";
    public static final String APPROVAL_MSG_TEXT_SELECTOR = ".scheduleView-approvalMsg";

    public static final String SCHEDULE_VERSION = "#versionSelect";

    @UiComponentMapping("#view-scheduleName")
    private Label name;

    @UiComponentMapping(".scheduleView-approver")
    private Label approvedBy;

    @UiComponentMapping(".scheduleView-approvalMsg")
    private Label approvalMsg;

    @UiComponentMapping("#view-scheduleType")
    private Label type;

    @UiComponentMapping("#view-scheduleTeam")
    private Label team;

    @UiComponentMapping(".scheduleForm-xml")
    private TextBox xml;

    @UiComponentMapping("#versionSelect")
    private Select versionSelect;

    @UiComponentMapping(".schedules-iconLink .fa-retweet")
    private UiComponent reviewIcon;

    @UiComponentMapping(".schedules-iconLink .fa-comments-o")
    private UiComponent commentsIcon;

    @UiComponentMapping("#scheduleViewApproved")
    private UiComponent scheduleIsApprovedLabel;

    @UiComponentMapping("#scheduleViewUnapproved")
    private UiComponent scheduleIsUnapprovedLabel;

    @UiComponentMapping("#scheduleViewRejected")
    private UiComponent scheduleIsRejectedLabel;

    @UiComponentMapping(".scheduleView-validation-success")
    private UiComponent scheduleIsValidatedLabel;

    @UiComponentMapping(".scheduleView-validation-error")
    private UiComponent scheduleNotValidatedLabel;

    @UiComponentMapping(".scheduleView-reviewBlock")
    private UiComponent reviewBlock;

    @UiComponentMapping(".scheduleView-reviewMessage")
    private TextBox reviewMessageTextBox;

    @UiComponentMapping(".scheduleView-reviewFooter .scheduleView-approveButton")
    private Button approveButton;

    @UiComponentMapping(".scheduleView-reviewFooter .scheduleView-rejectButton")
    private Button rejectButton;

    @UiComponentMapping(".scheduleView-reviewFooter .scheduleView-revokeButton")
    private Button revokeButton;

    @UiComponentMapping(".scheduleView-reviewFooter .scheduleView-cancelButton")
    private Button cancelButton;

    @UiComponentMapping("#versionSelect")
    private Select latestVersion;

    @Override
    public boolean isCurrentView() {
        return name.isDisplayed();
    }

    public String getName() {
        return name.getText();
    }

    public String getType() {
        return type.getText();
    }

    public String getTeam() {
        return team.getText();
    }

    public String getXml() {
        return xml.getText();
    }

    public void selectVersion(String versionNumber) {
        versionSelect.selectByTitle(versionNumber);
    }

    public void clickReviewIcon() {
        reviewIcon.click();
    }

    public boolean isScheduleApproved() {
        return scheduleIsApprovedLabel.isDisplayed() && !scheduleIsUnapprovedLabel.isDisplayed();
    }

    public boolean isApproverCorrect(String approver) {
        return approvedBy.getText().equals(approver);
    }

    public boolean isApprovalMsgCorrect(String msg) {
        return approvalMsg.getText().equals(msg);
    }

    public boolean isReviewIconDisplayed() {
        return reviewIcon.isDisplayed();
    }

    public boolean isApprovedLabelDisplayed() {
        return scheduleIsApprovedLabel.isDisplayed();
    }

    public UiComponent getApprovedLabel() {
        return scheduleIsApprovedLabel;
    }

    public boolean isUnapprovedLabelDisplayed() {
        return scheduleIsUnapprovedLabel.isDisplayed();
    }

    public boolean isRejectedLabelDisplayed() {
        return scheduleIsRejectedLabel.isDisplayed();
    }

    public void openReviewComments() {
        commentsIcon.click();
    }

    public void waitForReviewBlock() {
        waitUntilComponentIsDisplayed(reviewBlock, 2000);
    }

    public void enterApprovalMessage(String approvalMsg) {
        reviewMessageTextBox.setText(approvalMsg);
    }

    public void clickApproveButton() {
        approveButton.click();
    }

    public boolean isApproveButtonEnabled() {
        return approveButton.isEnabled();
    }

    public boolean isRejectButtonEnabled() {
        return rejectButton.isEnabled();
    }

    public void clickRejectButton() {
        rejectButton.click();
    }

    public void clickRevokeButton() {
        revokeButton.click();
    }

    public void waitForReviewBlockIsHidden() {
        waitUntilComponentIsHidden(reviewBlock, 2000);
    }

    public boolean latestVersionContainsString(String selectedVersion) {
        String version = latestVersion.getSelectedOptions().get(0).getTitle();
        return version.equals(selectedVersion);
    }

    public boolean isScheduleValidatedLabelDisplayed() {
        return scheduleIsValidatedLabel.isDisplayed();
    }

    public boolean isScheduleNotValidatedLabelDisplayed() {
        return scheduleNotValidatedLabel.isDisplayed();
    }
}
