/*
 * Copyright 2004-2005 (C) University of Washington. All Rights Reserved.
 * Created on Jan 7, 2005
 * University of Washington, CIRG
 * $Id: IdentifierManager.java,v 1.3 2005/01/26 19:35:15 ddrozd Exp $
 */
package edu.washington.cirg.himss.xds.util;

import java.util.Date;
import java.util.Random;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/** Static class used to generate pseudo-random unique numbers to be
 * used as identifiers in XDS submission documents and CCRs
 * @author <a href="mailto:ddrozd@u.washington.edu">Dan Drozd</a>
 * @version $Revision: 1.3 $
 *
 */
public class IdentifierManager {

	private static Log log = LogFactory.getLog(IdentifierManager.class);

	private static long seqNum = 0;
	
	private static Random random = new Random();
	
	
	/**
	 * 
	 */
	private IdentifierManager() {
	/*
	 * We don't want people to instatiate this object,
	 * so make it's default constructor private
	 */
	}
	
	
	public static String getUniqueId() {
		long id = new Date().getTime();
		long lRandom = Math.abs(random.nextLong());
		
		return new String(XDSConstants.OID_ROOT + "." + id + "." + lRandom);
	}
	
	

}


