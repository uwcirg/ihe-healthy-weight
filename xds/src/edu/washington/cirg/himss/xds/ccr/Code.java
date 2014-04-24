/*
 * Copyright 2004-2005 (C) University of Washington. All Rights Reserved.
 * Created on Dec 28, 2004
 * University of Washington, CIRG
 * $Id: Code.java,v 1.2 2005/01/26 19:45:44 ddrozd Exp $
 */
package edu.washington.cirg.himss.xds.ccr;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.dom4j.DocumentFactory;
import org.dom4j.Element;
import org.dom4j.QName;

/** Java implementation of the Continuity of Care (CCR) document object model
 * @author <a href="mailto:ddrozd@u.washington.edu">Dan Drozd</a>
 * @version $Revision: 1.2 $
 *
 */
public class Code {

	private static Log log = LogFactory.getLog(Code.class);

	private static final QName CODE_QNAME = 
		DocumentFactory.getInstance().createQName("Code", CCRConstants.XMLNS_CCR);	
	private static final QName CODING_SYSTEM_QNAME = 
		DocumentFactory.getInstance().createQName("CodingSystem", CCRConstants.XMLNS_CCR);	
	private static final QName VALUE_QNAME = 
		DocumentFactory.getInstance().createQName("Value", CCRConstants.XMLNS_CCR);	
	private static final QName VERSION_QNAME = 
		DocumentFactory.getInstance().createQName("Version", CCRConstants.XMLNS_CCR);	

	
	private String codingSystem;
	private String value;
	private String version;
	
	
	/**
	 * @param codingSystem
	 * @param value
	 * @param version
	 */
	public Code(String codingSystem, String value, String version) 
		throws IllegalArgumentException {
		this.codingSystem = codingSystem;
		if ( value == null ) {
			throw new IllegalArgumentException("value cannot be null");
		}
		this.value = value;
		this.version = version;
	}
	
	public Element getElement() {
		
		Element element = 
			DocumentFactory.getInstance().createElement(CODE_QNAME);
		
		if ( codingSystem != null ) {
			element.addElement(CODING_SYSTEM_QNAME)
			.addText(codingSystem);
		}

		element.addElement(VALUE_QNAME)
			.addText(value);
		
		if ( version != null ) {
			element.addElement(VERSION_QNAME)
			.addText(version);
		}
		
		return element;

	}
}
