package com.ericsson.cifwk.taf.scheduler.infrastructure.mapping;

import com.ericsson.cifwk.taf.scheduler.api.dto.DropInfo;
import com.ericsson.cifwk.taf.scheduler.model.Drop;
import com.ericsson.cifwk.taf.scheduler.model.Product;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

/**
 * Created by eniakel on 25/04/2016.
 */
public class DropMapperTest {
    DropMapper dropMapper;

    @Before
    public void setUp(){
        dropMapper = new DropMapper();
    }

    @Test
    public void testMap() {
        Drop entity = mockDrop();

        DropInfo dto = dropMapper.map(entity);

        assertEquals(dto.getId(), entity.getId());
        assertEquals(dto.getName(), entity.getName());
        assertEquals(dto.getProductName(), entity.getProduct().getName());
    }

    public static Drop mockDrop() {
        Drop entity = mock(Drop.class);
        when(entity.getId()).thenReturn(1L);
        when(entity.getName()).thenReturn("DropName");

        Product product = mockProduct();
        when(entity.getProduct()).thenReturn(product);
        return entity;
    }

    public static Product mockProduct() {
        Product entity = mock(Product.class);
        when(entity.getId()).thenReturn(1L);
        when(entity.getName()).thenReturn("ProductName");
        when(entity.getDescription()).thenReturn("ProductDescription");
        return entity;
    }
}
