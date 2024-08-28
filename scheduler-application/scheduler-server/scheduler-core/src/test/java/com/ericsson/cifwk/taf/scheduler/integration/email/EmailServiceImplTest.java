package com.ericsson.cifwk.taf.scheduler.integration.email;

import com.google.common.collect.Lists;
import org.hamcrest.Matchers;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.runners.MockitoJUnitRunner;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;

import java.util.Arrays;
import java.util.List;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.not;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.isEmptyString;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.verify;

@RunWith(MockitoJUnitRunner.class)
public class EmailServiceImplTest {

    @InjectMocks
    EmailServiceImpl emailService;
    @Mock
    JavaMailSender javaMailSender;

    @Test
    public void testSendEmail() throws Exception {
        Mockito.doNothing().when(javaMailSender).send(any(SimpleMailMessage.class));
        ArgumentCaptor<SimpleMailMessage> argument = ArgumentCaptor.forClass(SimpleMailMessage.class);

        emailService.sendEmail(Lists.newArrayList("taftest@ericsson.com", "  "), "Subj", "Text");

        verify(javaMailSender).send(argument.capture());
        List<String> recipients = Arrays.asList(argument.getValue().getTo());
        assertThat(recipients.size(), is(1));
        assertThat(recipients, Matchers.everyItem(not(isEmptyString())));
    }
}