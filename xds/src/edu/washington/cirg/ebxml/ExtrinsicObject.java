/*
 * Copyright 2004-2005 (C) University of Washington. All Rights Reserved.
 * Created on Dec 21, 2004
 * University of Washington, CIRG
 * $Id: ExtrinsicObject.java,v 1.3 2005/01/26 00:52:07 ddrozd Exp $
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
 * @version $Revision: 1.3 $
 *
 */
public class ExtrinsicObject implements EbXMLElement{

	private static Log log = LogFactory.getLog(ExtrinsicObject.class);

	
	public static final QName QNAME = DocumentFactory.getInstance().createQName("ExtrinsicObject", EbXMLConstants.XMLNS_RIM);
	private static final String ID_ATTRIBUTE = "id";
	private static final String MIME_TYPE_ATTRIBUTE = "mimeType";
	private static final String OBJECT_TYPE_ATTRIBUTE = "objectType";
	
	
	
	private String id;
	private String mimeType;
	private String objectType;
	private Name name;
	private List externalIdentifierList;
	private List slotList;
	private List classificationList;
	
	public ExtrinsicObject(String id, String mimeType, String objectType,
			Name name, List externalIdentifierList, 
			List slotList,
			List classificationList) {
		if ( mimeType == null || mimeType.equals("") ) {
			log.error("Required input parameter (mimeType) value is null or empty");
			throw new IllegalArgumentException("Required input parameter (mimeType) value is null or empty");

		}
		this.id = id;
		this.mimeType = mimeType;
		this.objectType = objectType;
		this.name = name;
		this.externalIdentifierList = externalIdentifierList;
		this.slotList = slotList;
		this.classificationList = classificationList;
	}
	
	
	public ExtrinsicObject(String id, String mimeType, String objectType,
			String nameStringValue, List externalIdentifierList, 
			List slotList,
			List classificationList) {
		this(id, mimeType, objectType, new Name(nameStringValue), externalIdentifierList, slotList, classificationList);
	}
	
	public Element getElement() {
		
		Element element = 
			DocumentFactory.getInstance().createElement(QNAME)
			.addAttribute(ID_ATTRIBUTE, id)
			.addAttribute(OBJECT_TYPE_ATTRIBUTE, objectType)
			.addAttribute(MIME_TYPE_ATTRIBUTE, mimeType);
		
		element.add(name.getElement());

		for ( Iterator iter = externalIdentifierList.iterator(); iter.hasNext(); ) {
			ExternalIdentifier ei = (ExternalIdentifier)iter.next();
			element.add(ei.getElement());
		}
		
		for ( Iterator iter = slotList.iterator(); iter.hasNext(); ) {
			Slot s = (Slot)iter.next();
			element.add(s.getElement());
		}

		for ( Iterator iter = classificationList.iterator(); iter.hasNext();  ) {
			Classification c = (Classification)iter.next();
			element.add(c.getElement());
		}
		
		return element;
	}
	
	
}
