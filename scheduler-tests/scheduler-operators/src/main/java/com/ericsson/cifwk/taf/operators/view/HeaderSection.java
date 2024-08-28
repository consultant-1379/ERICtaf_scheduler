package com.ericsson.cifwk.taf.operators.view;

import com.ericsson.cifwk.taf.ui.core.UiComponent;
import com.ericsson.cifwk.taf.ui.core.UiComponentMapping;
import com.ericsson.cifwk.taf.ui.sdk.GenericViewModel;
import com.ericsson.cifwk.taf.ui.sdk.Link;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class HeaderSection extends GenericViewModel {

    private static final Logger LOGGER = LoggerFactory.getLogger(HeaderSection.class);

    @UiComponentMapping(".navbar")
    private UiComponent navbar;

    @UiComponentMapping("#signOutButton")
    private Link signOutButton;

    @UiComponentMapping("#toast-container")
    private UiComponent toastsHolder;

    @UiComponentMapping("#helpButton")
    private UiComponent helpButton;

    public void logout() {
        signOutButton.click();
    }

    public boolean isToastsHolderDisplayed() {
        if (toastsHolder.isDisplayed()) {
            LOGGER.info("Toasts visible. Must wait for toasts to clear!");
        } else {
            LOGGER.info("Toasts not found. Nothing to clear!");
        }
        return toastsHolder.isDisplayed();
    }

    public void help() {
        helpButton.click();
    }

    public boolean isCurrentView() {
        return navbar.isDisplayed();
    }
}
