package com.ericsson.cifwk.taf.scheduler.application.services;

import com.ericsson.cifwk.taf.scheduler.model.Drop;
import com.ericsson.cifwk.taf.scheduler.model.Product;
import com.google.common.collect.Lists;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.runners.MockitoJUnitRunner;

import java.util.List;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.springframework.test.util.MatcherAssertionErrors.assertThat;

/**
 * Created by eniakel on 07/03/2016.
 */
@RunWith(MockitoJUnitRunner.class)
public class SchedulePromotionServiceTest {
    @InjectMocks
    SchedulePromotionService schedulePromotionService;

    @Test
    public void shouldReturnLatestVersion() {
        List<Drop> drops = createMockDrops();
        String latestDropName = schedulePromotionService.getLatestDropName(drops);
        assertThat(latestDropName, equalTo("1.5"));
    }

    private List<Drop> createMockDrops() {
        Product product = new Product();
        product.setName("ENM");

        Drop firstDrop = new Drop(product, "1.4");
        Drop secondDrop = new Drop(product, "1.2");
        Drop thirdDrop = new Drop(product, "1.5");

        List<Drop> drops = Lists.newArrayList();
        drops.add(firstDrop);
        drops.add(secondDrop);
        drops.add(thirdDrop);
        return drops;
    }
}

