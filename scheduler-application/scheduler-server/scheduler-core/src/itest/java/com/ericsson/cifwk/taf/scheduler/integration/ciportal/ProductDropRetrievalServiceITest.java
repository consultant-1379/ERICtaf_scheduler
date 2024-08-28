package com.ericsson.cifwk.taf.scheduler.integration.ciportal;

import com.ericsson.cifwk.taf.scheduler.Application;
import com.ericsson.cifwk.taf.scheduler.api.dto.DropInfo;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.boot.test.WebIntegrationTest;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.util.List;

import static junit.framework.Assert.assertFalse;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.MatcherAssert.assertThat;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = {Application.class})
@WebIntegrationTest(randomPort = true)
public class ProductDropRetrievalServiceITest {

    @Autowired
    ProductDropRetrievalService productDropRetrievalService;

    @Test
    public void testGetDrops() {
        List<DropInfo> enm = productDropRetrievalService.getDrops("ENM");
        assertThat(enm, notNullValue());
        assertFalse(enm.isEmpty());
    }
}
