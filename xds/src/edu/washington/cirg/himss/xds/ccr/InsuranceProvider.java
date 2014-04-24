/*
 * Copyright 2004-2005 (C) University of Washington. All Rights Reserved.
 * Created on Dec 28, 2004
 * University of Washington, CIRG
 * $Id: InsuranceProvider.java,v 1.3 2005/01/26 19:45:44 ddrozd Exp $
 */
package edu.washington.cirg.himss.xds.ccr;

import java.util.Iterator;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.dom4j.DocumentFactory;
import org.dom4j.Element;
import org.dom4j.QName;

/** Java implementation of the Continuity of Care (CCR) document object model
 * @author <a href="mailto:ddrozd@u.washington.edu">Dan Drozd</a>
 * @version $Revision: 1.3 $
 *
 */
public class InsuranceProvider extends Organization {
	
	private static Log log = LogFactory.getLog(InsuranceProvider.class);
	
	private static final QName INSURANCE_PROVIDER_QNAME = 
		DocumentFactory.getInstance().createQName("InsuranceProvider", CCRConstants.XMLNS_CCR);	

	public InsuranceProvider(String name) {
		super(name);
	}
	
	public Element getElement() {

		Element element = 
			DocumentFactory.getInstance().createElement(INSURANCE_PROVIDER_QNAME);
		element.add(ccrDataObjectId.getElement());
		element.add(super.getElement());
		Iterator iterator = addressList.iterator();
		while ( iterator.hasNext() ) {
			Address a = (Address)iterator.next();
			element.add(a.getElement());
		}
		iterator = null;

		iterator = telephoneList.iterator();
		while ( iterator.hasNext() ) {
			TelephoneNumber t = (TelephoneNumber)iterator.next();
			element.add(t.getElement());
		}
		iterator = null;
		
		return element;
	}

}
