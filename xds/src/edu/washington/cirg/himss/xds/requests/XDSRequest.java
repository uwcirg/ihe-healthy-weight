/*
 * Copyright 2004-2005 (C) University of Washington. All Rights Reserved.
 * Created on Dec 21, 2004
 * University of Washington, CIRG
 * $Id: XDSRequest.java,v 1.3 2005/01/26 18:59:47 ddrozd Exp $
 */
package edu.washington.cirg.himss.xds.requests;

import org.dom4j.Document;
import org.dom4j.DocumentException;

import edu.washington.cirg.himss.xds.XDSException;

/** Interface representing an XDS ebXML registry/repository request
 * @author <a href="mailto:ddrozd@u.washington.edu">Dan Drozd</a>
 * @version $Revision: 1.3 $
 *
 */
public interface XDSRequest {

	public Document generateRequest() 
		throws XDSException, DocumentException;	
}
