package com.ericsson.cifwk.taf.scheduler.infrastructure.mapping;

import com.ericsson.cifwk.taf.scheduler.api.dto.CommentInfo;
import com.ericsson.cifwk.taf.scheduler.model.Comment;
import org.springframework.stereotype.Service;

import java.util.LinkedList;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class CommentMapper {

    public CommentInfo map(Comment entity) {
        CommentInfo dto = new CommentInfo();
        dto.setMessage(entity.getMessage());
        dto.setCreated(entity.getCreated());
        dto.setCreatedBy(entity.getCreatedBy());
        dto.setScheduleVersion(entity.getSchedule().getVersion());
        return dto;
    }

    public List<CommentInfo> map(List<Comment> comments) {
        return comments.stream()
                .map(this::map)
                .collect(Collectors.toCollection(LinkedList::new));
    }
}
