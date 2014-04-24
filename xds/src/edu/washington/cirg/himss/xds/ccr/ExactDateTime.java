/*
 * Copyright 2004-2005 (C) University of Washington. All Rights Reserved.
 * Created on Dec 28, 2004
 * University of Washington, CIRG
 * $Id: ExactDateTime.java,v 1.3 2005/02/09 01:51:11 ddrozd Exp $
 */
package edu.washington.cirg.himss.xds.ccr;

import java.util.Date;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.dom4j.DocumentFactory;
import org.dom4j.Element;
import org.dom4j.QName;

import edu.washington.cirg.himss.xds.util.XDSUtil;

/** Java implementation of the Continuity of Care (CCR) document object model
 * @author <a href="mailto:ddrozd@u.washington.edu">Dan Drozd</a>
 * @version $Revision: 1.3 $
 *
 */

public class ExactDateTime extends CCRDateTime {
	
	private static Log log = LogFactory.getLog(ExactDateTime.class);
	
	private static final QName DATE_TIME_TYPE_QNAME = 
		DocumentFactory.getInstance().createQName("DateTimeType", CCRConstants.XMLNS_CCR);	

	private static final QName EXACT_DATE_TIME_QNAME = 
		DocumentFactory.getInstance().createQName("ExactDateTime", CCRConstants.XMLNS_CCR);	
	
	
	
	private String dateTimeType;
	private String xdsDateString;	


	public ExactDateTime(Date date, String dateTimeType) {
		this.dateTimeType = dateTimeType;
		xdsDateString = XDSUtil.dateToXdsDateString(date, XDSUtil.CCR_FORMAT);
	}
	
	public Element getElement() {
		
		Element dtElement = 
			DocumentFactory.getInstance().createElement(DATE_TIME_QNAME);
		
		if ( dateTimeType != null ) {
			Element dttElement =
				DocumentFactory.getInstance().createElement(DATE_TIME_TYPE_QNAME)
				.addText(dateTimeType);
			dtElement.add(dttElement);
		}
		
		Element edtElement =
			DocumentFactory.getInstance().createElement(EXACT_DATE_TIME_QNAME)
				.addText(xdsDateString);
			dtElement.add(edtElement);
			
		return dtElement;
	}
}
