package com.platzhaltr.xslt4json.util;

import org.xml.sax.ErrorHandler;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;

/**
 * Writes error messages to {@link System.err}
 */
public class SystemErrErrorHandler implements ErrorHandler {

	/*
	 * @see org.xml.sax.ErrorHandler#warning(org.xml.sax.SAXParseException)
	 */
	@Override
	public void warning(SAXParseException exception) throws SAXException {
		log("warning", exception);
	}

	/*
	 * @see org.xml.sax.ErrorHandler#fatalError(org.xml.sax.SAXParseException)
	 */
	@Override
	public void fatalError(SAXParseException exception) throws SAXException {
		log("fatal error", exception);
	}

	/*
	 * @see org.xml.sax.ErrorHandler#error(org.xml.sax.SAXParseException)
	 */
	@Override
	public void error(SAXParseException exception) throws SAXException {
		log("error", exception);
	}

	/**
	 * Log.
	 * 
	 * @param type
	 *          the type
	 * @param exception
	 *          the exception
	 */
	private void log(String type, SAXParseException exception) {
		System.err.println(type+" at line: "+exception.getLineNumber()+" col:"+exception.getColumnNumber()+" message: "
												+exception.getMessage());
	}
}
