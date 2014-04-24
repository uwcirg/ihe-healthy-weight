/*
 * Copyright 2004-2005 (C) University of Washington. All Rights Reserved.
 * Created on Dec 21, 2004
 * University of Washington, CIRG
 * $Id: AdhocQueryRequest.java,v 1.4 2005/01/26 18:59:47 ddrozd Exp $
 */
package edu.washington.cirg.himss.xds.requests;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;

import edu.washington.cirg.ebxml.EbXMLConstants;
import edu.washington.cirg.himss.xds.XDSException;

/** Class representing an Adhoc query to be sent to the XDS ebXML registry
 * @author <a href="mailto:ddrozd@u.washington.edu">Dan Drozd</a>
 * @version $Revision: 1.4 $
 *
 */
public class AdhocQueryRequest implements XDSRequest{
	
	private static Log log = LogFactory.getLog(AdhocQueryRequest.class);
	public static final String LEAF_CLASS = "LeafClass";
	public static final String OBJECT_REF = "ObjectRef";
	
	private String query;
	private String queryType;
	private String message;
	private boolean returnComposedObjects;
	private Document document;
	
	public AdhocQueryRequest(String query, String queryType, boolean returnComposedObjects) {
		this.query = query;
		this.queryType = queryType;
		this.returnComposedObjects = returnComposedObjects;
	}
	
	public AdhocQueryRequest(String queryType, boolean returnComposedObjects) {
		this(null, queryType, returnComposedObjects);
	}
	
	public void setQuery(String query) {
		this.query = query;
	}
	
	public Document generateRequest() throws XDSException, DocumentException{
		
		if ( ! isValidRequestType()) {
			throw new XDSException("Request type for AdhocQueryRequest invalid");
		}
		
		if ( query == null ) {
			throw new XDSException("Query string is null");			
		}
		document = DocumentHelper.createDocument();
		Element eAdhocQueryRequest = document.addElement("AdhocQueryRequest")
			.addAttribute("xmlns", EbXMLConstants.XMLNS_URI)
			.addAttribute("xmlns:q", EbXMLConstants.XMLNS_QUERY_URI)
			.addAttribute("xmlns:rim", EbXMLConstants.XMLNS_RIM_URI)
			.addAttribute("xmlns:rs", EbXMLConstants.XMLNS_RS_URI)
			.addAttribute("xmlns:xsi", EbXMLConstants.XMLNS_XSI_URI);
		Element eResponseOption = eAdhocQueryRequest.addElement("ResponseOption")
			.addAttribute("returnType", queryType)
			.addAttribute("returnComposedObjects", String.valueOf(returnComposedObjects));
		
		if ( log.isDebugEnabled() ) {
			log.debug(query);
		}
		Element eSQLQuery = eAdhocQueryRequest.addElement("SQLQuery")
			.addText(query);
		
		return document;
	}
	
	private boolean isValidRequestType() {
		if ( queryType.equals("LeafClass") ||
			queryType.equals("ObjectRef")) {
			return true;
		}
		return false;
	}
}
