package com.ericsson.cifwk.taf.operators.view;

import com.ericsson.cifwk.taf.ui.core.UiComponent;
import com.ericsson.cifwk.taf.ui.core.UiComponentMapping;
import com.ericsson.cifwk.taf.ui.sdk.GenericViewModel;
import com.ericsson.cifwk.taf.ui.sdk.Link;

import java.util.List;

/**
 * Created by ejhnhng on 08/03/2016.
 */
public class DocsView extends GenericViewModel {

    @UiComponentMapping(".scheduleDocs-link")
    private List<Link> navLinks;

    @UiComponentMapping(".schedules-docTemplate-heading")
    private UiComponent heading;

    public void clickLinkByName(String name) {
        navLinks.stream()
                .filter(l -> name.equals(l.getText()))
                .findFirst()
                .orElseThrow(IllegalArgumentException::new)
                .click();
    }

    public boolean isCorrectHeadingDisplayed(String name) {
        return name.equals(heading.getText());
    }

}
