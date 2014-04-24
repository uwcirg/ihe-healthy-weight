/*
 * Copyright 2004-2005 (C) University of Washington. All Rights Reserved.
 * Created on Dec 22, 2004
 * University of Washington, CIRG
 * $Id: XDSDocument.java,v 1.7 2005/02/21 16:43:25 ddrozd Exp $
 */
package edu.washington.cirg.himss.xds;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.dom4j.Element;

import edu.washington.cirg.ebxml.Classification;
import edu.washington.cirg.ebxml.ExternalIdentifier;
import edu.washington.cirg.ebxml.ExtrinsicObject;
import edu.washington.cirg.ebxml.Slot;
import edu.washington.cirg.himss.xds.util.XDSConstants;

/** Class representing an XDSDocumentEntry.  Contains both document
 * metadata and the document itself.  Documents may be files, XML documents,
 * DOM trees, which are themselves encapsulated in XDSFiles.
 * @author <a href="mailto:ddrozd@u.washington.edu">Dan Drozd</a>
 * @version $Revision: 1.7 $
 *
 */
public class XDSDocument {

	private static Log log = LogFactory.getLog(XDSDocument.class);
	
	private String documentTitle;
	private String patientId;
	private String docUniqueId;
	private String authorDepartment;
	private String authorInstitution;
	private String authorPerson;
	private String serviceStartTime;
	private String serviceStopTime;
	private String languageCode;
	private String creationTime;
	private String[] legalAuthenticatorList;
	private String sourcePatientId;
	private String[] sourcePatientInfoList;
	private String classCode;
	private String typeCode;
	private String practiceSettingCode;
	private String[] eventCodeList;
	private String confidentialityCode;
	private String healthCareFacilityTypeCode;
	private String formatCode;	
	private XDSFile xdsfile;
	private String symbolicDocumentName;
	
	private String classCodeValue;
	private String confidentialityCodeValue;
	private String formatCodeValue;
	private String healthCareFacilityTypeCodeValue;
	private String practiceSettingCodeValue;
	private String typeCodeValue;
	
	private String docAssociationType;
	private String parentDocId;
	private boolean docHasAssociation;
	
	
	/**
	 * @param documentTitle
	 * @param patientId
	 * @param authorDepartment
	 * @param authorInstitution
	 * @param authorPerson
	 * @param serviceStartTime
	 * @param serviceStopTime
	 * @param languageCode
	 * @param creationTime
	 * @param legalAuthenticatorList
	 * @param sourcePatientId
	 * @param sourcePatientInfoList
	 * @param classCode
	 * @param typeCode
	 * @param practiceSettingCode
	 * @param eventCodeList
	 * @param confidentialityCode
	 * @param healthCareFacilityTypeCode
	 * @param formatCode
	 * @param xdsfile
	 */
	public XDSDocument(String documentTitle, String patientId,
			String authorDepartment, String authorInstitution,
			String authorPerson, String serviceStartTime,
			String serviceStopTime, String languageCode, String creationTime,
			String[] legalAuthenticatorList, String sourcePatientId,
			String[] sourcePatientInfoList, String classCode, 
			String typeCode, String practiceSettingCode,
			String[] eventCodeList, String confidentialityCode,
			String healthCareFacilityTypeCode, String formatCode,
			XDSFile xdsfile) throws XDSException {
		this.documentTitle = documentTitle;
		this.patientId = patientId;
		this.docUniqueId = xdsfile.getUniqueId();
		this.authorDepartment = authorDepartment;
		this.authorInstitution = authorInstitution;
		this.authorPerson = authorPerson;
		this.serviceStartTime = serviceStartTime;
		this.serviceStopTime = serviceStopTime;
		this.languageCode = languageCode;
		this.creationTime = creationTime;
		this.legalAuthenticatorList = legalAuthenticatorList;
		this.sourcePatientId = sourcePatientId;
		this.sourcePatientInfoList = sourcePatientInfoList;
		this.classCode = classCode;
		this.typeCode = typeCode;
		this.practiceSettingCode = practiceSettingCode;
		this.eventCodeList = eventCodeList;
		this.confidentialityCode = confidentialityCode;
		this.healthCareFacilityTypeCode = healthCareFacilityTypeCode;
		this.formatCode = formatCode;
		this.xdsfile = xdsfile;
		this.symbolicDocumentName = xdsfile.getUUID();
		this.docHasAssociation = false;
		
		if ( languageCode == null ) {
			log.error("Required Slot (languageCode) value is null");
			throw new IllegalArgumentException("Required Slot (languageCode) value is null");
		}
		if ( creationTime == null ) {
			log.error("Required Slot (creationTime) value is null");
			throw new IllegalArgumentException("Required Slot (creationTime) value is null");
		}
		if ( sourcePatientId == null ) {
			log.error("Required Slot (sourcePatientId) value is null");
			throw new IllegalArgumentException("Required Slot (sourcePatientId) value is null");
		}
		if ( sourcePatientInfoList == null || sourcePatientInfoList.length < 1 ) {
			log.error("Required Slot (sourcePatientInfo) value is null");
			throw new IllegalArgumentException("Required Slot (sourcePatientInfo) value is null");
		}
		if ( patientId == null || patientId.equals("") ) {
			log.error("Error trying to create ExternalIdentifier, required argument (patientId) is null or empty");
			throw new IllegalArgumentException("Error trying to create ExternalIdentifier, required argument (patientId) is null or empty");			
		}
		
		if ( docUniqueId == null || docUniqueId.equals("") ) {
			log.error("Unable to generate valid uniqueId");
			throw new XDSException("Unable to generate valid uniqueId");
		}
		
		if ( classCode == null || classCode.equals("")) {
			log.error("Required Classification (classCode) is null or an empty string");
			throw new IllegalArgumentException("Required Classification (classCode) is null or an empty string");
		} else {
			classCodeValue = XDSConstants.getClassCodeValue(classCode);
				if ( classCodeValue == null || classCodeValue.equals("")) {
					log.error("Null or empty code value returned for classCode (" + classCode + ")");
					throw new XDSException("Null or empty code value returned for classCode (" + classCode + ")");
				}
		}

		if ( confidentialityCode == null || confidentialityCode.equals("")) {
			log.error("Required Classification (confidentialtyCode) is null or an empty string");
			throw new IllegalArgumentException("Required Classification (confidentialtyCode) is null or an empty string");
		} else {
			confidentialityCodeValue = XDSConstants.getConfidentialityCodeValue(confidentialityCode);
				if ( confidentialityCodeValue == null || confidentialityCodeValue.equals("")) {
					log.error("Null or empty code value returned for confidentialityCode (" + confidentialityCode + ")");
					throw new XDSException("Null or empty code value returned for confidentialityCode (" + confidentialityCode + ")");
				}
		}
		
		if ( formatCode == null || formatCode.equals("")) {
			log.error("Required Classification (formatCode) is null or an empty string");
			throw new IllegalArgumentException("Required Classification (formatCode) is null or an empty string");
		} else {
			formatCodeValue = XDSConstants.getFormatCodeValue(formatCode);
				if ( formatCodeValue == null || formatCodeValue.equals("")) {
					log.error("Null or empty code value returned for formatCode (" + formatCode + ")");
					throw new XDSException("Null or empty code value returned for formatCode (" + formatCode + ")");
				}
		}
		
		if ( healthCareFacilityTypeCode == null || healthCareFacilityTypeCode.equals("")) {
			log.error("Required Classification (healthCareFacilityTypeCode) is null or an empty string");
			throw new IllegalArgumentException("Required Classification (healthCareFacilityTypeCode) is null or an empty string");
		} else {
			healthCareFacilityTypeCodeValue = XDSConstants.getHealthCareFacilityTypeCodeValue(healthCareFacilityTypeCode);
				if ( healthCareFacilityTypeCodeValue == null || healthCareFacilityTypeCodeValue.equals("")) {
					log.error("Null or empty code value returned for healthCareFacilityTypeCode (" + healthCareFacilityTypeCode + ")");
					throw new XDSException("Null or empty code value returned for healthCareFacilityTypeCode (" + healthCareFacilityTypeCode + ")");
				}
		}
		
		if ( practiceSettingCode == null || practiceSettingCode.equals("")) {
			log.error("Required Classification (practiceSettingCode) is null or an empty string");
			throw new IllegalArgumentException("Required Classification (practiceSettingCode) is null or an empty string");
		} else {
			practiceSettingCodeValue = XDSConstants.getPracticeSettingCodeValue(practiceSettingCode);
				if ( practiceSettingCodeValue == null || practiceSettingCodeValue.equals("")) {
					log.error("Null or empty code value returned for practiceSettingCode (" + practiceSettingCode + ")");
					throw new XDSException("Null or empty code value returned for practiceSettingCode (" + practiceSettingCode + ")");
				}
		}
		
		if ( typeCode == null || typeCode.equals("")) {
			log.error("Required Classification (typeCode) is null or an empty string");
			throw new IllegalArgumentException("Required Classification (typeCode) is null or an empty string");
		} else {
			typeCodeValue = XDSConstants.getDocEntryTypeCodeValue(typeCode);
				if ( typeCodeValue == null || typeCodeValue.equals("")) {
					log.error("Null or empty code value returned for typeCode (" + typeCode + ")");
					throw new XDSException("Null or empty code value returned for typeCode (" + typeCode + ")");
				}
		}		
	}

	
	public void setDocumentAssociation(String docAssociationType, String parentDocId) {
		if ( docAssociationType == null ) {
			throw new IllegalArgumentException("Null document association argument provided");			
		} else if ( parentDocId == null ) {
			throw new IllegalArgumentException("Null parentDocId argument provided");				
		}
		
		if ( ! XDSConstants.isValidDocAssociationType(docAssociationType)) {
					throw new IllegalArgumentException("Invalid document association type [" + docAssociationType + " ] provided");
		}
		this.docHasAssociation = true;
		this.docAssociationType = docAssociationType;
		this.parentDocId = parentDocId;
	}
	
	public boolean docHasAssociation() {
		return docHasAssociation;
	}
	
	public String getDocAssociationType() {
		return docAssociationType;
	}
	
	public String getParentDocId() {
		return parentDocId;
	}
	
	public String getSymbolicDocName() {
		return symbolicDocumentName;
	}
	
	public XDSFile getXDSFile() {
		return xdsfile;
	}
	
	public Element getElement() throws XDSException {
		
		List externalIdentifierList = getExternalIdentifiers();
		List slotList = getSlots();
		List classificationList = getClassifications();
		
		String objectType = 
			XDSConstants.getObjectType(XDSConstants.XDS_DOCUMENT_ENTRY_ELEMENT);		

		ExtrinsicObject obj = 
			new ExtrinsicObject(symbolicDocumentName,
					xdsfile.getMimeType(),
					objectType,
					documentTitle,
					externalIdentifierList,
					slotList,
					classificationList);
		
		return obj.getElement();
		
	}

	private List getExternalIdentifiers() {
		
		List externalIdList = new ArrayList();

		ExternalIdentifier patientIdEI =  
			new ExternalIdentifier(XDSConstants.getExternalIdentifier(XDSConstants.XDS_DOCUMENT_ENTRY_PATIENT_ID_ELEMENT),
				patientId, "XdsDocumentEntry.patientId");
		externalIdList.add(patientIdEI);

		ExternalIdentifier docUniqueIdEI = 
			new ExternalIdentifier(XDSConstants.getExternalIdentifier(XDSConstants.XDS_DOCUMENT_ENTRY_UNIQUE_ID_ELEMENT),
				docUniqueId, "XdsDocumentEntry.uniqueId");
		externalIdList.add(docUniqueIdEI);
		
		return externalIdList;
	}
	
	
	private List getSlots() {

		List slotList = new ArrayList();
		
		//Start of required if available slot entries
		if ( authorDepartment != null ) {
			Slot authorDeptSlot = 
				new Slot("authorDepartment", new String[]{authorDepartment});			
			slotList.add(authorDeptSlot);
		}
		if ( authorInstitution != null ) {
			Slot authorInstitutionSlot = 
				new Slot("authorInstitution", new String[] {authorInstitution});
			slotList.add(authorInstitutionSlot);
		}
		if ( authorPerson != null ) {
			Slot authorPersonSlot = 
				new Slot("authorPerson", new String[] {authorPerson});
			slotList.add(authorPersonSlot);
		}
		if ( serviceStartTime != null ) {
			Slot serviceStartTimeSlot = 
				new Slot("serviceStartTime", new String[] {serviceStartTime});
			slotList.add(serviceStartTimeSlot);
		}
		if ( serviceStopTime != null ) {
			Slot serviceStopTimeSlot = 
				new Slot("serviceStopTime", new String[] {serviceStopTime});
			slotList.add(serviceStopTimeSlot);
		}
		
		//Start optional slot entries
		if ( legalAuthenticatorList != null && legalAuthenticatorList.length > 0 ) {
			Slot legalAuthenticatorSlot = 
				new Slot("legalAuthenticator", legalAuthenticatorList);
			slotList.add(legalAuthenticatorSlot);
		}
		
		//Start required slot entries
		Slot languageCodeSlot = 
			new Slot("languageCode", new String[] {languageCode});
		slotList.add(languageCodeSlot);
		
		Slot creationTimeSlot = 
			new Slot("creationTime", new String[] {creationTime});
		slotList.add(creationTimeSlot);
		
		Slot sourcePatientIdSlot = 
			new Slot("sourcePatientId", new String[] {sourcePatientId});
		slotList.add(sourcePatientIdSlot);
		
		Slot sourcePatientInfoSlot = 
			new Slot("sourcePatientInfo", sourcePatientInfoList);
		slotList.add(sourcePatientInfoSlot);
		
		
		return slotList;
		
	}
	
	private List getClassifications() throws XDSException {
		
		List classificationList = new ArrayList();

		//Start required Classifications
		//classCode
		Classification classCodeClassification = 
			new Classification(XDSConstants.UUID_CLASS_CODE,
					symbolicDocumentName,
					classCode,
					classCodeValue,
					XDSConstants.CODE_SLOT_NAME,
					new String[] {"Connect-a-thon classCodes"},
					null);
		classificationList.add(classCodeClassification);
		//confidentialityCode
		Classification confidentialityCodeClassification = 
			new Classification(XDSConstants.UUID_CONFIDENTIALITY_CODE,
					symbolicDocumentName,
					confidentialityCode,
					confidentialityCodeValue,
					XDSConstants.CODE_SLOT_NAME,
					new String[] {"Connect-a-thon confidentialityCodes"},
					null);
		classificationList.add(confidentialityCodeClassification);
		//formatCode
		Classification formatCodeClassification = 
			new Classification(XDSConstants.UUID_FORMAT_CODE,
					symbolicDocumentName,
					formatCode,
					formatCodeValue,
					XDSConstants.CODE_SLOT_NAME,
					new String[] {"Connect-a-thon formatCodes"},
					null);
		classificationList.add(formatCodeClassification);
		
		//healthCareFacilityTypeCode
		Classification healthCareFacilityTypeCodeClassification = 
			new Classification(XDSConstants.UUID_HEALTH_CARE_FACILITY_TYPE_CODE,
					symbolicDocumentName,
					healthCareFacilityTypeCode,
					healthCareFacilityTypeCodeValue,
					XDSConstants.CODE_SLOT_NAME,
					new String[] {"Connect-a-thon healthCareFacilityTypeCodes"},
					null);
		classificationList.add(healthCareFacilityTypeCodeClassification);
		
		//practiceSettingCode
		Classification practiceSettingCodeClassification = 
			new Classification(XDSConstants.UUID_PRACTICE_SETTING_CODE,
					symbolicDocumentName,
					practiceSettingCode,
					practiceSettingCodeValue,
					XDSConstants.CODE_SLOT_NAME,
					new String[] {"Connect-a-thon practiceSettingCodes"},
					null);
		classificationList.add(practiceSettingCodeClassification);
		
		//typeCode
		Classification typeCodeClassification = 
			new Classification(XDSConstants.UUID_TYPE_CODE,
					symbolicDocumentName,
					typeCode,
					typeCodeValue,
					XDSConstants.CODE_SLOT_NAME,
					new String[] {"Connect-a-thon typeCodes"},
					null);
		classificationList.add(typeCodeClassification);
		//End required Classifications
		
		
		//Start optional Classifications
		//eventCodeList
		if ( eventCodeList != null && eventCodeList.length > 0 ) {
			
			for ( int i = 0; i < eventCodeList.length; i++ ) {
				if ( eventCodeList[i] == null || eventCodeList[i].equals("") ) {
					log.error("Null or empty eventCode value");
					throw new IllegalArgumentException("Null or empty eventCode value");					
				}
				
				String codeValue = XDSConstants.getEventCodeValue(eventCodeList[i]);
				if ( codeValue == null || codeValue.equals("") ) {
					log.error("Null or empty code value returned for eventCode (" + eventCodeList[i] + ")");
					throw new XDSException("Null or empty code value returned for eventCode (" + eventCodeList[i] + ")");
				}
				Classification eventCodeClassification = 
					new Classification(XDSConstants.UUID_EVENT_CODE,
							symbolicDocumentName,
							eventCodeList[i],
							codeValue,
							XDSConstants.CODE_SLOT_NAME,
							new String[] {"Connect-a-thon eventCodes"},
							null);
				classificationList.add(eventCodeClassification);
			}
		}
		
		return classificationList;
		
	}
}
