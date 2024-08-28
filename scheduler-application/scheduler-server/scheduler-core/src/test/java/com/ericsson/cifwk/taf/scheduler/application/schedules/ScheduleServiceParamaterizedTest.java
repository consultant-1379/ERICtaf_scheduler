package com.ericsson.cifwk.taf.scheduler.application.schedules;

import com.ericsson.cifwk.taf.scheduler.application.repository.ScheduleRepository;
import com.ericsson.cifwk.taf.scheduler.constant.ApprovalStatus;
import com.ericsson.cifwk.taf.scheduler.api.dto.UserInfo;
import com.ericsson.cifwk.taf.scheduler.infrastructure.mapping.UserMapper;
import com.ericsson.cifwk.taf.scheduler.model.Drop;
import com.ericsson.cifwk.taf.scheduler.model.Product;
import com.ericsson.cifwk.taf.scheduler.model.Schedule;
import com.ericsson.cifwk.taf.scheduler.model.User;
import com.google.common.collect.Sets;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameters;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.Spy;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import static java.util.Arrays.asList;
import static org.junit.Assert.assertEquals;

/**
 * Created by eniakel on 13/11/2015.
 */

@RunWith(Parameterized.class)
public class ScheduleServiceParamaterizedTest {

    @InjectMocks
    ScheduleService scheduleService;
    @Mock
    private ScheduleRepository scheduleRepository;
    @Mock
    private Schedule schedule;
    @Spy
    UserMapper userMapper;
    @Mock
    Set<UserInfo> userInfo;

    private static final boolean SCHEDULE_VALIDATED = true;

    private Set<UserInfo> newReviewers;
    private Set<User> existingReviewers;
    private boolean expected;

    public ScheduleServiceParamaterizedTest(Set<UserInfo> newReviewers,
                                            Set<User> existingReviewers, boolean expected) {
        this.newReviewers = newReviewers;
        this.existingReviewers = existingReviewers;
        this.expected = expected;
    }

    @Before
    public void setUp() throws Exception {
        schedule = sampleScheduleEntity();
        MockitoAnnotations.initMocks(this);
    }

    @Parameters
    public static Iterable<Object[]> testData() {
        return asList(new Object[][]{
                {Sets.newHashSet(), Sets.newHashSet(), false},
                {Sets.newHashSet(), generateReviewerList(asList(1)), false},
                {generateReviewerDtoList(asList(1)), Sets.newHashSet(), true},
                {generateReviewerDtoList(asList(1)), generateReviewerList(asList(2, 3)), true},
                {generateReviewerDtoList(asList(2, 3)), generateReviewerList(asList(2, 3)), false},
                {generateReviewerDtoList(asList(1, 2)), generateReviewerList(asList(2, 3)), true},
                {generateReviewerDtoList(asList(1, 2)), generateReviewerList(asList(3, 4)), true}
        });
    }

    @Test
    public void testIfNewReviewersAreAdded() {
        boolean areNewReviewersAdded = scheduleService.checkIfNewReviewersAdded(newReviewers, existingReviewers);
        assertEquals(expected, areNewReviewersAdded);
    }

    private Schedule sampleScheduleEntity() {
        schedule = new Schedule("sample-schedule", 5, "<xml/>", sampleDrop(), ApprovalStatus.UNAPPROVED.name(), null, SCHEDULE_VALIDATED);
        return schedule;
    }

    private Drop sampleDrop() {
        Product product = new Product();
        product.setName("ENM");
        return new Drop(product, "1.0.enm.early");
    }

    private static User generateUser(int index) {
        return new User("testId" + index, "testName" + index, "testEmail" + index);
    }

    @SuppressWarnings("Duplicates")
    private static UserInfo generateUserDto(int index) {
        return new UserInfo("testId" + index, "testName" + index, "testEmail" + index);
    }

    private static Set<User> generateReviewerList(List<Integer> userIndexes) {
        return userIndexes.stream()
                .map(ScheduleServiceParamaterizedTest::generateUser)
                .collect(Collectors.toSet());
    }

    private static Set<UserInfo> generateReviewerDtoList(List<Integer> userIndexes) {
        return userIndexes.stream()
                .map(ScheduleServiceParamaterizedTest::generateUserDto)
                .collect(Collectors.toSet());
    }
}
