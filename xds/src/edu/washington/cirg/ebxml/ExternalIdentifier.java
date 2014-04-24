/*
 * Copyright 2004-2005 (C) University of Washington. All Rights Reserved.
 * Created on Dec 21, 2004
 * University of Washington, CIRG
 * $Id: ExternalIdentifier.java,v 1.3 2005/02/11 20:10:22 ddrozd Exp $
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
public class ExternalIdentifier implements EbXMLElement{

	private static Log log = LogFactory.getLog(ExternalIdentifier.class);

	
	public static final QName QNAME = DocumentFactory.getInstance().createQName("ExternalIdentifier", EbXMLConstants.XMLNS_RIM);

	private static final String IDENTIFICATION_SCHEME_ATTRIBUTE = "identificationScheme";
	private static final String VALUE_ATTRIBUTE = "value";
	
	
	private String identificationScheme = null;
	private String value = null;
	private Name name = null;
	
	/**
	 * 
	 * @param identificationScheme
	 * @param value
	 * @param name
	 */
	public ExternalIdentifier(String identificationScheme, String value, Name name) {
		this.identificationScheme = identificationScheme;
		this.value = value;
		this.name = name;
	}
	
	/**
	 * 
	 * @param identificationScheme
	 * @param value
	 * @param nameStringValue
	 */
	public ExternalIdentifier(String identificationScheme, String value, String nameStringValue) {
		this(identificationScheme, value, new Name(nameStringValue));
	}
	/**<p><code>getElement</code> builds and will return a {@link org.dom4j.Element}.
	 * 
	 */
	public Element getElement() {
		Element element = 
			DocumentFactory.getInstance().createElement(QNAME)
			.addAttribute(IDENTIFICATION_SCHEME_ATTRIBUTE, identificationScheme)
			.addAttribute(VALUE_ATTRIBUTE, value);
		if ( name != null ) {
			element.add(name.getElement());
		}
		return element;
	}

}
