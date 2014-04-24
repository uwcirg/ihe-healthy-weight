/*
 * Copyright 2004-2005 (C) University of Washington. All Rights Reserved.
 * Created on Dec 28, 2004
 * University of Washington, CIRG
 * $Id: Insurer.java,v 1.3 2005/01/26 19:45:44 ddrozd Exp $
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
 * @version $Revision: 1.3 $
 *
 */
public class Insurer extends CCRDataObject {

	private static Log log = LogFactory.getLog(Insurer.class);

	private static final QName INSURER_QNAME = 
		DocumentFactory.getInstance().createQName("Insurer", CCRConstants.XMLNS_CCR);		
	private static final QName INSURANCE_TYPE_QNAME = 
		DocumentFactory.getInstance().createQName("InsuranceType", CCRConstants.XMLNS_CCR);	
	private static final QName INSURANCE_PROVIDER_QNAME = 
		DocumentFactory.getInstance().createQName("InsuranceProvider", CCRConstants.XMLNS_CCR);	

	public static final String PRIMARY_INS_TYPE = "Primary Health Insurance";
	public static final String SUPPL_INS_TYPE = "Supplemental Health Insurance";
	
	private static final String staticQuery =
		"SELECT i.insuranceType AS 'insuranceType', " + 
		"o1.name AS 'providerName', " + 
		"d.line1 AS 'addrLine1', " +
		"d.line2 AS 'addrLine2', " +
		"d.city AS 'city', " + 
		"d.state AS 'state', " + 
		"d.country AS 'country', " + 
		"d.postalCode AS 'postalCode', " + 
		"w.value AS 'workPhone', " +
		"f.value AS 'faxPhone'" +
		"FROM actors a, insurers i, addresses d, " +
		"organizations o1 LEFT JOIN communications w ON o1.actorID = w.actorID AND w.type = 'workTelephone', " +
		"organizations o2 LEFT JOIN communications f ON o2.actorID = f.actorID AND f.type = 'Fax' " +
		"WHERE a.patientID = '" + CCRConstants.UNIQUE_ID_TOKEN + "' " + 
		"AND a.id = i.actorID " + 
		"AND o1.actorID = d.actorID " + 
		"AND d.type = 'org' " +
		"AND i.organizationID = o1.actorID " +
		"AND o1.actorID = o2.actorID";

	
	private String insuranceType;
	private InsuranceProvider provider;

	/**
	 * @param insuranceType
	 * @param insuranceProviderOrgName
	 */
	public Insurer(String insuranceType, String insuranceProviderOrgName) {
		this.insuranceType = insuranceType;
		this.provider = new InsuranceProvider(insuranceProviderOrgName);
	}
	
	public void addAddress(Address address) {
		provider.addAddress(address);
	}
	
	public void addPhone(TelephoneNumber number) {
		provider.addTelephoneNumber(number);
	}
	
	public static List getInsurers(String patientId, Connection conn) 
		throws SQLException, XDSException {
		String query = staticQuery.replaceAll(CCRConstants.UNIQUE_ID_TOKEN, patientId);		
		List insurerList = new ArrayList();
		Statement stmt = null;
		ResultSet rs = null;

		String providerName = null;
		String insuranceType = null;
		String faxPhone = null;
		String workPhone = null;
		String addrLine1 = null;
		String addrLine2 = null;
		String city = null;
		String state = null;
		String country = null;
		String postalCode = null;
		
		if ( log.isDebugEnabled() ) {
			log.debug(query);
		}
		try {
			stmt = conn.createStatement();
			rs = stmt.executeQuery(query);
			while ( rs.next() ) {
				insuranceType = rs.getString("insuranceType");
				providerName = rs.getString("providerName");
				faxPhone = rs.getString("faxPhone");
				workPhone = rs.getString("workPhone");
				addrLine1 = rs.getString("addrLine1");
				addrLine2 = rs.getString("addrLine2");				
				city = rs.getString("city");
				state = rs.getString("state");
				country = rs.getString("country");
				postalCode = rs.getString("postalCode");
				if ( providerName == null ) {
					throw new XDSException("providerName column returned null");					
				}
				
				if ( ! providerName.trim().equals("") && ! insuranceType.trim().equals("")) {
					Insurer ins = new Insurer(insuranceType, providerName);
				
					ins.addAddress(new Address(addrLine1, addrLine2, city, state, country, postalCode));
					if ( workPhone != null && ! workPhone.trim().equals("")) {
						ins.addPhone(new TelephoneNumber(workPhone, "Work"));
					}
					if ( faxPhone != null && ! faxPhone.trim().equals("")) {
						ins.addPhone(new TelephoneNumber(faxPhone, "Fax"));
					}
					insurerList.add(ins);
				}
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
		return insurerList;
	}
	
	public InsuranceProvider getProvider() {
		return provider;
	}
	
	
	public Element getElement() {
		Element element = 
			DocumentFactory.getInstance().createElement(INSURER_QNAME);

		element.add(ccrDataObjectId.getElement());

		if ( insuranceType != null ) {
			element.addElement(INSURANCE_TYPE_QNAME).addText(insuranceType);
		}
		element.add(provider.getElement());
		return element;
	}
}
