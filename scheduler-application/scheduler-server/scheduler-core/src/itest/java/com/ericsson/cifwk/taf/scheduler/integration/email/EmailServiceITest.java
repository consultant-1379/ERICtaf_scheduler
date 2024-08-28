package com.ericsson.cifwk.taf.scheduler.integration.email;

import com.ericsson.cifwk.taf.scheduler.Application;
import com.google.common.collect.Lists;
import com.icegreen.greenmail.util.GreenMail;
import com.icegreen.greenmail.util.GreenMailUtil;
import com.icegreen.greenmail.util.ServerSetupTest;
import org.hamcrest.Matchers;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.boot.test.WebIntegrationTest;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import javax.mail.internet.MimeMessage;
import java.util.Arrays;
import java.util.List;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.stringContainsInOrder;

/**
 * @author Vladimirs Iljins (vladimirs.iljins@ericsson.com)
 *         02/11/2015
 */
@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = {Application.class})
@WebIntegrationTest(randomPort = true)
public class EmailServiceITest {

    private static final String RECIPIENT1 = "taftest1@ericsson.com";
    private static final String RECIPIENT2 = "taftest2@ericsson.com";
    private static final String RECIPIENT3 = "taftest3@ericsson.com";

    private static final String SUBJECT = "Test subject";
    private static final String MESSAGE_TEXT = "Test message";

    @Autowired
    private EmailServiceImpl emailService;

    private GreenMail smtpServer;
    private List<String> recipients;

    @Before
    public void setUp() throws Exception {
        recipients = Lists.newArrayList(RECIPIENT1, RECIPIENT2, RECIPIENT3);

        smtpServer = new GreenMail(ServerSetupTest.SMTP);
        smtpServer.start();
    }

    @Test
    public void sendEmail() throws Exception {
        emailService.sendEmail(recipients, SUBJECT, MESSAGE_TEXT);

        List<MimeMessage> receivedMessages = Arrays.asList(smtpServer.getReceivedMessages());
        assertThat(receivedMessages, Matchers.hasSize(recipients.size()));

        MimeMessage message = receivedMessages.get(0);
        assertThat(message.getSubject(), is(SUBJECT));
        assertThat(GreenMailUtil.getBody(message), is(MESSAGE_TEXT));

        String addressList = GreenMailUtil.getAddressList(message.getAllRecipients());
        assertThat(addressList, stringContainsInOrder(recipients));
    }

    @After
    public void tearDown() throws Exception {
        smtpServer.stop();
    }
}