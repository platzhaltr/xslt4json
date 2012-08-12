package com.platzhaltr.xslt4json.core;

import static org.junit.Assert.*;

import java.io.File;
import java.io.IOException;

import org.junit.Before;
import org.junit.Test;

import com.google.common.base.CharMatcher;
import com.google.common.base.Charsets;
import com.google.common.io.Files;
import com.platzhaltr.xslt4json.core.JsonXsltTransformation;

public class JsonXsltTransformationTest {

	private static final String XSLT_PATH="/test.xsl";
	private static final String INPUT_PATH="/input.json";
	private static final String OUTPUT_PATH="/output.json";

	private final File xsltFile=new File(this.getClass().getResource(XSLT_PATH).getFile());
	private final File inputFile=new File(this.getClass().getResource(INPUT_PATH).getFile());
	private final File expectedOutputFile=new File(this.getClass().getResource(OUTPUT_PATH).getFile());

	private JsonXsltTransformation transformation;
	private File tempFile;

	@Before
	public void setUp() throws IOException {
		transformation=new JsonXsltTransformation(xsltFile);
		this.tempFile=File.createTempFile("temporary.json", ".tmp");
	}

	@Test
	public void testTransformation() throws IOException {

		transformation.transform(inputFile, tempFile);

		final String expectedOutput=CharMatcher.WHITESPACE.removeFrom(Files.toString(expectedOutputFile, Charsets.UTF_8));
		final String actualOutput=CharMatcher.WHITESPACE.removeFrom(Files.toString(tempFile, Charsets.UTF_8));

		assertEquals(expectedOutput, actualOutput);
	}
}
