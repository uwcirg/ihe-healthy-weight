/*
 * Copyright 2004-2005 (C) University of Washington. All Rights Reserved.
 * Created on Jan 5, 2005
 * University of Washington, CIRG
 * $Id: RetrieveObjectRefRequest.java,v 1.2 2005/01/26 18:59:47 ddrozd Exp $
 */
package edu.washington.cirg.himss.xds.requests;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/** Class representing a query request sent to an XDS ebXML registry
 * to retrieve Object references by PatientId
 * @author <a href="mailto:ddrozd@u.washington.edu">Dan Drozd</a>
 * @version $Revision: 1.2 $
 *
 */
public class RetrieveObjectRefRequest extends AdhocQueryRequest {

	private static Log log = LogFactory.getLog(RetrieveObjectRefRequest.class);

	public RetrieveObjectRefRequest(String patientId) {
		super(AdhocQueryRequest.OBJECT_REF, false);
		StringBuffer sb = new StringBuffer();
		sb.append("SELECT eo.id ");
		sb.append("FROM ExtrinsicObject eo, ExternalIdentifier ei ");
		sb.append("WHERE eo.id = ei.registryobject ");
		sb.append("AND ei.identificationScheme='urn:uuid:58a6f841-87b3-4a3e-92fd-a8ffeff98427' ");
		sb.append("AND ei.value='");
		sb.append(patientId);
		sb.append("' AND eo.status = 'Approved'");
		super.setQuery(sb.toString());
	}
}


