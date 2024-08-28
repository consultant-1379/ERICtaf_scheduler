package com.ericsson.cifwk.taf.operators.view;

import com.ericsson.cifwk.taf.ui.core.UiComponent;
import com.ericsson.cifwk.taf.ui.core.UiComponentMapping;
import com.ericsson.cifwk.taf.ui.sdk.GenericViewModel;

public class BreadcrumbsView extends GenericViewModel {

    @UiComponentMapping("#wrap > div.container.ng-scope > div > ol > li:nth-child(1) > a")
    private UiComponent schedulesBreadcrumb;

    public void goToSchedules() {
        schedulesBreadcrumb.click();
    }

    public boolean isDisplayed() {
        return schedulesBreadcrumb.isDisplayed();
    }
}
