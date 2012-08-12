package com.platzhaltr.xslt4json.io;

import java.io.IOException;

import org.antlr.runtime.ANTLRFileStream;
import org.antlr.runtime.CharStream;
import org.antlr.runtime.CommonTokenStream;
import org.antlr.runtime.RecognitionException;
import org.antlr.runtime.tree.CommonTree;
import org.antlr.runtime.tree.Tree;
import org.xerial.json.impl.JSONLexer;
import org.xerial.json.impl.JSONParser;
import org.xml.sax.ContentHandler;
import org.xml.sax.DTDHandler;
import org.xml.sax.EntityResolver;
import org.xml.sax.ErrorHandler;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.SAXNotRecognizedException;
import org.xml.sax.SAXNotSupportedException;
import org.xml.sax.XMLReader;
import org.xml.sax.helpers.AttributesImpl;

/**
 * The Class JsonSaxReader.
 */
public class JsonSaxReader implements XMLReader {

	/** The content handler. */
	private ContentHandler contentHandler;

	/** The error handler. */
	private ErrorHandler errorHandler;

	/*
	 * @see org.xml.sax.XMLReader#getContentHandler()
	 */
	@Override
	public ContentHandler getContentHandler() {
		return contentHandler;
	}

	/*
	 * @see org.xml.sax.XMLReader#getDTDHandler()
	 */
	@Override
	public DTDHandler getDTDHandler() {
		return null;
	}

	/*
	 * @see org.xml.sax.XMLReader#getEntityResolver()
	 */
	@Override
	public EntityResolver getEntityResolver() {
		return null;
	}

	/*
	 * @see org.xml.sax.XMLReader#getErrorHandler()
	 */
	@Override
	public ErrorHandler getErrorHandler() {
		return errorHandler;
	}

	/*
	 * @see org.xml.sax.XMLReader#getFeature(java.lang.String)
	 */
	@Override
	public boolean getFeature(String name) throws SAXNotRecognizedException, SAXNotSupportedException {
		throw new SAXNotSupportedException();
	}

	/*
	 * @see org.xml.sax.XMLReader#getProperty(java.lang.String)
	 */
	@Override
	public Object getProperty(String name) throws SAXNotRecognizedException, SAXNotSupportedException {
		throw new SAXNotSupportedException();
	}

	/**
	 * Parses the.
	 * 
	 * @param stream
	 *          the stream
	 * @throws SAXException
	 *           the sAX exception
	 */
	private void parse(CharStream stream) throws SAXException {
		JSONLexer lexer=new JSONLexer(stream);
		JSONParser parser=new JSONParser(new CommonTokenStream(lexer));
		CommonTree tree;
		try {
			tree=(CommonTree)parser.json().getTree();
		} catch(RecognitionException e) {
			throw new SAXException(e);
		}
		contentHandler.startDocument();
		try {
			visit(tree);
		} finally {
			contentHandler.endDocument();
		}
	}

	/*
	 * @see org.xml.sax.XMLReader#parse(org.xml.sax.InputSource)
	 */
	@Override
	public void parse(InputSource input) throws IOException, SAXException {
		parse(new ANTLRFileStream(input.getSystemId()));
	}

	/*
	 * @see org.xml.sax.XMLReader#parse(java.lang.String)
	 */
	@Override
	public void parse(String systemId) throws IOException, SAXException {
		throw new SAXNotSupportedException();
	}

	/*
	 * @see org.xml.sax.XMLReader#setContentHandler(org.xml.sax.ContentHandler)
	 */
	@Override
	public void setContentHandler(ContentHandler value) {
		contentHandler=value;
	}

	/*
	 * @see org.xml.sax.XMLReader#setDTDHandler(org.xml.sax.DTDHandler)
	 */
	@Override
	public void setDTDHandler(DTDHandler handler) {}

	/*
	 * @see org.xml.sax.XMLReader#setEntityResolver(org.xml.sax.EntityResolver)
	 */
	@Override
	public void setEntityResolver(EntityResolver resolver) {}

	/*
	 * @see org.xml.sax.XMLReader#setErrorHandler(org.xml.sax.ErrorHandler)
	 */
	@Override
	public void setErrorHandler(ErrorHandler value) {
		errorHandler=value;
	}

	/*
	 * @see org.xml.sax.XMLReader#setFeature(java.lang.String, boolean)
	 */
	@Override
	public void setFeature(String name, boolean value) throws SAXNotRecognizedException, SAXNotSupportedException {
		throw new SAXNotSupportedException();
	}

	/*
	 * @see org.xml.sax.XMLReader#setProperty(java.lang.String, java.lang.Object)
	 */
	@Override
	public void setProperty(String name, Object value) throws SAXNotRecognizedException, SAXNotSupportedException {
		throw new SAXNotSupportedException();
	}

	/**
	 * Visit.
	 * 
	 * @param tree
	 *          the tree
	 */
	private void visit(Tree tree) {
		String text=tree.getText();
		int type=tree.getType();
		try {
			if(type==JSONParser.XML_ELEMENT) {
				AttributesImpl attrs=new AttributesImpl();
				int i=0;
				for(;i<tree.getChildCount();i++) {
					Tree attr=tree.getChild(i);
					if(attr.getType()!=JSONParser.XML_ATTRIBUTE) {
						break;
					}
					String attrName=attr.getText();
					StringBuilder attrValue=new StringBuilder();
					for(int j=0;j<attr.getChildCount();j++) {
						Tree value=attr.getChild(j);
						attrValue.append(value.getText());
					}
					attrs.addAttribute("", attrName, attrName, "CDATA", attrValue.toString());
				}
				contentHandler.startElement("", text, text, attrs);
				for(;i<tree.getChildCount();i++) {
					visit(tree.getChild(i));
				}
				contentHandler.endElement("", text, text);
			} else {
				String name=JSONParser.tokenNames[type];
				if(name.equals(name.toUpperCase())) {
					contentHandler.startElement("", name, name, new AttributesImpl());
					for(int i=0;i<tree.getChildCount();i++) {
						visit(tree.getChild(i));
					}
					contentHandler.endElement("", name, name);
				} else {
					contentHandler.characters(text.toCharArray(), 0, text.length());
				}
			}
		} catch(SAXException e) {
			e.printStackTrace();
		}
	}

}
