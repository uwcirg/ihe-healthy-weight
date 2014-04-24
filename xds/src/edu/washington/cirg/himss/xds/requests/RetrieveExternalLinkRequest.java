/*
 * Copyright 2004-2005 (C) University of Washington. All Rights Reserved.
 * Created on Jan 5, 2005
 * University of Washington, CIRG
 * $Id: RetrieveExternalLinkRequest.java,v 1.3 2005/01/26 18:59:47 ddrozd Exp $
 */
package edu.washington.cirg.himss.xds.requests;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/** Class representing a query to retrieve a Document URL from the 
 * XDS ebXML registry
 * @author <a href="mailto:ddrozd@u.washington.edu">Dan Drozd</a>
 * @version $Revision: 1.3 $
 *
 */
public class RetrieveExternalLinkRequest extends AdhocQueryRequest {
	
	private static Log log = LogFactory.getLog(RetrieveExternalLinkRequest.class);
	public static final int BY_PATIENT_ID = 1;
	public static final int BY_EO_ID = 2;
	
	
	public RetrieveExternalLinkRequest(String id, int idType, String queryType) {
		super(queryType, true);
		StringBuffer sb = new StringBuffer();
		if ( idType == BY_EO_ID ) {
			sb.append("SELECT * FROM ExternalLink el, Association a WHERE a.targetobject = '");
			sb.append(id);
			sb.append("' AND a.sourceobject = el.id AND a.associationType='ExternallyLinks'");
		} else if ( idType == BY_PATIENT_ID ) {
			sb.append("SELECT * FROM ExternalLink el, Association a,");
			sb.append(" ExternalIdentifier ei, ExtrinsicObject eo");
			sb.append(" WHERE a.associationType = 'ExternallyLinks'");
			sb.append(" AND a.targetObject = eo.id");
			sb.append(" AND a.sourceObject = el.id");
			sb.append(" AND eo.id = ei.registryobject");
			sb.append(" AND ei.identificationScheme='urn:uuid:58a6f841-87b3-4a3e-92fd-a8ffeff98427'");
			sb.append(" AND ei.value='" + id + "'");
			sb.append(" AND eo.status = 'Approved'");
		} else {
			throw new IllegalArgumentException("Invalid id type provided as argument");
		}
		if ( log.isDebugEnabled() ) {
			log.debug(sb.toString());
		}
		super.setQuery(sb.toString());
	}	
}


