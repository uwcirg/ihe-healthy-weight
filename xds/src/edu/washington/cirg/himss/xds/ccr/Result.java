/*
 * Copyright 2004-2005 (C) University of Washington. All Rights Reserved.
 * Created on Dec 28, 2004
 * University of Washington, CIRG
 * $Id: Result.java,v 1.3 2005/02/09 01:51:11 ddrozd Exp $
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
public class Result extends CCRDataObject {
	private static Log log = LogFactory.getLog(Result.class);

	private static final QName RESULT_QNAME = 
		DocumentFactory.getInstance().createQName("Result", CCRConstants.XMLNS_CCR);	
	private static final QName STATUS_QNAME = 
		DocumentFactory.getInstance().createQName("Status", CCRConstants.XMLNS_CCR);	

	private static final String staticQuery = 	
	"SELECT p.procedureType AS 'testType', " +
	"p.description AS 'description', " + 
	"p.location AS 'location', " + 
	"p.dateOccurred AS 'resultDate', " + 
	"p.result AS 'value' " + 
	"FROM actors a, procedures p " + 
	"WHERE a.patientID = '" + CCRConstants.UNIQUE_ID_TOKEN + "' " + 
	"AND p.actorID = a.id " + 
	"AND p.procedureType = 'Diagnostic'";

	private String description;
	private Source source;
	private CCRDateTime resultDate;
	private String status;
	private Test test;
    
	/**
	 * @param test
     * @param description
	 * @param source
     * @param status
	 * @param resultDate
	 */
	public Result(Test test, String description, Source source, String status, CCRDateTime resultDate) {
		super();
        this.test = test;
		this.description = description;
		this.source = source;
		this.status = status;
		this.resultDate = resultDate;
	}
	
    public String getDescription() {
        return description;
    }

    public Test getTest() {
        return test;
    }
	
	public Element getElement() {

		Element element = 
			DocumentFactory.getInstance().createElement(RESULT_QNAME);
		element.add(ccrDataObjectId.getElement());

		if ( resultDate != null ) {
			element.add(resultDate.getElement());
		}
		
		if ( description != null && ! description.trim().equals("")) {
		    Element descElement = 
		        DocumentFactory.getInstance().createElement(CCRConstants.DESC_QNAME);
		    descElement.addElement(CCRConstants.TEXT_QNAME).addText(description);
		    element.add(descElement);
		}
		
		if ( test != null ){
            element.add(test.getElement());
        }
        
		if ( status != null ) {
			element.addElement(STATUS_QNAME).addText(status);
		}

		if ( source != null ) {
			element.add(source.getElement());
		}
		
		return element;
	}	

	
	public static List getResults(String patientId, Connection conn) 
	throws SQLException, XDSException {
		String query = staticQuery.replaceAll(CCRConstants.UNIQUE_ID_TOKEN, patientId);		
		List list = new ArrayList();
		Statement stmt = null;
		ResultSet rs = null;
		
		String testType = null;
		String desc = null;
		String location = null;
		String resultDate = null;
        String value = null;
        String status = null;
        
		try {
			stmt = conn.createStatement();
			rs = stmt.executeQuery(query);
			while ( rs.next() ) {
			    value = rs.getString("value");
                desc = rs.getString("description");
				location = rs.getString("location");
				resultDate = rs.getString("resultDate");
				Test t = new Test(testType, null, value);
				Source source = null;//This needs to be fixed later
				Result result = 
					new Result(t, desc, source, status, new ApproximateDateTime(resultDate));
				list.add(result);
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


