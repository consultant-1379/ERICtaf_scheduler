package com.ericsson.cifwk.taf.operators.view;

import com.ericsson.cifwk.taf.ui.core.GenericPredicate;
import com.ericsson.cifwk.taf.ui.sdk.Select;

/**
 * Created by egergle on 04/04/2017.
 */
public class NotEmptyDropdownWithValuePredicate extends GenericPredicate {

    private final Select dropdown;
    private final String value;

    public NotEmptyDropdownWithValuePredicate(Select dropdown, String value) {
        this.dropdown = dropdown;
        this.value = value;
    }

    @Override
    public boolean apply() {
        return dropdown.getAllOptions().stream()
                .filter(item -> item.getText().equals(this.value))
                .findFirst().isPresent();
    }
}