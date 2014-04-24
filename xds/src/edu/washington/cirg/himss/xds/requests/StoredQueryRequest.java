/*
 * Copyright 2004-2007 (C) University of Washington. All Rights Reserved.
 * Created on Jan 30, 2007
 * University of Washington, CIRG
 */
package edu.washington.cirg.himss.xds.requests;

import java.util.HashMap;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;

import edu.washington.cirg.ebxml.EbXMLConstants;
import edu.washington.cirg.himss.xds.XDSException;

/** Class representing a Stored query to be sent to the XDS ebXML registry
 * @author <a href="mailto:jsibley@u.washington.edu">Jim Sibley</a>
 *
 */
public class StoredQueryRequest implements XDSRequest{
	
	private static Log log = LogFactory.getLog(StoredQueryRequest.class);
	public static final String LEAF_CLASS = "LeafClass";
	public static final String OBJECT_REF = "ObjectRef";
	
	private String queryID;
	private String queryType;
	private String message;
	private boolean returnComposedObjects;
	private Document document;
	private Map<String, String> queryParams = new HashMap();
	
	public StoredQueryRequest(String queryID, String queryType, boolean returnComposedObjects) {
		this.queryID = queryID;
		this.queryType = queryType;
		this.returnComposedObjects = returnComposedObjects;
	}
	
	public StoredQueryRequest(String queryType, boolean returnComposedObjects) {
		this(null, queryType, returnComposedObjects);
	}
	
	public void setQueryID(String queryID) {
		this.queryID = queryID;
	}
	
	public void setQueryParams(Map queryParams) {
		this.queryParams = queryParams;
	}
	
	public Document generateRequest() throws XDSException, DocumentException{
		
		if ( ! isValidRequestType()) {
			throw new XDSException("Request type for StoredQueryRequest invalid");
		}
		
		if ( queryID == null ) {
			throw new XDSException("QueryID string is null");			
		}
		document = DocumentHelper.createDocument();
		Element eAdhocQueryRequest = document.addElement("query:AdhocQueryRequest")
			.addNamespace("query", EbXMLConstants.XMLNS_QUERY_URI)
			.addNamespace("rim", EbXMLConstants.XMLNS_RIM_URI)
			.addNamespace("rs", EbXMLConstants.XMLNS_RS_URI)
			.addNamespace("xsi", EbXMLConstants.XMLNS_XSI_URI);
//			.addAttribute("xmlns", EbXMLConstants.XMLNS_URI)
//			.addAttribute("xmlns:query", EbXMLConstants.XMLNS_QUERY_URI)
//			.addAttribute("xmlns:rim", EbXMLConstants.XMLNS_RIM_URI)
//			.addAttribute("xmlns:rs", EbXMLConstants.XMLNS_RS_URI)
//			.addAttribute("xmlns:xsi", EbXMLConstants.XMLNS_XSI_URI);
		Element eResponseOption = eAdhocQueryRequest.addElement("query:ResponseOption")
			.addAttribute("returnType", queryType)
			.addAttribute("returnComposedObjects", String.valueOf(returnComposedObjects));
		
//		if ( log.isDebugEnabled() ) {
//			log.debug(queryID);
//		}
		Element eStoredQuery = eAdhocQueryRequest.addElement("rim:AdhocQuery")
			.addAttribute("id", queryID);
		
		for (Map.Entry<String, String> entry : queryParams.entrySet()) {
		    Element eQueryParam = eStoredQuery.addElement("rim:Slot")
		    	.addAttribute("name", entry.getKey());
		    Element eParamValueList = eQueryParam.addElement("rim:ValueList");
		    Element eParamValue = eParamValueList.addElement("rim:Value")
		    	.addText(entry.getValue());
		}
		
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
