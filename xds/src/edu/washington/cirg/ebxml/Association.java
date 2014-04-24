/*
 * Copyright 2004-2005 (C) University of Washington. All Rights Reserved.
 * Created on Dec 21, 2004
 * University of Washington, CIRG
 * $Id: Association.java,v 1.3 2005/02/11 20:10:22 ddrozd Exp $
 */

package edu.washington.cirg.ebxml;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.dom4j.DocumentFactory;
import org.dom4j.Element;
import org.dom4j.QName;



/** A lightweight implementation of standard ebXML classes
 * @author <a href="mailto:ddrozd@u.washington.edu">Dan Drozd</a>
 * @version $Revision: 1.3 $
 *
 */

public class Association implements EbXMLElement {

	private static Log log = LogFactory.getLog(Association.class);

	
	public static final QName QNAME = DocumentFactory.getInstance().createQName("Association", EbXMLConstants.XMLNS_RIM);
	private static final String ASSOCIATION_TYPE_ATTRIBUTE = "associationType";
	private static final String SOURCE_OBJECT_ATTRIBUTE = "sourceObject";
	private static final String TARGET_OBJECT_ATTRIBUTE = "targetObject";

	
	private String associationType;
	private String sourceObject;
	private String targetObject;
	private Name name;
	private Slot slot;
	
	public Association(String associationType,
			String sourceObject,
			String targetObject,
			Name name,
			Slot slot){
		
		this.associationType = associationType;
		this.sourceObject = sourceObject;
		this.targetObject = targetObject;
		this.name = name;
		this.slot = slot;
	}

	public Association(String associationType,
			String sourceObject,
			String targetObject,
			String nameStringValue,
			String slotName, String[] slotValueArray ){
		
		this(associationType, sourceObject, targetObject, new Name(nameStringValue), new Slot(slotName, slotValueArray));
	
	}
	
	public Association(String associationType,
			String sourceObject,
			String targetObject,
			Name name,
			String slotName, String[] slotValueArray ){
		
		this(associationType, sourceObject, targetObject, name, new Slot(slotName, slotValueArray));
	
	}
	
	public Element getElement() {
		Element element = DocumentFactory.getInstance().createElement(QNAME)
			.addAttribute(ASSOCIATION_TYPE_ATTRIBUTE, associationType)
			.addAttribute(SOURCE_OBJECT_ATTRIBUTE, sourceObject)
			.addAttribute(TARGET_OBJECT_ATTRIBUTE, targetObject);
		
		if ( name != null ) {
			element.add(name.getElement());
		}
		
		if ( slot != null ) {
			element.add(slot.getElement());
		}
		return element;
	}

}