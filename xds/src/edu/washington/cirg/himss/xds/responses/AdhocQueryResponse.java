/*
 * Copyright 2004-2005 (C) University of Washington. All Rights Reserved.
 * Created on Dec 21, 2004
 * University of Washington, CIRG
 * $Id: AdhocQueryResponse.java,v 1.2 2005/01/26 19:04:39 ddrozd Exp $
 */
package edu.washington.cirg.himss.xds.responses;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import javax.xml.soap.SOAPException;
import javax.xml.soap.SOAPMessage;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.dom4j.Attribute;
import org.dom4j.DocumentException;
import org.dom4j.DocumentHelper;
import org.dom4j.XPath;
import org.jaxen.JaxenException;


/** Class extends RegistryResponse to representing an Adhoc query 
 * response returned from an XDS ebXML registry,
 * in response to an AdhocQuery request
 * @author <a href="mailto:ddrozd@u.washington.edu">Dan Drozd</a>
 * @version $Revision: 1.2 $
 *
 */


/*
 * <?xml version="1.0"?>
 * <RegistryResponse status=”Success” xmlns="urn:oasis:names:tc:ebxml-regrep:registry:xsd:2.1">
 * <AdhocQueryResponse xmlns="urn:oasis:names:tc:ebxml-regrep:query:xsd:2.1">
 * <SQLQueryResult>
 * <ObjectRef id=”urn:uuid:fbeacdb7-5421-4474-9267-985007cd8855”/>
 * </SQLQueryResult>
 * </AdhocQueryResponse>
 * </RegistryResponse>
 */

public class AdhocQueryResponse extends RegistryResponse {

	private static Log log = LogFactory.getLog(AdhocQueryResponse.class);

	private List objectRefList;
	private List externalLinkList;
	

	
	public AdhocQueryResponse(SOAPMessage response) 
		throws DocumentException, IOException, SOAPException, JaxenException {

		super(response);
		this.objectRefList = new ArrayList();
		this.externalLinkList = new ArrayList();
		
		XPath xpathSelector = DocumentHelper.createXPath("/SOAP-ENV:Envelope/SOAP-ENV:Body/*[name()='RegistryResponse']/*[name()='AdhocQueryResponse']/*[name()='SQLQueryResult']/*[name()='ExternalLink']/@externalURI");
		List results = xpathSelector.selectNodes(doc);
		Iterator iterator = results.iterator();
		while ( iterator.hasNext() ) {
			Attribute attr = (Attribute)iterator.next();
			externalLinkList.add(attr.getData());
		}
		
		results = null;
		xpathSelector = DocumentHelper.createXPath("/SOAP-ENV:Envelope/SOAP-ENV:Body/*[name()='RegistryResponse']/*[name()='AdhocQueryResponse']/*[name()='SQLQueryResult']/*[name()='ObjectRef']/@id");
		results = xpathSelector.selectNodes(doc);
		iterator = results.iterator();
		while ( iterator.hasNext() ) {
			Attribute attr = (Attribute)iterator.next();
			objectRefList.add(attr.getData());
		}

	}

	public List getObjectRefList() {
		return objectRefList;
	}
	
	public List getExternalLinkList() {
		return externalLinkList;
	}
	
}


