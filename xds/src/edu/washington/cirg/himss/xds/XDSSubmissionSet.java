/*
 * Copyright 2004-2005 (C) University of Washington. All Rights Reserved.
 * Created on Dec 22, 2004
 * University of Washington, CIRG
 * $Id: XDSSubmissionSet.java,v 1.5 2005/02/09 01:51:11 ddrozd Exp $
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

/** Each submission to the XDS ebXML Repository must contain one
 * and only one SubmissionSet element.
 * @author <a href="mailto:ddrozd@u.washington.edu">Dan Drozd</a>
 * @version $Revision: 1.5 $
 *
 */
public class XDSSubmissionSet {
	
	private static Log log = LogFactory.getLog(XDSSubmissionSet.class);

	
	private static final String ssSymbolicName = "SubmissionSet";

	private ArrayList folderList;
	private ArrayList docList;
	
	private String description;
	private String uniqueId;
	private String sourceId;
	private String submissionTime;
	private String authorDept;
	private String authorInstitution;
	private String authorPerson;
	private String contentTypeCode;
	private String contentTypeCodeValue;
	
	/**
	 * @param description
	 * @param sourceId
	 * @param submissionTime
	 * @param authorDept
	 * @param authorInstitution
	 * @param authorPerson
	 * @param contentTypeCode
	 */
	public XDSSubmissionSet(String description, String sourceId, 
			String submissionTime, String authorDept,
			String authorInstitution, String authorPerson,
			String contentTypeCode) throws XDSException {
	
		this.description = description;
		this.uniqueId = IdentifierManager.getUniqueId();
		this.sourceId = sourceId;
		this.submissionTime = submissionTime;
		this.authorDept = authorDept;
		this.authorInstitution = authorInstitution;
		this.authorPerson = authorPerson;
		this.contentTypeCode = contentTypeCode;
		
		
		if ( uniqueId == null || uniqueId.equals("") ) {
			log.error("Unable to generate valid uniqueId");
			throw new XDSException("Unable to generate valid uniqueId");
		}
		
		if ( sourceId == null || sourceId.equals("") ) {
			log.error("Null or empty sourceId passed to XDSSubmissionSet Constructor");
			throw new IllegalArgumentException("Null or empty sourceId passed to XDSSubmissionSet Constructor");
		}
		if ( contentTypeCode == null || contentTypeCode.equals("") ) {
			log.error("Null or empty contentTypeCode passed to XDSSubmissionSet Constructor");
			throw new IllegalArgumentException("Null or empty contentTypeCode passed to XDSSubmissionSet Constructor");
		}
		
		contentTypeCodeValue = XDSConstants.getClassCodeValue(contentTypeCode);
		if ( contentTypeCodeValue == null || contentTypeCodeValue.equals("")) {
			log.error("Null or empty code value returned for contentTypeCode (" + contentTypeCode + ")");
			throw new XDSException("Null or empty code value returned for contentTypeCode (" + contentTypeCode + ")");
		}


		folderList = new ArrayList();
		docList = new ArrayList();
	}
	
	public void setDescription(String description) {
		this.description = description;
	}
	public void setAuthorPerson(String authorPerson) {
		this.authorPerson = authorPerson;
	}
	public void setAuthorDepartment(String authorDept) {
		this.authorDept = authorDept;
	}
	public void setAuthorInstitution(String authorInstitution) {
		this.authorInstitution = authorInstitution;
	}
	
	
	public void addFolder(XDSFolder folder) {
		folderList.add(folder);
	}
	
	public void addDocument(XDSDocument doc) {
		docList.add(doc);
	}
	
	public List getXDSDocumentList() {
		return docList;
	}
	
	public List getXDSFolderList() {
		return folderList;
	}
	
	public List getElementList() throws XDSException {

		Name nullName = null;
		Slot nullSlot = null;
		ArrayList ssElementList = new ArrayList();
		
		ObjectRef objref1 = 
			new ObjectRef(XDSConstants.getExternalIdentifier(XDSConstants.XDS_SUBMISSION_SET_UNIQUE_ID_ELEMENT));
		ssElementList.add(objref1.getElement());

		ObjectRef objref2 = 
			new ObjectRef(XDSConstants.getExternalIdentifier(XDSConstants.XDS_SUBMISSION_SET_SOURCE_ID_ELEMENT));
		ssElementList.add(objref2.getElement());

		List externalIdentifierList = getExternalIdentifiers();
		
		List slotList = getSlots();
		
		RegistryPackage registrypackage = 
			new RegistryPackage(ssSymbolicName, description, 
					new Name(ssSymbolicName),
					externalIdentifierList, slotList, new Description(description));
		
		ssElementList.add(registrypackage.getElement());
		
		ObjectRef contentTypeObjRef = 
			new ObjectRef(XDSConstants.UUID_CONTENT_TYPE_CODE);
		ssElementList.add(contentTypeObjRef.getElement());
		
		Classification cContentTypeCode = 
			new Classification(XDSConstants.UUID_CONTENT_TYPE_CODE, ssSymbolicName,
					contentTypeCode,
					contentTypeCodeValue,
					XDSConstants.CODE_SLOT_NAME,
					new String[] {"Connect-a-thon contentTypeCodes"},
					null);
		ssElementList.add(cContentTypeCode.getElement());
		
		ObjectRef ssObjRef =
			new ObjectRef(XDSConstants.getObjectType(XDSConstants.XDS_SUBMISSION_SET_ELEMENT));
		ssElementList.add(ssObjRef.getElement());
		
		Classification cSubmissionSet = 
			new Classification(null, ssSymbolicName,
					null,
					nullName,
					nullSlot,
					XDSConstants.getObjectType(XDSConstants.XDS_SUBMISSION_SET_ELEMENT));
		ssElementList.add(cSubmissionSet.getElement());
		
		String nullString = null;
		//Associate each document with the submission set
		Iterator iterator = docList.iterator();
		while ( iterator.hasNext()) {
			XDSDocument xdsdoc = (XDSDocument)iterator.next();
			Association aDocSubSet = 
				new Association(XDSConstants.UUID_HAS_MEMBER_ASSN,
						ssSymbolicName,
						xdsdoc.getSymbolicDocName(),
						nullName,
						"SubmissionSetStatus",
						new String[]{"Original"});
			ssElementList.add(aDocSubSet.getElement());
		}
		iterator = null;
		
		//Associate each folder with the submission set
		iterator = folderList.iterator();
		while ( iterator.hasNext()) {
			XDSFolder xdsfolder = (XDSFolder)iterator.next();
			Association aFolderSubSet = 
				new Association(XDSConstants.UUID_HAS_MEMBER_ASSN,
						ssSymbolicName,
						xdsfolder.getFolderName(),
						nullName,
						null);
			ssElementList.add(aFolderSubSet.getElement());
		}


		
		return ssElementList;
	}
	
	
	private List getSlots() {
		
		List slotList = new ArrayList();
		
		//Start required
		//submissionTime
		if ( submissionTime != null && submissionTime != "" ) {
			slotList.add(new Slot("submissionTime", new String[]{submissionTime}));
		} else {
			log.error("Null or empty submissionTime passed to SubmissionSet");
			throw new IllegalArgumentException("Null or empty submissionTime passed to SubmissionSet");
		}
		
		//Start required if available
		if ( authorDept != null ) {
			slotList.add(new Slot("authorDepartment", new String[]{authorDept}));
		}
		if ( authorInstitution != null ) {
			slotList.add(new Slot("authorInstitution", new String[]{authorInstitution}));			
		}
		if ( authorPerson != null ) {
			slotList.add(new Slot("authorPerson", new String[]{authorPerson}));
		}
		
		
		return slotList;
	}
	
	private List getExternalIdentifiers() {
		List externalIdentifierList = new ArrayList();
		
		ExternalIdentifier uniqueIdEI = 
			new ExternalIdentifier(XDSConstants.getExternalIdentifier(XDSConstants.XDS_SUBMISSION_SET_UNIQUE_ID_ELEMENT),
					uniqueId, XDSConstants.XDS_SUBMISSION_SET_UNIQUE_ID_ELEMENT);
		externalIdentifierList.add(uniqueIdEI);

		ExternalIdentifier sourceIdEI = 
			new ExternalIdentifier(XDSConstants.getExternalIdentifier(XDSConstants.XDS_SUBMISSION_SET_SOURCE_ID_ELEMENT),
					sourceId, XDSConstants.XDS_SUBMISSION_SET_SOURCE_ID_ELEMENT);	
		externalIdentifierList.add(sourceIdEI);
		
		return externalIdentifierList;
	}

}
