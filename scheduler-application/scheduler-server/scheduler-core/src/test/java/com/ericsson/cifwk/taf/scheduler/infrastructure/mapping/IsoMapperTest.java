package com.ericsson.cifwk.taf.scheduler.infrastructure.mapping;

import com.ericsson.cifwk.taf.scheduler.api.dto.IsoInfo;
import com.ericsson.cifwk.taf.scheduler.model.ISO;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;


public class IsoMapperTest {

    IsoMapper isoMapper;

    @Before
    public void setUp(){
        isoMapper = new IsoMapper();
    }

    @Test
    public void testMap() {

        ISO entity = mockIso();

        IsoInfo dto = isoMapper.map(entity);

        assertEquals(dto.getId(), entity.getId());
        assertEquals(dto.getName(), entity.getName());
        assertEquals(dto.getVersion(), entity.getVersion());
    }

    public static ISO mockIso() {
        ISO entity = mock(ISO.class);
        when(entity.getId()).thenReturn(1L);
        when(entity.getName()).thenReturn("IsoName");
        when(entity.getVersion()).thenReturn("1.1");
        return entity;
    }

}
