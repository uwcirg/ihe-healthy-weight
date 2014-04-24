/*
 * Copyright 2004-2005 (C) University of Washington. All Rights Reserved.
 * Created on Dec 28, 2004
 * University of Washington, CIRG
 * $Id: Purpose.java,v 1.2 2005/01/26 19:45:44 ddrozd Exp $
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
public class Purpose {

	private static Log log = LogFactory.getLog(Purpose.class);

	private static final QName PURPOSE_QNAME = 
		DocumentFactory.getInstance().createQName("Purpose", CCRConstants.XMLNS_CCR);	
	private static final QName DESC_QNAME = 
		DocumentFactory.getInstance().createQName("Description", CCRConstants.XMLNS_CCR);	

	private String description;
	private Code code;

	

	/**
	 * @param description
	 * @param code
	 */
	public Purpose(String description, Code code) {
		this.description = description;
		this.code = code;
	}
	
	public Element getElement() {
		
		Element element = 
			DocumentFactory.getInstance().createElement(PURPOSE_QNAME);
		Element descElement = 
			DocumentFactory.getInstance().createElement(DESC_QNAME);
		descElement.addElement(CCRConstants.TEXT_QNAME)
			.addText(description);
		descElement.add(code.getElement());
		element.add(descElement);
		return element;

	}	
	
}
