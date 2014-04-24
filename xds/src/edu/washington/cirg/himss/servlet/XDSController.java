/*
 * Created on Jan 3, 2005
 * University of Washington, CIRG
 */
package edu.washington.cirg.himss.servlet;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * @author ddrozd
 *
 */
public class XDSController extends HttpServlet {
	
	private static Log log = LogFactory.getLog(XDSController.class);

	private static final String REQ_TYPE_PARAM = "reqtype";
	static final String PATIENT_ID_PARAM = "patientId";
	
	public void init(ServletConfig config) throws ServletException 
	{
    }
    
    public void doPost(HttpServletRequest req, HttpServletResponse resp)
    	throws ServletException {
    	
    	String requestType = req.getParameter(REQ_TYPE_PARAM);
    	String uniqueId = req.getParameter(PATIENT_ID_PARAM);
    	String remoteAddr = req.getRemoteAddr();
    	int remotePort = req.getRemotePort();
    	
    	/*
    	String targetURL = "";
    	req.setAttribute("option", "forward");
    	RequestDispatcher dispatcher = req.getRequestDispatcher(targetURL);
    	dispatcher.forward(req, resp);
    	*/        
    }

    public void doGet(HttpServletRequest req, HttpServletResponse resp)
    	throws ServletException {
    		doPost(req, resp);
    }
}


