/*
 * Copyright 2004-2005 (C) University of Washington. All Rights Reserved.
 * Created on Dec 21, 2004
 * University of Washington, CIRG
 * $Id: XDSException.java,v 1.3 2005/02/11 20:10:46 ddrozd Exp $
 */
package edu.washington.cirg.himss.xds;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/** Wrapper used to indicate XDS application errors.
 * @author <a href="mailto:ddrozd@u.washington.edu">Dan Drozd</a>
 * @version $Revision: 1.3 $
 *
 */
public class XDSException extends Exception {
	/**
     * Comment for <code>serialVersionUID</code>
     */
    private static final long serialVersionUID = 3258134643785283890L;
    private static Log log = LogFactory.getLog(XDSException.class);


	/**
	 * 
	 */
	public XDSException() {
		super();
		// TODO Auto-generated constructor stub
	}

	/**
	 * @param arg0
	 */
	public XDSException(String arg0) {
		super(arg0);
		// TODO Auto-generated constructor stub
	}

	/**
	 * @param arg0
	 * @param arg1
	 */
	public XDSException(String arg0, Throwable arg1) {
		super(arg0, arg1);
		// TODO Auto-generated constructor stub
	}

	/**
	 * @param arg0
	 */
	public XDSException(Throwable arg0) {
		super(arg0);
		// TODO Auto-generated constructor stub
	}

}
