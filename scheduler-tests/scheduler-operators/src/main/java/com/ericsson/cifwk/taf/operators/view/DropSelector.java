package com.ericsson.cifwk.taf.operators.view;

import com.ericsson.cifwk.taf.operators.DropSelection;
import com.ericsson.cifwk.taf.ui.BrowserTab;
import com.ericsson.cifwk.taf.ui.core.UiComponentMapping;
import com.ericsson.cifwk.taf.ui.core.WaitTimedOutException;
import com.ericsson.cifwk.taf.ui.sdk.GenericViewModel;
import com.ericsson.cifwk.taf.ui.sdk.Select;

import static com.google.common.base.Preconditions.checkState;

public class DropSelector extends GenericViewModel {

    private static final long TIMEOUT = 30000;
    private static final int MAX_RETRY = 5;

    @UiComponentMapping("#product")
    private Select product;

    @UiComponentMapping("#drop")
    private Select drop;

    public String getProduct() {
        return product.getText();
    }

    public String getDrop() {
        return drop.getText();
    }

    public boolean isEnabled() {
        checkState(product.isEnabled() == drop.isEnabled());
        return product.isEnabled();
    }

    public void selectDrop(DropSelection selection, BrowserTab browserTab ) {
        selectProduct(selection.getProduct());
        selectDrop(selection.getDrop(), browserTab);
    }

    public DropSelection getSelection() {
        return new DropSelection(getProduct(), getDrop());
    }

    public void selectDrop(String dropValue, BrowserTab browserTab) {
        int count = 0;
        while (count <= MAX_RETRY) {
            try {
                drop.waitUntil(new NotEmptyDropdownWithValuePredicate(drop, dropValue), TIMEOUT);
                count = MAX_RETRY + 1;
            } catch (WaitTimedOutException e) {
                browserTab.refreshPage();
                count++;
            }
        }
        drop.selectByValue(dropValue);
    }

    public void selectProduct(String productValue) {
        product.waitUntil(new NotEmptyDropdownPredicate(product), TIMEOUT);
        product.selectByValue(productValue);
    }

    public boolean areDropOptionsPresent() {
        return drop.getChildren().size() > 0;
    }

    public boolean isDisplayed() {
        return (product.isDisplayed() && drop.isDisplayed());
    }

}

