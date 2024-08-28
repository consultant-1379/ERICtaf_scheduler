package com.ericsson.cifwk.taf.scheduler.infrastructure.mapping;

import com.ericsson.cifwk.taf.scheduler.api.dto.CommentInfo;
import com.ericsson.cifwk.taf.scheduler.model.Comment;
import com.ericsson.cifwk.taf.scheduler.model.Schedule;
import com.google.common.collect.Lists;
import org.junit.Before;
import org.junit.Test;

import java.util.Date;
import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;


public class CommentMapperTest {

    private static final String MESSAGE = "comment text";
    private static final String AUTHOR = "user1";
    private static final Date DATE = new Date();
    private static final int SCHEDULE_VERSION = 1;
    CommentMapper commentMapper;

    @Before
    public void setUp() {
        commentMapper = new CommentMapper();
    }

    @Test
    public void testMap() {
        Comment entity = mockComment();

        CommentInfo dto = commentMapper.map(entity);

        assertEquals(dto.getMessage(), entity.getMessage());
        assertEquals(dto.getCreated(), entity.getCreated());
        assertEquals(dto.getCreatedBy(), entity.getCreatedBy());
        assertThat(dto.getScheduleVersion(), is(entity.getSchedule().getVersion()));
    }

    @Test
    public void testMap_MultipleComments() {
        Comment entity = mockComment();
        List<Comment> comments = Lists.newArrayList();
        comments.add(entity);
        comments.add(entity);

        List<CommentInfo> dto = commentMapper.map(comments);
        assertEquals(dto.size(), 2);
        assertEquals(dto.get(0).getMessage(), entity.getMessage());
        assertEquals(dto.get(0).getCreated(), entity.getCreated());
        assertEquals(dto.get(0).getCreatedBy(), entity.getCreatedBy());
        assertThat(dto.get(0).getScheduleVersion(), is(entity.getSchedule().getVersion()));
    }

    public static Comment mockComment() {
        Comment entity = mock(Comment.class);
        Schedule schedule = mock(Schedule.class);

        when(entity.getMessage()).thenReturn(MESSAGE);
        when(entity.getCreated()).thenReturn(DATE);
        when(entity.getCreatedBy()).thenReturn(AUTHOR);
        when(entity.getSchedule()).thenReturn(schedule);
        when(schedule.getVersion()).thenReturn(SCHEDULE_VERSION);
        return entity;
    }

}
