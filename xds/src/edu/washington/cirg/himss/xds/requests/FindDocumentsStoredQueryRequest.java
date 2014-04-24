/*
 * Copyright 2004-2009 (C) University of Washington. All Rights Reserved.
 * Created on Jan 30, 2007
 * University of Washington, CIRG
 */
package edu.washington.cirg.himss.xds.requests;

import java.util.HashMap;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import edu.washington.cirg.himss.xds.util.XDSConstants;

/** Class representing a query request sent to an XDS ebXML registry
 * to retrieve Extrinsic Object information using the 'FindDocuments'
 * stored query parameters
 * @author <a href="mailto:jsibley@u.washington.edu">Jim Sibley</a>
 *
 */
public class FindDocumentsStoredQueryRequest extends StoredQueryRequest {
	
//	public static final int BY_PATIENT_ID = 1;
//	public static final int BY_EO_ID = 2;

	private static Log log = LogFactory.getLog(FindDocumentsStoredQueryRequest.class);
	private Map<String, String> queryParams = new HashMap();
	
	public FindDocumentsStoredQueryRequest(String patientId, String timeSlot, String qStartTime, String qStopTime, String queryType, String storedQueryUuid) {
		super(queryType, true);
		super.setQueryID(storedQueryUuid);

		/* set parameters for the query */
		if (patientId.indexOf("%") == -1) {
			queryParams.put("$XDSDocumentEntryPatientId", "'" + patientId + "'");
		}
		queryParams.put("$XDSDocumentEntryCreationTimeFrom", qStartTime);
		queryParams.put("$XDSDocumentEntryCreationTimeTo", qStopTime);
		queryParams.put("$XDSDocumentEntryStatus", "urn:oasis:names:tc:ebxml-regrep:StatusType:Approved");
		super.setQueryParams(queryParams);
	}
	
//	public FindDocumentsRequest(String id, int queryBy, String queryType ) {
//		this(queryType);
//	}
	
}


