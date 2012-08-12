/*
 *******************************************************************************
 * JsonXsltTransformation.java
 * $Id: $
 *
 *******************************************************************************
 *
 * Copyright:   Q2WEB GmbH
 *              quality to the web
 *
 *              Tel  : +49 (0) 211 / 159694-00	Kronprinzenstr. 82-84
 *              Fax  : +49 (0) 211 / 159694-09	40217 Düsseldorf
 *              eMail: info@q2web.de						http://www.q2web.de
 *
 *
 * Author:      oliver.schrenk
 *
 * Created:     Aug 10, 2012
 *
 * Copyright (c) 2009 Q2WEB GmbH.
 * All rights reserved.
 *
 *******************************************************************************
 */
package com.platzhaltr.xslt4json.core;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.TransformerFactoryConfigurationError;
import javax.xml.transform.sax.SAXResult;
import javax.xml.transform.sax.SAXTransformerFactory;
import javax.xml.transform.sax.TransformerHandler;
import javax.xml.transform.stream.StreamSource;
import javax.xml.validation.Schema;
import javax.xml.validation.SchemaFactory;
import javax.xml.validation.ValidatorHandler;

import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import com.platzhaltr.xslt4json.io.JsonSaxReader;
import com.platzhaltr.xslt4json.io.JsonSaxWriter;
import com.platzhaltr.xslt4json.util.SystemErrErrorHandler;


/**
 * The Class JsonXsltTransformation.
 * 
 * @author Oliver Schrenk <oliver.schrenk@q2web.de>
 */
public class JsonXsltTransformation {

	/** The Constant HTTP_WWW_W3_ORG_2001_XML_SCHEMA. */
	private static final String HTTP_WWW_W3_ORG_2001_XML_SCHEMA="http://www.w3.org/2001/XMLSchema";

	/** The json xsd. */
	final String JSON_XSD="/org/json/json.xsd";

	private final File xsltFile;

	/**
	 * Instantiates a new json xslt transformation.
	 * 
	 * @param xsltFile
	 *          the xslt file
	 * @throws FileNotFoundException
	 *           the file not found exception
	 */
	public JsonXsltTransformation(final File xsltFile) {
		this.xsltFile=xsltFile;

	}

	/**
	 * Transform.
	 * 
	 * @param inputFile
	 *          the input file
	 * @param outputFile
	 *          the output file
	 * @throws IOException
	 *           Signals that an I/O exception has occurred.
	 */
	public void transform(final File inputFile, final File outputFile) throws IOException {
		try {
			JsonSaxWriter writer=new JsonSaxWriter(new FileOutputStream(outputFile));

			SchemaFactory schemaFactory=SchemaFactory.newInstance(HTTP_WWW_W3_ORG_2001_XML_SCHEMA);

			File xsdFile=new File(this.getClass().getResource(JSON_XSD).getFile());

			Schema schema=schemaFactory.newSchema(new StreamSource(xsdFile));
			ValidatorHandler validatorHandler=schema.newValidatorHandler();
			validatorHandler.setErrorHandler(new SystemErrErrorHandler());
			validatorHandler.setContentHandler(writer);

			TransformerHandler transformerHandler=((SAXTransformerFactory)TransformerFactory.newInstance())
					.newTransformerHandler(new StreamSource(xsltFile));
			transformerHandler.setResult(new SAXResult(validatorHandler));

			JsonSaxReader saxReader=new JsonSaxReader();
			saxReader.setContentHandler(transformerHandler);
			saxReader.parse(new InputSource(inputFile.toString()));
		} catch(SAXException e) {
			throw new IOException(e);
		} catch(TransformerConfigurationException e) {
			throw new IOException(e);
		} catch(TransformerFactoryConfigurationError e) {
			throw new IOException(e);
		}
	}

}
