/*
 * Copyright 2004-2005 (C) University of Washington. All Rights Reserved.
 * Created on Dec 28, 2004
 * University of Washington, CIRG
 * $Id: Source.java,v 1.2 2005/01/26 19:45:44 ddrozd Exp $
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
public class Source {

	private static Log log = LogFactory.getLog(Source.class);

	private static final QName SOURCE_QNAME = 
		DocumentFactory.getInstance().createQName("Source", CCRConstants.XMLNS_CCR);	

	
	private String referenceObjectId;
	private String internalCCRLinkId;
	
	
	/**
	 * @param referenceObjectId
	 * @param internalCCRLinkId
	 */
	public Source(String referenceObjectId, String internalCCRLinkId) {
		this.referenceObjectId = referenceObjectId;
		this.internalCCRLinkId = internalCCRLinkId;
	}
	
	public Element getElement() {
		Element element = 
			DocumentFactory.getInstance().createElement(SOURCE_QNAME);
		return element;

	}
}
