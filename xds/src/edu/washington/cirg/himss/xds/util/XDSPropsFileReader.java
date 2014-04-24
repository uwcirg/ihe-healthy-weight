/*
 * Copyright 2004-2005 (C) University of Washington. All Rights Reserved.
 * Created on Dec 22, 2004
 * University of Washington, CIRG
 * $Id: XDSPropsFileReader.java,v 1.3 2005/01/26 19:35:15 ddrozd Exp $
 */
package edu.washington.cirg.himss.xds.util;


import java.io.InputStream;
import java.util.HashMap;
import java.util.Iterator;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;


/** XDS initialization Property file reader.  The property files are in files
 * that follow the following general format:
 * <code name="" value=""/>.  We load these into a HashMap to be 
 * accessed later through a static constants class.
 * @author <a href="mailto:ddrozd@u.washington.edu">Dan Drozd</a>
 * @version $Revision: 1.3 $
 *
 */
public class XDSPropsFileReader {

	private static Log log = LogFactory.getLog(XDSPropsFileReader.class);
	
	private String file;
	private HashMap map;
	private Document doc;
	
	public static HashMap load(String sFile) 
		throws DocumentException {
		if ( log.isTraceEnabled()) {
			log.trace("Loading...." + sFile);
		}
		ClassLoader loader = Thread.currentThread ().getContextClassLoader();
	 	
	 	if (sFile == null) {
	 		log.error("Unable to load file, filename argument was null");
	 		throw new IllegalArgumentException ("Unable to load file, filename argument was null");
	 	}
	 	
	 	InputStream in = loader.getResourceAsStream(sFile);
	 	if ( in == null ) {
	 		throw new IllegalArgumentException("Unable to load file " + sFile + " it can't be located on classpath");
	 	}

		HashMap map = new HashMap();
		SAXReader reader = new SAXReader();
		Document doc;
		try {
			doc = reader.read(in);
		} catch ( DocumentException de ) {
			log.error(de);
			throw de;
		}
		Element root = doc.getRootElement();
		Iterator iterator = root.elementIterator();
		while ( iterator.hasNext()) {
			Element child = (Element)iterator.next();
			String name = child.attributeValue("name");
			String value = child.attributeValue("value");
			map.put(name, value);
		}
		return map;
	}
}