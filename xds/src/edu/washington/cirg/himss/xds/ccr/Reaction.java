/*
 * Copyright 2004-2005 (C) University of Washington. All Rights Reserved.
 * Created on Dec 28, 2004
 * University of Washington, CIRG
 * $Id: Reaction.java,v 1.2 2005/01/26 19:45:44 ddrozd Exp $
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
public class Reaction {

	private static Log log = LogFactory.getLog(Reaction.class);

	private static final QName REACTION_QNAME = 
		DocumentFactory.getInstance().createQName("Reaction", CCRConstants.XMLNS_CCR);	
	private static final QName SEVERITY_QNAME = 
		DocumentFactory.getInstance().createQName("Severity", CCRConstants.XMLNS_CCR);	

	private String desc;
	private String severity;
	
	
	/**
	 * @param desc
	 * @param severity
	 */
	public Reaction(String desc, String severity) {
		this.desc = desc;
		this.severity = severity;
	}
	
	public Element getElement() {
		Element element = 
			DocumentFactory.getInstance().createElement(REACTION_QNAME);
		if ( desc != null ) {
			Element descElement = 
				DocumentFactory.getInstance().createElement(CCRConstants.DESC_QNAME);
			descElement.addElement(CCRConstants.TEXT_QNAME).addText(desc);
			element.add(descElement);
		}
		if ( severity != null ) {
			element.addElement(SEVERITY_QNAME).addText(severity);
		}
		
		return element;
	}
}
