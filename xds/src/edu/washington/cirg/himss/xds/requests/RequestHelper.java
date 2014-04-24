/*
 * Copyright 2004-2005 (C) University of Washington. All Rights Reserved.
 * Created on Jan 5, 2005
 * University of Washington, CIRG
 * $Id: RequestHelper.java,v 1.2 2005/02/21 16:43:25 ddrozd Exp $
 */
package edu.washington.cirg.himss.xds.requests;

import java.util.Iterator;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.dom4j.Document;
import org.dom4j.DocumentException;

import edu.washington.cirg.himss.xds.XDSDocument;
import edu.washington.cirg.himss.xds.XDSException;
import edu.washington.cirg.himss.xds.XDSFile;
import edu.washington.cirg.himss.xds.ccr.Name;
import edu.washington.cirg.himss.xds.util.XDSConstants;
import edu.washington.cirg.himss.xds.util.XDSUtil;

/** Static helper class used to encapsulate UW standards in an XDS request
 * @author <a href="mailto:ddrozd@u.washington.edu">Dan Drozd</a>
 * @version $Revision: 1.2 $
 *
 */
public class RequestHelper {

	private static Log log = LogFactory.getLog(RequestHelper.class);

	/**
	 * 
	 */
	private RequestHelper() {
		super();
	}
	
    public static Document generateProvideDocRequest(String patientId, Name author, 
    		List xdsFileList, String docAssociationType, String parentDocId, String folderName)
    	throws DocumentException, XDSException {
    	
    	String authorPerson = XDSUtil.getHL7Name(author);
    	
		String serviceStartTime = null;
		String serviceStopTime = null;
		String[] legalAuthenticatorList = {};
		String[] sourcePatientInfoList = {""};
    	String sourcePatientId = patientId;
    	
    	String creationTime = XDSUtil.currentXdsDate(XDSUtil.XDS_FORMAT);
    	serviceStartTime = XDSUtil.currentXdsDate(XDSUtil.XDS_FORMAT);
    	serviceStopTime = XDSUtil.currentXdsDate(XDSUtil.XDS_FORMAT);
    	
    	
    	ProvideAndRegisterDocRequest xdsreq = 
			new ProvideAndRegisterDocRequest(patientId);
    	
    	if ( folderName != null && ! folderName.equals("")) {
    		xdsreq.createFolder(folderName, null);
    	}
    	
    	xdsreq.setDescription(XDSConstants.CCR_SUBMISSION_SET_DESC);
		xdsreq.setAuthorPerson(authorPerson);
		
		Iterator iterator = xdsFileList.iterator();
		
		while ( iterator.hasNext() ) {
			XDSFile file = (XDSFile)iterator.next();
			XDSDocument xdsdoc = new XDSDocument(XDSConstants.CCR_DOCUMENT_TITLE, 
	    			patientId,
					XDSConstants.CCR_AUTHOR_DEPARTMENT, 
					XDSConstants.CCR_AUTHOR_INSTITUTION, 
					authorPerson, 
					serviceStartTime,
					serviceStopTime, 
					XDSConstants.CCR_DE_LANGUAGE_CODE, 
					creationTime,
					legalAuthenticatorList, 
					sourcePatientId,
					sourcePatientInfoList,
					XDSConstants.CCR_DE_CLASS_CODE,
					XDSConstants.CCR_DE_TYPE_CODE, 
					XDSConstants.CCR_DE_PRACTICE_SETTING_CODE,
					new String[] {}, 
					XDSConstants.CCR_DE_CONFIDENTIALITY_CODE,
					XDSConstants.CCR_DE_HEALTH_CARE_FACILITY_TYPE_CODE, 
					XDSConstants.CCR_DE_FORMAT_CODE,
					file);
	    	if ( docAssociationType != null && ! docAssociationType.equals("")) {
	    		xdsdoc.setDocumentAssociation(docAssociationType, parentDocId);
	    	}
	    	if ( folderName != null && ! folderName.equals("")) {
	    		xdsreq.addXDSDocument(folderName, xdsdoc);
	    	} else {
	    		xdsreq.addXDSDocument(xdsdoc);
	    	}
		}
		return xdsreq.generateRequest();		
    }

}


