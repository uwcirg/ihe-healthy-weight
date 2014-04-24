/*
 * Copyright 2004-2005 (C) University of Washington. All Rights Reserved.
 * Created on Dec 28, 2004
 * University of Washington, CIRG
 * $Id: CCRConstants.java,v 1.3 2005/01/26 19:45:44 ddrozd Exp $
 */
package edu.washington.cirg.himss.xds.ccr;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.dom4j.DocumentFactory;
import org.dom4j.Namespace;
import org.dom4j.QName;

/** Java implementation of the Continuity of Care (CCR) document object model
 * @author <a href="mailto:ddrozd@u.washington.edu">Dan Drozd</a>
 * @version $Revision: 1.3 $
 *
 */
public class CCRConstants {

	private static Log log = LogFactory.getLog(CCRConstants.class);
	
	public static Namespace XMLNS_CCR = null;
	public static final String XMLNS_CCR_URI = "urn:astm-org:CCR";
	public static final String UNIQUE_ID_TOKEN = "UNIQUE_ID_TOKEN";
	
	
	static {
		
		XMLNS_CCR = DocumentFactory.getInstance().createNamespace("", XMLNS_CCR_URI);
	}
	
	public static final QName TEXT_QNAME = 
		DocumentFactory.getInstance().createQName("Text", XMLNS_CCR);	

	public static final QName NAME_QNAME = 
		DocumentFactory.getInstance().createQName("Name", XMLNS_CCR);	

	public static final QName DESC_QNAME = 
		DocumentFactory.getInstance().createQName("Description", XMLNS_CCR);	

	public static final QName VALUE_QNAME = 
		DocumentFactory.getInstance().createQName("Value", XMLNS_CCR);
	
	public static final String RECIPIENT_TYPE = "InformationSystem";
	public static final String RECIPIENT_SYSTEM_NAME = "IHE Interoperability Showcase";
	
	
	
	


}
