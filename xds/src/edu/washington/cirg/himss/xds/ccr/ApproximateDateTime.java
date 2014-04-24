/*
 * Copyright 2004-2005 (C) University of Washington. All Rights Reserved.
 * Created on Dec 28, 2004
 * University of Washington, CIRG
 * $Id: ApproximateDateTime.java,v 1.2 2005/01/26 19:45:44 ddrozd Exp $
 */
package edu.washington.cirg.himss.xds.ccr;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.dom4j.DocumentFactory;
import org.dom4j.Element;
import org.dom4j.QName;

/** Java implementation of the Continuity of Care (CCR) document object model
 * @author <a href="mailto:ddrozd@u.washington.edu">Dan Drozd</a>
 * @version $Revision: 1.2 $
 *
 */
public class ApproximateDateTime extends CCRDateTime {
	
	private static Log log = LogFactory.getLog(ApproximateDateTime.class);

	private static final QName APPROX_DATE_TIME_QNAME = 
		DocumentFactory.getInstance().createQName("ApproximateDateTime", CCRConstants.XMLNS_CCR);	

	private static final QName TEXT_QNAME = 
		DocumentFactory.getInstance().createQName("Text", CCRConstants.XMLNS_CCR);	

	private String approxDateTime;
	private boolean isDOB;
	
	/**
	 * @param approxDateTime
	 * @param isDOB
	 */
	public ApproximateDateTime(String approxDateTime, boolean isDOB) {
		this.approxDateTime = approxDateTime;
		this.isDOB = isDOB;
	}
	
	public ApproximateDateTime(String approxDateTime) {
		this.approxDateTime = approxDateTime;
		this.isDOB = false;
	}
	

	public Element getElement() {
		
		Element dtElement = null;
				
		Element adtElement =
			DocumentFactory.getInstance().createElement(APPROX_DATE_TIME_QNAME);
		Element textElement =
			DocumentFactory.getInstance().createElement(TEXT_QNAME)
			.addText(approxDateTime);
		adtElement.add(textElement);
		
		if ( isDOB ) {
			return adtElement;
		} else {
			dtElement = DocumentFactory.getInstance().createElement(DATE_TIME_QNAME);
			dtElement.add(adtElement);
			return dtElement;
		}
	}
}
