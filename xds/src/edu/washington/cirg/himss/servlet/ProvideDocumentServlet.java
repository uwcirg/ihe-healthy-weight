/*
 * Copyright 2004-2005 (C) University of Washington. All Rights Reserved.
 * Created on Jan 7, 2005
 * University of Washington, CIRG
 * $Id: ProvideDocumentServlet.java,v 1.4 2005/02/09 01:51:11 ddrozd Exp $
 */

package edu.washington.cirg.himss.servlet;

import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import javax.mail.MessagingException;
import javax.naming.NamingException;
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
import org.iso_relax.verifier.VerifierConfigurationException;
import org.jaxen.JaxenException;
import org.xml.sax.SAXException;

import edu.washington.cirg.himss.xds.XDSConfigurationException;
import edu.washington.cirg.himss.xds.XDSException;
import edu.washington.cirg.himss.xds.ccr.ContinuityOfCareRecord;
import edu.washington.cirg.himss.xds.requests.RequestHelper;
import edu.washington.cirg.himss.xds.responses.RegistryResponse;
import edu.washington.cirg.himss.xds.soap.XDSSoapRequest;
import edu.washington.cirg.himss.xds.util.XDSConstants;
import edu.washington.cirg.himss.xds.util.XDSUtil;
import edu.washington.cirg.util.Util;

/** This servlet acts as a wrapper around classes that generate XDS Provide
 * and Register Document transactions.  These transactions are designed to allow
 * for the sharing of documents ( in this case Continuity Of Care Records ) 
 * between trading partners.  Documents are sent to an ebXML Repository.
 * @author <a href="mailto:ddrozd@u.washington.edu">Dan Drozd</a>
 * @version $Revision: 1.4 $
 *
 */
public class ProvideDocumentServlet extends HttpServlet {

	private static Log log = LogFactory.getLog(ProvideDocumentServlet.class);
	private static boolean LOG_PERFORMANCE_DATA = false; 

	private String DATASOURCE_NAME;
	/**
	 * 
	 */
	public ProvideDocumentServlet() {
		super();
	}
	
	public void init(ServletConfig config) throws ServletException 
	{	
		super.init(config);
		try {
			XDSConstants.loadConstants();
			LOG_PERFORMANCE_DATA = XDSConstants.LOG_PERFORMANCE_DATA;
			DATASOURCE_NAME = XDSConstants.PHR_DATASOURCE_NAME;
			
			if ( DATASOURCE_NAME == null || DATASOURCE_NAME.equals("") ) {
				throw new ServletException("Error initializing sevlet unable to determine datasource name");
			}

		} catch (XDSConfigurationException e) {
			log.error(e);
			throw new ServletException(e);
		}
	}
	
	public void doGet(HttpServletRequest req, HttpServletResponse resp)
	throws ServletException, IOException {
		doPost(req, resp);
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
    	String repositoryUrl = req.getParameter(XDSControllerServlet.TARGET_URL_PARAM);
    	String parentDocId = req.getParameter(XDSControllerServlet.PARENT_DOC_ID_PARAM);
    	String docAssociationType = req.getParameter(XDSControllerServlet.DOC_ASSOCIATION_TYPE_PARAM);
    	String folderName =  req.getParameter(XDSControllerServlet.FOLDER_NAME_PARAM);
    	String sendTwice = req.getParameter(XDSControllerServlet.SEND_TWICE_PARAM);
    	String sDontSend = req.getParameter(XDSControllerServlet.DONT_SEND_PARAM);
    	
    	boolean sendSOAP = true;
    	if ( sDontSend != null ) {
    		sendSOAP = false;
    	}
    	
    	if ( patientId == null || patientId.equals("") ) {
    		throw new ServletException("patientId param not passed to servlet");
    	}
    	if ( repositoryUrl == null || repositoryUrl.equals("") ) {
    		throw new ServletException("targetUrl param not passed to servlet");
    	}
    	
    	if ( docAssociationType != null ) {
    		if ( ! XDSConstants.isValidDocAssociationType(docAssociationType)) {
    			throw new ServletException("invalid docAssociationType passed to servlet");    		
    		}
    		if ( parentDocId == null || parentDocId.equals("") ) {
    			throw new ServletException("parentDocId param not passed to servlet");    			
    		}
    	}
    	
    	Document ccrDoc;
    	Connection conn = null;
    	try {
    		
    		if ( LOG_PERFORMANCE_DATA ) {//Begin CreateCCR
    			startTime = System.currentTimeMillis();
    		}

    		conn = Util.getConnection(DATASOURCE_NAME);
    		if ( conn == null ) {
    			log.error("Connection is null");
    			throw new SQLException("Unable to obtain a database connection");
    		}

    		ContinuityOfCareRecord ccr = 
    			new ContinuityOfCareRecord(patientId, XDSConstants.SYMBOLIC_DOC_NAME_ROOT, conn);
			//ccrDoc = ccr.getDocument();
			String docUniqueId = ccr.getDocUniqueId();

			if ( LOG_PERFORMANCE_DATA ) { //End CreateCCR
    			stopTime = System.currentTimeMillis();
    			log.info("TIMER,Generate CCR took," + (stopTime-startTime) + " ms");
    			startTime = System.currentTimeMillis();
			}//Begin ValidateCCR
			
			XDSUtil.printDOM(System.err, ccr.getDocument());

			Util.validate(XDSConstants.CCR_MSV_COMPILED_SCHEMA, ccr.getDocument());
			
			if ( LOG_PERFORMANCE_DATA ) { //End ValidateCCR
    			stopTime = System.currentTimeMillis();
    			log.info("TIMER,Validate CCR," + (stopTime-startTime) + " ms");
    			startTime = System.currentTimeMillis();
			}//Begin GenerateXDSRequest

			List xdsFileList = new ArrayList();
			xdsFileList.add(ccr);
			
			Document xdsRequestDoc = 
				RequestHelper.generateProvideDocRequest(patientId, ccr.getPatient().getPerson().getCurrentName(), xdsFileList, docAssociationType, parentDocId, folderName);

			if ( log.isDebugEnabled() ) {
				log.debug(xdsRequestDoc.asXML());
			}

			if ( LOG_PERFORMANCE_DATA ) {//End GenerateXDSRequest
    			stopTime = System.currentTimeMillis();
    			log.info("TIMER,Generate Provide Doc XDS Request," + (stopTime-startTime) + " ms");
    			startTime = System.currentTimeMillis();
			}//Start GenerateSOAPRequest

			if ( sendSOAP ) {
				
				XDSSoapRequest soapReq = 
					new XDSSoapRequest(xdsRequestDoc, xdsFileList, repositoryUrl, "1.1");
				
				if ( log.isDebugEnabled() ) {
					soapReq.printSOAPMessage(System.out);				
				}
				
				if ( LOG_PERFORMANCE_DATA ) {//End GenerateSOAPRequest
					stopTime = System.currentTimeMillis();
					log.info("TIMER,Generate Provide Doc SOAP Request," + (stopTime-startTime) + " ms");
					startTime = System.currentTimeMillis();
				}//Start SendSOAPRequest
				
				
				SOAPMessage response = soapReq.sendMessage();
				
				if ( LOG_PERFORMANCE_DATA ) {//End SendSOAPRequest
					stopTime = System.currentTimeMillis();
					log.info("TIMER,Send Provide Doc SOAP Request," + (stopTime-startTime) + " ms");
					startTime = System.currentTimeMillis();
				}//Start ParseReponseMessage
				
				if ( log.isDebugEnabled() ) {
					response.writeTo(System.out);
				}
				
				RegistryResponse rr = new RegistryResponse(response);
				if ( LOG_PERFORMANCE_DATA ) {//End ParseSOAPResponse
					stopTime = System.currentTimeMillis();
					log.info("TIMER,Parse Provide Doc SOAP Response Message," + (stopTime-startTime) + " ms");				
					startTime = System.currentTimeMillis();
				}//Start WriteHTMLResponse
				
				PrintWriter out = resp.getWriter();
				out.print("<HTML><BODY>");
				
				if ( ! rr.wasSuccessful() ) {
					out.print("<H2>Application Error #12</H2>");
					String error;
					for ( Iterator iter = rr.getErrorList().iterator(); iter.hasNext();) {
						error = (String)iter.next();
						log.error(error);
						out.print("<H4>" + error + "</H4>");
					}
				} else {
					out.print("<H2>CCR Successfully sent to repository " + repositoryUrl + "</H2>");
				}
				out.print("</BODY></HTML>");
				out.flush();
				if ( LOG_PERFORMANCE_DATA ) {//End WriteHTMLResponse
					stopTime = System.currentTimeMillis();
					log.info("TIMER,Parse SOAP Response Message," + (stopTime-startTime) + " ms");				
				}			
			} else { //We didn't send the SOAP message, this is for internal performance testing
				resp.getWriter().println("No message sent");
			}
		} catch (JaxenException e) {
			log.error(e);
			handleError(req, resp);
		} catch (NamingException e) {
			log.error(e);
			handleError(req, resp);
		} catch (SQLException e) {
			log.error(e);
			handleError(req, resp);
		} catch (XDSException e) {
			log.error(e);
			handleError(req, resp);
		} catch (DocumentException e) {
			log.error(e);
			handleError(req, resp);
		} catch (SOAPException e) {
			log.error(e);
			handleError(req, resp);
		} catch (IOException e) {
			log.error(e);
			handleError(req, resp);
		} catch (MessagingException e) {
			log.error(e);
			handleError(req, resp);
		} catch (SAXException e) {
			log.error(e);
			handleError(req, resp);
		} catch (VerifierConfigurationException e) {
			log.error(e);
			handleError(req, resp);
		} finally {
			try {
				Util.closeConnection(conn);
			} catch ( SQLException sqle ) {
				log.error(sqle);
			}
			if ( LOG_PERFORMANCE_DATA ) { //End RequestTimer
				stopTime = System.currentTimeMillis();
				log.info("TIMER,Provide Doc Request Total," + (stopTime-reqStart) + " ms");
			}
		}

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