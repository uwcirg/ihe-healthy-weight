/*
 * Copyright 2004-2005 (C) University of Washington. All Rights Reserved.
 * Created on Dec 28, 2004
 * University of Washington, CIRG
 * $Id: Test.java,v 1.3 2005/02/09 01:51:11 ddrozd Exp $
 */
package edu.washington.cirg.himss.xds.ccr;

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
public class Test extends CCRDataObject {

	private static Log log = LogFactory.getLog(Test.class);
	private static final QName TEST_QNAME = 
		DocumentFactory.getInstance().createQName("Test", CCRConstants.XMLNS_CCR);	
    private static final QName TEST_TYPE_QNAME = 
        DocumentFactory.getInstance().createQName("TestType", CCRConstants.XMLNS_CCR);  
    private static final QName VALUE_TYPE_QNAME = 
        DocumentFactory.getInstance().createQName("Value", CCRConstants.XMLNS_CCR);  
	
	private String testType;
	private String description;
    private String value;
	
	
	/**
	 * @param testType
	 * @param description
     * @param value
	 */
	public Test(String testType, String description, String value) {
		this.testType = testType;
		this.description = description;
        this.value = value;
	}
	
	
	/* (non-Javadoc)
	 * @see edu.washington.cirg.himss.xds.ccr.CCRDataObject#getElement()
	 */
	public Element getElement() {
		Element element = 
			DocumentFactory.getInstance().createElement(TEST_QNAME);
		element.add(ccrDataObjectId.getElement());
		
		if ( testType != null ) {
			element.addElement(TEST_TYPE_QNAME).addText(testType);
		}
		
		if ( description != null && ! description.trim().equals("")) {
			Element descElement = 
				DocumentFactory.getInstance().createElement(CCRConstants.DESC_QNAME);
			descElement.addElement(CCRConstants.TEXT_QNAME).addText(description);
			element.add(descElement);
		}
        if ( value != null ) {
            element.addElement(VALUE_TYPE_QNAME).addText(value);
        }

        return element;
	}

}


