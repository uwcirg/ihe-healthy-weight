/*
 * Copyright 2004-2007 (C) University of Washington. All Rights Reserved.
 * Created on Dec 22, 2004
 * University of Washington, CIRG
 * $Id: EbXMLConstants.java,v 1.2 2005/01/26 00:52:07 ddrozd Exp $
 */
package edu.washington.cirg.ebxml;


import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.dom4j.DocumentFactory;
import org.dom4j.Namespace;

import edu.washington.cirg.himss.xds.util.XDSConstants;


/** A lightweight implementation of standard ebXML classes
 * @author <a href="mailto:ddrozd@u.washington.edu">Dan Drozd</a>
 * @version $Revision: 1.2 $
 *
 */
public class EbXMLConstants {

	private static Log log = LogFactory.getLog(EbXMLConstants.class);


public static final String XMLNS_QUERY_URI = "urn:oasis:names:tc:ebxml-regrep:xsd:query:3.0";
public static final String XMLNS_RS_URI = "urn:oasis:names:tc:ebxml-regrep:xsd:rs:3.0";
public static final String XMLNS_RIM_URI = "urn:oasis:names:tc:ebxml-regrep:xsd:rim:3.0";
public static final String XMLNS_TNS_URI = "urn:oasis:names:tc:ebxml-regrep:rim:xsd:2.1";
public static final String XMLNS_XSI_URI = "http://www.w3.org/2001/XMLSchema-instance";
//public static final String XMLNS_URI = "urn:ihe:xds:xsd:1.0";
public static final String XMLNS_URI = "urn:oasis:names:tc:ebxml-regrep:xsd:rs:3.0";
public static final String XSI_SCHEMA_LOCATION_URI = "urn:oasis:names:tc:ebxml-regrep:registry:xsd:2.0 rs.xsd";


public static Namespace XMLNS_RS;
public static Namespace XMLNS_RIM;


	static {
		
		if ( XDSConstants.NAMESPACE_SUPPORT == true ) {
		XMLNS_RS = DocumentFactory.getInstance().createNamespace("rs", XMLNS_RS_URI);
		XMLNS_RIM = DocumentFactory.getInstance().createNamespace("rim", XMLNS_RIM_URI);
		} else {
			XMLNS_RS = null;
			XMLNS_RIM = null;
		}
	}

}
