package com.ericsson.cifwk.taf.operators.view;

import com.ericsson.cifwk.taf.ui.core.UiComponent;
import com.ericsson.cifwk.taf.ui.core.UiComponentMapping;
import com.ericsson.cifwk.taf.ui.sdk.GenericViewModel;

public class LoginPopupViewModel extends GenericViewModel {

    @UiComponentMapping(".login-Holder")
    private UiComponent loginPopupHolder;

    @Override
    public boolean isCurrentView() {
        return loginPopupHolder.isDisplayed();
    }

}
