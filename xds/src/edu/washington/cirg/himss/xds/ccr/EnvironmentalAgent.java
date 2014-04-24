/*
 * Copyright 2004-2005 (C) University of Washington. All Rights Reserved.
 * Created on Dec 28, 2004
 * University of Washington, CIRG
 * $Id: EnvironmentalAgent.java,v 1.2 2005/01/26 19:45:44 ddrozd Exp $
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

public class EnvironmentalAgent extends Agent {

	private static Log log = LogFactory.getLog(EnvironmentalAgent.class);

	private static final QName ENVIRONMENTAL_AGENT_QNAME = 
		DocumentFactory.getInstance().createQName("EnvironmentalAgent", CCRConstants.XMLNS_CCR);	

	
	private String desc;
	
	public EnvironmentalAgent(String desc) {
		this.desc = desc;
	}
	
	
	public Element getElement() {
		Element element = 
			DocumentFactory.getInstance().createElement(AGENT_QNAME);
		
		Element eaElement = 
			DocumentFactory.getInstance().createElement(ENVIRONMENTAL_AGENT_QNAME);

		eaElement.add(ccrDataObjectId.getElement());

		if ( desc != null ) {
			Element descElement = 
				DocumentFactory.getInstance().createElement(CCRConstants.DESC_QNAME);
			descElement.addElement(CCRConstants.TEXT_QNAME).addText(desc);
			eaElement.add(descElement);
		}
		element.add(eaElement);
		
		return element;
	}
}
