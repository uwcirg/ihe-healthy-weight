/*
 * Copyright 2004-2005 (C) University of Washington. All Rights Reserved.
 * Created on Dec 28, 2004
 * University of Washington, CIRG
 * $Id: InformationSystem.java,v 1.2 2005/01/26 19:45:44 ddrozd Exp $
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
public class InformationSystem extends Actor {
	
	private static Log log = LogFactory.getLog(InformationSystem.class);

	private static final QName INFO_SYSTEM_QNAME = 
		DocumentFactory.getInstance().createQName("InformationSystem", CCRConstants.XMLNS_CCR);	
	private static final QName NAME_QNAME = 
		DocumentFactory.getInstance().createQName("Name", CCRConstants.XMLNS_CCR);	
	private static final QName TYPE_QNAME = 
		DocumentFactory.getInstance().createQName("Type", CCRConstants.XMLNS_CCR);	
	private static final QName VERSION_QNAME = 
		DocumentFactory.getInstance().createQName("Version", CCRConstants.XMLNS_CCR);	
	
	
	private String name;
	private String type;
	private String version;	
	

	/**
	 * @param name
	 * @param type
	 * @param version
	 */
	public InformationSystem(String name, String type, String version) 
		throws IllegalArgumentException {
		super();
		if ( name == null ) {
			throw new IllegalArgumentException("argument name cannot be null");
		}
		
		this.name = name;
		this.type = type;
		this.version = version;
	}
	
	/*
	 * The XML Schema uses an <Actor> tag for all uses 
	 * of the various Actors, except when they are 
	 * included in the <To> tag.  So we give you a way
	 * to toggle the <Actor> tag on and off.  On is default.
	 * 
	 */
	
	public Element getElement() {

		Element element = 
			DocumentFactory.getInstance().createElement(INFO_SYSTEM_QNAME);
		element.addElement(NAME_QNAME)
			.addText(name);
		
		if ( type != null ) {
			element.addElement(TYPE_QNAME)
			.addText(type);
		}
		if ( version != null ) {
			element.addElement(VERSION_QNAME)
			.addText(version);
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
