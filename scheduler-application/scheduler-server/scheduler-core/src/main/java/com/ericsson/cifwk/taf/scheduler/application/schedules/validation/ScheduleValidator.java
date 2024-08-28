
package com.ericsson.cifwk.taf.scheduler.application.schedules.validation;

import com.ericsson.cifwk.taf.scheduler.api.dto.ErrorRange;
import com.ericsson.cifwk.taf.scheduler.api.dto.ScheduleErrorInfo;
import org.xml.sax.Attributes;
import org.xml.sax.ErrorHandler;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;
import org.xml.sax.XMLReader;
import org.xml.sax.helpers.XMLFilterImpl;
import org.xml.sax.helpers.XMLReaderFactory;

import javax.xml.transform.sax.SAXSource;
import javax.xml.validation.Schema;
import javax.xml.validation.Validator;
import java.io.IOException;
import java.io.StringReader;
import java.util.LinkedList;
import java.util.List;

public class ScheduleValidator {

    public static final String NAMESPACE = "http://taf.lmera.ericsson.se/schema/te";

    private final Validator validator;
    private final ScheduleErrorHandler errorHandler;

    public ScheduleValidator(Schema schema) {
        validator = schema.newValidator();
        errorHandler = new ScheduleErrorHandler();
        validator.setErrorHandler(errorHandler);
    }

    public List<ScheduleErrorInfo> validate(String xml) {
        try {
            NamespaceFilter filter = new NamespaceFilter(NAMESPACE, XMLReaderFactory.createXMLReader());
            InputSource inputSource = new InputSource(new StringReader(xml));
            SAXSource source = new SAXSource(filter, inputSource);
            validator.validate(source);
        } catch (SAXException e) {
            errorHandler.addException(e);
        } catch (IOException e) {
            throw new RuntimeException("Schedule XML validation failed", e);
        }
        return errorHandler.getErrors();
    }

    private static class ScheduleErrorHandler implements ErrorHandler {

        private List<ScheduleErrorInfo> errors = new LinkedList<>();

        public List<ScheduleErrorInfo> getErrors() {
            return errors;
        }

        public void addException(SAXParseException e) {
            errors.add(new ScheduleErrorInfo(
                    new ErrorRange(e.getLineNumber(), 1, e.getLineNumber(), e.getColumnNumber()),
                    e.getMessage()));
        }

        public void addException(SAXException e) {
            errors.add(new ScheduleErrorInfo(
                    new ErrorRange(),
                    e.getMessage()));
        }

        @Override
        public void warning(SAXParseException exception) {
            addException(exception);
        }

        @Override
        public void fatalError(SAXParseException exception) {
            addException(exception);
        }

        @Override
        public void error(SAXParseException exception) {
            addException(exception);
        }

    }

    private static class NamespaceFilter extends XMLFilterImpl {

        private final String implicitNamespace;

        NamespaceFilter(String implicitNamespace, XMLReader parent) {
            super(parent);
            this.implicitNamespace = implicitNamespace;
        }

        @Override
        public void startElement(String uri, String localName, String qName, Attributes atts) throws SAXException {
            String namespace = uri.isEmpty() ? implicitNamespace : uri;
            super.startElement(namespace, localName, qName, atts);
        }
    }
}
