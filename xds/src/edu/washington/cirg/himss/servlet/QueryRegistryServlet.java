/*
 * Copyright 2004-2005 (C) University of Washington. All Rights Reserved.
 * Created on Jan 11, 2005
 * University of Washington, CIRG
 * $Id: QueryRegistryServlet.java,v 1.4 2005/02/09 01:51:11 ddrozd Exp $
 */
package edu.washington.cirg.himss.servlet;

import java.io.IOException;
import java.io.OutputStream;

import javax.mail.MessagingException;
import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.xml.soap.SOAPException;
import javax.xml.soap.SOAPMessage;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.dom4j.Document;
import org.dom4j.DocumentException;

import edu.washington.cirg.himss.xds.XDSConfigurationException;
import edu.washington.cirg.himss.xds.XDSException;
import edu.washington.cirg.himss.xds.requests.AdhocQueryRequest;
import edu.washington.cirg.himss.xds.requests.RetrieveExtrinsicObjectRequest;
import edu.washington.cirg.himss.xds.soap.XDSSoapRequest;
import edu.washington.cirg.himss.xds.util.XDSConstants;

/** This servlet acts as a wrapper around classes that generate XDS Query Registry
 * transactions.  These requests query an ebXML Registry for documents matching 
 * particular criteria and return a list of matching documents ( in this case 
 * Continuity Of Care Records ).
 * @author <a href="mailto:ddrozd@u.washington.edu">Dan Drozd</a>
 * @version $Revision: 1.4 $
 *
 */
public class QueryRegistryServlet extends HttpServlet {

	private static Log log = LogFactory.getLog(QueryRegistryServlet.class);
	private static boolean LOG_PERFORMANCE_DATA = false; 

	public QueryRegistryServlet() {
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


		String patientId = req.getParameter(XDSControllerServlet.PATIENT_ID_PARAM);
		String queryUrl = req.getParameter(XDSControllerServlet.TARGET_URL_PARAM);
		
		if ( patientId == null || patientId.equals("") ) {
    		throw new ServletException("patientId param not passed to servlet");
    	}
    	if ( queryUrl == null || queryUrl.equals("") ) {
    		throw new ServletException("targetUrl param not passed to servlet");
    	}

		try {
			if ( LOG_PERFORMANCE_DATA ) { //Start GenerateRequest
				startTime=System.currentTimeMillis();
			}

			RetrieveExtrinsicObjectRequest xdsreq =
				new RetrieveExtrinsicObjectRequest(patientId, RetrieveExtrinsicObjectRequest.BY_PATIENT_ID, AdhocQueryRequest.LEAF_CLASS );
			Document reqDocument = xdsreq.generateRequest();
	    	if ( LOG_PERFORMANCE_DATA) { //End GenerateRequest
				stopTime = System.currentTimeMillis();
				log.info("TIMER,Generate XDS Query Request," + (stopTime-startTime) + " ms");
				startTime=System.currentTimeMillis();
	    	}//Start GenerateSOAPRequest

			XDSSoapRequest soapReq = 
				new XDSSoapRequest(reqDocument, queryUrl, "1.1");

			if ( LOG_PERFORMANCE_DATA) { //End GenerateSOAPRequest
				stopTime = System.currentTimeMillis();
				log.info("TIMER,Generate Query SOAP Request," + (stopTime-startTime) + " ms");
				startTime=System.currentTimeMillis();
	    	}//Start SendSOAPRequest

			
			SOAPMessage response = soapReq.sendMessage();
	    	
			if ( LOG_PERFORMANCE_DATA) {//End SendSOAPRequest
				stopTime = System.currentTimeMillis();
				log.info("TIMER,Send Query SOAP Request," + (stopTime-startTime) + " ms");
			}
			
			if ( log.isDebugEnabled() ) {
				response.writeTo(System.out);
			}

			OutputStream outs = resp.getOutputStream();
			response.writeTo(outs);//We forward on SOAP Message to caller
			
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
			if ( LOG_PERFORMANCE_DATA ) {
				stopTime = System.currentTimeMillis();
				log.info("TIMER,Query Request Total," + (stopTime-reqStart) + " ms");
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


}


