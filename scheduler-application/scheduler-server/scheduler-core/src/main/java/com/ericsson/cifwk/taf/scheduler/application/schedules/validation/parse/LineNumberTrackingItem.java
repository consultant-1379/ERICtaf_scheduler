package com.ericsson.cifwk.taf.scheduler.application.schedules.validation.parse;

import com.ericsson.cifwk.taf.scheduler.api.dto.ErrorRange;
import org.w3c.dom.Node;

import static com.ericsson.cifwk.taf.scheduler.application.schedules.validation.parse.LineNumberTracking.COL_END;
import static com.ericsson.cifwk.taf.scheduler.application.schedules.validation.parse.LineNumberTracking.COL_START;
import static com.ericsson.cifwk.taf.scheduler.application.schedules.validation.parse.LineNumberTracking.ROW_END;
import static com.ericsson.cifwk.taf.scheduler.application.schedules.validation.parse.LineNumberTracking.ROW_START;

/**
 * Created by Alexey Nikolaenko alexey.nikolaenko@ericsson.com
 * 17/07/2015
 */
public final class LineNumberTrackingItem {

    private LineNumberTrackingItem() {
    }

    public static ErrorRange getErrorRange(Node item) {
        return new ErrorRange(getStartRow(item), getStartColumn(item), getEndRow(item), getEndColumn(item));
    }

    public static int getStartRow(Node item) {
        return Integer.valueOf((String) item.getUserData(ROW_START));
    }

    public static int getStartColumn(Node item) {
        return Integer.valueOf((String) item.getUserData(COL_START));
    }

    public static int getEndRow(Node item) {
        return Integer.valueOf((String) item.getUserData(ROW_END));
    }

    public static int getEndColumn(Node item) {
        return Integer.valueOf((String) item.getUserData(COL_END));
    }
}
