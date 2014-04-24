/*
 * Copyright 2004-2005 (C) University of Washington. All Rights Reserved.
 * Created on Dec 22, 2004
 * University of Washington, CIRG
 * $Id: XDSConfigurationException.java,v 1.2 2005/02/11 20:10:46 ddrozd Exp $
 */
package edu.washington.cirg.himss.xds;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/** Simple wrapper class to be thrown if a server cannot properly
 * configure the XDS system.
 * @author <a href="mailto:ddrozd@u.washington.edu">Dan Drozd</a>
 * @version $Revision: 1.2 $
 *
 */
public class XDSConfigurationException extends Exception {

	/**
     * Comment for <code>serialVersionUID</code>
     */
    private static final long serialVersionUID = 3257570598497498680L;
    private static Log log = LogFactory.getLog(XDSConfigurationException.class);

	/**
	 * 
	 */
	public XDSConfigurationException() {
		super();
	}

	/**
	 * @param arg0
	 */
	public XDSConfigurationException(String arg0) {
		super(arg0);
	}

	/**
	 * @param arg0
	 * @param arg1
	 */
	public XDSConfigurationException(String arg0, Throwable arg1) {
		super(arg0, arg1);
	}

	/**
	 * @param arg0
	 */
	public XDSConfigurationException(Throwable arg0) {
		super(arg0);
	}

}


