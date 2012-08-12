package com.platzhaltr.xslt4json.io;

import java.io.IOException;
import java.io.OutputStream;

import org.xml.sax.Attributes;
import org.xml.sax.ContentHandler;
import org.xml.sax.Locator;
import org.xml.sax.SAXException;

/**
 * The Class JsonSaxWriter.
 */
public class JsonSaxWriter implements ContentHandler {

	/** The indent. */
	private boolean indent=true;

	/** The indent level. */
	private int indentLevel;

	/** The indent str. */
	private String indentStr="\t";

	/** The needs comma. */
	private boolean needsComma;

	/** The output stream. */
	private OutputStream outputStream;

	/**
	 * Instantiates a new json sax writer.
	 * 
	 * @param outputStream
	 *          the output stream
	 */
	public JsonSaxWriter(OutputStream outputStream) {
		this.outputStream=outputStream;
	}

	/*
	 * @see org.xml.sax.ContentHandler#characters(char[], int, int)
	 */
	@Override
	public void characters(char[] ch, int start, int length) throws SAXException {
		write(String.valueOf(ch, start, length));
	}

	/*
	 * @see org.xml.sax.ContentHandler#endDocument()
	 */
	@Override
	public void endDocument() throws SAXException {

	}

	/*
	 * @see org.xml.sax.ContentHandler#endElement(java.lang.String, java.lang.String, java.lang.String)
	 */
	@Override
	public void endElement(String uri, String localName, String qName) throws SAXException {
		if((uri==null||uri.length()==0)&&localName.equals(qName)) {
			needsComma=true;
			if(localName.equals("object")) {
				indentLevel--;
				writeln();
				write("}");
			} else if(localName.equals("array")) {
				indentLevel--;
				writeln();
				write("]");
			} else if(localName.equals("field")) {} else if(localName.equals("string")) {
				write("\"");
			} else if(localName.equals("integer")||localName.equals("double")||localName.equals("boolean")) {} else {
				throw new SAXException("not valid element: "+localName);
			}
		} else {
			throw new SAXException("not valid element: "+qName);
		}
	}

	/*
	 * @see org.xml.sax.ContentHandler#endPrefixMapping(java.lang.String)
	 */
	@Override
	public void endPrefixMapping(String prefix) throws SAXException {

	}

	/*
	 * @see org.xml.sax.ContentHandler#ignorableWhitespace(char[], int, int)
	 */
	@Override
	public void ignorableWhitespace(char[] ch, int start, int length) throws SAXException {

	}

	/*
	 * @see org.xml.sax.ContentHandler#processingInstruction(java.lang.String, java.lang.String)
	 */
	@Override
	public void processingInstruction(String target, String data) throws SAXException {

	}

	/*
	 * @see org.xml.sax.ContentHandler#setDocumentLocator(org.xml.sax.Locator)
	 */
	@Override
	public void setDocumentLocator(Locator locator) {

	}

	/*
	 * @see org.xml.sax.ContentHandler#skippedEntity(java.lang.String)
	 */
	@Override
	public void skippedEntity(String name) throws SAXException {

	}

	/*
	 * @see org.xml.sax.ContentHandler#startDocument()
	 */
	@Override
	public void startDocument() throws SAXException {

	}

	/*
	 * @see org.xml.sax.ContentHandler#startElement(java.lang.String, java.lang.String, java.lang.String, org.xml.sax.Attributes)
	 */
	@Override
	public void startElement(String uri, String localName, String qName, Attributes atts) throws SAXException {
		if((uri==null||uri.length()==0)&&localName.equals(qName)) {
			if(needsComma) {
				write(",");
				writeln();
				needsComma=false;
			}
			if(localName.equals("field")) {
				if(atts.getLength()==1&&atts.getQName(0).equals("name")) {
					write("\""+atts.getValue(0)+"\": ");
				} else {
					throw new SAXException("element 'field' must have only one name attribute");
				}
			} else {
				if(atts.getLength()!=0) { throw new SAXException("element '"+localName+"' must not have attributes"); }
				if(localName.equals("object")) {
					write("{");
					indentLevel++;
					writeln();
				} else if(localName.equals("array")) {
					write("[");
					indentLevel++;
					writeln();
				} else if(localName.equals("string")) {
					write("\"");
				} else if(localName.equals("integer")||localName.equals("double")||localName.equals("boolean")) {} else {
					throw new SAXException("not valid element: "+localName);
				}
			}
		} else {
			throw new SAXException("not valid element: "+qName);
		}
	}

	/*
	 * @see org.xml.sax.ContentHandler#startPrefixMapping(java.lang.String, java.lang.String)
	 */
	@Override
	public void startPrefixMapping(String prefix, String uri) throws SAXException {

	}

	/**
	 * Write.
	 * 
	 * @param s
	 *          the s
	 * @throws SAXException
	 *           the sAX exception
	 */
	private void write(String s) throws SAXException {
		try {
			outputStream.write(s.getBytes());
		} catch(IOException e) {
			throw new SAXException(e);
		}
	}

	/**
	 * Writeln.
	 * 
	 * @throws SAXException
	 *           the sAX exception
	 */
	private void writeln() throws SAXException {
		if(!indent) {
			write(" ");
		} else {
			StringBuilder sb=new StringBuilder();
			sb.append("\n");
			for(int i=0;i<indentLevel;i++) {
				sb.append(indentStr);
			}
			write(sb.toString());
		}
	}

}
