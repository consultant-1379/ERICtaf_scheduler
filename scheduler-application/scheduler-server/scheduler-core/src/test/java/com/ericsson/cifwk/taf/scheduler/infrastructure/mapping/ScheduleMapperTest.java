package com.ericsson.cifwk.taf.scheduler.infrastructure.mapping;

import com.ericsson.cifwk.taf.scheduler.application.repository.ScheduleRepository;
import com.ericsson.cifwk.taf.scheduler.constant.ApprovalStatus;
import com.ericsson.cifwk.taf.scheduler.api.dto.ScheduleInfo;
import com.ericsson.cifwk.taf.scheduler.api.dto.SimpleScheduleInfo;
import com.ericsson.cifwk.taf.scheduler.api.dto.TypeInfo;
import com.ericsson.cifwk.taf.scheduler.model.Drop;
import com.ericsson.cifwk.taf.scheduler.model.ISO;
import com.ericsson.cifwk.taf.scheduler.model.Product;
import com.ericsson.cifwk.taf.scheduler.model.Schedule;
import com.google.common.collect.Lists;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import java.util.Date;
import java.util.List;

import static junit.framework.Assert.assertFalse;
import static junit.framework.TestCase.assertTrue;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.IsEqual.equalTo;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class ScheduleMapperTest {

    private static final String TEAM_CI_TOR = "CI-TOR";
    
    @InjectMocks
    ScheduleMapper mapper;

    @Mock
    ScheduleRepository scheduleRepository;

    @Mock
    DropMapper dropMapper;

    @Mock
    UserMapper userMapper;

    @Mock
    ISO iso;

    @Mock
    Drop drop;

    @Before
    public void setUp() {
        drop = mockDrop("ENM", "1.0.enm.early");
    }

    @Test
    public void testMapToDto() {
        Schedule schedule = mockSchedule(1L, 1, false);
        assertDtoMappedCorrectly(schedule);
    }

    @Test
    public void testMapScheduleListToDtos() {
        List<Schedule> allVersions = Lists.newArrayList(mockSchedule(1L, 1, false), mockSchedule(36L, 2, false));
        allVersions.forEach(s -> assertDtoMappedCorrectly(s));
    }

    private void assertDtoMappedCorrectly(Schedule schedule) {
        List<Schedule> allVersions = Lists.newArrayList(mockSchedule(1L, 1, false), mockSchedule(36L, 2, false));
        when(scheduleRepository.findAllVersions(any(Long.class))).thenReturn(allVersions);

        ScheduleInfo dto = mapper.map(schedule);

        assertEquals(schedule.getOriginalId(), dto.getId());
        assertEquals(schedule.getVersion(), dto.getVersion());
        assertEquals(schedule.getName(), dto.getName());
        assertEquals(schedule.getXml(), dto.getXmlContent());
        assertEquals(dto.getTeam(), TEAM_CI_TOR);
        assertThat(dto.getVersionList().size(), equalTo(2));
        assertTrue(dto.getVersionList().containsAll(Lists.newArrayList(1, 2)));
    }

    @Test
    public void testMapToSummaryDto() {
        Schedule schedule = mockSchedule(1L, 1, false);
        assertSummaryMappedCorrectly(schedule);
    }

    @Test
    public void testMapScheduleListToSummaryDtos() {
        List<Schedule> schedules = Lists.newArrayList(mockSchedule(1L, 1, false), mockSchedule(2L, 3, false));
        schedules.forEach(s -> assertSummaryMappedCorrectly(s));
    }

    private void assertSummaryMappedCorrectly(Schedule schedule) {
        SimpleScheduleInfo summary = mapper.mapSummary(schedule);

        assertThat(summary.getId(), equalTo(schedule.getId()));
        assertThat(summary.getName(), equalTo(schedule.getName()));
        assertThat(summary.getVersion(), equalTo(schedule.getVersion()));
        assertThat(summary.getType().getId(), equalTo(schedule.getType()));
        assertThat(summary.getCreatedBy(), equalTo(schedule.getCreatedBy()));
        assertThat(summary.getXml(), equalTo(schedule.getXml()));
        assertFalse(summary.isLastVersion());

        String[] urlFragments = summary.getUrl().split("/");
        Long originalScheduleIdInUrl = Long.parseLong(urlFragments[3]);
        Integer scheduleVersionInUrl = Integer.parseInt(urlFragments[5]);

        assertThat(originalScheduleIdInUrl, equalTo(schedule.getOriginalId()));
        assertThat(scheduleVersionInUrl, equalTo(schedule.getVersion()));
    }

    @Test
    public void testMapToEntity() {
        ScheduleInfo schedule = new ScheduleInfo();
        schedule.setId(1L);
        schedule.setName("Schedule1");
        schedule.setType(new TypeInfo(7));
        schedule.setXmlContent("</xml>");
        schedule.setCreated(new Date());
        schedule.setTeam(TEAM_CI_TOR);

        Schedule entity = mapper.map(schedule, drop);
        assertEquals(entity.getName(), schedule.getName());
        assertEquals(entity.getXml(), schedule.getXmlContent());
        assertEquals(entity.getType(), schedule.getType().getId());
        assertNotEquals(entity.getId(), schedule.getId());
        assertNotEquals(entity.getCreated(), schedule.getCreated());
        assertEquals(entity.getDrop().getProduct().getName(), "ENM");
        assertEquals(entity.getDrop().getName(), "1.0.enm.early");
        assertEquals(entity.getTeam(), TEAM_CI_TOR);
    }

    @Test
    public void mapType() {
        TypeInfo type = new TypeInfo();
        type.setId(12);

        TypeInfo dto = mapper.mapType(15);
        assertThat(dto.getId(), is(15));
        assertEquals(dto.getName(), "Virtual");
    }

    @Test
    public void shouldReturnProperScheduleURL() {
        mapper.port = 0;
        mapper.hostName = "localhost";

        String scheduleUrl = mapper.getScheduleUrl(1L, 2);
        assertThat(scheduleUrl, is("localhost/api/schedules/1/versions/2"));

        mapper.port = 8080;
        mapper.hostName = "atvts2827.athtem.eei.ericsson.se";

        scheduleUrl = mapper.getScheduleUrl(1L, 2);
        assertThat(scheduleUrl, is("atvts2827.athtem.eei.ericsson.se:8080/api/schedules/1/versions/2"));
    }

    private Schedule mockSchedule(Long id, Integer version, boolean isFirstVersion) {
        Schedule schedule = mock(Schedule.class);
        when(schedule.getId()).thenReturn(id);
        when(schedule.getOriginalId()).thenReturn(isFirstVersion ? null : id);
        when(schedule.getVersion()).thenReturn(version);
        when(schedule.getName()).thenReturn("Schedule1");
        when(schedule.getXml()).thenReturn("</xml>");
        when(schedule.getDrop()).thenReturn(drop);
        when(schedule.getApprovalStatus()).thenReturn(ApprovalStatus.UNAPPROVED);
        when(schedule.getTeam()).thenReturn(TEAM_CI_TOR);
        when(schedule.isLastVersion()).thenReturn(false);
        return schedule;
    }

    public Drop mockDrop(String productName, String dropName) {
        Product product = mock(Product.class);
        when(product.getName()).thenReturn(productName);

        Drop drop = mock(Drop.class);
        when(drop.getName()).thenReturn(dropName);
        when(drop.getProduct()).thenReturn(product);

        return drop;
    }

}
