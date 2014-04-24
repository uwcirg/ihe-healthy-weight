/*
 * Copyright 2004-2005 (C) University of Washington. All Rights Reserved.
 * Created on Dec 28, 2004
 * University of Washington, CIRG
 * $Id: Agent.java,v 1.2 2005/01/26 19:45:44 ddrozd Exp $
 */
package edu.washington.cirg.himss.xds.ccr;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.dom4j.DocumentFactory;
import org.dom4j.QName;

/** Java implementation of the Continuity of Care (CCR) document object model
 * @author <a href="mailto:ddrozd@u.washington.edu">Dan Drozd</a>
 * @version $Revision: 1.2 $
 *
 */
public abstract class Agent extends CCRDataObject {
	
	private static Log log = LogFactory.getLog(Agent.class);

	public static final String PRODUCT_AGENT = "Product";
	public static final String ENVIRONMENTAL_AGENT = "Environmental";
	

	protected static final QName AGENT_QNAME = 
		DocumentFactory.getInstance().createQName("Agent", CCRConstants.XMLNS_CCR);	

}
