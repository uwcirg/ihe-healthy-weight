/*
 * Copyright 2004-2005 (C) University of Washington. All Rights Reserved.
 * Created on Jan 5, 2004
 * University of Washington, CIRG
 * $Id: RetrieveExtrinsicObjectRequest.java,v 1.3 2005/01/26 18:59:47 ddrozd Exp $
 */
package edu.washington.cirg.himss.xds.requests;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/** Class representing a query request sent to an XDS ebXML registry
 * to retrieve Extrinsic Object information by PatientId or by ObjectId
 * @author <a href="mailto:ddrozd@u.washington.edu">Dan Drozd</a>
 * @version $Revision: 1.3 $
 *
 */
public class RetrieveExtrinsicObjectRequest extends AdhocQueryRequest {
	
	public static final int BY_PATIENT_ID = 1;
	public static final int BY_EO_ID = 2;

	private static Log log = LogFactory.getLog(RetrieveExtrinsicObjectRequest.class);
	
	public RetrieveExtrinsicObjectRequest(String[] idArray, int queryBy, String queryType ) {
		super(queryType, true);
		StringBuffer sb = new StringBuffer();
		if ( queryBy == BY_EO_ID ) {
			sb.append("SELECT * FROM ExtrinsicObject eo, Slot s ");
			sb.append("WHERE eo.id = s.parent ");
			sb.append("AND s.name_ = 'creationTime' AND (");
			for ( int i = 0; i < idArray.length; i++ ) {
				if ( i > 0 ) {
					sb.append(" OR");
				}
				sb.append(" eo.id = '");
				sb.append(idArray[i]);
				sb.append("'");
			}
			sb.append(" )");
		} else if ( queryBy == BY_PATIENT_ID ) {
			sb.append("SELECT * FROM ExtrinsicObject eo, ExternalIdentifier ei, Slot s ");
			sb.append("WHERE eo.id = ei.registryobject ");
			sb.append("AND eo.id = s.parent ");
			sb.append("AND s.name_= 'creationTime' ");
			sb.append("AND ei.identificationScheme='urn:uuid:58a6f841-87b3-4a3e-92fd-a8ffeff98427' ");
			sb.append("AND (");
			for ( int i = 0; i < idArray.length; i++ ) {
				if ( i > 0 ) {
					sb.append(" OR");
				}
				sb.append(" ei.value = '");
				sb.append(idArray[i]);
				sb.append("'");
			}
			sb.append(" )");
		} else {
			throw new IllegalArgumentException("Invalid query type");
		}
		sb.append(" AND eo.status = 'Approved'");
		sb.append(" order by s.value desc");
		super.setQuery(sb.toString());
	}
	
	public RetrieveExtrinsicObjectRequest(String id, int queryBy, String queryType ) {
		this(new String[]{id}, queryBy, queryType);
	}
	
}


