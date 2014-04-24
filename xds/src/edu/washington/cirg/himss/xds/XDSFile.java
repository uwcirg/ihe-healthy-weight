/*
 * Copyright 2004-2005 (C) University of Washington. All Rights Reserved.
 * Created on Dec 22, 2004
 * University of Washington, CIRG
 * $Id: XDSFile.java,v 1.4 2005/01/26 01:06:28 ddrozd Exp $
 */
package edu.washington.cirg.himss.xds;

import javax.activation.DataSource;

import edu.washington.cirg.himss.xds.util.IdentifierManager;




/** Class representing a file/document that can be submitted to an 
 * XDS ebXML repository as an XDSDocument.  Any document that can be
 * represented as a javax.activation.DataSource can be attached as a 
 * document.
 * @author <a href="mailto:ddrozd@u.washington.edu">Dan Drozd</a>
 * @version $Revision: 1.4 $
 *
 */
public abstract class XDSFile {

	private String mimetype;
	private String uuid;
	private String uniqueId;
	private DataSource datasource;

	
	public XDSFile(String mimetype, String uuid) {
		this.mimetype = mimetype;
		this.uuid = uuid;
		this.uniqueId = IdentifierManager.getUniqueId();
	}
	
	public void setDataSource(DataSource datasource) {
		this.datasource = datasource;
	}
	
	public String getMimeType() { return mimetype; }
	public String getUUID() { return uuid; }
	public String getUniqueId() { return uniqueId; }
	public DataSource getDataSource() { return datasource; }
	
	
	/*
	private String sFileName;
	
	
	public XDSFile(String sFileName, String sMimeType, String sUUID) {
		this.sFileName = sFileName;
		this.sMimeType = sMimeType;
		this.sUUID = sUUID;
	}
	
	public XDSFile(Document document, String sMimeType, String sUUID) {
		this.document = document;
		this.sMimeType = sMimeType;
		this.sUUID = sUUID;
	}
	
	public String getFileName() { return sFileName; }
*/
}
