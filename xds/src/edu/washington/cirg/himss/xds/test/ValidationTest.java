/*
 * Copyright 2004-2005 (C) University of Washington. All Rights Reserved.
 * Created on Jan 24, 2005
 * University of Washington, CIRG
 * $Id: ValidationTest.java,v 1.1 2005/01/26 19:35:15 ddrozd Exp $
 */


package edu.washington.cirg.himss.xds.test;
import java.io.FileNotFoundException;
import java.net.URL;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.io.SAXReader;
import org.iso_relax.verifier.Schema;
import org.iso_relax.verifier.VerifierFactory;

import edu.washington.cirg.util.Util;

/**Simple test client used to test document validation
 * @author <a href="mailto:ddrozd@u.washington.edu">Dan Drozd</a>
 * @version $Revision: 1.1 $
 *
 */
public class ValidationTest {

	private static Log log = LogFactory.getLog(ValidationTest.class);

	/**
	 * @throws DocumentException
	 * 
	 */
	public ValidationTest(String file, String schemaName) throws Exception {
		super();
		
		URL fileURL = ClassLoader.getSystemResource(file);
		URL schemaURL = ClassLoader.getSystemResource(schemaName);
		
		if ( fileURL == null ) {
			throw new FileNotFoundException("Unable to find CCR file");
		} else if ( schemaURL == null ) {
			throw new FileNotFoundException("Unable to find XML-Schema file");
		}
		
		
		SAXReader reader = new SAXReader();
		Document document = reader.read(fileURL);
	    VerifierFactory factory = new com.sun.msv.verifier.jarv.TheFactoryImpl();
	    Schema schema = factory.compileSchema( schemaURL.toString() );


		
		Util.validate(schema, document);
		log.info("Validation successful");
	}
	
	public static void main(String[] args) throws Exception {
		String file = args[0];
		String schema =  args[1];
		
		ValidationTest t = new ValidationTest(file, schema);
		
	}

}


