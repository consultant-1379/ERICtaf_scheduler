package com.ericsson.cifwk.taf.operators.view;

import com.ericsson.cifwk.taf.operators.components.BasicModalDialog;
import com.ericsson.cifwk.taf.ui.core.UiComponent;
import com.ericsson.cifwk.taf.ui.core.UiComponentMapping;
import com.ericsson.cifwk.taf.ui.sdk.GenericViewModel;

public class ApprovalPopupModel extends GenericViewModel {

    public static final String MODAL_DIALOG = ".modal-dialog";

    @UiComponentMapping(MODAL_DIALOG)
    private BasicModalDialog approvalPopupHolder;

    @UiComponentMapping(".modal-dialog .input")
    private UiComponent approverDetails;

    @UiComponentMapping("#saveOrSendDialog ul")
    private UiComponent approvers;

    @UiComponentMapping(".modal-dialog .tag-list")
    private UiComponent reviewerTags;

    @UiComponentMapping("#approvalOptions-warningIcon")
    private UiComponent warningIcon;

    public void clickSaveButton() {
        approvalPopupHolder.getPrimaryButton().click();
    }

    public void clickSendAndSaveButton() {
        approvalPopupHolder.getSuccessButton().click();
    }

    public void setApproverDetails(String userName) {
        this.approverDetails.sendKeys(userName);
    }

    public UiComponent getApprovers() {
        return this.approvers;
    }

    public UiComponent getModalPopupHolder() {
        return approvalPopupHolder;
    }

    public void clickRemoveReviewerByIndex(int index) {
       reviewerTags.getDescendantsBySelector(".remove-button").get(index).click();
    }

    public int getNumberOfReviewers() {
        return reviewerTags.getDescendantsBySelector("li").size();
    }

    public boolean isWarningIconDisplayed() {
        return warningIcon.exists();
    }

}
