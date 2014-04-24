/*
 * Copyright 2004-2005 (C) University of Washington. All Rights Reserved.
 * Created on Dec 28, 2004
 * University of Washington, CIRG
 * $Id: DateTimeRange.java,v 1.3 2005/01/26 19:45:44 ddrozd Exp $
 */
package edu.washington.cirg.himss.xds.ccr;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.dom4j.DocumentFactory;
import org.dom4j.Element;
import org.dom4j.QName;

/** Java implementation of the Continuity of Care (CCR) document object model
 * @author <a href="mailto:ddrozd@u.washington.edu">Dan Drozd</a>
 * @version $Revision: 1.3 $
 *
 */


public class DateTimeRange extends CCRDateTime {

	private static Log log = LogFactory.getLog(DateTimeRange.class);
	private static final QName DATE_TIME_RANGE_QNAME = 
		DocumentFactory.getInstance().createQName("DateTimeRange", CCRConstants.XMLNS_CCR);	
	private static final QName BEGIN_RANGE_QNAME = 
		DocumentFactory.getInstance().createQName("BeginRange", CCRConstants.XMLNS_CCR);	
	private static final QName END_RANGE_QNAME = 
		DocumentFactory.getInstance().createQName("EndRange", CCRConstants.XMLNS_CCR);	
	
	
	
	private CCRDateTime beginDate;
	private CCRDateTime endDate;

	/**
	 * @param beginDate
	 * @param endDate
	 */
	public DateTimeRange(CCRDateTime beginDate, CCRDateTime endDate) {
		this.beginDate = beginDate;
		this.endDate = endDate;
	}
	
	public Element getElement() {
		
		Element dtElement = 
			DocumentFactory.getInstance().createElement(DATE_TIME_QNAME);
		
		Element dtrElement =
			DocumentFactory.getInstance().createElement(DATE_TIME_RANGE_QNAME);
		
		Element beginRangeElement =
			DocumentFactory.getInstance().createElement(BEGIN_RANGE_QNAME);
		beginRangeElement.add(beginDate.getElement());
		dtrElement.add(beginRangeElement);

		Element endRangeElement =
			DocumentFactory.getInstance().createElement(END_RANGE_QNAME);
		endRangeElement.add(endDate.getElement());
		dtrElement.add(endRangeElement);
		
		dtElement.add(dtrElement);
			
		return dtElement;
	}
}