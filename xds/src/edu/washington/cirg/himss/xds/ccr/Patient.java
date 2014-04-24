/*
 * Copyright 2004-2005 (C) University of Washington. All Rights Reserved.
 * Created on Dec 28, 2004
 * University of Washington, CIRG
 * $Id: Patient.java,v 1.5 2005/02/13 17:54:55 ddrozd Exp $
 */
package edu.washington.cirg.himss.xds.ccr;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.dom4j.DocumentFactory;
import org.dom4j.Element;
import org.dom4j.QName;

import edu.washington.cirg.himss.xds.XDSException;
import edu.washington.cirg.himss.xds.util.XDSConstants;

/** Java implementation of the Continuity of Care (CCR) document object model
 * @author <a href="mailto:ddrozd@u.washington.edu">Dan Drozd</a>
 * @version $Revision: 1.5 $
 *
 */
public class Patient extends CCRDataObject {
	
	private static Log log = LogFactory.getLog(Patient.class);

	private static final QName PATIENT_QNAME = 
		DocumentFactory.getInstance().createQName("Patient", CCRConstants.XMLNS_CCR);	
	private static final QName EXTERNAL_IDENTIFIERS_QNAME = 
		DocumentFactory.getInstance().createQName("ExternalIdentifiers", CCRConstants.XMLNS_CCR);	
	private static final QName ROLES_QNAME = 
		DocumentFactory.getInstance().createQName("Roles", CCRConstants.XMLNS_CCR);	
	private static final QName ROLE_QNAME = 
		DocumentFactory.getInstance().createQName("Role", CCRConstants.XMLNS_CCR);	

	private static final String staticQuery =
		"SELECT DISTINCT a.uwNetID AS 'uwNetID', p.givenName AS 'givenName', " +
		"p.middleName AS 'middleName', " +
		"p.lastName AS 'lastName', " +
		"p.suffix AS 'suffix', " +
		"p.title AS 'title', " +
		"p.dateOfBirth AS 'dateOfBirth'," + 
		"p.gender AS 'gender'," + 
		"p.language AS 'language', " +
		"p.proficiency AS 'proficiency', " +
		"(SELECT c.value FROM communications AS c, actors AS a WHERE c.type = 'homeTelephone' AND c.actorID = a.id AND a.patientID = '" + CCRConstants.UNIQUE_ID_TOKEN + "') AS 'homePhone', " + 
		"(SELECT c.value FROM communications c, actors a WHERE c.type = 'workTelephone' AND c.actorID = a.id AND a.patientID = '" + CCRConstants.UNIQUE_ID_TOKEN + "') AS 'workPhone', " +
		"(SELECT c.value FROM communications c, actors a WHERE c.type = 'mobileTelephone' AND c.actorID = a.id AND a.patientID = '" + CCRConstants.UNIQUE_ID_TOKEN + "') AS 'mobilePhone', " + 
		"(SELECT c.value FROM communications c, actors a WHERE c.type = 'EMail' AND c.actorID = a.id AND a.patientID = '" + CCRConstants.UNIQUE_ID_TOKEN + "') AS 'emailAddr', " +
		"d.line1 AS 'addrLine1', " +
		"d.city AS 'city', " +
		"d.state AS 'state', " +
		"d.country AS 'country', " +
		"d.postalCode AS 'postalCode' " + 
		"FROM persons p, actors a " + 
		"LEFT JOIN addresses d on a.id = d.actorId " +
		"WHERE a.patientID = '" + CCRConstants.UNIQUE_ID_TOKEN + "'" + 
		"AND p.actorID = a.id " +
		"AND ( d.type = 'home' OR d.type is NULL )";
	
	private Person person;
	private List externalIdentifierList;
	private List addressList;
	private List telephoneNumberList;
	private String role;
	private String patientId;
	private String sourcePatientId;
	
	/**
	 * @param person
	 * @param externalIdentifierList
	 * @param addressList
	 * @param telephoneNumberList
	 * @param role
	 */
	public Patient(Person person,
			List externalIdentifierList, List addressList,
			List telephoneNumberList, String role) {
		this.person = person;
		this.externalIdentifierList = externalIdentifierList;
		this.addressList = addressList;
		this.telephoneNumberList = telephoneNumberList;
		this.role = role;
	}
	
	public Patient(Person person, String role) {
		this();
		this.person = person;
		this.role = role;
	}
	
	public Patient() {
		this.externalIdentifierList = new ArrayList();
		this.addressList = new ArrayList();
		this.telephoneNumberList = new ArrayList();		
	}
	
	public Patient(String patientId, Connection conn) 
	throws SQLException, XDSException {
		this();
		this.patientId = patientId;
		String query = staticQuery.replaceAll(CCRConstants.UNIQUE_ID_TOKEN, patientId);
		Statement stmt = null;
		ResultSet rs = null;

		String givenName = null;
		String middleName = null;
		String lastName = null;
		String suffix = null;
		String title = null;
		String dob = null;
		String gender = null; 
		String language = null;
		String proficiency = null;
		String homePhone = null;
		String workPhone = null;
		String mobilePhone = null;
		String email = null;
		String addrLine1 = null;
		String city = null;
		String state = null;
		String country = null;
		String postalCode = null;
		String uwNetID = null;

		
		try {
			stmt = conn.createStatement();
			rs = stmt.executeQuery(query);
			while ( rs.next() ) {
				uwNetID = rs.getString("uwNetID");
				givenName = rs.getString("givenName");
				middleName = rs.getString("middleName");
				lastName = rs.getString("lastName");
				suffix = rs.getString("suffix");
				title = rs.getString("title");
				dob = rs.getString("dateOfBirth");
				gender = rs.getString("gender");
				language = rs.getString("language");
				proficiency = rs.getString("proficiency");
				homePhone = rs.getString("homePhone");
				workPhone = rs.getString("workPhone");
				mobilePhone = rs.getString("mobilePhone");
				email = rs.getString("emailAddr");
				addrLine1 = rs.getString("addrLine1");
				city = rs.getString("city");
				state = rs.getString("state");
				country = rs.getString("country");
				postalCode = rs.getString("postalCode");
			}
			if ( givenName == null ) {
				throw new XDSException("No user information found for patient Id " + patientId);
			}
			rs.close();
			rs = null;
			stmt.close();
			stmt = null;
			this.person = new Person(new Name(Name.CURRENTNAME_TYPE, givenName, middleName, lastName, suffix, title, null));
			person.setDateOfBirth(new ApproximateDateTime(dob,  true));

			person.setGender(gender);
			person.addLanguage(new Language(language, proficiency));
			this.role = "Patient";
			StringBuffer sb = new StringBuffer();
			sb.append(XDSConstants.VENDOR_CODE);
			sb.append("-");
			sb.append(uwNetID);
			sb.append("^^^&");
			sb.append(XDSConstants.ROOT_OID_PART);
			sb.append("&ISO^PI");
			
			this.sourcePatientId = sb.toString();
			sb = null;
			
			this.addAddress(new Address(addrLine1, null, city, state, country, postalCode));
			if ( homePhone != null && ! homePhone.trim().equals("")) {
				this.addTelephone(new TelephoneNumber(homePhone, "Home"));
			}
			if ( workPhone != null && ! workPhone.trim().equals("")) {
				this.addTelephone(new TelephoneNumber(workPhone, "Work"));
			}
			if ( mobilePhone != null && ! mobilePhone.trim().equals("")) {
				this.addTelephone(new TelephoneNumber(mobilePhone, "Mobile"));
			}
			
			this.addExternalIdentifier(new ExternalID(patientId, "Patient ID", null, null));

			
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
		
	}
	
	
	public Person getPerson() {
		return person;
	}
	
	public String getSourcePatientId() {
		return sourcePatientId;
	}
	
	public void addExternalIdentifier(ExternalID ei) {
		externalIdentifierList.add(ei);
	}
	
	public void addTelephone(TelephoneNumber tn) {
		telephoneNumberList.add(tn);
	}
	
	public void addAddress(Address address) {
		addressList.add(address);
	}

	
	public Element getElement() {

		Element patientElement = 
			DocumentFactory.getInstance().createElement(PATIENT_QNAME);
		patientElement.add(ccrDataObjectId.getElement());

		patientElement.add(person.getElement());
		Iterator iterator = null;
		if ( externalIdentifierList.size() > 0 ) {
			
			Element externalIdentifiers =
				DocumentFactory.getInstance().createElement(EXTERNAL_IDENTIFIERS_QNAME);
			
			iterator = externalIdentifierList.iterator();
			
			while ( iterator.hasNext() ) {
				ExternalID ei = (ExternalID)iterator.next();
				externalIdentifiers.add(ei.getElement());
			}
			patientElement.add(externalIdentifiers);
			iterator = null;
		}
		iterator = addressList.iterator();
		while ( iterator.hasNext() ) {
			Address address = (Address)iterator.next();
			patientElement.add(address.getElement());
		}
		iterator = null;
		iterator = telephoneNumberList.iterator();
		while ( iterator.hasNext() ) {
			TelephoneNumber phone = (TelephoneNumber)iterator.next();
			patientElement.add(phone.getElement());
		}
		iterator = null;
		if ( role != null ) {
			patientElement.addElement(ROLES_QNAME)
				.addElement(ROLE_QNAME)
				.addText(role);
		}
		return patientElement;
	}

}
