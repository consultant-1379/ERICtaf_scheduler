package com.ericsson.cifwk.taf.scheduler.application.schedules.validation.parse;

import org.junit.Test;

/**
 * Created by Alexey Nikolaenko alexey.nikolaenko@ericsson.com
 * 22/07/2015
 */
public class LineNumberTrackingXmlParserTest {

    @Test(expected = ParserException.class)
    public void shouldThrowParseExceptionWhenNotXmlIsPassed() {
        LineNumberTrackingXmlParser.parse("notxml");
    }
}
