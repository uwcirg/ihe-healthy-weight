/*
 * Copyright 2004-2005 (C) University of Washington. All Rights Reserved.
 * Created on Dec 28, 2004
 * University of Washington, CIRG
 * $Id: Address.java,v 1.2 2005/01/26 19:45:44 ddrozd Exp $
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
public class Address {

	private static Log log = LogFactory.getLog(Address.class);
	
	private static final QName ADDRESS_QNAME = 
		DocumentFactory.getInstance().createQName("Address", CCRConstants.XMLNS_CCR);	
	private static final QName LINE1_QNAME = 
		DocumentFactory.getInstance().createQName("Line1", CCRConstants.XMLNS_CCR);	
	private static final QName LINE2_QNAME = 
		DocumentFactory.getInstance().createQName("Line2", CCRConstants.XMLNS_CCR);	
	private static final QName CITY_QNAME = 
		DocumentFactory.getInstance().createQName("City", CCRConstants.XMLNS_CCR);	
	private static final QName STATE_QNAME = 
		DocumentFactory.getInstance().createQName("State", CCRConstants.XMLNS_CCR);	
	private static final QName COUNTRY_QNAME = 
		DocumentFactory.getInstance().createQName("Country", CCRConstants.XMLNS_CCR);	
	private static final QName POSTAL_CODE_QNAME = 
		DocumentFactory.getInstance().createQName("PostalCode", CCRConstants.XMLNS_CCR);	

	
	private String line1;
	private String line2;
	private String city;
	private String state;
	private String country;
	private String postalCode;

	/**
	 * @param line1
	 * @param line2
	 * @param city
	 * @param state
	 * @param country
	 * @param postalCode
	 */
	public Address(String line1, String line2, String city, String state,
			String country, String postalCode) {
		this.line1 = line1;
		this.line2 = line2;
		this.city = city;
		this.state = state;
		this.country = country;
		this.postalCode = postalCode;
	}
	
	
	public Element getElement() {
		
		Element element = 
			DocumentFactory.getInstance().createElement(ADDRESS_QNAME);
		if ( line1 != null ) {
			element.addElement(LINE1_QNAME).addText(line1);
		}
		if ( line2 != null ) {
			element.addElement(LINE2_QNAME).addText(line2);
		}
		if ( city != null ) {
			element.addElement(CITY_QNAME).addText(city);
		}
		if ( state != null ) {
			element.addElement(STATE_QNAME).addText(state);
		}
		if ( country != null ) {
			element.addElement(COUNTRY_QNAME).addText(country);
		}
		if ( postalCode !=  null ) {
			element.addElement(POSTAL_CODE_QNAME).addText(postalCode);
		}	
		return element;
	}
}
