package com.ericsson.cifwk.taf.operators.view;

import com.ericsson.cifwk.taf.ui.core.UiComponent;
import com.ericsson.cifwk.taf.ui.core.UiComponentMapping;
import com.ericsson.cifwk.taf.ui.sdk.Button;
import com.ericsson.cifwk.taf.ui.sdk.CheckBox;
import com.ericsson.cifwk.taf.ui.sdk.GenericViewModel;
import com.google.common.collect.Lists;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

public class TestwareSuitesPopupPage extends GenericViewModel {

    private static final Logger LOGGER = LoggerFactory.getLogger(TestwareSuitesPopupPage.class);

    public static final String MODAL_POPUP_HOLDER_CSS = "body.modal-open > .modal";
    public static final String SUITE_ITEM_CSS = ".suitesModal-suiteItem";
    public static final String SUITE_ITEM_CHECKBOXES_CSS = ".suitesModal-suiteItem .suitesModal-suiteCheckbox";
    public static final String SUITE_CHECKBOX_CSS = ".suitesModal-suiteCheckbox";
    public static final String SUITE_NAME_CLASSNAME = ".suitesModal-suiteName";
    public static final String SUITE_INFO_ICON_CLASSNAME = ".suitesModal-infoIcon";

    @UiComponentMapping(MODAL_POPUP_HOLDER_CSS + " .suitesModal-spinner")
    private UiComponent suitesLoadSpinner;

    @UiComponentMapping(MODAL_POPUP_HOLDER_CSS + " .modal-body")
    private UiComponent suitesBodyHolder;

    @UiComponentMapping(MODAL_POPUP_HOLDER_CSS + " .modal-body .suitesModal-listHolder")
    private UiComponent suitesListHolder;

    @UiComponentMapping(MODAL_POPUP_HOLDER_CSS + " .modal-footer .btn-primary")
    private Button confirmButton;

    @UiComponentMapping(MODAL_POPUP_HOLDER_CSS + " .modal-footer .btn-default")
    private Button cancelButton;

    @UiComponentMapping(MODAL_POPUP_HOLDER_CSS + " .suitesModal-listHolder .suitesModal-suiteItem")
    private List<UiComponent> suitesItems;

    public UiComponent getSuitesLoadSpinner() {
        return suitesLoadSpinner;
    }

    @Override
    public boolean isCurrentView() {
        return suitesBodyHolder.isDisplayed() && !suitesLoadSpinner.isDisplayed();
    }

    public UiComponent getSuitesBodyHolder() {
        return suitesBodyHolder;
    }

    public boolean areSuitesChecked() {
        List<UiComponent> suiteItems = suitesListHolder.getDescendantsBySelector(SUITE_ITEM_CSS);
        for (UiComponent suiteItem : suiteItems) {
            CheckBox suiteCheckbox = suiteItem.getDescendantsBySelector(SUITE_CHECKBOX_CSS).get(0).as(CheckBox.class);
            if (!suiteCheckbox.isSelected()) {
                return false;
            }
        }
        return true;
    }

    public void deselectSuites() {
        List<UiComponent> suiteItems = suitesListHolder.getDescendantsBySelector(SUITE_ITEM_CSS);
        for (UiComponent suiteItem : suiteItems) {
            CheckBox suiteCheckbox = suiteItem.getDescendantsBySelector(SUITE_CHECKBOX_CSS).get(0).as(CheckBox.class);
            suiteCheckbox.deselect();
        }
    }

    public void deselectSuitesByNames(String[] suitesNames) {
        List<UiComponent> suiteItems = suitesListHolder.getDescendantsBySelector(SUITE_ITEM_CSS);
        List suiteNameList = Lists.newArrayList(suitesNames);
        for (UiComponent suiteItem : suiteItems) {
            UiComponent suiteName = suiteItem.getDescendantsBySelector(SUITE_NAME_CLASSNAME).get(0);
            if (!suiteNameList.contains(suiteName.getText())) {
                continue;
            }

            CheckBox suiteCheckbox = suiteItem.getDescendantsBySelector(SUITE_CHECKBOX_CSS).get(0).as(CheckBox.class);
            suiteCheckbox.deselect();
        }
    }

    public void checkSuitesByOrderNr(int... orderNumbers) {
        List<UiComponent> suiteItems = suitesListHolder.getDescendantsBySelector(SUITE_ITEM_CHECKBOXES_CSS);
        int size = suiteItems.size();
        if (size < 1) {
            return;
        }
        for (int orderNr : orderNumbers) {
            if (orderNr >= size) {
                continue;
            }
            suiteItems.get(orderNr).as(CheckBox.class).select();
        }
    }

    public void confirmSuitesSelection() {
        confirmButton.click();
    }

    public boolean isConfirmButtonEnabled() {
        return confirmButton.isEnabled();
    }

    public List<String> getCheckedSuitesNames() {
        List<String> suitesNames = Lists.newArrayList();
        List<UiComponent> suiteItems = suitesListHolder.getDescendantsBySelector(SUITE_ITEM_CSS);
        for (UiComponent suiteItem : suiteItems) {
            CheckBox suiteCheckbox = suiteItem.getDescendantsBySelector(SUITE_CHECKBOX_CSS).get(0).as(CheckBox.class);
            if (suiteCheckbox.isSelected()) {
                UiComponent suiteNameComponent = suiteItem.getDescendantsBySelector(SUITE_NAME_CLASSNAME).get(0);
                suitesNames.add(suiteNameComponent.getText());
            }
        }
        return suitesNames;
    }

    public boolean isSuiteAdded(String suiteName) {
        for (UiComponent suiteItem : suitesItems) {
            UiComponent actualSuiteName = suiteItem.getDescendantsBySelector(SUITE_NAME_CLASSNAME).get(0);
            if (!suiteName.equals(actualSuiteName.getText())) {
                continue;
            }
            UiComponent infoIcon = suiteItem.getDescendantsBySelector(SUITE_INFO_ICON_CLASSNAME).get(0);
            if (infoIcon.isDisplayed()) {
                return true;
            }
        }
        return false;
    }
}
