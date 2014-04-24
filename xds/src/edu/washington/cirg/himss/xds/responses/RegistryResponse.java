/*
 * Copyright 2004-2007 (C) University of Washington. All Rights Reserved.
 * Created on Dec 21, 2004
 * University of Washington, CIRG
 * $Id: RegistryResponse.java,v 1.3 2005/01/26 19:04:39 ddrozd Exp $
 */
package edu.washington.cirg.himss.xds.responses;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import javax.xml.soap.SOAPException;
import javax.xml.soap.SOAPMessage;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.DocumentHelper;
import org.dom4j.Node;
import org.jaxen.JaxenException;
import org.jaxen.SimpleNamespaceContext;
import org.jaxen.dom4j.Dom4jXPath;

/** Class representing a response SOAP message from an XDS ebXML registry,
 * in response to any registry/repository request
 * @author <a href="mailto:ddrozd@u.washington.edu">Dan Drozd</a>
 * @version $Revision: 1.3 $
 *
 */
/*
<SOAP-ENV:Envelope xmlns:SOAP-ENV="http://schemas.xmlsoap.org/soap/envelope/">
<SOAP-ENV:Body>
<RegistryResponse 
   status="Failure" 
   xmlns="urn:oasis:names:tc:ebxml-regrep:registry:xsd:2.1">
   <RegistryErrorList>
      <RegistryError 
         codeContext="Test 11746"
         errorCode="Unknown" 
         severity="Error">

Error: LocalizedString element found with no value attribute
        
Validation failed
            

     </RegistryError>
   </RegistryErrorList>
</RegistryResponse>
</SOAP-ENV:Body>
</SOAP-ENV:Envelope>
*/
public class RegistryResponse {

	private static Log log = LogFactory.getLog(RegistryResponse.class);

	protected Document doc;
	protected boolean wasSuccessful;
	protected List errorList;
	
	public RegistryResponse(SOAPMessage response) 
		throws DocumentException, IOException, SOAPException, JaxenException {
		
		this.doc = soap2DOM(response);
		this.errorList = new ArrayList();
		
//		if ( log.isDebugEnabled() ) {
//			try {
//				//Pretty print the document to System.out
//				OutputFormat format = OutputFormat.createPrettyPrint();
//				XMLWriter xmlwriter = new XMLWriter( System.out, format );
//				xmlwriter.write( doc );
//			} catch ( Exception e ) {
//				log.debug(e);
//			}
//		}

		// Check for success or failure.
		Dom4jXPath xpath = new Dom4jXPath("//soapenv:Envelope/soapenv:Body/ns4:AdhocQueryResponse/@status");
		SimpleNamespaceContext nsContext = new SimpleNamespaceContext();
		nsContext.addNamespace("soapenv", "http://schemas.xmlsoap.org/soap/envelope/");
		nsContext.addNamespace("ns4", "urn:oasis:names:tc:ebxml-regrep:xsd:query:3.0");
		xpath.setNamespaceContext(nsContext);
		List<Node> results = xpath.selectNodes(doc);
		Node status = results.get(0);
		wasSuccessful = status.getText().toLowerCase().endsWith("success");
		
		// Check for errors.
		xpath = new Dom4jXPath("/soap-env:Envelope/soap-env:Body/*[name()='rs:RegistryResponse']/*[name()='rs:RegistryErrorList']/*[name()='rs:RegistryError']");
		xpath.setNamespaceContext(nsContext);
		results = xpath.selectNodes(doc);
		Iterator iterator = results.iterator();
		while ( iterator.hasNext() ) {
			Node node = (Node)iterator.next();
			errorList.add(node.getText());
		}
	}
	
	protected Document soap2DOM(SOAPMessage message) 
		throws SOAPException, IOException, DocumentException {
		Document doc;
		ByteArrayOutputStream out = new ByteArrayOutputStream();
		try {
			message.writeTo(out);
		} catch ( SOAPException se ) {
			log.trace(se);
			throw se;
		} catch ( IOException ioe ) {
			log.trace(ioe);
			throw ioe;
		}
		try {
			doc = DocumentHelper.parseText(out.toString());
		} catch ( DocumentException de ) {
			log.trace(de);
			throw de;
		}
		return doc;
	}
	
	public boolean wasSuccessful() {
		return wasSuccessful;
	}
	
	public List getErrorList() {
		return errorList;
	}
	
	public Document getDocument() {
		return doc;
	}
	
}


