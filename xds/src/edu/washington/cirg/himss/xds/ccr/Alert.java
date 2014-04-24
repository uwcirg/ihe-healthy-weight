/*
 * Copyright 2004-2005 (C) University of Washington. All Rights Reserved.
 * Created on Dec 28, 2004
 * University of Washington, CIRG
 * $Id: Alert.java,v 1.4 2005/02/09 01:51:11 ddrozd Exp $
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
public class Alert extends CCRDataObject {

	private static Log log = LogFactory.getLog(Alert.class);

	private static final QName ALERT_QNAME = 
		DocumentFactory.getInstance().createQName("Alert", CCRConstants.XMLNS_CCR);	
	private static final QName ALERT_TYPE_QNAME = 
		DocumentFactory.getInstance().createQName("AlertType", CCRConstants.XMLNS_CCR);	

	private static final String staticQuery = 
		"SELECT l.type AS 'alertType', " + 
		"l.description AS 'alertDesc', " +
		"l.agentType AS 'agentType', " +
		"l.agent AS 'agentName', " +
		"l.reaction AS 'rxnDesc', " + 
		"l.onsetDate AS 'onsetDate', " +
		"l.productType AS 'productType' " + 
		"FROM actors a, alerts l " + 
		"WHERE a.patientID = '" + CCRConstants.UNIQUE_ID_TOKEN + "' " + 
		"AND l.actorID = a.id ";
	
	private String alertType;
	private String description;
	private Agent agent;
	private String onsetDate;
	private Reaction reaction;
	
	/**
	 * @param alertType
	 * @param description
	 */
	public Alert(String alertType, String description, Agent agent, String onsetDate, Reaction reaction) {
		this.alertType = alertType;
		this.description = description;
		this.agent = agent;
		this.onsetDate = onsetDate;
		this.reaction = reaction;
	}
	
	public Agent getAgent() {
		return agent;
	}

	
	public Element getElement() {
		
		Element element = 
			DocumentFactory.getInstance().createElement(ALERT_QNAME);
		element.add(ccrDataObjectId.getElement());

		if ( onsetDate != null && ! onsetDate.trim().equals("")) {
			element.add(new ApproximateDateTime(onsetDate).getElement());
		}

		if ( alertType != null ) {
			element.addElement(ALERT_TYPE_QNAME).addText(alertType);
		}
		
		if ( description != null && ! description.trim().equals("")) {
			Element descElement = 
				DocumentFactory.getInstance().createElement(CCRConstants.DESC_QNAME);
			descElement.addElement(CCRConstants.TEXT_QNAME).addText(description);
			element.add(descElement);
		}
		element.add(agent.getElement());
		
		if ( reaction != null ) {
			element.add(reaction.getElement());
		}
		
		return element;
	}
	
	public static List getAlerts(String patientId, Connection conn) 
	throws SQLException, XDSException {
		String query = staticQuery.replaceAll(CCRConstants.UNIQUE_ID_TOKEN, patientId);		
		List list = new ArrayList();
		Statement stmt = null;
		ResultSet rs = null;
		
		String alertType = null;
		String alertDesc = null;
		String agentType = null;
		String onsetDate = null;
		
		//ProductAgent
		//String productDesc = null;//We don't store this, do we need it?
		String productType = null;
		String agentName = null;
		//String brandName = null;//We don't store this, do we need it?
		
		//Reaction
		String rxnDesc = null;
		//String rxnSeverity = null;//We don't store, do we need it?
		
		
		try {
			stmt = conn.createStatement();
			rs = stmt.executeQuery(query);
			while ( rs.next() ) {
				alertType = rs.getString("alertType");
				alertDesc = rs.getString("alertDesc");
				agentType = rs.getString("agentType");
				agentName = rs.getString("agentName");
				productType = rs.getString("productType");
				rxnDesc = rs.getString("rxnDesc");
				onsetDate = rs.getString("onsetDate");

				Agent agent = null;
				if ( agentType.equals(Agent.PRODUCT_AGENT) ) {
					agent = new ProductAgent(new Product(productType, agentName, null, new Reaction(rxnDesc, null), null), null);
				} else if ( agentType.equals(Agent.ENVIRONMENTAL_AGENT) ) {
					agent = new EnvironmentalAgent(agentName);
				} else {
					log.error("Invalid agent type specified");
					throw new XDSException("Invalid agent type specified " + agentType);
				}
				Reaction reaction = null;
				if ( rxnDesc != null ) {
					//We don't store severity
					reaction = new Reaction(rxnDesc, null);
				}
				Alert alert = 
					new Alert(alertType, alertDesc, agent, onsetDate, reaction);				
				list.add(alert);
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
