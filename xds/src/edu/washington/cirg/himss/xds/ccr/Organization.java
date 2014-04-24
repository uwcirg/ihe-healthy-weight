/*
 * Copyright 2004-2005 (C) University of Washington. All Rights Reserved.
 * Created on Dec 28, 2004
 * University of Washington, CIRG
 * $Id: Organization.java,v 1.4 2005/02/21 16:43:25 ddrozd Exp $
 */
package edu.washington.cirg.himss.xds.ccr;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.dom4j.DocumentFactory;
import org.dom4j.Element;
import org.dom4j.QName;

/** Java implementation of the Continuity of Care (CCR) document object model
 * @author <a href="mailto:ddrozd@u.washington.edu">Dan Drozd</a>
 * @version $Revision: 1.4 $
 *
 */
public class Organization extends Actor {

	private static Log log = LogFactory.getLog(Organization.class);

	private static final QName ORG_QNAME = 
		DocumentFactory.getInstance().createQName("Organization", CCRConstants.XMLNS_CCR);	

	private String name;
	
	

	/**
	 * @param name
	 */
	public Organization(String name) throws IllegalArgumentException {
		super();
		if ( name == null ) {
			throw new IllegalArgumentException("argument name may not be null");
		}
		this.name = name;
	}

	
	public Organization(String name, Specialty specialty) throws IllegalArgumentException {
		this(name);
		this.specialty = specialty;
	}
	
	public Element getElement() {
		
		Element element = 
			DocumentFactory.getInstance().createElement(ORG_QNAME);
		if ( name != null ) {
			element.addElement(CCRConstants.NAME_QNAME)
				.addText(name);
		}
		
		if ( isActor ) {
			Element actorElement = 
				DocumentFactory.getInstance().createElement(ACTOR_QNAME);
			
			actorElement.add(ccrDataObjectId.getElement());
			actorElement.add(element);
			if ( specialty != null ) {
				actorElement.add(specialty.getElement());
			}
			return actorElement;
		} else {
			return element;
		}

	}
}
