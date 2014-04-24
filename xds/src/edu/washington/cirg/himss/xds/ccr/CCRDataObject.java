/*
 * Copyright 2004-2005 (C) University of Washington. All Rights Reserved.
 * Created on Dec 28, 2004
 * University of Washington, CIRG
 * $Id: CCRDataObject.java,v 1.2 2005/01/26 19:45:44 ddrozd Exp $
 */
package edu.washington.cirg.himss.xds.ccr;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.dom4j.Element;

/** Java implementation of the Continuity of Care (CCR) document object model
 * @author <a href="mailto:ddrozd@u.washington.edu">Dan Drozd</a>
 * @version $Revision: 1.2 $
 *
 */
public abstract class CCRDataObject {
	
	private static Log log = LogFactory.getLog(CCRDataObject.class);

	
	protected CCRDataObjectID ccrDataObjectId;
	
	public void setCCRDataObjectId(String ccrDataObjectIdString ) {
		this.ccrDataObjectId = new CCRDataObjectID(ccrDataObjectIdString);		
	}
	
	public abstract Element getElement();
}
