package com.net.parse;

import java.util.ArrayList;
import java.util.List;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;
import org.xml.sax.ErrorHandler;
import org.apache.log4j.Logger;

public class ParserValidationHandler implements ErrorHandler {

	private transient final List<SAXParseException> parsingErrors = new ArrayList<SAXParseException>();

	public boolean hasParsingErrors() {

		return parsingErrors.size() > 0;
	}

	public void logParsingErrors(final Logger LOGGER) {

		for (SAXParseException error : parsingErrors) {
			LOGGER.error("XML parsing error at line: " + error.getLineNumber() + " " + error.getMessage());
		}
	}

	public void error(final SAXParseException exception) throws SAXException {
		parsingErrors.add(exception);
	}

	public void fatalError(final SAXParseException exception) throws SAXException {
		parsingErrors.add(exception);
	}

	public void warning(final SAXParseException exception) throws SAXException {
		parsingErrors.add(exception);
	}
}
