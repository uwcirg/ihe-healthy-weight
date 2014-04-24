/*
 * Copyright 2004-2006 (C) University of Washington. All Rights Reserved.
 * Created on Jan 17, 2006
 * University of Washington, CIRG
 */
package edu.washington.cirg.himss.xds.requests;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import edu.washington.cirg.himss.xds.util.XDSConstants;

/** Class representing a query request sent to an XDS ebXML registry
 * to retrieve Extrinsic Object information using the 'FindDocuments'
 * query parameters
 * @author <a href="mailto:jsibley@u.washington.edu">Jim Sibley</a>
 *
 */
public class FindDocumentsRequest extends AdhocQueryRequest {
	
//	public static final int BY_PATIENT_ID = 1;
//	public static final int BY_EO_ID = 2;

	private static Log log = LogFactory.getLog(FindDocumentsRequest.class);
	
	public FindDocumentsRequest(String patientId, String timeSlot, String qStartTime, String qStopTime, String queryType) {
		super(queryType, true);
		StringBuffer sb = new StringBuffer();
		sb.append ("SELECT doc.id\n");
		sb.append ("FROM ExtrinsicObject doc\n");
		sb.append (", ExternalIdentifier patId\n");
		/* , Classification clCode */
		sb.append (", Slot dateTime\n");
		/* , Classification psc
		   , Classification hftc
		   , Classification ecl */
		sb.append ("WHERE doc.objectType = '" + XDSConstants.UUID_DOCUMENT_OBJECT_TYPE_CODE + "'\n");
		sb.append (" AND ( doc.id = patId.registryobject AND\n");
		sb.append ("       patId.identificationScheme='" + XDSConstants.UUID_PATIENT_IDENT_SCHEME_CODE + "' AND\n");
		if (patientId.indexOf("%") != -1) {
			sb.append ("       patId.value LIKE '" + patientId + "' )\n");
		} else {
			sb.append ("       patId.value = '" + patientId + "' )\n");
		}
		/* AND ( clCode.classifiedobject = doc.id AND
		         clCode.classificationScheme = '" + XDSConstants.UUID_CLASS_CODE + "' AND
		         clCode.nodeRepresentation IN $classCodes ) */
		sb.append (" AND ( dateTime.parent = doc.id AND\n");
		sb.append ("       dateTime.name = '" + timeSlot + "' AND\n");
		sb.append ("       dateTime.value >= '" + qStartTime + "' AND\n");
		sb.append ("       dateTime.value < '" + qStopTime + "' )\n");
		 /* AND ( psc.classifiedObject = doc.id AND
		       psc.classificationScheme = '" + XDSConstants.UUID_PRACTICE_SETTING_CODE + "' AND
		       psc.nodeRepresentation IN $psCodes )
		 AND ( hftc.classifiedObject = doc.id AND
		       hftc.classificationScheme = '" + XDSConstants.UUID_HEALTH_CARE_FACILITY_TYPE_CODE + "' AND
		       hftc.nodeRepresentation IN $hcftCodes )
		 AND ( ecl.classifiedObject = doc.id AND
		       ecl.classificationScheme = '" + XDSConstants.UUID_EVENT_CODE + "' AND
		       ecl.nodeRepresentation IN $evCodes )
		*/
		sb.append (" AND doc.status IN ('Approved')");
		super.setQuery(sb.toString());
	}
	
//	public FindDocumentsRequest(String id, int queryBy, String queryType ) {
//		this(queryType);
//	}
	
}


