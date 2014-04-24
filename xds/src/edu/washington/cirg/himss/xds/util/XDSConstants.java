/*
 * Copyright 2004-2007 (C) University of Washington. All Rights Reserved.
 * Created on Dec 20, 2004
 * University of Washington, CIRG
 * $Id: XDSConstants.java,v 1.7 2005/02/21 16:42:56 ddrozd Exp $
 */
package edu.washington.cirg.himss.xds.util;


import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Set;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.dom4j.DocumentException;
import org.iso_relax.verifier.Schema;
import org.iso_relax.verifier.VerifierConfigurationException;
import org.iso_relax.verifier.VerifierFactory;
import org.xml.sax.SAXException;

import edu.washington.cirg.himss.xds.XDSConfigurationException;



/** Static constants class.  Initialized once on application load and
 * subsequently shared by all application classes.
 * @author <a href="mailto:ddrozd@u.washington.edu">Dan Drozd</a>
 * @version $Revision: 1.7 $
 *
 */
public final class XDSConstants {
	
	private static boolean constantsAreLoaded = false;
	private static Log log = LogFactory.getLog(XDSConstants.class);
	
	private static final String PROPS_FILE = "xds.properties";
	
	//ConnectAThon config
	public static String VENDOR_CODE;
	public static String ROOT_OID_PART;
	public static String CONNECTATHON_OID_PART;
	public static String UW_OID_PART;
	public static String OID_ROOT;
	public static String PATIENT_ID_SUFFIX;
	public static boolean NAMESPACE_SUPPORT = true;
	public static boolean LOG_PERFORMANCE_DATA = false;
	public static String YCARDS_DIR;
	public static String REPO_URL;
	public static String SECURE_REPO_URL;
	public static String CCR_PURPOSE_DESCRIPTION;
	public static String CCR_PURPOSE_CODE_SYSTEM;
	public static String CCR_PURPOSE_CODE_VERSION;
	public static String CCR_PURPOSE_CODE_VALUE;
	public static String CONFORMANCE_PROFILE_FILE;

	//UW Config
	public static String PHR_DATASOURCE_NAME;
	public static String HIMSS_DATASOURCE_NAME;
	public static String CCR_SCHEMA_LOCATION;
	public static String CCR_INFO_SYSTEM_NAME;
	public static String CCR_INFO_SYSTEM_TYPE;
	public static String CCR_INFO_SYSTEM_VERSION;

	//Hard-coded UUIDs
	public static String UUID_TYPE_CODE;
	public static String UUID_PRACTICE_SETTING_CODE;
	public static String UUID_CLASS_CODE;
	public static String UUID_EVENT_CODE;
	public static String UUID_CONFIDENTIALITY_CODE;
	public static String UUID_HEALTH_CARE_FACILITY_TYPE_CODE;	
	public static String UUID_FORMAT_CODE;
	public static String UUID_FOLDER_CODE_LIST;
	public static String UUID_HAS_MEMBER_ASSN;
	public static String UUID_CONTENT_TYPE_CODE;
	public static String UUID_PATIENT_IDENT_SCHEME_CODE;
	public static String UUID_DOCUMENT_OBJECT_TYPE_CODE;
	public static String UUID_TYPE_CODE_IDENT_SCHEME_CODE;
	public static String UUID_AUTHOR_IDENT_SCHEME_CODE;
	public static String UUID_EVENT_CODE_IDENT_SCHEME_CODE;
	public static String UUID_UNIQUE_ID_IDENT_SCHEME_CODE;
	public static String UUID_FIND_DOCUMENTS_STORED_QUERY_CODE;
	public static String UUID_PH_FIND_DOCUMENTS_STORED_QUERY_CODE;
	public static String UUID_FIND_SUBMISSION_SETS_STORED_QUERY_CODE;
	public static String UUID_FIND_FOLDERS_STORED_QUERY_CODE;
	public static String UUID_GET_ALL_STORED_QUERY_CODE;
	public static String UUID_GET_DOCUMENTS_STORED_QUERY_CODE;
	public static String UUID_GET_FOLDERS_STORED_QUERY_CODE;
	public static String UUID_GET_ASSOCIATIONS_STORED_QUERY_CODE;
	public static String UUID_GET_DOCUMENTS_AND_ASSOCIATIONS_STORED_QUERY_CODE;
	public static String UUID_GET_SUBMISSION_SETS_STORED_QUERY_CODE;
	public static String UUID_GET_SUBMISSION_SET_AND_CONTENTS_STORED_QUERY_CODE;
	public static String UUID_GET_FOLDER_AND_CONTENTS_STORED_QUERY_CODE;
	public static String UUID_GET_FOLDERS_FOR_DOCUMENT_STORED_QUERY_CODE;
	public static String UUID_GET_RELATED_DOCUMENTS_STORED_QUERY_CODE;
	
	//Servlet Config
	public static final String DATASOURCE_PARAM_NAME = "DataSource";

	//CCR Config
	public static String SYMBOLIC_DOC_NAME_ROOT;
	public static String SYMBOLIC_FOLDER_NAME;
	public static String CCR_FOLDER_CODE;
	public static String CCR_SS_CONTENT_TYPE_CODE;
	public static String CCR_DE_FORMAT_CODE;
	public static String CCR_DE_CLASS_CODE;
	public static String CCR_DE_LANGUAGE_CODE;
	public static String CCR_DE_MIME_TYPE;
	public static String CCR_DE_TYPE_CODE;
	public static String CCR_DE_CONFIDENTIALITY_CODE;
	public static String CCR_DE_PRACTICE_SETTING_CODE;
	public static String CCR_DE_HEALTH_CARE_FACILITY_TYPE_CODE;
	public static String CCR_AUTHOR_INSTITUTION;
	public static String CCR_AUTHOR_DEPARTMENT;
	public static String CCR_DOCUMENT_TITLE;
	public static String CCR_SUBMISSION_SET_DESC;
	public static String CCR_ORGANIZATION_NAME;
	public static String CCR_ORGANIZATION_SPECIALTY;
	
	// TLS Config
	public static String TLS_KEYSTORE;
	public static String TLS_KEYSTORE_PASS;
	public static String TLS_CERTFILE;
	public static String TLS_CERTFILE_PASS;

	public static Schema CCR_MSV_COMPILED_SCHEMA;
	
			
	//public static HashMap hmUUIDs;
	
	private static Map hmDocEntryClassCodes;
	private static Map hmDocEntryConfidentialityCodes;
	private static Map hmSubSetContentTypeCodes;
	private static Map hmDocEntryHealthCareFacilityCodes;
	private static Map hmExternalIdentifiers;
	private static Map hmDocEntryFormatCodes;
	private static Map hmDocEntryMimeTypeCodes;
	private static Map hmDocEntryPracticeSettingCodes;
	private static Map hmDocEntryTypeCodes;
	private static Map hmObjectTypeCodes;
	public static Map hmEventCodes;
	private static List externalIdentifierList;
	
	
	
	public static final String XDS_DOCUMENT_ENTRY_ELEMENT = "XDSDocumentEntry";
	public static final String XDS_SUBMISSION_SET_ELEMENT = "XDSSubmissionSet";
	public static final String XDS_FOLDER_ELEMENT = "XDSFolder";
	public static final String XDS_OBJECT_TYPE_ELEMENT = "XDSObjectType";
	public static final String XDS_DOCUMENT_ENTRY_STUB_ELEMENT = "XDSDocumentEntryStub";
	public static final String XDS_CLASSIFICATION_ELEMENT = "XDSClassification";
	public static final String XDS_ASSOCIATION_TYPE_APND = "APND";
	public static final String XDS_ASSOCIATION_TYPE_RPLC = "RPLC";
	public static final String XDS_ASSOCIATION_TYPE_XFRM = "XFRM";
	public static final String XDS_DOCUMENT_ENTRY_CLASS_CODE_ELEMENT = "XDSDocumentEntry.classCode";
	public static final String XDS_DOCUMENT_ENTRY_EVENT_CODE_LIST_ELEMENT = "XDSDocumentEntry.eventCodeList";
	public static final String XDS_DOCUMENT_ENTRY_CONFIDENTIALITY_CODE_ELEMENT = "XDSDocumentEntry.confidentialityCode";
	public static final String XDS_DOCUMENT_ENTRY_HEALTH_CARE_FACILITY_TYPE_CODE_ELEMENT = "XDSDocumentEntry.healthCareFacilityTypeCode";
	public static final String XDS_DOCUMENT_ENTRY_FORMAT_CODE_ELEMENT = "XDSDocumentEntry.formatCode";
	public static final String XDS_SUBMISSION_SET_CONTENT_TYPE_CODE_ELEMENT = "XDSSubmissionSet.contentTypeCode";
	public static final String XDS_FOLDER_CODE_LIST_ELEMENT = "XDSFolder.codeList";
	public static final String XDS_DOCUMENT_ENTRY_PRACTICE_SETTING_CODE_ELEMENT = "XDSDocumentEntry.practiceSettingCode";
	public static final String XDS_DOCUMENT_ENTRY_TYPE_CODE_ELEMENT = "XDSDocumentEntry.typeCode";
	public static final String XDS_SUBMISSION_SET_SOURCE_ID_ELEMENT = "XDSSubmissionSet.sourceId";
	public static final String XDS_SUBMISSION_SET_UNIQUE_ID_ELEMENT = "XDSSubmissionSet.uniqueId";
	public static final String XDS_FOLDER_PATIENT_ID_ELEMENT = "XDSFolder.patientId";
	public static final String XDS_FOLDER_UNIQUE_ID_ELEMENT = "XDSFolder.uniqueId";
	public static final String XDS_DOCUMENT_ENTRY_PATIENT_ID_ELEMENT = "XDSDocumentEntry.patientId";
	public static final String XDS_DOCUMENT_ENTRY_UNIQUE_ID_ELEMENT = "XDSDocumentEntry.uniqueId";
	public static final String CODE_SLOT_NAME = "codingScheme";
	
	//Private default constructor, so class can't be instantiated 
	private XDSConstants() { }

	private static Properties loadProperties (String name)
	 	throws IOException {
	 	
	 	ClassLoader loader = Thread.currentThread().getContextClassLoader();
	 	if (name == null) {
	 		log.error("Unable to load properties file, no file specified");
	 		throw new IllegalArgumentException ("Unable to load properties file, no file specified");
	 	}
	 	Properties props = null;
	 	
	 	InputStream in = loader.getResourceAsStream(name);
	 	if (in != null)
	 	{
	 		props = new Properties ();
	 		props.load (in);
	 	}
	 	return props;
	 }
	        

	
	public static synchronized void loadConstants() 
	throws XDSConfigurationException {
		
		if ( constantsAreLoaded ) {
			return;
		}
		
		try {
			Properties props = loadProperties(PROPS_FILE);
			
			//ConnectAThon config
			VENDOR_CODE = props.getProperty("VendorCode");
			ROOT_OID_PART = props.getProperty("RootOidPart");
			CONNECTATHON_OID_PART =props.getProperty("ConnectAThonOidPart");
			UW_OID_PART = props.getProperty("UWOidPart");
			OID_ROOT = ROOT_OID_PART + CONNECTATHON_OID_PART + UW_OID_PART;
			PATIENT_ID_SUFFIX = props.getProperty("PatientIdSuffix");
			PHR_DATASOURCE_NAME = props.getProperty("PHRDataSourceName");
			HIMSS_DATASOURCE_NAME = props.getProperty("HIMSSDataSourceName");
			YCARDS_DIR = props.getProperty("yCardsDir");
			REPO_URL = props.getProperty("repoUrl");
			SECURE_REPO_URL = props.getProperty("secureRepoUrl");
			
			CCR_INFO_SYSTEM_NAME = props.getProperty("CCRInformationSystemName");
			CCR_INFO_SYSTEM_TYPE = props.getProperty("CCRInformationSystemType");
			CCR_INFO_SYSTEM_VERSION = props.getProperty("CCRInformationSystemVersion");

			CCR_ORGANIZATION_NAME = props.getProperty("CCROrganizationName");
			CCR_ORGANIZATION_SPECIALTY = props.getProperty("CCROrganizationSpecialty");
			
			CCR_PURPOSE_DESCRIPTION = props.getProperty("CCRPurposeDescription");
			CCR_PURPOSE_CODE_SYSTEM = props.getProperty("CCRPurposeCodeSystem");;
			CCR_PURPOSE_CODE_VERSION = props.getProperty("CCRPurposeCodeVersion");;
			CCR_PURPOSE_CODE_VALUE = props.getProperty("CCRPurposeCodeValue");;

			
			String ccrXMLSchema = props.getProperty("CCRSchemaLocation");
			
			ClassLoader loader = Thread.currentThread().getContextClassLoader(); 
			URL schemaURL = loader.getResource(ccrXMLSchema.trim());

			if ( schemaURL == null ) {
				throw new FileNotFoundException("Unable to locate the CCR XML Schema [ " + ccrXMLSchema + " ], it is not on the classpath");
			} else {
				log.info("Found XML Schema at " + schemaURL.toString());
				CCR_SCHEMA_LOCATION = schemaURL.toString();
			}
			
		    VerifierFactory factory = new com.sun.msv.verifier.jarv.TheFactoryImpl();
		    CCR_MSV_COMPILED_SCHEMA = factory.compileSchema( CCR_SCHEMA_LOCATION );

			
			String sNameSpace = props.getProperty("NamespaceSupport");
			
			if ( sNameSpace != null && sNameSpace.equalsIgnoreCase("true")) {
				NAMESPACE_SUPPORT = true;
			} else {
				NAMESPACE_SUPPORT = false;
			}

			String sLogPerformanceData = props.getProperty("LogPerformanceData");
			
			if ( sLogPerformanceData != null && sLogPerformanceData.equalsIgnoreCase("true")) {
				LOG_PERFORMANCE_DATA = true;
			} else {
				LOG_PERFORMANCE_DATA = false;
			}
				
			
			String objectTypeCodeFile = props.getProperty("objectTypeFile");
			String docEntryClassCodeFile = props.getProperty("classCodeFile");
			String docEntryConfidentialityCodeFile = props.getProperty("confidentialityCodeFile");
			String subSetContentTypeCodeFile = props.getProperty("contentTypeCodeFile");
			String docEntryHealthCareFacilityTypeCodeFile = props.getProperty("healthCareFacilityTypeCodeFile");
			String externalIdentifierCodeFile = props.getProperty("externalIdentifierFile");
			String docEntryFormatCodeFile = props.getProperty("formatCodeFile");
			String docEntryMimeTypeCodeFile = props.getProperty("mimeTypeCodeFile");
			String docEntryPracticeSettingCodeFile = props.getProperty("practiceSettingCodeFile");
			String docEntryTypeCodeFile = props.getProperty("typeCodeFile");
			
			//UW XDS Config
			SYMBOLIC_DOC_NAME_ROOT = props.getProperty("SymbolicDocumentName");
			SYMBOLIC_FOLDER_NAME = props.getProperty("SymbolicFolderName");
			
			//UW CCR Config
			CCR_FOLDER_CODE = props.getProperty("CCRFolderCode");
			CCR_SS_CONTENT_TYPE_CODE = props.getProperty("CCRSSContentTypeCode");
			CCR_DE_FORMAT_CODE = props.getProperty("CCRDEFormatCode");
			CCR_DE_CLASS_CODE = props.getProperty("CCRDEClassCode");
			CCR_DE_LANGUAGE_CODE = props.getProperty("CCRDELanguageCode");
			CCR_DE_MIME_TYPE = props.getProperty("CCRDEMimeType");
			CCR_DE_TYPE_CODE = props.getProperty("CCRDETypeCode");
			CCR_DE_CONFIDENTIALITY_CODE = props.getProperty("CCRDEConfidentialityCode");
			CCR_DE_PRACTICE_SETTING_CODE = props.getProperty("CCRDEPracticeSettingCode");
			CCR_DE_HEALTH_CARE_FACILITY_TYPE_CODE = props.getProperty("CCRDEHealthCareFacilityTypeCode");
			CCR_AUTHOR_INSTITUTION = props.getProperty("CCRAuthorInstitution");
			CCR_AUTHOR_DEPARTMENT = props.getProperty("CCRAuthorDepartment");
			CCR_DOCUMENT_TITLE = props.getProperty("CCRDETitle");
			CCR_SUBMISSION_SET_DESC = props.getProperty("CCRSubmissionSetDescription");
			UUID_CLASS_CODE = props.getProperty("classCodeUUID");
			UUID_EVENT_CODE = props.getProperty("eventCodeUUID");
			UUID_CONFIDENTIALITY_CODE = props.getProperty("confidentialityCodeUUID");
			UUID_HEALTH_CARE_FACILITY_TYPE_CODE = props.getProperty("healthCareFacilityTypeCodeUUID");
			UUID_FORMAT_CODE = props.getProperty("formatCodeUUID");
			UUID_FOLDER_CODE_LIST = props.getProperty("folderCodeListUUID");
			UUID_HAS_MEMBER_ASSN = props.getProperty("hasMemberUUID");
			UUID_CONTENT_TYPE_CODE = props.getProperty("contentTypeCodeUUID");
			UUID_PATIENT_IDENT_SCHEME_CODE = props.getProperty("patientIdIdentificationSchemeUUID");
			UUID_DOCUMENT_OBJECT_TYPE_CODE = props.getProperty("documentObjectTypeCodeUUID");
			UUID_PRACTICE_SETTING_CODE = props.getProperty("practiceSettingCodeUUID");
			UUID_TYPE_CODE_IDENT_SCHEME_CODE = props.getProperty("typeCodeIdentificationSchemeUUID");
			UUID_AUTHOR_IDENT_SCHEME_CODE = props.getProperty("authorIdentificationSchemeUUID");
			UUID_EVENT_CODE_IDENT_SCHEME_CODE = props.getProperty("eventCodeIdentificationSchemeUUID");
			UUID_UNIQUE_ID_IDENT_SCHEME_CODE = props.getProperty("uniqueIdIdentificationSchemeUUID");
			UUID_FIND_DOCUMENTS_STORED_QUERY_CODE = props.getProperty("findDocumentsStoredQueryUUID");
			UUID_PH_FIND_DOCUMENTS_STORED_QUERY_CODE = props.getProperty("phFindDocumentsStoredQueryUUID");
			UUID_FIND_SUBMISSION_SETS_STORED_QUERY_CODE = props.getProperty("findSubmissionSetsStoredQueryUUID");
			UUID_FIND_FOLDERS_STORED_QUERY_CODE = props.getProperty("findFoldersStoredQueryUUID");
			UUID_GET_ALL_STORED_QUERY_CODE = props.getProperty("getAllStoredQueryUUID");
			UUID_GET_DOCUMENTS_STORED_QUERY_CODE = props.getProperty("getDocumentsStoredQueryUUID");
			UUID_GET_FOLDERS_STORED_QUERY_CODE = props.getProperty("getFoldersStoredQueryUUID");
			UUID_GET_ASSOCIATIONS_STORED_QUERY_CODE = props.getProperty("getAssociationsStoredQueryUUID");
			UUID_GET_DOCUMENTS_AND_ASSOCIATIONS_STORED_QUERY_CODE = props.getProperty("getDocumentsAndAssociationsStoredQueryUUID");
			UUID_GET_SUBMISSION_SETS_STORED_QUERY_CODE = props.getProperty("getSubmissionSetsStoredQueryUUID");
			UUID_GET_SUBMISSION_SET_AND_CONTENTS_STORED_QUERY_CODE = props.getProperty("getSubmissionSetAndContentsStoredQueryUUID");
			UUID_GET_FOLDER_AND_CONTENTS_STORED_QUERY_CODE = props.getProperty("getFolderAndContentsStoredQueryUUID");
			UUID_GET_FOLDERS_FOR_DOCUMENT_STORED_QUERY_CODE = props.getProperty("getFoldersForDocumentStoredQueryUUID");
			UUID_GET_RELATED_DOCUMENTS_STORED_QUERY_CODE = props.getProperty("getRelatedDocumentsStoredQueryUUID");
			CONFORMANCE_PROFILE_FILE = props.getProperty("conformanceProfileFile");
			
			// TLS Config
			TLS_KEYSTORE = props.getProperty("keystore");
			TLS_KEYSTORE_PASS = props.getProperty("keystorepass");
			TLS_CERTFILE = props.getProperty("certsfile");
			TLS_CERTFILE_PASS = props.getProperty("certsfilepass");

			hmObjectTypeCodes = XDSPropsFileReader.load(objectTypeCodeFile);
			hmDocEntryClassCodes = XDSPropsFileReader.load(docEntryClassCodeFile);	
			hmDocEntryConfidentialityCodes = XDSPropsFileReader.load(docEntryConfidentialityCodeFile);
			hmSubSetContentTypeCodes = XDSPropsFileReader.load(subSetContentTypeCodeFile);
			hmDocEntryHealthCareFacilityCodes = XDSPropsFileReader.load(docEntryHealthCareFacilityTypeCodeFile);
			hmExternalIdentifiers = XDSPropsFileReader.load(externalIdentifierCodeFile);
			hmDocEntryFormatCodes = XDSPropsFileReader.load(docEntryFormatCodeFile);
			hmDocEntryMimeTypeCodes = XDSPropsFileReader.load(docEntryMimeTypeCodeFile);
			hmDocEntryPracticeSettingCodes = XDSPropsFileReader.load(docEntryPracticeSettingCodeFile);
			hmDocEntryTypeCodes = XDSPropsFileReader.load(docEntryTypeCodeFile);
			
			//Print loaded code values if in debug
//			if ( log.isDebugEnabled() ) {
//				Map codeHashes = new HashMap();
//				codeHashes.put("ObjectTypeCodes", hmObjectTypeCodes);
//				codeHashes.put("ClassCodes", hmDocEntryClassCodes);
//				codeHashes.put("ConfidentialityCodes", hmDocEntryConfidentialityCodes);
//				codeHashes.put("ContentTypeCodes", hmSubSetContentTypeCodes);
//				codeHashes.put("healthCareFacilityType", hmDocEntryHealthCareFacilityCodes);
//				codeHashes.put("externalIdentifiers", hmExternalIdentifiers);
//				codeHashes.put("formatCodes", hmDocEntryFormatCodes);
//				codeHashes.put("mimeTypes", hmDocEntryMimeTypeCodes);
//				codeHashes.put("practiceSettingCodes", hmDocEntryPracticeSettingCodes);
//				codeHashes.put("typeCodes", hmDocEntryTypeCodes);
//				
//				Set keys = codeHashes.keySet();
//				Iterator iterator = keys.iterator();
//				String key;
//				while ( iterator.hasNext() ) {
//					key = (String)iterator.next();
//					HashMap m = (HashMap)codeHashes.get(key);
//					log.trace("Listing loaded " + key + " values....");
//					Set codeSet = m.keySet();
//					Iterator i2 = codeSet.iterator();
//					String code;
//					while ( i2.hasNext() ) {
//						code = (String)i2.next();
//						log.trace(code + "=" + m.get(code));
//					}
//				}
//			}
			
		} catch (MalformedURLException e) {
			log.error(e);
			throw new XDSConfigurationException(e);
		} catch (DocumentException e) {
			log.error(e);
			throw new XDSConfigurationException(e);
		} catch (FileNotFoundException e) {
			log.error(e);
			throw new XDSConfigurationException(e);
		} catch (IOException e) {
			log.error(e);
			throw new XDSConfigurationException(e);
		} catch (VerifierConfigurationException e) {
			log.error(e);
			throw new XDSConfigurationException(e);
		} catch (SAXException e) {
			log.error(e);
			throw new XDSConfigurationException(e);
		}
		
		UUID_TYPE_CODE = 
			(String)hmExternalIdentifiers.get(XDS_DOCUMENT_ENTRY_TYPE_CODE_ELEMENT);
		UUID_PRACTICE_SETTING_CODE =
			(String)hmExternalIdentifiers.get(XDS_DOCUMENT_ENTRY_PRACTICE_SETTING_CODE_ELEMENT);
		
		externalIdentifierList = new ArrayList();
		Set keys = hmExternalIdentifiers.keySet();
		Iterator iterator = keys.iterator();
		while ( iterator.hasNext()) {
			externalIdentifierList.add((String)hmExternalIdentifiers.get(iterator.next()));
		}
		
		hmEventCodes = new HashMap();
		constantsAreLoaded = true;
	}

	public static boolean isValidDocAssociationType(String relationship) {
		if ( relationship == null ) {
			return false;
		} else if ( relationship.equals(XDS_ASSOCIATION_TYPE_RPLC) ||
				relationship.equals(XDS_ASSOCIATION_TYPE_APND) ||
				relationship.equals(XDS_ASSOCIATION_TYPE_XFRM)) {
			return true;
		}
		return false;
	}	
	
	
	public static String getExternalIdentifier(String key) {
		return (String)hmExternalIdentifiers.get(key);
	}

	public static String getObjectType(String key) {
		return (String)hmObjectTypeCodes.get(key);
	}
	
	public static String getClassCodeValue(String code) {
		return (String)hmDocEntryClassCodes.get(code);
	}

	public static String getDocTypeValue(String code) {
		return (String)hmDocEntryTypeCodes.get(code);
	}
	
	public static String getPracticeSettingCodeValue(String code) {
		return (String)hmDocEntryPracticeSettingCodes.get(code);
	}

	public static String getEventCodeValue(String code) {
		return (String)hmEventCodes.get(code);
	}
	
	public static String getConfidentialityCodeValue(String code) {
		return (String)hmDocEntryConfidentialityCodes.get(code);
	}
	
	public static String getHealthCareFacilityTypeCodeValue(String code) {
		return (String)hmDocEntryHealthCareFacilityCodes.get(code);
	}

	public static String getFormatCodeValue(String code) {
		return (String)hmDocEntryFormatCodes.get(code);
	}
	
	public static String getContentTypeCodeValue(String code) {
		return (String)hmSubSetContentTypeCodes.get(code);
	}
		
	public static List getExternalIdentifierUUIDs() {
		return externalIdentifierList;
	}

	public static String getDocEntryTypeCodeValue(String code) {
		return (String)hmDocEntryTypeCodes.get(code);
	}
		
	
}
