/*
 * Copyright 2004-2005 (C) University of Washington. All Rights Reserved.
 * Created on Dec 21, 2004
 * University of Washington, CIRG
 * $Id: RegistryPackage.java,v 1.4 2005/01/26 00:52:07 ddrozd Exp $
 */
package edu.washington.cirg.ebxml;

import java.util.Iterator;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.dom4j.DocumentFactory;
import org.dom4j.Element;
import org.dom4j.QName;

/** A lightweight implementation of standard ebXML classes
 * @author <a href="mailto:ddrozd@u.washington.edu">Dan Drozd</a>
 * @version $Revision: 1.4 $
 *
 */
public class RegistryPackage implements EbXMLElement{

	private static Log log = LogFactory.getLog(RegistryPackage.class);
	
	public static final QName QNAME = DocumentFactory.getInstance().createQName("RegistryPackage", EbXMLConstants.XMLNS_RIM);

	private static final String ID_ATTRIBUTE = "id";
	private static final String DESCRIPTION_ATTRIBUTE = "description";	
	
	
	private String id;
	private String description;
	private Name name;
	private List externalIdentifierList;
	private List slotList;
	private Description descriptionObject;
	
	public RegistryPackage(String id, String description,
			Name name, List externalIdentifierList, 
			List slotList, Description descriptionObject) {
		this.id = id;
		this.description = description;
		this.name = name;
		this.externalIdentifierList = externalIdentifierList;
		this.slotList = slotList;
		this.descriptionObject = descriptionObject;
	}

	public RegistryPackage(String id, String description,
			String nameStringValue, List externalIdentifierList, 
			List slotList, Description descriptionObject) {
		this(id, description, new Name(nameStringValue), 
				externalIdentifierList, slotList, descriptionObject);
	}	
	
	public Element getElement() {
		
		Element element = 
			DocumentFactory.getInstance().createElement(QNAME)
				.addAttribute("id", id)
				.addAttribute("description", description);
		element.add(name.getElement());
		
		if ( descriptionObject != null && descriptionObject.getValue() != null ) {
			element.add(descriptionObject.getElement());
		}
		
		if ( externalIdentifierList != null && externalIdentifierList.size() > 0 ) {
		
			for ( Iterator iter = externalIdentifierList.iterator(); iter.hasNext(); ) {
				ExternalIdentifier ei = (ExternalIdentifier)iter.next();
				element.add(ei.getElement());
			}
		}
		
		if ( slotList != null && slotList.size() > 0 ) {
			for (Iterator iter = slotList.iterator(); iter.hasNext(); ) {
				Slot s = (Slot)iter.next();
				element.add(s.getElement());
			}
		}
		return element;
	}
}