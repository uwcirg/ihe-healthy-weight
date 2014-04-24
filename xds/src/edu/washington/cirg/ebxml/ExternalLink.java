/*
 * Copyright 2004-2005 (C) University of Washington. All Rights Reserved.
 * Created on Dec 21, 2004
 * University of Washington, CIRG
 * $Id: ExternalLink.java,v 1.3 2005/02/11 20:10:22 ddrozd Exp $
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
public class ExternalLink implements EbXMLElement {
	
	private static Log log = LogFactory.getLog(ExternalLink.class);

	public static final QName QNAME = DocumentFactory.getInstance().createQName("ExternalLink", EbXMLConstants.XMLNS_RIM);

	private static final String ID_ATTRIBUTE = "id";
	private static final String EXTERNAL_URI_ATTRIBUTE = "externalURI";

	
	private String id;
	private String externalURI;
	private Name name;
	
	/**
	 * 
	 * @param id
	 * @param externalURI
	 * @param name
	 */
	public ExternalLink(String id,
			String externalURI,
			Name name){
		
		this.id = id;
		this.externalURI = externalURI;
		this.name = name;		
	}
	
	/**
	 * 
	 * @param id
	 * @param externalURI
	 * @param nameStringValue
	 */
	public ExternalLink(String id,
			String externalURI,
			String nameStringValue){
		
		this(id, externalURI, new Name(nameStringValue));
	}

	/* (non-Javadoc)
	 * @see edu.washington.cirg.ebxml.EbXMLElement#getElement()
	 */
	public Element getElement() {
		Element externallink = 
			DocumentFactory.getInstance().createElement(QNAME)
			.addAttribute(ID_ATTRIBUTE, id)
			.addAttribute(EXTERNAL_URI_ATTRIBUTE, externalURI);
		externallink.add(name.getElement());
		return externallink;
	}

}

