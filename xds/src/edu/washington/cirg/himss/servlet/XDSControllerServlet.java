/*
 * Copyright 2004-2005 (C) University of Washington. All Rights Reserved.
 * Created on Jan 3, 2005
 * University of Washington, CIRG
 * $Id: XDSControllerServlet.java,v 1.6 2005/02/09 01:51:11 ddrozd Exp $
 */
package edu.washington.cirg.himss.servlet;


import java.io.IOException;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/** This servlet is a simple controller servlet that passes work requests onto other
 * servlets.
 * @author <a href="mailto:ddrozd@u.washington.edu">Dan Drozd</a>
 * @version $Revision: 1.6 $
 *
 */
public class XDSControllerServlet extends HttpServlet {
	
	private static Log log = LogFactory.getLog(XDSControllerServlet.class);

	private static final String PROVIDE_DOC_REQ = "provideDocReq";
	private static final String QUERY_REGISTRY_REQ = "queryRegistryReq";
	private static final String RETRIEVE_DOC_REQ = "getDocReq";
	private static final String FIND_DOCS_REQ = "findDocsReq";
	private static final String PDQ_FIND_PATS_REQ = "pdqFindPatsReq";
	private static final String VFM_FIND_DOCS_REQ = "vfmFindDocsReq";
	private static final String HW_FIND_DOCS_REQ = "hwFindDocsReq";
	private static final String TEST_REQ = "testReq";
	
	private static final String PROVIDE_DOC_URL = "ProvideDocument";
	private static final String QUERY_REGISTRY_URL = "QueryRegistry";
	private static final String RETRIEVE_DOC_URL = "RetrieveExternalLink";
	private static final String FIND_DOCS_URL = "FindDocuments";
	private static final String PDQ_FIND_PATS_URL = "PDQFindPatients";
	private static final String VFM_FIND_DOCS_URL = "VFMFindDocuments";
	private static final String HW_FIND_DOCS_URL = "HWFindDocuments";
	private static final String TEST_URL = "Test";

	private static final String REQ_TYPE_PARAM = "reqtype";
	static final String TARGET_URL_PARAM = "targetUrl";
	static final String VFM_URL_PARAM = "vfmUrl";
	static final String VFM_CARD_URL_PARAM = "vfmCardUrl";
	static final String PIX_MANAGER_URL_PARAM = "pixUrl";
	static final String ATNA_URL_PARAM = "atnaUrl";
	static final String REPO_URL_PARAM = "repoUrl";
	static final String PATIENT_ID_PARAM = "patientId";
	static final String OBJECT_REF_ID_PARAM = "objectRefId";
	static final String PARENT_DOC_ID_PARAM = "parentDocId";
	static final String DOC_ASSOCIATION_TYPE_PARAM = "docAssociationType";
	static final String FOLDER_NAME_PARAM = "folderName";
	static final String SEND_TWICE_PARAM = "sendTwice";
	static final String DONT_SEND_PARAM = "dontSend";
	static final String TIME_SLOT_PARAM = "timeSlot";
	static final String START_TIME_PARAM = "startTime";
	static final String STOP_TIME_PARAM = "stopTime";
	static final String USE_PH_PARAM = "usePH";
	static final String USE_TLS_PARAM = "useTls";
	static final String LIST_DOCS_PARAM = "listDocs";
	static final String PDQ_PID_LIST_PARAM = "pidList";
	static final String PDQ_PAT_NAME_PARAM = "patName";
	static final String PDQ_PAT_DOB_PARAM = "patDob";
	static final String PDQ_PAT_ADDR_PARAM = "patAddr";
	static final String PDQ_PAT_SEX_PARAM = "patSex";
	static final String PDQ_PAT_ACCT_PARAM = "patAcct";
	static final String TEST_ACTION_PARAM = "testAction";
	

	
	static final String ERROR_HANDLER = "/error.jsp";
	
	public void init(ServletConfig config) throws ServletException 
	{
		super.init();
    }
    
    public void doPost(HttpServletRequest req, HttpServletResponse resp)
    	throws ServletException, IOException {
    	
    	String requestType = req.getParameter(REQ_TYPE_PARAM);
    	String uniqueId = req.getParameter(PATIENT_ID_PARAM);
    	String remoteAddr = req.getRemoteAddr();
    	int remotePort = req.getRemotePort();
    	req.setAttribute("option", "forward");
		RequestDispatcher dispatcher;
    	
		if ( requestType == null ) {
			throw new ServletException("Null requestType provided");
		}
		
    	if ( requestType.equals(PROVIDE_DOC_REQ)) {
    		dispatcher = req.getRequestDispatcher(PROVIDE_DOC_URL);
    		dispatcher.forward(req, resp);
    	} else if  (requestType.equals(QUERY_REGISTRY_REQ)) {
    		dispatcher = req.getRequestDispatcher(QUERY_REGISTRY_URL);
    		dispatcher.forward(req, resp);    		
    	} else if (requestType.equals(RETRIEVE_DOC_REQ)) {
    		dispatcher = req.getRequestDispatcher(RETRIEVE_DOC_URL);
    		dispatcher.forward(req, resp);
    	} else if (requestType.equals(FIND_DOCS_REQ)) {
    		dispatcher = req.getRequestDispatcher(FIND_DOCS_URL);
    		dispatcher.forward(req, resp);
    	} else if (requestType.equals(PDQ_FIND_PATS_REQ)) {
    		dispatcher = req.getRequestDispatcher(PDQ_FIND_PATS_URL);
    		dispatcher.forward(req, resp);
    	} else if (requestType.equals(VFM_FIND_DOCS_REQ)) {
    		dispatcher = req.getRequestDispatcher(VFM_FIND_DOCS_URL);
    		dispatcher.forward(req, resp);
    	} else if (requestType.equals(HW_FIND_DOCS_REQ)) {
    		dispatcher = req.getRequestDispatcher(HW_FIND_DOCS_URL);
    		dispatcher.forward(req, resp);
    	} else if (requestType.equals(TEST_REQ)) {
    		dispatcher = req.getRequestDispatcher(TEST_URL);
    		dispatcher.forward(req, resp);
    	} else {
    		throw new ServletException("Unknown request type " + requestType);
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


