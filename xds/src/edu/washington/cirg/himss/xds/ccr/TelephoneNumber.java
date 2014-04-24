/*
 * Copyright 2004-2005 (C) University of Washington. All Rights Reserved.
 * Created on Dec 28, 2004
 * University of Washington, CIRG
 * $Id: TelephoneNumber.java,v 1.2 2005/01/26 19:45:44 ddrozd Exp $
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
public class TelephoneNumber {

	private static Log log = LogFactory.getLog(TelephoneNumber.class);

	private static final QName TELEPHONE_QNAME = 
		DocumentFactory.getInstance().createQName("Telephone", CCRConstants.XMLNS_CCR);	
	private static final QName VALUE_QNAME = 
		DocumentFactory.getInstance().createQName("Value", CCRConstants.XMLNS_CCR);	
	private static final QName TYPE_QNAME = 
		DocumentFactory.getInstance().createQName("Type", CCRConstants.XMLNS_CCR);	
	
	private String phoneNumber;
	private String type;

	/**
	 * @param phoneNumber
	 * @param type
	 */
	public TelephoneNumber(String phoneNumber, String type) 
		throws IllegalArgumentException {
		if ( phoneNumber == null ) {
			throw new IllegalArgumentException("argument phoneNumber may not be null");
		}
		this.phoneNumber = phoneNumber;
		this.type = type;
	}
	
	public Element getElement() {
		Element element = 
			DocumentFactory.getInstance().createElement(TELEPHONE_QNAME);
		element.addElement(VALUE_QNAME).addText(phoneNumber);
		if ( type != null ) {
			element.addElement(TYPE_QNAME).addText(type);
		}
		return element;

	}

}
