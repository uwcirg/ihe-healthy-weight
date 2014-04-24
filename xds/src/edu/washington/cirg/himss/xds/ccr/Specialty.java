/*
 * Copyright 2004-2005 (C) University of Washington. All Rights Reserved.
 * Created on Dec 28, 2004
 * University of Washington, CIRG
 * $Id: Specialty.java,v 1.2 2005/01/26 19:45:44 ddrozd Exp $
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
public class Specialty {

	private static Log log = LogFactory.getLog(Specialty.class);

	private static final QName SPECIALTY_QNAME = 
		DocumentFactory.getInstance().createQName("Specialty", CCRConstants.XMLNS_CCR);	
	
	private String specialty;
	private Code code;

	/**
	 * @param specialty
	 * @param code
	 */
	public Specialty(String specialty, Code code) {
		this.specialty = specialty;
		this.code = code;
	}
	
	public Element getElement() {
		
		Element element = 
			DocumentFactory.getInstance().createElement(SPECIALTY_QNAME);
		element.addElement(CCRConstants.TEXT_QNAME)
			.addText(specialty);
		if ( code != null ) {
			element.add(code.getElement());
		}
		return element;

	}
}
