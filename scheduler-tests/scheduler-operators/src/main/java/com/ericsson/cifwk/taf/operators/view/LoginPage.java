package com.ericsson.cifwk.taf.operators.view;

import com.ericsson.cifwk.taf.ui.core.UiComponentMapping;
import com.ericsson.cifwk.taf.ui.sdk.GenericViewModel;
import com.ericsson.cifwk.taf.ui.sdk.TextBox;

public class LoginPage extends GenericViewModel {

    @UiComponentMapping("#username")
    private TextBox userNameInput;

    @UiComponentMapping("#password")
    private TextBox passwordInput;

    public void login(String userName, String password) {
        userNameInput.setText(userName);
        passwordInput.setText(password);
        passwordInput.sendKeys("\n");
    }

    public boolean isOnLoginPage() {
        return passwordInput.exists();
    }

    public TextBox getPasswordInput() {
        return passwordInput;
    }
}
