/*
 * Copyright 2004-2005 (C) University of Washington. All Rights Reserved.
 * Created on Dec 21, 2004
 * University of Washington, CIRG
 * $Id: Value.java,v 1.3 2005/01/26 00:52:07 ddrozd Exp $
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
public class Value implements EbXMLElement {

	private static Log log = LogFactory.getLog(Value.class);

	public static final QName QNAME = DocumentFactory.getInstance().createQName("Value", EbXMLConstants.XMLNS_RIM);


	private String value;
	

	public Value(String value) {
		if ( value == null ) {
			log.error("Null pointer passed to Value constructor");
			throw new IllegalArgumentException("Null pointer passed to Value constructor");
		}
		this.value = value;
	}
	
	public Element getElement() {
		return DocumentFactory.getInstance().createElement(QNAME)
			.addText(value);
	}

}
