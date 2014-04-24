/*
 * Copyright 2004-2005 (C) University of Washington. All Rights Reserved.
 * Created on Dec 28, 2004
 * University of Washington, CIRG
 * $Id: ExternalID.java,v 1.2 2005/01/26 19:45:44 ddrozd Exp $
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

public class ExternalID {

	private static Log log = LogFactory.getLog(ExternalID.class);

	private static final QName EXTERNAL_ID_QNAME = 
		DocumentFactory.getInstance().createQName("ExternalID", CCRConstants.XMLNS_CCR);	
	private static final QName ID_QNAME = 
		DocumentFactory.getInstance().createQName("ID", CCRConstants.XMLNS_CCR);	
	private static final QName ID_TYPE_QNAME = 
		DocumentFactory.getInstance().createQName("IDType", CCRConstants.XMLNS_CCR);	
	private static final QName TEXT_QNAME = 
		DocumentFactory.getInstance().createQName("Text", CCRConstants.XMLNS_CCR);	
	
	
	private String id;
	private String idType;
	private String referenceObjectId;
	private String internalLinkId;
	
	

	/**
	 * @param id
	 * @param idType
	 * @param referenceObjectId
	 * @param internalLinkId
	 */
	public ExternalID(String id, String idType, String referenceObjectId,
			String internalLinkId) throws IllegalArgumentException {
		if ( id == null ) {
			throw new IllegalArgumentException("argument id cannot be null");
		}
		this.id = id;
		this.idType = idType;
		this.referenceObjectId = referenceObjectId;
		this.internalLinkId = internalLinkId;
	}
	
	public Element getElement() {
		Element element = 
			DocumentFactory.getInstance().createElement(EXTERNAL_ID_QNAME);

		element.addElement(ID_QNAME).addText(id);

		if ( idType != null ) {
			element.addElement(ID_TYPE_QNAME)
			.addElement(TEXT_QNAME).addText(idType);
		}
		return element;
	}
}
