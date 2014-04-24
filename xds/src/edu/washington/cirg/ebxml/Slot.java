/*
 * Copyright 2004-2005 (C) University of Washington. All Rights Reserved.
 * Created on Dec 21, 2004
 * University of Washington, CIRG
 * $Id: Slot.java,v 1.3 2005/01/26 00:52:07 ddrozd Exp $
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
public class Slot implements EbXMLElement{

	private static Log log = LogFactory.getLog(Slot.class);

	public static final QName QNAME = DocumentFactory.getInstance().createQName("Slot", EbXMLConstants.XMLNS_RIM);

	private static final String NAME_ATTRIBUTE = "name";
	
	private String name;
	private ValueList valuelist;
	
	public Slot(String name, ValueList valuelist) {
		this.name = name;
		this.valuelist = valuelist;
	}
	
	public Slot(String name, String[] valueStringArray) {
		if ( name == null ) {
			log.error("Null name passed to Slot constructor");
			throw new IllegalArgumentException("Null name passed to Slot constructor");
		}
		this.name = name;
		Value[] values;
		
		if ( valueStringArray.length == 0 ) {
			values = new Value[]{new Value("")};
		} else {
			values = new Value[valueStringArray.length];
			for ( int i=0; i < valueStringArray.length; i++ ) {
				if ( valueStringArray[i] == null ) {
					log.error("Passed a null String to be added to ValueList object for name " + name);
					throw new IllegalArgumentException("Passed a null String to be added to ValueList object for name " + name);
				}
				Value v = new Value(valueStringArray[i]);
				values[i]=v;
			}
		}
		
		valuelist = new ValueList(values);
	}
	
	public Element getElement() {
		Element element = 
			DocumentFactory.getInstance().createElement(QNAME)
			.addAttribute(NAME_ATTRIBUTE, name);
		if ( valuelist != null ) {
			element.add(valuelist.getElement());
		}
		return element;
	}
}
