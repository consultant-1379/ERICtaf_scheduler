package com.ericsson.cifwk.taf.operators.view;

import com.ericsson.cifwk.taf.ui.core.GenericPredicate;
import com.ericsson.cifwk.taf.ui.sdk.Select;

/**
 * Created by eniakel on 05/04/2016.
 */
public class NotEmptyDropdownPredicate extends GenericPredicate {

    private final Select dropdown;

    public NotEmptyDropdownPredicate(Select dropdown) {
        this.dropdown = dropdown;
    }

    @Override
    public boolean apply() {
        return dropdown.getAllOptions().size() > 0;
    }
}