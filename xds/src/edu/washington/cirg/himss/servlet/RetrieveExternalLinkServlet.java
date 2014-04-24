/*
 * Copyright 2004-2005 (C) University of Washington. All Rights Reserved.
 * Created on Jan 11, 2005
 * University of Washington, CIRG
 * $Id: RetrieveExternalLinkServlet.java,v 1.6 2005/02/21 16:43:25 ddrozd Exp $
 */
package edu.washington.cirg.himss.servlet;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.util.List;

import javax.mail.MessagingException;
import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.xml.soap.SOAPException;
import javax.xml.soap.SOAPMessage;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.jaxen.JaxenException;

import edu.washington.cirg.himss.xds.XDSConfigurationException;
import edu.washington.cirg.himss.xds.XDSException;
import edu.washington.cirg.himss.xds.requests.AdhocQueryRequest;
import edu.washington.cirg.himss.xds.requests.RetrieveExternalLinkRequest;
import edu.washington.cirg.himss.xds.responses.AdhocQueryResponse;
import edu.washington.cirg.himss.xds.soap.XDSSoapRequest;
import edu.washington.cirg.himss.xds.util.XDSConstants;

/** This servlet acts as a wrapper around classes that generate XDS Retrieve
 * External Link transactions.  These requests retrieve documents from an 
 * ebXML Repository.
 * @author <a href="mailto:ddrozd@u.washington.edu">Dan Drozd</a>
 * @version $Revision: 1.6 $
 *
 */
public class RetrieveExternalLinkServlet extends HttpServlet {

	private static Log log = LogFactory.getLog(RetrieveExternalLinkServlet.class);
	private static boolean LOG_PERFORMANCE_DATA = false; 
	
	public RetrieveExternalLinkServlet() {
		super();
	}
	
	public void init(ServletConfig config) throws ServletException 
	{
		super.init(config);

		try {
			XDSConstants.loadConstants();
			LOG_PERFORMANCE_DATA = XDSConstants.LOG_PERFORMANCE_DATA;
		} catch (XDSConfigurationException e) {
			log.error(e);
			throw new ServletException(e);
		}
	}
	
	public void doPost(HttpServletRequest req, HttpServletResponse resp)
	throws ServletException, IOException {
		
    	long reqStart = 0;
		long startTime = 0;
		long stopTime = 0;
		
		if ( LOG_PERFORMANCE_DATA ) {//Start RequestTimer
			reqStart = System.currentTimeMillis();
		}
		
		String objectId = req.getParameter(XDSControllerServlet.OBJECT_REF_ID_PARAM);
		String queryUrl = req.getParameter(XDSControllerServlet.TARGET_URL_PARAM);
		
		if ( objectId == null || objectId.equals("") ) {
    		throw new ServletException("objectId param not passed to servlet");
    	}
    	if ( queryUrl == null || queryUrl.equals("") ) {
    		throw new ServletException("targetUrl param not passed to servlet");
    	}

		
		try {
			if ( LOG_PERFORMANCE_DATA ) { //Start GenerateRequest
				startTime=System.currentTimeMillis();
			}

		RetrieveExternalLinkRequest xdsreq =
			new RetrieveExternalLinkRequest(objectId,RetrieveExternalLinkRequest.BY_EO_ID, AdhocQueryRequest.LEAF_CLASS );
		Document reqDocument = xdsreq.generateRequest();
    	if ( LOG_PERFORMANCE_DATA) { //End GenerateRequest
			stopTime = System.currentTimeMillis();
			log.info("TIMER,Generate XDS External Link Request," + (stopTime-startTime) + " ms");
			startTime=System.currentTimeMillis();
    	}//Start GenerateSOAPRequest

		XDSSoapRequest soapReq = new XDSSoapRequest(reqDocument, queryUrl, "1.1");
		if ( LOG_PERFORMANCE_DATA) { //End GenerateSOAPRequest
			stopTime = System.currentTimeMillis();
			log.info("TIMER,Generate External Link SOAP Request," + (stopTime-startTime) + " ms");
			startTime=System.currentTimeMillis();
    	}//Start SendSOAPRequest

		SOAPMessage response = soapReq.sendMessage();

		if ( LOG_PERFORMANCE_DATA) {//End SendSOAPRequest
			stopTime = System.currentTimeMillis();
			log.info("TIMER,Send External Link SOAP Request," + (stopTime-startTime) + " ms");
			startTime=System.currentTimeMillis();
		}//Start Parse SOAP Response

		AdhocQueryResponse qresponse = new AdhocQueryResponse(response);

		if ( LOG_PERFORMANCE_DATA ) {//End ParseSOAPResponse
			stopTime = System.currentTimeMillis();
			log.info("TIMER,Parse External Link SOAP Response Message," + (stopTime-startTime) + " ms");				
			startTime = System.currentTimeMillis();
		}//Start Retrieve File

		ServletOutputStream out = resp.getOutputStream();
		List externalLinkList = qresponse.getExternalLinkList();

		if ( externalLinkList.size() != 1 ) {
			log.error("Expected to get a single object back from repository matching object id [ " + objectId + " ], instead the query returned " + externalLinkList.size() + " links");
			throw new XDSException("External link request retrieved unexpected number of links");
		} else {
			log.debug("Got our expected one document");
			String url = 
				(String)externalLinkList.get(0);//There should only be one element
			log.info("Attempting to retrieve " + url);
			
			httpStreamBinaryData(url, out, resp);
		}

		if ( LOG_PERFORMANCE_DATA ) {//End ParseSOAPResponse
			stopTime = System.currentTimeMillis();
			log.info("TIMER,Retrieve/Send file," + (stopTime-startTime) + " ms");				
		}//Start Retrieve/Send file

		
		} catch (JaxenException e) {
			log.error(e);
			handleError(req, resp);
		} catch ( DocumentException e ) {
			log.error(e);
			handleError(req, resp);
		} catch ( XDSException e ) {
			log.error(e);
			handleError(req, resp);	   		
		} catch ( SOAPException e ) {
			log.error(e);
			handleError(req, resp);
		} catch ( IOException e ) {
			log.error(e);
			handleError(req, resp);
		} catch (MessagingException e) {
			log.error(e);
			handleError(req, resp);
		} finally {
			if ( LOG_PERFORMANCE_DATA ) { //End RequestTimer
				stopTime = System.currentTimeMillis();
				log.info("TIMER,ExternalLink Request Total," + (stopTime-reqStart) + " ms");
			}
		}

	}
	
	   public void doGet(HttpServletRequest req, HttpServletResponse resp)
		throws ServletException, IOException {
	   		doPost(req, resp);
	   }
	   
	   public void handleError(HttpServletRequest req, HttpServletResponse res) 
	   throws ServletException, IOException {
           res.getOutputStream().print("ERROR");
        /*
        RequestDispatcher rd = 
            req.getRequestDispatcher(req.getContextPath() + "/" + XDSControllerServlet.ERROR_HANDLER);
            rd.forward(req, res);
           */

	   }
	   
		public static void httpStreamBinaryData(String srcUrlString, ServletOutputStream out, HttpServletResponse resp) 
		throws IOException, MalformedURLException {
			
			int BUF_SIZE = 1024;

			URL url;
			URLConnection conn;
			BufferedInputStream bis = null;
			BufferedOutputStream bos = null;
			try {
				url = new URL(srcUrlString);
			} catch ( MalformedURLException mue ) {
				log.error(mue);
				throw mue;
			}
			
			try {
				conn = url.openConnection();
				int length = conn.getContentLength();
				resp.setContentLength(length);
				bis = new BufferedInputStream(conn.getInputStream(), BUF_SIZE);
				bos = new BufferedOutputStream(out);
				byte[] buff = new byte[BUF_SIZE];
				int bytesRead;
				while ( -1 != (bytesRead = bis.read(buff, 0, buff.length)) ) {
					bos.write(buff, 0, bytesRead);
				}
				bos.flush();
				
				if ( log.isDebugEnabled() ) {
					log.debug("Retrieved file from " + srcUrlString);
				}
			} catch ( IOException ioe ) {
				log.debug(ioe);
				throw ioe;
			} finally {
				if ( bis != null ) {
					bis.close();
				}
				if ( bos != null ) {
					bos.close();
				}
			}
		}


}
