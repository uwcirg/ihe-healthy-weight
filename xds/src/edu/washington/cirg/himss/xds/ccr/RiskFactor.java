/*
 * Copyright 2004-2005 (C) University of Washington. All Rights Reserved.
 * Created on Dec 28, 2004
 * University of Washington, CIRG
 * $Id: RiskFactor.java,v 1.2 2005/01/26 19:45:44 ddrozd Exp $
 */
package edu.washington.cirg.himss.xds.ccr;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/** Java implementation of the Continuity of Care (CCR) document object model
 * @author <a href="mailto:ddrozd@u.washington.edu">Dan Drozd</a>
 * @version $Revision: 1.2 $
 *
 */
public class RiskFactor {

	private static Log log = LogFactory.getLog(RiskFactor.class);

	private String CCRDataObjectId;
	private String description;
	
	

	/**
	 * @param dataObjectId
	 * @param description
	 */
	public RiskFactor(String dataObjectId, String description) {
		CCRDataObjectId = dataObjectId;
		this.description = description;
	}
}
