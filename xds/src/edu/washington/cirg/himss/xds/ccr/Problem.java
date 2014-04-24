/*
 * Copyright 2004-2005 (C) University of Washington. All Rights Reserved.
 * Created on Dec 28, 2004
 * University of Washington, CIRG
 * $Id: Problem.java,v 1.5 2005/02/11 20:10:58 ddrozd Exp $
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

import edu.washington.cirg.himss.xds.util.XDSUtil;

/** Java implementation of the Continuity of Care (CCR) document object model
 * @author <a href="mailto:ddrozd@u.washington.edu">Dan Drozd</a>
 * @version $Revision: 1.5 $
 *
 */
public class Problem extends CCRDataObject {

	private static Log log = LogFactory.getLog(Problem.class);

	public static final String PT_CONDITION = "Condition";
	public static final String PT_DIAGNOSIS = "Diagnosis";
	public static final String PT_FINDING = "Finding";
	public static final String PT_COMPLAINT = "Complaint";
	public static final String PT_SYMPTOMS = "Symptoms";
	public static final String PT_PROBLEM = "Problem";
	
	private static final QName PROBLEM_QNAME = 
		DocumentFactory.getInstance().createQName("Problem", CCRConstants.XMLNS_CCR);	
	private static final QName PROBLEM_TYPE_QNAME = 
		DocumentFactory.getInstance().createQName("ProblemType", CCRConstants.XMLNS_CCR);	
	private static final QName DESCRIPTION_QNAME = 
		DocumentFactory.getInstance().createQName("Description", CCRConstants.XMLNS_CCR);	
//    private static final QName COMMENT_QNAME = 
//        DocumentFactory.getInstance().createQName("Comment", CCRConstants.XMLNS_CCR);   
    private static final QName CURRENT_HEALTH_STATUS_QNAME = 
        DocumentFactory.getInstance().createQName("CurrentHealthStatus", CCRConstants.XMLNS_CCR);   
    private static final QName STATUS_QNAME = 
        DocumentFactory.getInstance().createQName("Status", CCRConstants.XMLNS_CCR);   
	
	private static final String staticQuery = 
		"SELECT p.name AS 'description', " +
		"p.type AS 'type', " +
		"p.diagnosisDate AS 'diagnosisDate' " + 
		"FROM actors a, problems p " + 
		"WHERE a.patientID = '" + CCRConstants.UNIQUE_ID_TOKEN + "' " + 
		"AND a.id = p.actorID";
	

	private CCRDateTime ccrDateTime;
	private String problemType;
	private String description;
	private Source source;
    private String comment;
	
	public Problem(String problemType, String description, String comment) {
		this.problemType = problemType;
		this.description = description;
        this.comment = comment;
	}
	
	public void setDateTime(CCRDateTime ccrDateTime) {
		this.ccrDateTime = ccrDateTime;
	}
		
	public void setSource(Source source) {
		this.source = source;
	}
	
	
	public Element getElement() {

		Element element = 
			DocumentFactory.getInstance().createElement(PROBLEM_QNAME);
		element.add(ccrDataObjectId.getElement());
		
		if ( ccrDateTime != null && ! ccrDateTime.equals("")) {
			element.add(ccrDateTime.getElement());
		}
		
		if ( problemType != null && ! problemType.equals("")) {
			element.addElement(PROBLEM_TYPE_QNAME).addText(problemType);
		}
		
		if ( description != null && ! description.equals("")) {
			Element descElement = 
				DocumentFactory.getInstance().createElement(DESCRIPTION_QNAME);
			descElement.addElement(CCRConstants.TEXT_QNAME).addText(description);
			element.add(descElement);
		}
		
        if ( comment != null && ! comment.equals("")) {
            Element currentHealthStatusElement = 
                DocumentFactory.getInstance().createElement(CURRENT_HEALTH_STATUS_QNAME);
            currentHealthStatusElement.addElement(STATUS_QNAME).addText(comment);
            element.add(currentHealthStatusElement);
        }
        
		if ( source != null && ! source.equals("")) {
			element.add(source.getElement());
		}
				
		return element;
	}
	
	public static List getProblems(String patientId, Connection conn) 
	throws SQLException {
		String query = staticQuery.replaceAll(CCRConstants.UNIQUE_ID_TOKEN, patientId);		
		List list = new ArrayList();
		Statement stmt = null;
		ResultSet rs = null;
		
		String description = null;
		String type = null;
		String diagnosisDate = null;
		
		try {
			stmt = conn.createStatement();
			rs = stmt.executeQuery(query);
			while ( rs.next() ) {
				description = rs.getString("description");
				type = rs.getString("type");
				/*
				 *This is a hack for now, as we are storing these values lowercased
				 *and the XML Schema has the first letter capitalized 
				 */
				type = XDSUtil.capitalize(type);
				diagnosisDate = rs.getString("diagnosisDate");
				Problem p = new Problem(type, description, null);
				p.setDateTime(new ApproximateDateTime(diagnosisDate));
				list.add(p);
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
