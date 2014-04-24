/*
 * Copyright 2004-2005 (C) University of Washington. All Rights Reserved.
 * Created on Dec 21, 2004
 * University of Washington, CIRG
 * $Id: Description.java,v 1.4 2005/02/11 20:10:22 ddrozd Exp $
 */
package edu.washington.cirg.ebxml;

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
public class Description implements EbXMLElement{

	private static Log log = LogFactory.getLog(Description.class);

	public static final QName QNAME = DocumentFactory.getInstance().createQName("Description", EbXMLConstants.XMLNS_RIM);

	private LocalizedString localizedstring;
	
	/**
	 * 
	 * @param localizedstring
	 */
	public Description(LocalizedString localizedstring) {
		this.localizedstring = localizedstring;
	}
	/**
	 * 
	 * @param desc
	 */
	public Description(String desc) {
		localizedstring = new LocalizedString(desc);
	}
	/**<p><code>getValue</code> returns the {@link String} value of the Description.
	 * 
	 */
	public String getValue() {
		return localizedstring.getValue();
	}
	/**<p><code>getElement</code> builds and will return a {@link org.dom4j.Element}.
	 * 
	 */
	public Element getElement() {
		Element element = 
			DocumentFactory.getInstance().createElement(QNAME);
		element.add(localizedstring.getElement());
		return element;
	}
	
}
