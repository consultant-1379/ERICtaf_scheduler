package com.ericsson.cifwk.taf.scheduler.application.schedules.validation.parse;

/**
 * Created by Alexey Nikolaenko alexey.nikolaenko@ericsson.com
 * 16/07/2015
 */

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.xml.sax.Attributes;
import org.xml.sax.InputSource;
import org.xml.sax.Locator;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;
import java.io.IOException;
import java.io.StringReader;
import java.util.ArrayDeque;
import java.util.Deque;

import static com.ericsson.cifwk.taf.scheduler.application.schedules.validation.parse.LineNumberTracking.COL_END;
import static com.ericsson.cifwk.taf.scheduler.application.schedules.validation.parse.LineNumberTracking.COL_START;
import static com.ericsson.cifwk.taf.scheduler.application.schedules.validation.parse.LineNumberTracking.ROW_END;
import static com.ericsson.cifwk.taf.scheduler.application.schedules.validation.parse.LineNumberTracking.ROW_START;

public final class LineNumberTrackingXmlParser {

    private LineNumberTrackingXmlParser() {
    }

    public static Document parse(String xml) {
        try {
            final SAXParser parser = initParser();
            final Document doc = createNewDocument();

            try (StringReader reader = new StringReader(xml)) {
                InputSource is = new InputSource(reader);
                parser.parse(is, new LineInfoExtendingHandler(doc));
            }
            return doc;
        } catch (ParserConfigurationException | SAXException | IOException e) {
            throw new ParserException(e);
        }
    }

    private static SAXParser initParser() throws ParserConfigurationException, SAXException {
        final SAXParserFactory factory = SAXParserFactory.newInstance();
        return factory.newSAXParser();
    }

    private static Document createNewDocument() throws ParserConfigurationException {
        final DocumentBuilderFactory docBuilderFactory = DocumentBuilderFactory.newInstance();
        final DocumentBuilder docBuilder = docBuilderFactory.newDocumentBuilder();
        return docBuilder.newDocument();
    }

    /**
     * Handler appends to document element copies extended with line and column numbers
     */
    private static class LineInfoExtendingHandler extends DefaultHandler {
        private final Document document;

        private Locator locator;
        private Deque<Element> elementStack = new ArrayDeque<>();
        private StringBuilder textBuffer = new StringBuilder();

        LineInfoExtendingHandler(Document document) {
            this.document = document;
        }

        @Override
        public void setDocumentLocator(final Locator locator) {
            this.locator = locator;
        }

        @Override
        public void startElement(final String uri, final String localName, final String qName, final Attributes attributes)
                throws SAXException {
            addTagBodyIfExists();

            Element el = document.createElement(qName);

            copyAttributesTo(attributes, el);
            appendStartLocation(el);

            elementStack.push(el);
        }

        @Override
        public void endElement(final String uri, final String localName, final String qName) {
            addTagBodyIfExists();

            final Element closedElement = elementStack.pop();
            appendEndLocation(closedElement);
            // Is root element
            if (elementStack.isEmpty()) {
                document.appendChild(closedElement);
            } else {
                final Element parentElement = elementStack.peek();
                parentElement.appendChild(closedElement);
            }
        }

        @Override
        public void characters(final char[] ch, final int start, final int length) throws SAXException {
            textBuffer.append(ch, start, length);
        }

        // Outputs text accumulated under the current node
        private void addTagBodyIfExists() {
            if (textBuffer.length() > 0) {
                Element el = elementStack.peek();
                Node textNode = document.createTextNode(textBuffer.toString());
                el.appendChild(textNode);
                textBuffer.delete(0, textBuffer.length());
            }
        }

        private static void copyAttributesTo(Attributes attributes, Element el) {
            for (int i = 0; i < attributes.getLength(); i++) {
                el.setAttribute(attributes.getQName(i), attributes.getValue(i));
            }
        }

        private void appendStartLocation(Element el) {
            el.setUserData(ROW_START, String.valueOf(locator.getLineNumber()), null);
            el.setUserData(COL_START, String.valueOf(locator.getColumnNumber()), null);
        }

        private void appendEndLocation(Element el) {
            el.setUserData(ROW_END, String.valueOf(locator.getLineNumber()), null);
            el.setUserData(COL_END, String.valueOf(locator.getColumnNumber()), null);
        }
    }
}
