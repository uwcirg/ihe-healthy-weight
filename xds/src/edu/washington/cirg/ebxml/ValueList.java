/*
 * Copyright 2004-2005 (C) University of Washington. All Rights Reserved.
 * Created on Dec 21, 2004
 * University of Washington, CIRG
 * $Id: ValueList.java,v 1.2 2005/01/26 00:52:07 ddrozd Exp $
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
public class ValueList implements EbXMLElement {

	private static Log log = LogFactory.getLog(ValueList.class);

	
	public static final QName QNAME = DocumentFactory.getInstance().createQName("ValueList", EbXMLConstants.XMLNS_RIM);

	
	private Value[] values;
	
	public ValueList(Value[] values) {
		this.values = values;
		//We need to add at least one Value object to the array
		//Add an empty one
		if ( this.values.length == 0 ) {
			values = new Value[] { new Value("") };
		}
	}
	
	public Element getElement() {
		Element element = 
			DocumentFactory.getInstance().createElement(QNAME);
		
		for ( int i = 0; i < values.length; i++ ) {
			element.add(values[i].getElement());
		}
		return element;
	}

}
