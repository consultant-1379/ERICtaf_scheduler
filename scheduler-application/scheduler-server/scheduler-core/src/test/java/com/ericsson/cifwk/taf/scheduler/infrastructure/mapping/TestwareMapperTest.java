package com.ericsson.cifwk.taf.scheduler.infrastructure.mapping;

import com.ericsson.cifwk.taf.scheduler.api.dto.TestwareInfo;
import com.ericsson.cifwk.taf.scheduler.model.Gav;
import com.ericsson.cifwk.taf.scheduler.model.Testware;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

/**
 * Created by eniakel on 25/04/2016.
 */
public class TestwareMapperTest {
    TestwareMapper testwareMapper;

    @Before
    public void setUp(){
        testwareMapper = new TestwareMapper();
    }

    @Test
    public void testMap() {

        Testware testwareEntity = mockTestware();

        TestwareInfo dto = testwareMapper.map(testwareEntity);

        assertEquals(dto.getId(), testwareEntity.getId());
        assertEquals(dto.getCxpNumber(), testwareEntity.getCxpNumber());
        assertEquals(dto.getGroupId(), testwareEntity.getGav().getGroupId());
        assertEquals(dto.getArtifactId(), testwareEntity.getGav().getArtifactId());
        assertEquals(dto.getVersion(), testwareEntity.getGav().getVersion());
    }

    @Test
    public void testMapToEntity() {
        TestwareInfo testware = mockTestwareInfo();

        Testware entity = testwareMapper.map(testware);

        assertEquals(entity.getGav().getGroupId(), testware.getGroupId());
        assertEquals(entity.getGav().getArtifactId(), testware.getArtifactId());
        assertEquals(entity.getGav().getVersion(), testware.getVersion());
        assertEquals(entity.getCxpNumber(), testware.getCxpNumber());
    }

    private static Testware mockTestware() {
        Testware entity = mock(Testware.class);
        when(entity.getId()).thenReturn(1L);
        when(entity.getCxpNumber()).thenReturn("CXP1234");

        Gav gavEntity = mockGav();
        when(entity.getGav()).thenReturn(gavEntity);
        return entity;
    }

    private static Gav mockGav() {
        Gav entity = mock(Gav.class);
        when(entity.getGroupId()).thenReturn("GroupId");
        when(entity.getArtifactId()).thenReturn("ArtifactId");
        when(entity.getVersion()).thenReturn("2");
        return entity;
    }

    private static TestwareInfo mockTestwareInfo() {
        TestwareInfo dto = mock(TestwareInfo.class);
        when(dto.getGroupId()).thenReturn("GroupId");
        when(dto.getArtifactId()).thenReturn("ArtifactId");
        when(dto.getVersion()).thenReturn("2");
        when(dto.getCxpNumber()).thenReturn("CXP1234");
        return dto;
    }
}
