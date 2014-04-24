/*
 * Copyright 2004-2009 (C) University of Washington. All Rights Reserved.
 * Created on Dec 21, 2004
 * University of Washington, CIRG
 * $Id: XDSSoapRequest.java,v 1.4 2005/01/26 19:04:39 ddrozd Exp $
 */
package edu.washington.cirg.himss.xds.soap;

import java.io.ByteArrayInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintStream;
import java.io.UnsupportedEncodingException;
import java.util.Iterator;
import java.util.List;

import javax.activation.DataHandler;
import javax.mail.MessagingException;
import javax.xml.soap.AttachmentPart;
import javax.xml.soap.MessageFactory;
import javax.xml.soap.MimeHeaders;
import javax.xml.soap.SOAPConnection;
import javax.xml.soap.SOAPConnectionFactory;
import javax.xml.soap.SOAPConstants;
import javax.xml.soap.SOAPException;
import javax.xml.soap.SOAPMessage;
import javax.xml.soap.SOAPPart;
import javax.xml.transform.stream.StreamSource;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.dom4j.Document;

import edu.washington.cirg.himss.xds.XDSFile;

/** Class represents an XDS-compliant SOAP message
 * @author <a href="mailto:ddrozd@u.washington.edu">Dan Drozd</a>
 * @version $Revision: 1.4 $
 *
 */
public class XDSSoapRequest {
	
	private static Log log = LogFactory.getLog(XDSSoapRequest.class);
	
	private Document body;
	private List xdsFileList;
	private String sURL;
	private SOAPMessage message;
	
	public XDSSoapRequest(Document body,
			List xdsFileList,
			String sURL,
			String version)
	throws SOAPException, MessagingException
	{
		this.body = body;
		this.xdsFileList = xdsFileList;
		this.sURL = sURL;		
		this.message = generateMessage(version);
	}
	
	public XDSSoapRequest(Document body, String sURL, String version)
	throws SOAPException, MessagingException
	{
		this(body, null, sURL, version);	
	}
	
	public void printSOAPMessage(PrintStream stream)
	throws SOAPException, IOException{
		message.writeTo(stream);
	}
	
	private void addAttachment(SOAPMessage msg, String id, DataHandler dh){
		AttachmentPart ap = msg.createAttachmentPart(dh);
		ap.setMimeHeader("Content-Type", dh.getContentType());
		ap.setContentId("<" + id + ">");
		msg.addAttachmentPart(ap);
		if ( log.isTraceEnabled() ) {
			log.trace("adding attachment: contentId=" + id);
		}
	}
	
	private SOAPMessage generateMessage(String version) throws SOAPException {
		
		String requestString = body.asXML();
		// Remove the XML Declaration, if any
		if (requestString.startsWith("<?xml")) {
			requestString =
				requestString.substring(requestString.indexOf("?>") + 2).trim();
		}
		
		StringBuffer soapText = new StringBuffer("");
		if ("1.2".equals(version)) {
			soapText = new StringBuffer(
			"<soap-env:Envelope xmlns:soap-env=\"http://www.w3.org/2003/05/soap-envelope\">");
		} else {
			soapText = new StringBuffer(
			"<soap-env:Envelope xmlns:soap-env=\"http://schemas.xmlsoap.org/soap/envelope/\">");
		}
		
		// ??eeg The registry server seems to require an empty Header
		// element even though the SOAP 1.1 spec says that it is optional.
		soapText.append("<soap-env:Header/>");
		
		soapText.append("<soap-env:Body>");
		soapText.append(requestString);
		soapText.append("</soap-env:Body>");
		soapText.append("</soap-env:Envelope>");
		
		//log.trace("requestString=\"" + requestString + "\"");
		SOAPMessage message = null;
		try {
			// Use Unicode (utf-8) to getBytes (server and client). Rely on platform default encoding is not safe.
			InputStream soapStream = new ByteArrayInputStream(soapText.toString().getBytes("utf-8"));
			if ("1.2".equals(version)) {
				MessageFactory factory = MessageFactory.newInstance(SOAPConstants.SOAP_1_2_PROTOCOL);
//				MessageFactory factory = MessageFactory.newInstance();
				//MimeHeaders mimeHeader = new MimeHeaders();
				//mimeHeader.addHeader("Content-Type", "application/soap+xml");
				message = factory.createMessage();
				//message = factory.createMessage(mimeHeader, soapStream);
			} else {
				MessageFactory factory = MessageFactory.newInstance();
				message = factory.createMessage();
			}
			SOAPPart soapPart = message.getSOAPPart();
			soapPart.setContent(new StreamSource(soapStream));
			log.info("Content-Type is: '" + soapPart.getMimeHeader("Content-Type")[0] + "'");
//			soapPart.setMimeHeader("Content-Type", "application/soap+xml");
//			log.info("Content-Type is: '" + soapPart.getMimeHeader("Content-Type")[0] + "'");
			
//			if ( log.isTraceEnabled() ) {
//				try {
//					message.writeTo(System.err);
//				} catch (SOAPException e) {
//					e.printStackTrace();
//				} catch (IOException e) {
//					e.printStackTrace();
//				}
//			}
			if ( xdsFileList != null ) {
				Iterator iterator = xdsFileList.iterator();
				while ( iterator.hasNext() ) {
					XDSFile file = (XDSFile)iterator.next();
					String id = file.getUUID();
					DataHandler dh = new DataHandler(file.getDataSource());
					addAttachment(message, id, dh);
				}
			}

//			if ( log.isDebugEnabled() ) {
//				try {
//					message.writeTo(System.err);
//				} catch (SOAPException e) {
//					e.printStackTrace();
//				} catch (IOException e) {
//					e.printStackTrace();
//				}
//			}
			
			
		} catch (UnsupportedEncodingException x) {
			throw new SOAPException(x);
		} catch (IOException x) {
			throw new SOAPException(x);
		}
		
		return message;
	}
	
	
	public SOAPMessage send() throws SOAPException {
		
		SOAPConnectionFactory scf = SOAPConnectionFactory.newInstance();
		SOAPConnection connection = scf.createConnection();
		
		SOAPMessage reply = null;
		reply = connection.call(message, sURL);
		
		return reply;
	}
	
	
	public SOAPMessage sendMessage()
	throws SOAPException {
//		SOAPMessageContext msgCtx = (SOAPMessageContext) message;
//		String sReply = msgCtx.getMessage().toString();
//		log.info("DEBUG,Soap Request: " + msgCtx.getMessage().toString() + ",URL: " + sURL.toString());
		try {
		    FileOutputStream fos = new FileOutputStream("/tmp/soapRequest.txt");
		    message.writeTo(fos);
		    fos.close();
		} catch (FileNotFoundException e) {
		    System.err.println("XDSSoapRequest: " + e);
		} catch (IOException e) {
		    System.err.println("XDSSoapRequest: " + e);
		}
		
		SOAPConnectionFactory connFactory = SOAPConnectionFactory.newInstance();
		SOAPConnection conn = connFactory.createConnection();
		SOAPMessage response = conn.call(message, sURL);
		try {
		    FileOutputStream fos = new FileOutputStream("/tmp/soapResponse.txt");
		    response.writeTo(fos);
		    fos.close();
		} catch (FileNotFoundException e) {
		    System.err.println("XDSSoapRequest: " + e);
		} catch (IOException e) {
		    System.err.println("XDSSoapRequest: " + e);
		}

		conn.close();
		
		//returns response back from the registry
		return response;
	}

	public SOAPMessage sendVFMMessage()
	throws SOAPException {
//		SOAPMessageContext msgCtx = (SOAPMessageContext) message;
//		String sReply = msgCtx.getMessage().toString();
//		log.info("DEBUG,Soap Request: " + msgCtx.getMessage().toString() + ",URL: " + sURL.toString());
		try {
		    FileOutputStream fos = new FileOutputStream("/tmp/vfmSoapRequest.txt");
		    message.writeTo(fos);
		    fos.close();
		} catch (FileNotFoundException e) {
		    System.err.println("XDSSoapRequest: " + e);
		} catch (IOException e) {
		    System.err.println("XDSSoapRequest: " + e);
		}
		
		SOAPConnectionFactory connFactory = SOAPConnectionFactory.newInstance();
		SOAPConnection conn = connFactory.createConnection();
		SOAPMessage response = conn.call(message, sURL);
		try {
		    FileOutputStream fos = new FileOutputStream("/tmp/vfmSoapResponse.txt");
		    response.writeTo(fos);
		    fos.close();
		} catch (FileNotFoundException e) {
		    System.err.println("XDSSoapRequest: " + e);
		} catch (IOException e) {
		    System.err.println("XDSSoapRequest: " + e);
		}

		conn.close();
		
		//returns response back from the registry
		return response;
	}
	
	public SOAPMessage sendVFMCardMessage()
	throws SOAPException {
//		SOAPMessageContext msgCtx = (SOAPMessageContext) message;
//		String sReply = msgCtx.getMessage().toString();
//		log.info("DEBUG,Soap Request: " + msgCtx.getMessage().toString() + ",URL: " + sURL.toString());
		try {
		    FileOutputStream fos = new FileOutputStream("/tmp/vfmCardSoapRequest.txt");
		    message.writeTo(fos);
		    fos.close();
		} catch (FileNotFoundException e) {
		    System.err.println("XDSSoapRequest: " + e);
		} catch (IOException e) {
		    System.err.println("XDSSoapRequest: " + e);
		}
		
		SOAPConnectionFactory connFactory = SOAPConnectionFactory.newInstance();
		SOAPConnection conn = connFactory.createConnection();
		SOAPMessage response = conn.call(message, sURL);
		try {
		    FileOutputStream fos = new FileOutputStream("/tmp/vfmCardSoapResponse.txt");
		    response.writeTo(fos);
		    fos.close();
		} catch (FileNotFoundException e) {
		    System.err.println("XDSSoapRequest: " + e);
		} catch (IOException e) {
		    System.err.println("XDSSoapRequest: " + e);
		}

		conn.close();
		
		//returns response back from the registry
		return response;
	}
}