package com.ericsson.cifwk.taf.operators.view;

import com.ericsson.cifwk.taf.ui.core.SelectorType;
import com.ericsson.cifwk.taf.ui.core.UiComponent;
import com.ericsson.cifwk.taf.ui.core.UiComponentMapping;
import com.ericsson.cifwk.taf.ui.sdk.Button;
import com.ericsson.cifwk.taf.ui.sdk.GenericViewModel;
import com.ericsson.cifwk.taf.ui.sdk.Label;
import com.ericsson.cifwk.taf.ui.sdk.Link;
import com.ericsson.cifwk.taf.ui.sdk.Select;
import com.ericsson.cifwk.taf.ui.sdk.TextBox;
import org.openqa.selenium.Keys;

import java.util.List;

public class ScheduleEditPage extends GenericViewModel {

    public static final String TAB_LABEL_SELECTOR = ".scheduleForm-tabLabel";
    private final static String ADDED_TESTWARE_CLASSNAME = "li.scheduleForm-groupItem_isAdded";
    private final static String TESTWARE_NAME_CLASSNAME = "span.scheduleForm-itemName";
    private final static String SUITES_STATUS_CLASSNAME = "span.scheduleForm-suitesStatus";

    @UiComponentMapping(selector = ".scheduleForm")
    private UiComponent scheduleForm;

    @UiComponentMapping(selector = "#scheduleName")
    private TextBox name;

    @UiComponentMapping(selector = "#scheduleType")
    private Select type;

    @UiComponentMapping(selector = "#scheduleTeam")
    private Select team;

    @UiComponentMapping(selector = ".scheduleForm-xml")
    private TextBox xml;

    @UiComponentMapping(selector = "textarea.ace_text-input")
    private TextBox xmlWrite;

    @UiComponentMapping(selector = ".scheduleForm-testwareList")
    private UiComponent testwareListHolder;

    @UiComponentMapping(selector = ".scheduleForm-spinner")
    private UiComponent testwareListSpinner;

    @UiComponentMapping(selector = ".scheduleForm-testwareList > li.active a.scheduleForm-actionLink")
    private Link addTestwareLink;

    @UiComponentMapping(selector = ".scheduleForm-warningIcon")
    private UiComponent warningIcon;

    @UiComponentMapping(selector = ".scheduleForm-xmlError")
    private List<Label> xmlErrorMarkers;

    @UiComponentMapping(selector = "#validateButton")
    private Button validateButton;

    @UiComponentMapping(selector = "#saveButton")
    private Button saveButton;

    @UiComponentMapping(selector = "#cancelButton")
    private Link cancelButton;

    @UiComponentMapping(selector = "#addTagsButtonsBlock")
    private UiComponent addTagsButtonsBlock;

    @UiComponentMapping(selector = "#addItemAttributesButtonsBlock")
    private UiComponent addItemAttributesButtonsBlock;

    @UiComponentMapping(selector = "#addPropertyAttributesButtonsBlock")
    private UiComponent addPropertyAttributesButtonsBlock;

    @UiComponentMapping(selector = "#addTestCampaignAttributesButtonsBlock")
    private UiComponent addTestCampaignAttributesButtonsBlock;

    @UiComponentMapping(selector = "#addItemGroupValuesButtonsBlock")
    private UiComponent addItemGroupValuesButtonsBlock;

    @UiComponentMapping(selector = "#addCommentsBlockButton")
    private Button addCommentsBlockButton;

    @UiComponentMapping(selector = "#addItemGroupTagsButton")
    private Button addItemGroupTagsButton;

    @UiComponentMapping(selector = "#addPropertyTagsButton")
    private Button addPropertyTagsButton;

    @UiComponentMapping(selector = "#addEnvironmentTagsButton")
    private Button addEnvironmentTagsButton;

    @UiComponentMapping(selector = "#addItemTagsButton")
    private Button addItemTagsButton;

    @UiComponentMapping(selector = "#addManualItemTagsButton")
    private Button addManualItemTagsButton;

    @UiComponentMapping(selector = "#addTestCampaignsTagsButton")
    private Button addTestCampaignsTagsButton;

    @UiComponentMapping(selector = "#addTestCampaignTagsButton")
    private Button addTestCampaignTagsButton;

    @UiComponentMapping(selector = "#includeScheduleButton")
    private Button includeScheduleButton;

    @UiComponentMapping(selector = "#addParallelAttributeButton")
    private Button addParallelAttributeButton;

    @UiComponentMapping(selector = "#addStopOnFailAttributeButton")
    private Button addStopOnFailAttributeButton;

    @UiComponentMapping(selector = "#addPropertyAttributesButton")
    private Button addPropertyAttributesButton;

    @UiComponentMapping(selector = "#addTestCampaignAttributeButton")
    private Button addTestCampaignAttributeButton;

    @UiComponentMapping(selector = ".scheduleForm-iconHolder")
    private UiComponent refreshTestwareListIcon;

    @UiComponentMapping(selector = ".scheduleForm-tabLabel")
    private List<UiComponent> scheduleTabLabels;

    @UiComponentMapping(selector = ".scheduleForm-kgbIndicator")
    private UiComponent kgbIndicator;

    private static final String SELECT_ALL = Keys.chord(Keys.CONTROL, "a");
    private static final String DELETE_LINE = Keys.chord(Keys.CONTROL, "d");
    private static final String COMMENTS_BLOCK_SHORTCUT = Keys.chord(Keys.CONTROL, "/");
    private static final String CUSTOM_AUTOCOMPLETE_SHORTCUT = Keys.chord(Keys.CONTROL, ".");
    private static final String CONTROL_AND_LEFT_ARROW = Keys.chord(Keys.CONTROL, Keys.ARROW_LEFT);
    private static final String CONTROL_AND_RIGHT_ARROW = Keys.chord(Keys.CONTROL, Keys.ARROW_RIGHT);

    public String getName() {
        return name.getText();
    }

    public String getType() {
        return type.getText();
    }

    public void selectTeam(String team){
        this.team.selectByTitle(team);
    }

    public String getTeam() {
        return team.getText();
    }

    public void selectType(String type){
        this.type.selectByValue(type);
    }

    public String getXml() {
        return xml.getText();
    }

    public void setXml(String xml) {
        this.xml.setText(xml);
    }

    public void setName(String name) {
        this.name.setText(name);
    }

    public void save() {
        saveButton.click();
    }

    public void validate() {
        validateButton.click();
    }

    public void cancel() {
        cancelButton.click();
    }

    public boolean isWarningDisplayed() {
        return warningIcon.isDisplayed();
    }

    public boolean hasErrorMarkers() {
        return !xmlErrorMarkers.isEmpty();
    }

    public void removeDefaultItemContent() {
        // Go to <item> line
        xmlWrite.sendKeys(Keys.ARROW_DOWN, Keys.ARROW_DOWN, Keys.ARROW_DOWN, Keys.ARROW_DOWN);
        // Remove 5 lines with <item> content
        xmlWrite.sendKeys(
                DELETE_LINE, DELETE_LINE, DELETE_LINE, DELETE_LINE,
                DELETE_LINE, DELETE_LINE, DELETE_LINE, DELETE_LINE
        );
        // Add new line and move cursor UP
        xmlWrite.sendKeys(Keys.ENTER, Keys.ARROW_UP, Keys.TAB);
    }

    public void selectTestware(String testwareName) {
        String xpath = String.format("//li//span[contains(text(), '%s')]", testwareName);
        List<UiComponent> testwareComponent =
                testwareListHolder.getDescendantsBySelector(SelectorType.XPATH, xpath);
        if (testwareComponent.size() > 0) {
            testwareComponent.iterator().next().click();
        }
    }

    public boolean isTestwareSelected(String testwareName) {
        List<UiComponent> testwareComponent = testwareListHolder.getDescendantsBySelector("li.active");
        if (testwareComponent.size() != 1) {
            return false;
        }
        UiComponent listItem = testwareComponent.iterator().next();
        List<UiComponent> itemNameComponents = listItem.getDescendantsBySelector("span.scheduleForm-itemName");
        return itemNameComponents.size() == 1 && testwareName.equals(itemNameComponents.iterator().next().getText());
    }

    public void openSuitesPopup() {
        addTestwareLink.click();
    }

    public boolean scheduleContainsString(String suitesString) {
        return xml.getText().contains(suitesString);
    }

    @Override
    public boolean isCurrentView() {
        return scheduleForm.isDisplayed();
    }

    public boolean isSpinnerHidden() {
        return !testwareListSpinner.isDisplayed();
    }

    public boolean isAddTagsIconsBlockDisplayed() {
        return addTagsButtonsBlock.isDisplayed();
    }

    public boolean isAddItemAttributesIconsBlockDisplayed() {
        return addItemAttributesButtonsBlock.isDisplayed();
    }

    public boolean isAddItemGroupValuesIconsBlockDisplayed() {
        return addItemGroupValuesButtonsBlock.isDisplayed();
    }

    public boolean isAddPropertyAttributesIconBlockDisplayed() {
        return addPropertyAttributesButtonsBlock.isDisplayed();
    }

    public boolean isAddTestCampaignAttributesIconBlockDisplayed() {
        return addTestCampaignAttributesButtonsBlock.isDisplayed();
    }

    public void addCommentsBlock() {
        addCommentsBlockButton.click();
    }

    public void addItemGroupTagsBlock() {
        addItemGroupTagsButton.click();
    }

    public void addItemTagsBlock() {
        addItemTagsButton.click();
    }

    public void addManualItemTagsBlock() {
        addManualItemTagsButton.click();
    }

    public void addTestCampaignsTagsBlock() {
        addTestCampaignsTagsButton.click();
    }

    public void addTestCampaignTagsBlock() {
        addTestCampaignTagsButton.click();
    }

    public void addPropertyTagsBlock() {
        addPropertyTagsButton.click();
    }

    public void addEnvironmentTagsBlock() {
        addEnvironmentTagsButton.click();
    }

    public void sendKeysToEditor(CharSequence... chars) {
        xmlWrite.sendKeys(chars);
    }

    public void addParallelAttribute() {
        addParallelAttributeButton.click();
    }

    public void addStopOnFailAttribute() {
        addStopOnFailAttributeButton.click();
    }

    public void addPropertyAttributes() {
        addPropertyAttributesButton.click();
    }

    public void addTestCampaignAttributes() {
        addTestCampaignAttributeButton.click();
    }

    public void sendAutocompleteShortcut() {
        xmlWrite.sendKeys(CUSTOM_AUTOCOMPLETE_SHORTCUT);
    }

    public void sendCommentBlockShortcut() {
        xmlWrite.sendKeys(COMMENTS_BLOCK_SHORTCUT);
    }

    public void moveCursorInsideTagToAddAttribute() {
        xmlWrite.sendKeys(
                CONTROL_AND_LEFT_ARROW,
                CONTROL_AND_LEFT_ARROW,
                CONTROL_AND_LEFT_ARROW
        );
    }

    public void moveCursorInsideTestCampaignOrPropertyTagToAddAttribute() {
        xmlWrite.sendKeys(
                Keys.ARROW_LEFT,
                Keys.ARROW_LEFT
        );
    }

    public void moveCursorInsideIdTagToAddAttribute() {
        xmlWrite.sendKeys(
                Keys.ARROW_LEFT
        );
    }

    public void moveCursorInsideDoubleNamedTagToAddValue() {
        xmlWrite.sendKeys(
                CONTROL_AND_LEFT_ARROW,
                CONTROL_AND_LEFT_ARROW,
                CONTROL_AND_LEFT_ARROW,
                CONTROL_AND_LEFT_ARROW,
                Keys.ARROW_LEFT,
                Keys.ARROW_LEFT
        );
    }

    public void moveCursorOutOfEnvironmentTags() {
        xmlWrite.sendKeys(
                Keys.ARROW_DOWN
        );
    }

    public void moveCursorInsideTypeToAddAttribute() {
        xmlWrite.sendKeys(
                CONTROL_AND_LEFT_ARROW,
                CONTROL_AND_LEFT_ARROW,
                CONTROL_AND_LEFT_ARROW,
                CONTROL_AND_LEFT_ARROW,
                Keys.ARROW_LEFT
        );
    }

    public void moveCursorInsideTagsToAddValue() {
        xmlWrite.sendKeys(
                CONTROL_AND_LEFT_ARROW,
                CONTROL_AND_LEFT_ARROW,
                Keys.ARROW_LEFT,
                Keys.ARROW_LEFT
        );
    }

    public void moveCursorInsideItemGroupToAddAttribute() {
        xmlWrite.sendKeys(
                CONTROL_AND_LEFT_ARROW,
                CONTROL_AND_LEFT_ARROW,
                CONTROL_AND_LEFT_ARROW,
                CONTROL_AND_LEFT_ARROW,
                CONTROL_AND_LEFT_ARROW
        );
    }

    public void moveCursorInsidePropertyToAddValue() {
        xmlWrite.sendKeys(
                CONTROL_AND_RIGHT_ARROW,
                CONTROL_AND_RIGHT_ARROW,
                CONTROL_AND_RIGHT_ARROW,
                CONTROL_AND_RIGHT_ARROW,
                Keys.ARROW_RIGHT,
                Keys.ARROW_RIGHT
        );
    }

    public void moveCursorToTheEndOfLine() {
        xmlWrite.sendKeys(Keys.END);
    }

    public void pressEnterInEditor() {
        xmlWrite.sendKeys(Keys.ENTER);
    }

    public void moveCursorToCommentBlockValue() {
        xmlWrite.sendKeys(Keys.ARROW_LEFT, Keys.ARROW_LEFT, Keys.ARROW_LEFT);
    }

    public void pressDownArrowInEditor() {
        xmlWrite.sendKeys(Keys.ARROW_DOWN);
    }

    public void moveCursorToTheLine(int lineNr) {
        for (int i = 1; i < lineNr; i++) {
            xmlWrite.sendKeys(Keys.ARROW_DOWN);
        }
    }

    public boolean isTestwareIncluded(String testwareName) {
        List<UiComponent> addedTestwareList = getAddedTestwareList();
        for (UiComponent testwareItem : addedTestwareList) {
            UiComponent testwareNameComponent = testwareItem.getDescendantsBySelector(TESTWARE_NAME_CLASSNAME).get(0);
            if (testwareName.equals(testwareNameComponent.getText())) {
                return true;
            }
        }
        return false;
    }

    public boolean hasTestwareSuitesDistinctions(String testwareName) {
        List<UiComponent> addedTestwareList = getAddedTestwareList();
        for (UiComponent testwareItem : addedTestwareList) {
            UiComponent testwareNameComponent = testwareItem.getDescendantsBySelector(TESTWARE_NAME_CLASSNAME).get(0);
            if (!testwareName.equals(testwareNameComponent.getText())) {
                continue;
            }

            UiComponent suitesStatusComponent = testwareItem.getDescendantsBySelector(SUITES_STATUS_CLASSNAME).get(0);
            if (suitesStatusComponent.isDisplayed()) {
                return true;
            }
        }
        return false;
    }

    private List<UiComponent> getAddedTestwareList() {
        return testwareListHolder.getDescendantsBySelector(ADDED_TESTWARE_CLASSNAME);
    }

    public void refreshTestwareList() {
        refreshTestwareListIcon.click();
    }

    public void clickIncludeScheduleButton() {
        includeScheduleButton.click();
    }

    public boolean isIncludeScheduleButtonDisplayed() {
        return includeScheduleButton.isDisplayed();
    }

    public boolean isScheduleTabLabelDisplayed(String scheduleName) {
        String scheduleNameWithoutXML = scheduleName.split("\\.")[0];
        for (UiComponent label : scheduleTabLabels) {
            if (label.getText().toLowerCase().contains(scheduleNameWithoutXML.toLowerCase())) {
                return true;
            }
        }
        return false;
    }

    public boolean isKgbModeEnabled() {
        return kgbIndicator.isDisplayed();
    }
}
