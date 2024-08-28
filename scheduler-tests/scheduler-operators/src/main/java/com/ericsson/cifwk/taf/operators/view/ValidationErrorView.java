package com.ericsson.cifwk.taf.operators.view;

import com.ericsson.cifwk.taf.ui.core.UiComponentMapping;
import com.ericsson.cifwk.taf.ui.sdk.GenericViewModel;
import com.ericsson.cifwk.taf.ui.sdk.Label;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Created by Vladimirs Iljins vladimirs.iljins@ericsson.com
 * 24/07/2015
 */
public class ValidationErrorView extends GenericViewModel {

    @UiComponentMapping(".scheduleForm-validationError")
    private List<Label> validationErrors;

    public List<String> getValidationErrors() {
        return validationErrors.stream().map(e -> e.getText()).collect(Collectors.toList());
    }


}
