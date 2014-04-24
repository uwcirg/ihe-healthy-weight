/*
 * Copyright 2004-2005 (C) University of Washington. All Rights Reserved.
 * Created on Dec 21, 2004
 * University of Washington, CIRG
 * $Id: Classification.java,v 1.3 2005/02/11 20:10:22 ddrozd Exp $
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
public class Classification implements EbXMLElement {

	private static Log log = LogFactory.getLog(Classification.class);

	
	public static final QName QNAME = DocumentFactory.getInstance().createQName("Classification", EbXMLConstants.XMLNS_RIM);
	private static final String CLASSIFICATION_SCHEME_ATTRIBUTE = "classificationScheme";
	private static final String CLASSIFIED_OBJECT_ATTRIBUTE = "classifiedObject";
	private static final String NODE_REPRESENTATION_ATTRIBUTE = "nodeRepresentation";
	private static final String CLASSIFICATION_NODE_ATTRIBUTE = "classificationNode";

	
	private String classificationScheme;
	private String classifiedObject;
	private String nodeRepresentation;
	private Name name;
	private Slot slot;
	private String classificationNode;
	
	public Classification(String classificationScheme,
			String classifiedObject,
			String nodeRepresentation,
			Name name,
			Slot slot,
			String classificationNode) {
		
		this.classificationScheme = classificationScheme;
		this.classifiedObject = classifiedObject;
		this.nodeRepresentation = nodeRepresentation;
		this.name = name;
		this.slot = slot;
		this.classificationNode = classificationNode;
		
	}

	public Classification(String classificationScheme, String classifiedObject,
			String nodeRepresentation, String nameStringValue, 
			String slotName, String[] slotValueArray, String classificationNode) {
		this(classificationScheme, classifiedObject, 
				nodeRepresentation, new Name(nameStringValue), 
				new Slot(slotName, slotValueArray), classificationNode);
	}

	public Element getElement() {
		Element element = DocumentFactory.getInstance().createElement(QNAME);
		
		if ( classificationScheme != null ) {
			element.addAttribute(CLASSIFICATION_SCHEME_ATTRIBUTE, classificationScheme);
		}
		
		element.addAttribute(CLASSIFIED_OBJECT_ATTRIBUTE, classifiedObject);
		
		if ( nodeRepresentation != null ) {	
			element.addAttribute(NODE_REPRESENTATION_ATTRIBUTE, nodeRepresentation);
		}
		
		if ( classificationNode != null ) {
			element.addAttribute(CLASSIFICATION_NODE_ATTRIBUTE, classificationNode);
		}
		
		if ( name != null ) {
			element.add(name.getElement());
		}
		if ( slot != null ) {
			element.add(slot.getElement());
		}
		return element;
	}
}
