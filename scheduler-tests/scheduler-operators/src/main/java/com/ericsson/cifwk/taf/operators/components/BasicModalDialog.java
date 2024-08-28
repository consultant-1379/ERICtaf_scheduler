package com.ericsson.cifwk.taf.operators.components;

import com.ericsson.cifwk.taf.ui.core.AbstractUiComponent;
import com.ericsson.cifwk.taf.ui.core.UiComponentMapping;
import com.ericsson.cifwk.taf.ui.sdk.Button;

/**
 * Created by eniakel on 05/04/2016.
 */
public class BasicModalDialog extends AbstractUiComponent {

    @UiComponentMapping(".modal-footer .btn-primary")
    private Button primaryButton;

    @UiComponentMapping(".modal-footer .btn-success")
    private Button successButton;

    @UiComponentMapping(".modal-footer .btn-default")
    private Button defaultButton;

    public Button getPrimaryButton() {
        return primaryButton;
    }

    public Button getSuccessButton() {
        return successButton;
    }

    public Button getDefaultButton() {
        return defaultButton;
    }
}
