/*
 * Copyright 2004-2005 (C) University of Washington. All Rights Reserved.
 * Created on Dec 28, 2004
 * University of Washington, CIRG
 * $Id: Recipient.java,v 1.2 2005/01/26 19:45:44 ddrozd Exp $
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
public class Recipient extends CCRDataObject {

	private static Log log = LogFactory.getLog(Recipient.class);

	private static final QName TO_QNAME = 
		DocumentFactory.getInstance().createQName("To", CCRConstants.XMLNS_CCR);	

	private Actor actor;

	
	/**
	 * @param actor
	 */
	public Recipient(Actor actor) {
		this.actor = actor;
	}
	
	public Element getElement() {

		Element element = 
			DocumentFactory.getInstance().createElement(TO_QNAME);
		
		element.add(ccrDataObjectId.getElement());
		element.add(actor.getElement());
		
		return element;
	}
	
}
