package com.ericsson.cifwk.taf.operators.view;

import com.ericsson.cifwk.taf.operators.components.BasicModalDialog;
import com.ericsson.cifwk.taf.ui.core.UiComponent;
import com.ericsson.cifwk.taf.ui.core.UiComponentMapping;
import com.ericsson.cifwk.taf.ui.sdk.GenericViewModel;

public class DeleteSchedulePopup extends GenericViewModel {

    public static final String MODAL_POPUP_HOLDER = "body.modal-open > .modal";

    @UiComponentMapping(MODAL_POPUP_HOLDER)
    private BasicModalDialog modalPopupHolder;

    public void clickDeleteButton() {
        modalPopupHolder.getPrimaryButton().click();
    }

    public void clickCancelButton() {
        modalPopupHolder.getDefaultButton().click();
    }

    public UiComponent getModalPopupHolder() {
        return modalPopupHolder;
    }
}
