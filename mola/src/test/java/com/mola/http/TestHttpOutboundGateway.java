package com.mola.http;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.integration.support.MessageBuilder;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.PollableChannel;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

/**
 * Created by bilgi on 3/7/15.
 */
@RunWith(SpringJUnit4ClassRunner.class)
//@ContextConfiguration("../../../../../main/webapp/WEB-INF/applicationContext.xml")
public class TestHttpOutboundGateway {

    @Autowired
    @Qualifier("quakeinfo.channel")
    PollableChannel quakeinfoChannel;
    @Autowired
    @Qualifier("quakeinfotrigger.channel")
    MessageChannel quakeinfoTriggerChannel;

    @Test
    public void testHttpOutbound() {
        quakeinfoTriggerChannel.send(MessageBuilder.withPayload("").build());
        Message<?> message = quakeinfoChannel.receive();
        assert message.getPayload() != null;
    }
}