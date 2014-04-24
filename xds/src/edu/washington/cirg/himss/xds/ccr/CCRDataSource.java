/*
 * Copyright 2004-2005 (C) University of Washington. All Rights Reserved.
 * Created on Dec 28, 2004
 * University of Washington, CIRG
 * $Id: CCRDataSource.java,v 1.2 2005/01/26 19:45:44 ddrozd Exp $
 */
package edu.washington.cirg.himss.xds.ccr;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import javax.activation.DataSource;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.dom4j.Document;

import edu.washington.cirg.himss.xds.util.XDSConstants;

/** Java implementation of the Continuity of Care (CCR) document object model
 * @author <a href="mailto:ddrozd@u.washington.edu">Dan Drozd</a>
 * @version $Revision: 1.2 $
 *
 */
public class CCRDataSource extends Object implements DataSource {
	
	private static Log log = LogFactory.getLog(CCRDataSource.class);
	
	private static final String MIME_TYPE = XDSConstants.CCR_DE_MIME_TYPE;
	private static final String NAME = XDSConstants.CCR_DOCUMENT_TITLE;
	private Document ccrDoc;
	private String xml;
	
	/**
	 * 
	 */
	public CCRDataSource(Document ccrDoc) {
		super();
		this.ccrDoc = ccrDoc;
		if ( ccrDoc != null ) {
			this.xml = ccrDoc.asXML();
		}
	}

	/* (non-Javadoc)
	 * @see javax.activation.DataSource#getContentType()
	 */
	public String getContentType() {
		return MIME_TYPE;
	}

	/* (non-Javadoc)
	 * @see javax.activation.DataSource#getInputStream()
	 */
	public InputStream getInputStream() throws IOException {
		if ( xml == null ) {
			return null;
		}
		return new ByteArrayInputStream(xml.getBytes());
	}

	/* (non-Javadoc)
	 * @see javax.activation.DataSource#getName()
	 */
	public String getName() {
		return NAME;
	}

	/* (non-Javadoc)
	 * @see javax.activation.DataSource#getOutputStream()
	 */
	public OutputStream getOutputStream() throws IOException {
		throw new IOException("DataHandler does not provide an output stream");
	}

}