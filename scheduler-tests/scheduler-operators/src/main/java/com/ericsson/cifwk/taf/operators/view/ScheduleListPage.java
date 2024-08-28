package com.ericsson.cifwk.taf.operators.view;

import com.beust.jcommander.internal.Lists;
import com.beust.jcommander.internal.Sets;
import com.ericsson.cifwk.taf.ui.UI;
import com.ericsson.cifwk.taf.ui.core.UiComponent;
import com.ericsson.cifwk.taf.ui.core.UiComponentMapping;
import com.ericsson.cifwk.taf.ui.sdk.GenericViewModel;
import com.ericsson.cifwk.taf.ui.sdk.Link;
import com.ericsson.cifwk.taf.ui.sdk.Select;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import static java.util.stream.Collectors.toList;

public class ScheduleListPage extends GenericViewModel {

    @UiComponentMapping("#createScheduleLink")
    private Link createScheduleButton;

    @UiComponentMapping(".schedules-results")
    private List<UiComponent> schedules;

    @UiComponentMapping("#team")
    private Select team;

    @UiComponentMapping(".schedules-tabTitle")
    private List<UiComponent> tabs;

    public void clickCreateScheduleButton() {
        createScheduleButton.click();
    }

    @Override
    public boolean isCurrentView() {
        return createScheduleButton.isDisplayed();
    }

    public List<ScheduleListRow> getSchedules() {
        return schedules.stream().map(ScheduleListRow::new).collect(toList());
    }

    public void viewSchedule(String scheduleName) {
        getScheduleListRow(scheduleName).clickViewIcon();
    }

    public void clickEditScheduleButton(String scheduleName) {
        ScheduleListRow scheduleToEdit = getScheduleListRow(scheduleName);
        scheduleToEdit.clickEditIcon();
    }

    public void clickViewScheduleButton(String scheduleName) {
        ScheduleListRow scheduleToEdit = getScheduleListRow(scheduleName);
        scheduleToEdit.clickViewIcon();
    }

    public boolean isScheduleEditButtonDisplayed(String scheduleName) {
        ScheduleListRow scheduleToEdit = getScheduleListRow(scheduleName);
        return scheduleToEdit.getIconByTitle("Edit").isDisplayed();
    }

    public void clickDeleteScheduleIcon(String scheduleName) {
        ScheduleListRow scheduleToDelete = getScheduleListRow(scheduleName);
        scheduleToDelete.clickDeleteIcon();
    }

    public boolean isScheduleInList(String scheduleName) {
        return getSchedules()
                .stream()
                .anyMatch(s -> s.getName().equals(scheduleName));
    }

    private ScheduleListRow getScheduleListRow(String scheduleName) {
        UI.pause(5000);
        return getSchedules()
                    .stream()
                    .filter(s -> s.getName().equals(scheduleName))
                    .findFirst()
                    .orElseThrow(() -> new AssertionError("Schedule " + scheduleName + " not found"));
    }

    public int getNumberOfSchedules() {
        List<UiComponent> displayedSchedules = getDisplayedSchedules();
        return displayedSchedules.size();
    }

    private List<UiComponent> getDisplayedSchedules() {
        return schedules.stream()
                .filter(s -> s.isDisplayed())
                .collect(Collectors.toList());
    }

    public String getTeam() {
        return team.getText();
    }

    public void selectTeam(String team){
        this.team.selectByTitle(team);
    }

    public String getUniqueTeamFromTable() {
        Set<String> teamNames = getUniqueTeamsOnTable();
        return Lists.newArrayList(teamNames).get(0);
    }

    public int getNumberOfUniqueTeams() {
        return getUniqueTeamsOnTable().size();
    }

    private Set<String> getUniqueTeamsOnTable() {
        Set<String> teamNames = Sets.newHashSet();
        for (UiComponent scheduleRow : getDisplayedSchedules()) {
            String teamName = scheduleRow.getDescendantsBySelector(".schedule-td_team").get(0).getText();
            teamNames.add(teamName);
        }
        return teamNames;
    }

    public void selectTabByName(String name) {
        UiComponent tab = tabs.stream()
                .filter(t -> name.equals(t.getText()))
                .findAny()
                .orElseThrow(IllegalArgumentException::new);
        tab.click();
    }
}

