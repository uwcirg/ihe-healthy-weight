/*
 * Copyright 2004-2005 (C) University of Washington. All Rights Reserved.
 * Created on Dec 28, 2004
 * University of Washington, CIRG
 * $Id: History.java,v 1.4 2005/02/11 20:10:58 ddrozd Exp $
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

public class History extends CCRDataObject {
	private static Log log = LogFactory.getLog(History.class);

    private static final QName HISTORY_QNAME = 
        DocumentFactory.getInstance().createQName("History", CCRConstants.XMLNS_CCR);   


	private static final String staticQuery = 	
		"SELECT f.problemDescription AS 'description', " +
        "f.relation AS 'relation', " +
        "f.code AS 'code', " +
        "f.comment AS 'comment' " +
		"FROM actors a, familyHistory f " +
		"WHERE a.patientID = '" + CCRConstants.UNIQUE_ID_TOKEN + "' " +
		"AND f.actorID = a.id";
	
	private Problem problem;
    private FamilyMember familyMember;
	
	
	/**
	 * 
	 */
	public History(Problem problem, FamilyMember familyMember) {
		super();
		this.problem = problem;
        this.familyMember = familyMember;
	}
	
	public Problem getProblem() {
		return problem;
	}
    
    public FamilyMember getFamilyMember() {
        return familyMember;
    }
	
	public static List getHistory(String patientId, Connection conn) 
	throws SQLException, XDSException {
		String query = staticQuery.replaceAll(CCRConstants.UNIQUE_ID_TOKEN, patientId);		
		List list = new ArrayList();
		Statement stmt = null;
		ResultSet rs = null;
		String desc = null;
        String relation = null;
        String code = null;
        String problemType = "Condition";
        String comment = null;
        
		try {
			stmt = conn.createStatement();
			rs = stmt.executeQuery(query);
			while ( rs.next() ) {
				desc = rs.getString("description");
                relation = rs.getString("relation");
                code = rs.getString("code");
                comment = rs.getString("comment");
                if ( relation.equalsIgnoreCase("Other")) {
                    relation = "Other (" + code + ")";
                }
				History history = new History(new Problem(problemType, desc, comment),
                        new FamilyMember(new Person(
                                new Name(Name.CURRENTNAME_TYPE, null, null, null, null, null, null)),
                               relation));
				list.add(history);
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

	public Element getElement() {
		Element element = 
			DocumentFactory.getInstance().createElement(HISTORY_QNAME);
		element.add(ccrDataObjectId.getElement());

		if ( problem != null ) {
			element.add(problem.getElement());
		}
        
        if ( familyMember != null ) {
            element.add(familyMember.getElement());
        }
		
		return element;
	}

}