package com.ericsson.cifwk.taf.operators.view;

import com.ericsson.cifwk.taf.operators.components.BasicModalDialog;
import com.ericsson.cifwk.taf.ui.core.UiComponent;
import com.ericsson.cifwk.taf.ui.core.UiComponentMapping;
import com.ericsson.cifwk.taf.ui.sdk.GenericViewModel;

import java.util.List;

import static java.util.stream.Collectors.toList;

public class IncludeScheduleModal extends GenericViewModel {

    public static final String MODAL_POPUP_HOLDER_CSS = "body.modal-open > .modal";
    public static final String TABLE_HOLDER_SELECTOR = ".schedulesModal-table";

    @UiComponentMapping(".schedules-results")
    private List<UiComponent> schedules;

    @UiComponentMapping(MODAL_POPUP_HOLDER_CSS)
    private BasicModalDialog basicModalDialog;

    public List<ScheduleListRow> getSchedules() {
        return schedules.stream().map(ScheduleListRow::new).collect(toList());
    }

    public void confirmSelection() {
        basicModalDialog.getPrimaryButton().click();
    }

    public void selectSchedule(String scheduleName) {
        ScheduleListRow scheduleToInclude = getSchedules().stream()
                .filter(r -> scheduleName.equals(r.getName())).findFirst()
                .orElseThrow(IllegalArgumentException::new);
        scheduleToInclude.clickCheckbox();
    }


}
