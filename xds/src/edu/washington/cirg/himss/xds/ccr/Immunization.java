/*
 * Copyright 2004-2005 (C) University of Washington. All Rights Reserved.
 * Created on Dec 28, 2004
 * University of Washington, CIRG
 * $Id: Immunization.java,v 1.4 2005/02/09 01:51:11 ddrozd Exp $
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
 * @version $Revision: 1.4 $
 *
 */
public class Immunization extends CCRDataObject {

	private static Log log = LogFactory.getLog(Immunization.class);

	
	private static final QName IMMUNIZATION_QNAME = 
		DocumentFactory.getInstance().createQName("Immunization", CCRConstants.XMLNS_CCR);	

	private static final String staticQuery = 
		"SELECT i.type AS 'description', " + 
		"i.date AS 'date', " +
		"i.product AS 'productName' " +
		"FROM actors a, immunizations i " + 
		"WHERE a.patientID = '" + CCRConstants.UNIQUE_ID_TOKEN + "' " + 
		"AND i.actorID = a.id ";

	
	private Product product;
	
	public Immunization(Product product, CCRDateTime ccrDateTime) {
		this.product = product;
		product.setDateTime(ccrDateTime);
	}
	
	public Product getProduct() {
		return product;
	}
	
	public Element getElement() {
		Element element = 
			DocumentFactory.getInstance().createElement(IMMUNIZATION_QNAME);
		element.add(ccrDataObjectId.getElement());
		element.add(product.getElement());
		
		return element;
	}
	
	public static List getImmunizations(String patientId, Connection conn) 
	throws SQLException, XDSException {
		String query = staticQuery.replaceAll(CCRConstants.UNIQUE_ID_TOKEN, patientId);		
		List list = new ArrayList();
		Statement stmt = null;
		ResultSet rs = null;
		
		String productName = null;
		String date = null;
		String description = null;
		try {
			stmt = conn.createStatement();
			rs = stmt.executeQuery(query);
			while ( rs.next() ) {
				productName = rs.getString("productName");
				description = rs.getString("description");
				date = rs.getString("date");
				Product product = new Product(Product.IMMUNIZATION_PRODUCT, productName);
				ApproximateDateTime immDate = new ApproximateDateTime(date);
				Immunization imm = new Immunization(product, immDate);
				list.add(imm);
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