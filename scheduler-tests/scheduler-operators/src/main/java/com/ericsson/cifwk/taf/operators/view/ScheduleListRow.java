package com.ericsson.cifwk.taf.operators.view;

import com.ericsson.cifwk.taf.ui.core.UiComponent;
import com.ericsson.cifwk.taf.ui.core.UiComponentNotFoundException;
import com.ericsson.cifwk.taf.ui.sdk.CheckBox;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.NoSuchElementException;

public class ScheduleListRow {

    private static final Logger LOGGER = LoggerFactory.getLogger(ScheduleListRow.class);

    public static final String SCHEDULE_TD_NAME_CLASS = "schedule-td_name";
    public static final String SCHEDULE_TD_CREATED_BY_CLASS = "schedule-td_createdBy";
    public static final String SCHEDULE_TD_UPDATED_CLASS = "schedule-td_updated";
    public static final String SCHEDULE_TD_CONTROLS = "schedule-td_controls";
    public static final String SCHEDULE_TD_CHECKBOX = "schedule-td_enabled";

    private UiComponent uiComponent;
    private List<UiComponent> children;
    private List<UiComponent> controls;

    public ScheduleListRow(UiComponent uiComponent) {
        this.uiComponent = uiComponent;
    }

    public String getName() {
        return getProperty(SCHEDULE_TD_NAME_CLASS);
    }

    public UiComponent getIconByTitle(String title) {
        return   getControls()
                .stream()
                .filter(u -> u.getProperty("title").contains(title))
                .findFirst()
                .orElseThrow(() -> new NoSuchElementException("Not Found"));
    }

    public String getCreatedBy() {
        return getProperty(SCHEDULE_TD_CREATED_BY_CLASS);
    }

    public String getUpdateDate() {
        return getProperty(SCHEDULE_TD_UPDATED_CLASS);
    }

    private String getProperty(String elementClass) {
        String children = "";
        try {
            children = getChildren()
                    .stream()
                    .filter(u -> u.getProperty("class").contains(elementClass))
                    .map(UiComponent::getText).findFirst().get();
        } catch (UiComponentNotFoundException exception) {
            LOGGER.info("UIComponent not found");
        } finally {
            return children;
        }
    }

    public void clickEditIcon() {
        clickIcon("Edit");
    }

    public void clickDeleteIcon() {
        clickIcon("Delete");
    }

    public void clickViewIcon() {
        clickIcon("View");
    }

    private void clickIcon(String iconId) {
        UiComponent icon = getControls()
                .stream()
                .filter(u -> u.getProperty("title").contains(iconId))
                .findFirst()
                .orElseThrow(() -> new NoSuchElementException("Not Found"));

        icon.click();
    }

    public void clickCheckbox() {
        getCheckBox().click();
    }

    public boolean isEnabled() {
        return getCheckBox().isSelected();
    }

    private CheckBox getCheckBox() {
        UiComponent checkBoxTd = getChildren().stream()
                .filter(u -> u.getProperty("class").contains(SCHEDULE_TD_CHECKBOX))
                .findFirst()
                .get();

        return checkBoxTd.getChildren().get(0).as(CheckBox.class);
    }

    private List<UiComponent> getControls() {
        if (controls == null) {
            controls = getChildren()
                    .stream()
                    .filter(u -> u.getProperty("class").contains(SCHEDULE_TD_CONTROLS))
                    .findFirst()
                    .map(UiComponent::getChildren).orElseThrow(() -> new NoSuchElementException("Controls Not Found"));
        }
        return controls;
    }

    private List<UiComponent> getChildren() {
        if (children == null) {
            children = uiComponent.getChildren();
            LOGGER.info("Found {} cells in row", children.size());
        }
        return children;
    }

    public UiComponent getUiComponent() {
        return uiComponent;
    }
}
