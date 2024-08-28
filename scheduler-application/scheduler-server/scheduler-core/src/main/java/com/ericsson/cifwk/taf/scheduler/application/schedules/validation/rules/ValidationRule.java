package com.ericsson.cifwk.taf.scheduler.application.schedules.validation.rules;

import com.ericsson.cifwk.taf.scheduler.application.schedules.validation.parse.LineNumberTrackingItem;
import com.ericsson.cifwk.taf.scheduler.api.dto.ErrorRange;
import com.ericsson.cifwk.taf.scheduler.api.dto.ScheduleErrorInfo;
import org.w3c.dom.Node;

import java.util.Optional;

/**
 * Created by Alexey Nikolaenko alexey.nikolaenko@ericsson.com
 * 17/07/2015
 */
public interface ValidationRule {

    boolean isMandatory();

    Optional<ScheduleErrorInfo> validate(Optional<Node> item);

    default ScheduleErrorInfo errorForItem(Node node, String description) {
        ErrorRange errorRange = LineNumberTrackingItem.getErrorRange(node);
        return new ScheduleErrorInfo(errorRange, description);
    }
}
