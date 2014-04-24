/*
 * Copyright 2004-2005 (C) University of Washington. All Rights Reserved.
 * Created on Dec 28, 2004
 * University of Washington, CIRG
 * $Id: Medication.java,v 1.5 2005/02/09 01:51:11 ddrozd Exp $
 */
package edu.washington.cirg.himss.xds.ccr;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.dom4j.DocumentFactory;
import org.dom4j.Element;
import org.dom4j.QName;

import edu.washington.cirg.himss.xds.XDSException;

/** Java implementation of the Continuity of Care (CCR) document object model
 * @author <a href="mailto:ddrozd@u.washington.edu">Dan Drozd</a>
 * @version $Revision: 1.5 $
 *
 */
public class Medication extends CCRDataObject {
	
	private static Log log = LogFactory.getLog(Medication.class);

	private static final QName MEDICATION_QNAME = 
		DocumentFactory.getInstance().createQName("Medication", CCRConstants.XMLNS_CCR);	

	private static final String staticQuery = 
		"SELECT m.productName AS 'productName', " + 
		"m.brandName AS 'brandName', " +
		"m.quantity AS 'quantity', " +
		"m.dateStarted AS 'startDate', " +
        "m.doseStrength AS 'doseStrength', " +
		"m.dateStopped AS 'stopDate' " +
		"FROM actors a, medications m " + 
		"WHERE a.patientID = '" + CCRConstants.UNIQUE_ID_TOKEN + "' " + 
		"AND m.actorID = a.id " +
		"AND m.productType = 'Medication'";

	private Product product;
	
	
	/**
	 * @param medDateTime
	 * @param product
	 */
	public Medication(Product product, CCRDateTime medDateTime) {
		this.product = product;
		product.setDateTime(medDateTime);
	}
	
	public Product getProduct() {
		return product;
	}
	
	public Element getElement() {
		Element element = 
			DocumentFactory.getInstance().createElement(MEDICATION_QNAME);
		element.add(ccrDataObjectId.getElement());
		element.add(product.getElement());
		
		return element;
	}
	
	public static List getMedications(String patientId, Connection conn) 
	throws SQLException, XDSException {
		String query = staticQuery.replaceAll(CCRConstants.UNIQUE_ID_TOKEN, patientId);		
		List list = new ArrayList();
		Statement stmt = null;
		ResultSet rs = null;
		
		String productName = null;
		String brandName = null;
		String quantity = null;
		String startDate = null;
		String stopDate = null;
        String doseStrength = null;
		
		try {
			stmt = conn.createStatement();
			rs = stmt.executeQuery(query);
			while ( rs.next() ) {
				doseStrength = rs.getString("doseStrength");
				productName = rs.getString("productName");
				brandName = rs.getString("brandName");
				quantity = rs.getString("quantity");
				startDate = rs.getString("startDate");
				stopDate = rs.getString("stopDate");
				Product product = new Product(Product.MEDICATION_PRODUCT, productName, brandName, doseStrength);
				DateTimeRange dateRange = new DateTimeRange(new ApproximateDateTime(startDate, true), new ApproximateDateTime(stopDate, true));
				Medication med = new Medication(product, dateRange);
				list.add(med);
			}
			
			rs.close();
			rs = null;
			stmt.close();
			stmt = null;
		} catch ( SQLException sqle ) {
			log.error(sqle);
			throw sqle;
		} finally {
			if ( rs != null ) {
				try { rs.close(); } catch ( SQLException sqle ) { ; }
			}
			if ( stmt != null ) {
				try { stmt.close(); } catch ( SQLException sqle ) { ; }
			}
		}
		return list;
	}
	
}
