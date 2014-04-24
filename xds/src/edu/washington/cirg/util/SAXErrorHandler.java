/*
 * Copyright 2004-2005 (C) University of Washington. All Rights Reserved.
 * Created on Jan 14, 2005
 * University of Washington, CIRG
 * $Id: SAXErrorHandler.java,v 1.2 2005/01/26 19:35:15 ddrozd Exp $
 */
package edu.washington.cirg.util;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.xml.sax.ErrorHandler;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;

/** Simple SAXErrorHandler
 * @author <a href="mailto:ddrozd@u.washington.edu">Dan Drozd</a>
 * @version $Revision: 1.2 $
 *
 */
public class SAXErrorHandler implements ErrorHandler {
	private static Log log = LogFactory.getLog(SAXErrorHandler.class);
	
	/**
	 * 
	 */
	public SAXErrorHandler() {
		super();
	}
	/* (non-Javadoc)
	 * @see org.xml.sax.ErrorHandler#warning(org.xml.sax.SAXParseException)
	 */
	public void warning(SAXParseException arg0) throws SAXException {
		log.warn(arg0);
	}
	
	/* (non-Javadoc)
	 * @see org.xml.sax.ErrorHandler#error(org.xml.sax.SAXParseException)
	 */
	public void error(SAXParseException arg0) throws SAXException {
		log.error(arg0);
		throw arg0;
	}
	
	/* (non-Javadoc)
	 * @see org.xml.sax.ErrorHandler#fatalError(org.xml.sax.SAXParseException)
	 */
	public void fatalError(SAXParseException arg0) throws SAXException {
		log.fatal(arg0);
		throw arg0;
	}
	
}


