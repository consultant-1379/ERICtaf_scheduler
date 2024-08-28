package com.ericsson.cifwk.taf.scheduler.application.services;

import com.ericsson.cifwk.taf.scheduler.application.repository.DropRepository;
import com.ericsson.cifwk.taf.scheduler.application.repository.ProductRepository;
import com.ericsson.cifwk.taf.scheduler.api.dto.DropInfo;
import com.ericsson.cifwk.taf.scheduler.integration.ciportal.CiPortalClient;
import com.ericsson.cifwk.taf.scheduler.model.Drop;
import com.ericsson.cifwk.taf.scheduler.model.Product;
import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.runners.MockitoJUnitRunner;
import org.mockito.stubbing.Answer;

import java.util.List;
import java.util.Optional;
import java.util.Set;

import static java.util.stream.Collectors.toList;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.Matchers.containsInAnyOrder;
import static org.hamcrest.core.IsNull.notNullValue;
import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.*;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;
import static org.springframework.test.util.MatcherAssertionErrors.assertThat;


@RunWith(MockitoJUnitRunner.class)
public class DropServiceTest {

    @InjectMocks
    DropService dropService;

    @Mock
    CiPortalClient ciPortalClient;

    @Mock
    ProductRepository productRepository;

    @Mock
    DropRepository dropRepository;

    @Mock
    SchedulePromotionService schedulePromotionService;

    @Mock
    Product product;

    List<DropInfo> ciDropList = Lists.newArrayList();

    Set<Drop> productDrops = Sets.newHashSet();

    DropInfo newCiDrop = new DropInfo("ENM", "newDrop");
    DropInfo oldCiDrop = new DropInfo("ENM", "oldDrop");

    @Mock
    Drop deprecated;
    @Mock
    Drop oldProductDrop;


    @Before
    public void setUp() {
        when(productRepository.findByName(anyString())).then(mockedProductWithName());
        when(ciPortalClient.getDrops(anyString())).thenReturn(ciDropList);
        when(dropRepository.save(anyCollectionOf(Drop.class))).then(returnMockedDropsWithIds());
        doNothing().when(schedulePromotionService).promoteSchedulesByDrop(anyString(), anyList());

        //Mock CI Portal returned drops (1 old, 1 new)
        ciDropList.add(newCiDrop);
        ciDropList.add(oldCiDrop);

        //Mock Product returned drops (1 same, 1 deprecated)
        when(oldProductDrop.getName()).thenReturn("oldDrop");
        when(oldProductDrop.getProduct()).thenReturn(product);
        when(oldProductDrop.getId()).thenReturn(0L);

        when(deprecated.getName()).thenReturn("deprecatedDrop");
        when(deprecated.getProduct()).thenReturn(product);
        when(deprecated.getId()).thenReturn(3L);

        productDrops.add(deprecated);
        productDrops.add(oldProductDrop);

        when(product.getDrops()).thenReturn(productDrops);
    }

    @Test
    public void allReturnedDropsShouldHaveIds() {
        List<DropInfo> enm = dropService.getDrops("ENM");
        assertThat(enm.size(), is(3));
        enm.stream().forEach(d -> assertThat(d.getId(), notNullValue()));
    }

    @Test
    public void oldDropsHavePopulatedId() {
        List<DropInfo> enm = dropService.getDrops("ENM");
        Optional<DropInfo> oldDrop = enm.stream().filter(d -> d.getName().equals("oldDrop")).findFirst();
        assertTrue(oldDrop.isPresent());
        oldDrop.ifPresent(d -> assertThat(d.getId(), is(0L)));
    }

    @Test
    public void bothOldDropsAndNewAreReturned() {
        List<DropInfo> enm = dropService.getDrops("ENM");
        List<String> dropNames = enm.stream().map(DropInfo::getName).collect(toList());

        assertThat(dropNames, containsInAnyOrder("oldDrop", "newDrop", "deprecatedDrop"));
    }

    private Answer<Object> mockedProductWithName() {
        return invocationOnMock -> {
            String productName = (String) invocationOnMock.getArguments()[0];
            when(product.getId()).thenReturn(1L);
            when(product.getName()).thenReturn(productName);
            return product;
        };
    }

    @SuppressWarnings("unchecked")
    private Answer<Object> returnMockedDropsWithIds() {
        return invocationOnMock -> {
            List<Drop> newDrops = (List<Drop>) invocationOnMock.getArguments()[0];
            List<Drop> mockedDrops = Lists.newArrayList();

            long i = 1;
            for (Drop newDrop : newDrops) {
                Drop mockedDrop = Mockito.mock(Drop.class);
                when(mockedDrop.getId()).thenReturn(i++);
                when(mockedDrop.getName()).thenReturn(newDrop.getName());
                when(mockedDrop.getProduct()).thenReturn(product);
                mockedDrops.add(mockedDrop);
            }
            newDrops.clear();
            newDrops.addAll(mockedDrops);
            return newDrops;
        };
    }
}
