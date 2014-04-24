/*
 * Copyright 2004-2005 (C) University of Washington. All Rights Reserved.
 * Created on Dec 21, 2004
 * University of Washington, CIRG
 * $Id: LocalizedString.java,v 1.2 2005/01/26 00:52:07 ddrozd Exp $
 */
package edu.washington.cirg.ebxml;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.dom4j.DocumentFactory;
import org.dom4j.Element;
import org.dom4j.QName;

/** A lightweight implementation of standard ebXML classes
 * @author <a href="mailto:ddrozd@u.washington.edu">Dan Drozd</a>
 * @version $Revision: 1.2 $
 *
 */
public class LocalizedString implements EbXMLElement{

	private static Log log = LogFactory.getLog(LocalizedString.class);

	
	public static final QName QNAME = DocumentFactory.getInstance().createQName("LocalizedString", EbXMLConstants.XMLNS_RIM);

	public static final String VALUE_ATTRIBUTE = "value";
	
	private String value;
	
	public LocalizedString(String value) {
		this.value = value;
	}
	
	public Element getElement() {
		return DocumentFactory.getInstance().createElement(QNAME)
		.addAttribute("value", value);
	}
	
	public String getValue() {
		return value;
	}
}