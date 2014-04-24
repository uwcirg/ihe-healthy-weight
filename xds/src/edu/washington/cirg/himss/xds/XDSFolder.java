/*
 * Copyright 2004-2005 (C) University of Washington. All Rights Reserved.
 * Created on Dec 22, 2004
 * University of Washington, CIRG
 * $Id: XDSFolder.java,v 1.6 2005/02/09 01:51:11 ddrozd Exp $
 */
package edu.washington.cirg.himss.xds;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import edu.washington.cirg.ebxml.Association;
import edu.washington.cirg.ebxml.Classification;
import edu.washington.cirg.ebxml.Description;
import edu.washington.cirg.ebxml.ExternalIdentifier;
import edu.washington.cirg.ebxml.Name;
import edu.washington.cirg.ebxml.ObjectRef;
import edu.washington.cirg.ebxml.RegistryPackage;
import edu.washington.cirg.ebxml.Slot;
import edu.washington.cirg.himss.xds.util.IdentifierManager;
import edu.washington.cirg.himss.xds.util.XDSConstants;

/** An XDSDocumentEntry may belong to a folder that encapsulates a specific
 * course of medical treatment.
 * @author <a href="mailto:ddrozd@u.washington.edu">Dan Drozd</a>
 * @version $Revision: 1.6 $
 *
 */
public class XDSFolder {
	
	private static Log log = LogFactory.getLog(XDSFolder.class);
	
	
	private String folderName;	
	private String comments;
	private String folderUniqueId;
	private String patientId;
	private String[] folderCodeList;
	private XDSSubmissionSet submissionSet;
	private List xdsFileList;
	
	
	/**
	 * @param folderName
	 * @param comments
	 * @param patientId
	 * @param folderCodeList
	 * @param submissionSet
	 */
	public XDSFolder(String folderName, String comments, String patientId,
			String[] folderCodeList, XDSSubmissionSet submissionSet) 
		throws XDSException {
		
		this.folderName = folderName;
		this.comments = comments;
		this.patientId = patientId;
		this.folderCodeList = folderCodeList;
		this.submissionSet = submissionSet;
		this.folderUniqueId = IdentifierManager.getUniqueId();
		xdsFileList = new ArrayList();
		
		if ( patientId == null || patientId.equals("") ) {
			log.error("Error trying to create ExternalIdentifier, required argument (patientId) is null or empty");
			throw new IllegalArgumentException("Error trying to create ExternalIdentifier, required argument (patientId) is null or empty");			
		}
		
		if ( folderUniqueId == null || folderUniqueId.equals("") ) {
			log.error("Unable to generate valid uniqueId");
			throw new XDSException("Unable to generate valid uniqueId");
		}

		if (comments == null) {
			comments = "";//Set comments to empty string, if null
		}
		
		if ( folderCodeList == null || folderCodeList.length < 1 ) {
			log.error("Required ExternalClassification (folderCodeList) value is null");
			throw new IllegalArgumentException("Required ExternalClassification (folderCodeList) value is null");
		}

	}
	
	public void addXDSFile(XDSFile xdsfile) {
		xdsFileList.add(xdsfile);
	}

	public String getFolderName() {
		return folderName;
	}
	
	public List getElementList() {
		
		ArrayList folderElementList = new ArrayList();
		
		ObjectRef folderPatientIdObjRef = 
			new ObjectRef(XDSConstants.getExternalIdentifier(XDSConstants.XDS_FOLDER_PATIENT_ID_ELEMENT));
		folderElementList.add(folderPatientIdObjRef.getElement());
		
		List externalIdentifierList = getExternalIdentifiers();
		
		String nullString = null;

		Description desc = null;
		
		if ( comments != null ) {
			desc = new Description(comments);
		}
		
		
		RegistryPackage folderPackage =
			new RegistryPackage(folderName, nullString, "FOLDER", externalIdentifierList, null, desc);

		folderElementList.add(folderPackage.getElement());
		
		for ( int i = 0; i < folderCodeList.length; i++ ) {
			ObjectRef folderCodeListObjRef = 
				new ObjectRef(XDSConstants.UUID_FOLDER_CODE_LIST);
			folderElementList.add(folderCodeListObjRef.getElement());

			if ( folderCodeList[i] == null || folderCodeList[i].equals("") ) {
				log.error("Null or empty folderCode value");
				throw new IllegalArgumentException("Null or empty folderCode value");
			}
			
			Classification cFolderCodeList =
				new Classification(XDSConstants.UUID_FOLDER_CODE_LIST, folderName, 
					folderCodeList[i], new Name(folderCodeList[i] + " Name"), 
					new Slot("codingScheme", new String[]{"Loinc"}), null);
			folderElementList.add(cFolderCodeList.getElement());
		}

		Name nullName = null;
		Slot nullSlot = null;
		
		ObjectRef folderClassRef = new ObjectRef(XDSConstants.getObjectType(XDSConstants.XDS_FOLDER_ELEMENT));
		folderElementList.add(folderClassRef.getElement());
		Classification cFolder = 
			new Classification(null, folderName,
					null,
					nullName,
					nullSlot,
					XDSConstants.getObjectType(XDSConstants.XDS_FOLDER_ELEMENT));
		folderElementList.add(cFolder.getElement());		
		
		Iterator iterator = xdsFileList.iterator();
		
		while ( iterator.hasNext()) {
			XDSFile xdsfile = (XDSFile)iterator.next();
			Association hasMemberAssociation =
				new Association(XDSConstants.UUID_HAS_MEMBER_ASSN,
						folderName,
						xdsfile.getUUID(),
						nullName,
						null);
			folderElementList.add(hasMemberAssociation.getElement());
		}
		
		return folderElementList;
	}
	
	private List getExternalIdentifiers() {
		
		List externalIdentifierList = new ArrayList();
		
		ExternalIdentifier uniqueIdEI = 
			new ExternalIdentifier(XDSConstants.getExternalIdentifier(XDSConstants.XDS_FOLDER_UNIQUE_ID_ELEMENT),
					folderUniqueId, XDSConstants.XDS_FOLDER_UNIQUE_ID_ELEMENT);
		externalIdentifierList.add(uniqueIdEI);

		ExternalIdentifier patientIdEI = 
			new ExternalIdentifier(XDSConstants.getExternalIdentifier(XDSConstants.XDS_FOLDER_PATIENT_ID_ELEMENT),
					patientId, XDSConstants.XDS_FOLDER_PATIENT_ID_ELEMENT );
		externalIdentifierList.add(patientIdEI);
				
		return externalIdentifierList;
	}
	
}
