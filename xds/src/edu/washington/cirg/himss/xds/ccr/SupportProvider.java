/*
 * Copyright 2004-2005 (C) University of Washington. All Rights Reserved.
 * Created on Dec 28, 2004
 * University of Washington, CIRG
 * $Id: SupportProvider.java,v 1.4 2005/02/13 17:54:55 ddrozd Exp $
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

/** Java implementation of the Continuity of Care (CCR) document object model
 * @author <a href="mailto:ddrozd@u.washington.edu">Dan Drozd</a>
 * @version $Revision: 1.4 $
 *
 */
public class SupportProvider extends CCRDataObject {

	private static Log log = LogFactory.getLog(SupportProvider.class);

	private static final QName SUPPORT_PROVIDER_QNAME = 
		DocumentFactory.getInstance().createQName("SupportProvider", CCRConstants.XMLNS_CCR);	
	private static final QName ROLES_QNAME = 
		DocumentFactory.getInstance().createQName("Roles", CCRConstants.XMLNS_CCR);	
	private static final QName ROLE_QNAME = 
		DocumentFactory.getInstance().createQName("Role", CCRConstants.XMLNS_CCR);	
	private static final QName RELATIONS_QNAME = 
		DocumentFactory.getInstance().createQName("Relations", CCRConstants.XMLNS_CCR);	
	private static final QName RELATION_QNAME = 
		DocumentFactory.getInstance().createQName("Relation", CCRConstants.XMLNS_CCR);	

	private static final String staticQuery = 
		"SELECT p.givenName AS 'givenName', " +
		"p.middleName AS 'middleName', " +
		"p.lastName AS 'familyName', " +
		"p.suffix AS 'suffix', " +
		"p.title AS 'title', " +
		"p.gender AS 'gender', " +
		"s.relation AS 'relation', " + 
		"c.value AS  'homePhone' " +
		"FROM actors a, persons p, supports s, communications c " + 
		"WHERE a.patientID = '" + CCRConstants.UNIQUE_ID_TOKEN + "' " +
		"AND s.actorID = a.id " +
		"AND s.contactActorID = p.actorID " +
		"AND c.actorID = s.contactActorID " + 
		"AND c.type =  'homeTelephone'";
	
	
	private Person person;
	private List roleList;
	private List relationList;
	
	public SupportProvider(Person person, List roleList, List relationList) 
		throws IllegalArgumentException {
		this(person);
		this.roleList = roleList;
		this.relationList = relationList;
	}
		
	public SupportProvider(Person person)
		throws IllegalArgumentException {
		if ( person == null ) {
			throw new IllegalArgumentException("argument person may not be null");
		}
		this.person = person;
		this.roleList = new ArrayList();
		this.relationList = new ArrayList();
	}
	
	public void addRole(String role) {
		roleList.add(role);
	}
	
	public void addRelation(String relation) {
		relationList.add(relation);
	}
	
	public Element getElement() {

		Element element = 
			DocumentFactory.getInstance().createElement(SUPPORT_PROVIDER_QNAME);
		element.add(ccrDataObjectId.getElement());
		element.add(person.getElement());
		
		List telephoneList = person.getTelephoneNumberList();
		Iterator iterator = telephoneList.iterator();
		while ( iterator.hasNext() ) {
			TelephoneNumber number = (TelephoneNumber) iterator.next();
			element.add(number.getElement());
		}
		iterator = null;

		List addressList = person.getAddressList();
		iterator = addressList.iterator();
		while ( iterator.hasNext() ) {
			Address address = (Address) iterator.next();
			element.add(address.getElement());
		}
		iterator = null;
		
		/*
		 * We decided to leave out roles for now, since 
		 * our DB values do not match the schema values
		 */
		/*
		Element rolesElement = 
			DocumentFactory.getInstance().createElement(ROLES_QNAME);
		iterator = roleList.iterator();
		while ( iterator.hasNext() ) {
			String role = (String)iterator.next();
			rolesElement.addElement(ROLE_QNAME).addText(role);
		}
		element.add(rolesElement);
		*/
	
		if ( relationList.size() > 0 ) {	
		  Element relationsElement = 
			DocumentFactory.getInstance().createElement(RELATIONS_QNAME);
		  iterator = relationList.iterator();
		  while ( iterator.hasNext() ) {
			String relation = (String)iterator.next();
			relationsElement.addElement(RELATION_QNAME).addText(relation);
		  }
		  element.add(relationsElement);
		}

		return element;
	}
	
	public static List getSupportProviders(String patientId, Connection conn) 
	throws SQLException {
		String query = staticQuery.replaceAll(CCRConstants.UNIQUE_ID_TOKEN, patientId);		
		List list = new ArrayList();
		Statement stmt = null;
		ResultSet rs = null;

		String givenName = null;
		String middleName = null;
		String familyName = null;
		String suffix = null;
		String title = null;
		String gender = null; 
		String homePhone = null;
		String relation = null;
		String role = null;
		
		try {
			stmt = conn.createStatement();
			rs = stmt.executeQuery(query);
			while ( rs.next() ) {
				givenName = rs.getString("givenName");
				middleName = rs.getString("middleName");
				familyName = rs.getString("familyName");
				suffix = rs.getString("suffix");
				title = rs.getString("title");
				gender = rs.getString("gender");
				relation = rs.getString("relation");
				homePhone = rs.getString("homePhone");
								
				Person person = new Person(new Name(Name.CURRENTNAME_TYPE, givenName, middleName, familyName, suffix, title, null));
				person.setGender(gender);
				if ( homePhone != null ) {
					person.addTelephoneNumber(new TelephoneNumber(homePhone, "Home"));
				}
				SupportProvider sp = new SupportProvider(person);
				sp.addRelation(relation);
				list.add(sp);
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
