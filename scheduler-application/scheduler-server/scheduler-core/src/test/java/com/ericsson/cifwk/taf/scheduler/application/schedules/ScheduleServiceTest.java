package com.ericsson.cifwk.taf.scheduler.application.schedules;

import com.ericsson.cifwk.taf.scheduler.api.dto.CommentInfo;
import com.ericsson.cifwk.taf.scheduler.api.dto.DropInfo;
import com.ericsson.cifwk.taf.scheduler.api.dto.ScheduleInfo;
import com.ericsson.cifwk.taf.scheduler.api.dto.TypeInfo;
import com.ericsson.cifwk.taf.scheduler.api.dto.UserInfo;
import com.ericsson.cifwk.taf.scheduler.application.repository.DropRepository;
import com.ericsson.cifwk.taf.scheduler.application.repository.ScheduleRepository;
import com.ericsson.cifwk.taf.scheduler.application.users.UserService;
import com.ericsson.cifwk.taf.scheduler.constant.ApprovalStatus;
import com.ericsson.cifwk.taf.scheduler.infrastructure.mapping.CommentMapper;
import com.ericsson.cifwk.taf.scheduler.infrastructure.mapping.ScheduleMapper;
import com.ericsson.cifwk.taf.scheduler.infrastructure.mapping.UserMapper;
import com.ericsson.cifwk.taf.scheduler.infrastructure.template.TemplateService;
import com.ericsson.cifwk.taf.scheduler.integration.email.EmailService;
import com.ericsson.cifwk.taf.scheduler.model.Comment;
import com.ericsson.cifwk.taf.scheduler.model.Drop;
import com.ericsson.cifwk.taf.scheduler.model.Product;
import com.ericsson.cifwk.taf.scheduler.model.Schedule;
import com.ericsson.cifwk.taf.scheduler.model.User;
import com.google.common.collect.Sets;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import java.util.Optional;
import java.util.Set;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.Is.is;
import static org.junit.Assert.*;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyListOf;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.*;

/**
 * Created by Vladimirs Iljins vladimirs.iljins@ericsson.com
 * 17/07/2015
 */

@RunWith(MockitoJUnitRunner.class)
public class ScheduleServiceTest {

    private static final long ID = 42L;
    private static final String USER_ID = "taf1";
    private static final UserInfo reviewer = new UserInfo(USER_ID, "TafUser", "taf1@ericsson.com");
    private static final String EMAIL_TEXT = "Text";
    private static final boolean SCHEDULE_VALIDATED = true;

    @InjectMocks
    ScheduleService scheduleService;
    @Mock
    private ScheduleRepository scheduleRepository;
    @Mock
    private DropRepository dropRepository;
    @Mock
    private ScheduleMapper scheduleMapper;
    @Mock
    private Schedule schedule;
    @Mock
    private User user;
    @Mock
    private UserService userService;
    @Mock
    private TemplateService templateService;
    @Mock
    EmailService emailService;
    @Mock
    UserMapper userMapper;
    @Mock
    CommentMapper commentMapper;

    @Before
    public void setUp() throws Exception {
        when(scheduleRepository.save(any(Schedule.class))).thenReturn(sampleScheduleEntity());
        when(scheduleRepository.findLastVersionByOriginalScheduleId(ID)).thenReturn(schedule);
        when(schedule.getId()).thenReturn(ID);

        ScheduleInfo persisted = sampleSchedule();
        persisted.setId(ID);
        when(scheduleMapper.map(any(Schedule.class))).thenReturn(persisted);
        when(scheduleMapper.map(any(ScheduleInfo.class), any(Drop.class))).thenReturn(sampleScheduleEntity());
    }

    @Test
    public void testCreate() throws Exception {
        when(dropRepository.findByProductAndDropNames(anyString(), anyString())).thenReturn(sampleDrop());

        ScheduleInfo schedule = scheduleService.create(sampleSchedule());

        assertNotNull(schedule);
        assertNotNull(schedule.getId());
        verify(scheduleRepository).save(any(Schedule.class));
    }

    @Test
    public void testCreate_shouldReturnNullIfIsoNotFound() throws Exception {
        when(dropRepository.findByProductAndDropNames(anyString(), anyString())).thenReturn(null);

        ScheduleInfo schedule = scheduleService.create(sampleSchedule());

        verify(scheduleRepository, never()).save(any(Schedule.class));
        assertNull(schedule);
    }

    @Test
    public void testUpdate() throws Exception {
        when(scheduleRepository.findLastVersionByOriginalScheduleId(ID)).thenReturn(sampleScheduleEntity());

        ScheduleInfo schedule = scheduleService.update(sampleSchedule(), ID);

        assertNotNull(schedule);
        verify(scheduleRepository, times(2)).save(any(Schedule.class));
    }

    @Test
    public void testUpdate_shouldReturnNullIfScheduleNotFound() throws Exception {
        when(scheduleRepository.findLastVersionByOriginalScheduleId(ID)).thenReturn(null);

        ScheduleInfo schedule = scheduleService.update(sampleSchedule(), ID);

        verify(scheduleRepository, never()).save(any(Schedule.class));
        assertNull(schedule);
    }

    @Test
    public void shouldReturnSchedule() {
        Optional<ScheduleInfo> maybeSchedule = scheduleService.getSchedule(ID);
        assertThat(maybeSchedule.get().getId(), is(ID));
    }

    @Test
    public void shouldReturnOptionalEmptyWhenScheduleNotFound() {
        Optional<ScheduleInfo> maybeSchedule = scheduleService.getSchedule(15L);
        assertThat(maybeSchedule.isPresent(), is(false));
    }

    @Test
    public void addReviewer() {
        when(userService.findOrCreateUser(reviewer)).thenReturn(new User());
        when(userMapper.map(any(User.class))).thenReturn(reviewer);
        when(scheduleRepository.findVersion(1L, 1)).thenReturn(sampleScheduleEntity());
        when(templateService.generateFromTemplate(any(), any())).thenReturn(EMAIL_TEXT);
        when(userService.findByExternalIdOrEmail(any())).thenReturn(Optional.empty());

        Optional<UserInfo> user = scheduleService.addReviewer(1L, 1, reviewer);

        assertThat(user.isPresent(), is(true));
        verify(scheduleRepository).save(any(Schedule.class));
        verify(emailService).sendEmail(anyListOf(String.class), any(), any());
    }

    @Test
    public void addComment() {
        Schedule schedule = sampleScheduleEntity();
        schedule.addComment("Comment");
        when(scheduleRepository.findVersion(1L, 1)).thenReturn(schedule);
        when(scheduleRepository.save(any(Schedule.class))).thenReturn(schedule);
        when(commentMapper.map(any(Comment.class))).thenReturn(new CommentInfo());

        Optional<CommentInfo> comment = scheduleService.addComment(1L, 1, "Comment");

        assertThat(comment.isPresent(), is(true));
        verify(scheduleRepository).save(any(Schedule.class));
    }

    @Test
    public void updateApprovalStatus() {
        ScheduleInfo schedule = sampleSchedule();
        schedule.setApprovalStatus(ApprovalStatus.APPROVED.toString());
        Schedule scheduleEntity = sampleScheduleEntity();

        when(scheduleMapper.map(any(ScheduleInfo.class), any(Schedule.class))).thenReturn(scheduleEntity);
        when(scheduleRepository.save(any(Schedule.class))).thenReturn(scheduleEntity);

        scheduleService.updateApprovalStatus(schedule, ID);

        when(scheduleMapper.map(any(Schedule.class))).thenReturn(schedule);
        assertThat(schedule.getApprovalStatus(), is(ApprovalStatus.APPROVED.toString()));
    }

    private ScheduleInfo sampleSchedule() {
        ScheduleInfo schedule = new ScheduleInfo();
        schedule.setName("sample-schedule");
        schedule.setType(new TypeInfo(4));
        schedule.setVersion(1);
        schedule.setDrop(new DropInfo("ENM", "1.0.enm.early"));
        schedule.setXmlContent("<xml/>");
        schedule.setReviewers(sampleReviewers());
        return schedule;
    }

    private Schedule sampleScheduleEntity() {
        return new Schedule("sample-schedule", 5, "<xml/>", sampleDrop(), ApprovalStatus.UNAPPROVED.name(), "CI-TAF", SCHEDULE_VALIDATED);
    }

    private Drop sampleDrop() {
        Product product = new Product();
        product.setName("ENM");
        return new Drop(product, "1.0.enm.early");
    }

    private Set<UserInfo> sampleReviewers() {
        UserInfo user = new UserInfo();
        user.setName("testName");
        user.setEmail("testEmail@mail.com");
        user.setUserId("testId");

        Set<UserInfo> reviewers = Sets.newHashSet();
        reviewers.add(user);
        return reviewers;
    }

}
