/*
 * Copyright 2004-2005 (C) University of Washington. All Rights Reserved.
 * Created on Dec 28, 2004
 * University of Washington, CIRG
 * $Id: CCRDataObjectID.java,v 1.3 2005/01/26 19:45:44 ddrozd Exp $
 */
package edu.washington.cirg.himss.xds.ccr;

import org.dom4j.DocumentFactory;
import org.dom4j.Element;
import org.dom4j.QName;

/** Java implementation of the Continuity of Care (CCR) document object model
 * @author <a href="mailto:ddrozd@u.washington.edu">Dan Drozd</a>
 * @version $Revision: 1.3 $
 *
 */
public class CCRDataObjectID {
	
	private static final QName CCR_DATAOBJECT_ID_QNAME = 
		DocumentFactory.getInstance().createQName("CCRDataObjectID", CCRConstants.XMLNS_CCR);	

	
	private String ccrDataObjectId;
	
	public CCRDataObjectID(String ccrDataObjectId) 
	throws IllegalArgumentException {
		if ( ccrDataObjectId == null ) {
			throw new IllegalArgumentException("ccrDataObjectId cannot be null");
		}
		this.ccrDataObjectId = ccrDataObjectId;
	}
	
	public Element getElement() {
		return DocumentFactory.getInstance().createElement(CCR_DATAOBJECT_ID_QNAME)
			.addText(ccrDataObjectId);
	}
}
