/*
 * Copyright 2004-2009 (C) University of Washington. All Rights Reserved.
 * Created on Aug 13, 2008
 * University of Washington, CIRG
 */
package edu.washington.cirg.himss.servlet;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.InputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.PrintStream;
import java.io.PrintWriter;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.ParseException;
import java.text.ParsePosition;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Iterator;
import java.util.List;
import java.util.Random;
import java.util.TimeZone;

import javax.mail.MessagingException;
import javax.microedition.io.Connector;
import javax.microedition.io.HttpConnection;
import javax.naming.NamingException;
import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.xml.soap.SOAPException;
import javax.xml.soap.SOAPMessage;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.dom4j.Attribute;
import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;
import org.dom4j.Node;
import org.dom4j.XPath;
import org.dom4j.io.SAXReader;
import org.jaxen.JaxenException;
import org.jaxen.dom4j.Dom4jXPath;
//import org.eclipse.ohf.ihe.atna.agent.AtnaAgentFactory;
//import org.eclipse.ohf.ihe.common.hl7v2.message.PixPdqMessageUtilities;
//import org.eclipse.ohf.ihe.common.hl7v2.message.PixPdqMessageException;
//import org.eclipse.ohf.ihe.common.hl7v2.mllpclient.ClientException;
//import org.eclipse.ohf.ihe.common.mllp.*;
//import org.eclipse.ohf.ihe.pdq.consumer.*;
import org.openhealthtools.ihe.atna.auditor.PDQConsumerAuditor;
import org.openhealthtools.ihe.atna.auditor.XDSConsumerAuditor;
import org.openhealthtools.ihe.atna.auditor.codes.rfc3881.RFC3881EventCodes.RFC3881EventOutcomeCodes;
import org.openhealthtools.ihe.common.hl7v2.CX;
import org.openhealthtools.ihe.common.hl7v2.Hl7v2Factory;
import org.openhealthtools.ihe.common.hl7v2.XON;
import org.openhealthtools.ihe.common.hl7v2.message.PixPdqMessageUtilities;
import org.openhealthtools.ihe.common.hl7v2.message.PixPdqMessageException;
import org.openhealthtools.ihe.common.hl7v2.mllpclient.ClientException;
import org.openhealthtools.ihe.common.mllp.*;
import org.openhealthtools.ihe.pdq.consumer.*;
import org.openhealthtools.ihe.xds.consumer.B_Consumer;
import org.openhealthtools.ihe.xds.consumer.query.DateTimeRange;
import org.openhealthtools.ihe.xds.consumer.query.MalformedQueryException;
import org.openhealthtools.ihe.xds.consumer.retrieve.DocumentRequestType;
import org.openhealthtools.ihe.xds.consumer.retrieve.RetrieveDocumentSetRequestType;
import org.openhealthtools.ihe.xds.consumer.retrieve.RetrieveFactory;
import org.openhealthtools.ihe.xds.consumer.storedquery.FindDocumentsForMultiplePatientsQuery;
import org.openhealthtools.ihe.xds.consumer.storedquery.FindDocumentsQuery;
import org.openhealthtools.ihe.xds.consumer.storedquery.MalformedStoredQueryException;
import org.openhealthtools.ihe.xds.consumer.storedquery.StoredQuery;
import org.openhealthtools.ihe.xds.consumer.storedquery.StoredQueryParameterList;
import org.openhealthtools.ihe.xds.metadata.AuthorType;
import org.openhealthtools.ihe.xds.metadata.AvailabilityStatusType;
import org.openhealthtools.ihe.xds.metadata.CodedMetadataType;
import org.openhealthtools.ihe.xds.metadata.DocumentEntryType;
import org.openhealthtools.ihe.xds.metadata.InternationalStringType;
import org.openhealthtools.ihe.xds.metadata.LocalizedStringType;
import org.openhealthtools.ihe.xds.metadata.MetadataFactory;
import org.openhealthtools.ihe.xds.metadata.constants.DocumentEntryConstants;
import org.openhealthtools.ihe.xds.response.DocumentEntryResponseType;
import org.openhealthtools.ihe.xds.response.XDSQueryResponseType;
import org.openhealthtools.ihe.xds.response.XDSRetrieveResponseType;
import org.openhealthtools.ihe.xds.response.XDSStatusType;

import edu.washington.cirg.ebxml.LocalizedString;
import edu.washington.cirg.himss.xds.XDSConfigurationException;
import edu.washington.cirg.himss.xds.XDSException;
import edu.washington.cirg.himss.xds.requests.AdhocQueryRequest;
import edu.washington.cirg.himss.xds.requests.FindDocumentsStoredQueryRequest;
import edu.washington.cirg.himss.xds.responses.FindDocumentsResponse;
import edu.washington.cirg.himss.xds.responses.XDSLABDocumentParse;
import edu.washington.cirg.himss.xds.responses.XDSMSDocumentParse;
import edu.washington.cirg.himss.xds.soap.XDSSoapRequest;
import edu.washington.cirg.himss.xds.util.XDSConstants;
import edu.washington.cirg.himss.xds.util.XDSUtil;
import edu.washington.cirg.util.Util;

/** This servlet acts as a wrapper around classes that perform a patient demographic
 * query (PDQ) to retrieve patient identifiers, then display the results to the caller.
 * @author <a href="mailto:jsibley@u.washington.edu">Jim Sibley</a>
 *
 */
public class TestServlet extends HttpServlet {

	private static Log log = LogFactory.getLog(TestServlet.class);
	private static boolean LOG_PERFORMANCE_DATA = false; 

	private String DATASOURCE_NAME;
	
	public TestServlet() {
		super();
	}
	
	public void init(ServletConfig config) throws ServletException 
	{

		super.init(config);
		try {
			XDSConstants.loadConstants();
			LOG_PERFORMANCE_DATA = XDSConstants.LOG_PERFORMANCE_DATA;
			DATASOURCE_NAME = XDSConstants.HIMSS_DATASOURCE_NAME;
			
			if ( DATASOURCE_NAME == null || DATASOURCE_NAME.equals("") ) {
				throw new ServletException("Error initializing servlet unable to determine datasource name");
			}

		} catch (XDSConfigurationException e) {
			log.error(e);
			throw new ServletException(e);
		}
	}
	
	public void doPost(HttpServletRequest req, HttpServletResponse resp)
	throws ServletException, IOException {

    	long reqStart = 0;
		long startTime = 0;
		long stopTime = 0;
		
		if ( LOG_PERFORMANCE_DATA ) {//Start RequestTimer
			reqStart = System.currentTimeMillis();
		}

		registerMyHostnameVerifier();
	
		String patientId = req.getParameter(XDSControllerServlet.PATIENT_ID_PARAM);
		String queryUrl = req.getParameter(XDSControllerServlet.TARGET_URL_PARAM);
		String atnaUrl = req.getParameter(XDSControllerServlet.ATNA_URL_PARAM);
		String timeSlot = req.getParameter(XDSControllerServlet.TIME_SLOT_PARAM);
		String qStartTime = req.getParameter(XDSControllerServlet.START_TIME_PARAM);
		String qStopTime = req.getParameter(XDSControllerServlet.STOP_TIME_PARAM);
		String usePH = req.getParameter(XDSControllerServlet.USE_PH_PARAM);
		String storedQueryUuid = XDSConstants.UUID_FIND_DOCUMENTS_STORED_QUERY_CODE;
		String useTls = req.getParameter(XDSControllerServlet.USE_TLS_PARAM);
		String listDocs = req.getParameter(XDSControllerServlet.LIST_DOCS_PARAM);
//		String repoUrl = "http://xds-ibm.lgs.com:9080/IBMXDSRepository/XDSb/SOAP12/Repository";
//		String repoUrl = XDSConstants.REPO_URL;
		String repoUrl = req.getParameter(XDSControllerServlet.REPO_URL_PARAM);
		String action = req.getParameter(XDSControllerServlet.TEST_ACTION_PARAM);

    	if ( !( useTls == null || useTls.equals("") || "0".equals(useTls) ) ) {
            // set the system security properties
            System.setProperty("javax.net.ssl.keyStore", XDSConstants.TLS_KEYSTORE);
            System.setProperty("javax.net.ssl.keyStorePassword", XDSConstants.TLS_KEYSTORE_PASS);
            System.setProperty("javax.net.ssl.trustStore", XDSConstants.TLS_CERTFILE);
            System.setProperty("javax.net.ssl.trustStorePassword", XDSConstants.TLS_CERTFILE_PASS);
    	}

    	try {
			if ( LOG_PERFORMANCE_DATA ) { //Start GeneratePDQQuery
				startTime=System.currentTimeMillis();
			}

			switch (Integer.parseInt(action)) {
				case 1: atnaLoggingTest(atnaUrl, useTls);
						break;
				case 2: mpqTest(queryUrl, repoUrl, atnaUrl, patientId, usePH, qStartTime, qStopTime, useTls, storedQueryUuid);
						break;
				case 3: retrieveDocsTest(queryUrl, repoUrl, atnaUrl, patientId, qStartTime, qStopTime, useTls, storedQueryUuid);
						break;
				case 4: hwFindDocumentsTest(queryUrl, repoUrl, atnaUrl, patientId, qStartTime, qStopTime, useTls, storedQueryUuid);
						break;
			}
			
			if ( LOG_PERFORMANCE_DATA) { //End ProcessPDQResponse
				stopTime = System.currentTimeMillis();
				log.info("TIMER,Finished," + (stopTime-startTime) + " ms");
				startTime=System.currentTimeMillis();
	    	}
		} finally {
			if ( LOG_PERFORMANCE_DATA ) {
				stopTime = System.currentTimeMillis();
				log.info("TIMER,Query Request Total," + (stopTime-reqStart) + " ms");
			}
		}
	}
	
	private void mpqTest(String queryUrl, String repoUrl, String atnaUrl, String patientId, String usePH,
			  			 String qStartTime, String qStopTime, String useTls, String storedQueryUuid) throws ServletException {
    	if ( !( usePH == null || "".equals(usePH) || "0".equals(usePH) ) ) {
    		// change the stored query uuid if we want the "public health" version of the query
    		storedQueryUuid = XDSConstants.UUID_PH_FIND_DOCUMENTS_STORED_QUERY_CODE;
    		// nuke the pid to trigger the Public Health version of the Find Documents query
    		patientId = "";
    	}
    	
		B_Consumer c = null;
		java.net.URI registryURI = null;
		java.net.URI repositoryURI = null;
		XDSQueryResponseType response = null;
		
		try {
			registryURI = new java.net.URI(queryUrl);
			repositoryURI = new java.net.URI(repoUrl);
		} catch (URISyntaxException e) {
			log.error("QUERY URI CANNOT BE SET: \n" + e.getMessage());
			throw new ServletException(e);
		}
		c = new B_Consumer(registryURI);
		
		XDSConsumerAuditor auditor = XDSConsumerAuditor.getAuditor();
		try {
			auditor.getConfig().setAuditRepositoryUri(new URI((useTls == null || useTls.equals("") || "0".equals(useTls) ? "syslog" : "tls") + "://" + atnaUrl));
			
		} catch (URISyntaxException e) {
			e.printStackTrace();
		}
		auditor.getConfig().setAuditorEnabled(true);
		auditor.getConfig().setSystemIpAddress("10.242.31.18");
		auditor.getConfig().setAuditSourceId("OTHER_CDC_State2_HW");
		auditor.auditRegistryStoredQueryEvent(RFC3881EventOutcomeCodes.SUCCESS, queryUrl, storedQueryUuid, "N/A", "N/A", patientId);
		auditor.auditActorStartEvent(RFC3881EventOutcomeCodes.SUCCESS, "OTHER_CDC_State2_HW", "ihe2014");
		
		StoredQuery q = null;
		
		if (! "".equals(patientId)) {
			CX queryPatientId = Hl7v2Factory.eINSTANCE.createCX();
			queryPatientId.setIdNumber(patientId.substring(0, patientId.indexOf("^")));
			queryPatientId.setAssigningAuthorityName("IHENA");
//			queryPatientId.setAssigningAuthorityUniversalId("1.3.6.1.4.1.21367.2007.2.1");
			queryPatientId.setAssigningAuthorityUniversalId("1.3.6.1.4.1.21367.2005.3.7.9");
//			queryPatientId.setAssigningAuthorityUniversalId("1.3.6.1.4.1.21367.2010.1.2.300");
//			queryPatientId.setAssigningAuthorityUniversalId(patientId.substring(patientId.lastIndexOf("^") + 1));
			queryPatientId.setAssigningAuthorityUniversalIdType("ISO");
			// make query
			DateTimeRange creationTimeRange = null;
			try {
				creationTimeRange = new DateTimeRange(DocumentEntryConstants.CREATION_TIME, qStartTime, qStopTime);
			} catch (MalformedQueryException e) {
				e.printStackTrace();
			}
			try {
				q = new FindDocumentsQuery(queryPatientId,
						null, // no classCodes
						new DateTimeRange[]{creationTimeRange},
						null, // no practiceSettingCodes
						null, // no healthcareFacilityCodes
						null, // no eventCodes
						null, // no confidentialityCodes
						null, // no formatCodes
						null, // no author person
						new AvailabilityStatusType[]{AvailabilityStatusType.APPROVED_LITERAL});
			} catch (MalformedStoredQueryException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		} else {
			// make query
			DateTimeRange creationTimeRange = null;
			CodedMetadataType[] eventCodes;
			CodedMetadataType event1 = MetadataFactory.eINSTANCE.createCodedMetadataType();
			event1.setCode("TRID1018");
			event1.setSchemeName("RSNA2008 eventCodeList");
						
//			CodedMetadataType event1 = MetadataFactory.eINSTANCE.createCodedMetadataType();
//			event1.setCode("MPQ-eventcode-1");
//			event1.setSchemeName("IHE");
//			CodedMetadataType event2 = MetadataFactory.eINSTANCE.createCodedMetadataType();
//			event2.setCode("MPQ-eventcode-2");
//			event2.setSchemeName("IHE");
//			CodedMetadataType event3 = MetadataFactory.eINSTANCE.createCodedMetadataType();
//			event3.setCode("MPQ-eventcode-3");
//			event3.setSchemeName("IHE");
//			CodedMetadataType event1 = MetadataFactory.eINSTANCE.createCodedMetadataType();
//			event1.setCode("55014007");
//			event1.setSchemeName("SNOMED-CT");
//			CodedMetadataType event2 = MetadataFactory.eINSTANCE.createCodedMetadataType();
//			event2.setCode("407479009");
//			event2.setSchemeName("SNOMED-CT");
//			CodedMetadataType event3 = MetadataFactory.eINSTANCE.createCodedMetadataType();
//			event3.setCode("420362005");
//			event3.setSchemeName("SNOMED-CT");
//			CodedMetadataType event4 = MetadataFactory.eINSTANCE.createCodedMetadataType();
//			event4.setCode("421539000");
//			event4.setSchemeName("SNOMED-CT");
//			CodedMetadataType event5 = MetadataFactory.eINSTANCE.createCodedMetadataType();
//			event5.setCode("420508007");
//			event5.setSchemeName("SNOMED-CT");
//			CodedMetadataType event6 = MetadataFactory.eINSTANCE.createCodedMetadataType();
//			event6.setCode("421264001");
//			event6.setSchemeName("SNOMED-CT");
//			CodedMetadataType event7 = MetadataFactory.eINSTANCE.createCodedMetadataType();
//			event7.setCode("407480007");
//			event7.setSchemeName("SNOMED-CT");
//			CodedMetadataType event8 = MetadataFactory.eINSTANCE.createCodedMetadataType();
//			event8.setCode("407482004");
//			event8.setSchemeName("SNOMED-CT");
//			eventCodes = new CodedMetadataType[]{event1, event2, event3, event4, event5, event6, event7, event8}; 
			eventCodes = new CodedMetadataType[]{event1}; 
			
			try {
				creationTimeRange = new DateTimeRange(DocumentEntryConstants.CREATION_TIME, qStartTime, qStopTime);
			} catch (MalformedQueryException e) {
				e.printStackTrace();
			}
//			q = new PublicHealthFindDocumentsQuery(
			try {
				q = new FindDocumentsForMultiplePatientsQuery(
						null, // no classCodes
						new DateTimeRange[]{creationTimeRange},
						null, // no practiceSettingCodes
						null, // no healthcareFacilityCodes
						eventCodes, // event codes required for MPQ
						null, // no confidentialityCodes
						null, // no formatCodes
						new AvailabilityStatusType[]{AvailabilityStatusType.APPROVED_LITERAL});
			} catch (MalformedStoredQueryException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		
//		SOAPMessage response = soapReq.sendMessage();

		// run query
		try {
		    FileOutputStream fos = new FileOutputStream("/tmp/soapRequest.txt");
		    PrintStream p = new PrintStream(fos);
		    StoredQueryParameterList l = q.getQueryParameters();
		    String u = q.getQueryUUID();
		    p.println("Query UUID='" + u + ";");
		    for (int i = 0; i < l.size(); i++) {
		    	p.println("Param name='" + l.get(i).getName() + "', Param value='" + l.get(i).getValue() + "'");
		    }
		    p.close();
		} catch (FileNotFoundException e) {
		    System.err.println("XDSSoapRequest: " + e);
		} catch (IOException e) {
		    System.err.println("XDSSoapRequest: " + e);
		}
		
		try {
			response = c.invokeStoredQuery(q, false);
		} catch (Exception e) {
			log.error("Error running query: \n" + e.toString());
			throw new ServletException(e);
		}

		auditor.auditActorStopEvent(RFC3881EventOutcomeCodes.SUCCESS, "OTHER_CDC_State2_HW", "ihe2014");

		if (response.getStatus().equals(XDSStatusType.SUCCESS_LITERAL)) {

			try {
				FileOutputStream fos = new FileOutputStream("/tmp/soapResponse.txt");
				PrintStream p = new PrintStream(fos);
				p.println("Status='" + response.getStatus().getName() + "'");
				p.println("DocEntResp size='" + response.getDocumentEntryResponses().size() + "'");
				for (int i = 0; i < response.getAssociations().size(); i++) {
					p.println(response.getAssociations().get(i).toString());
				}
				for (int i = 0; i < response.getDocumentEntryResponses().size(); i++) {
					DocumentEntryType doc = ((DocumentEntryResponseType)response.getDocumentEntryResponses().get(i)).getDocumentEntry();
					p.println("========================== Doc Entry #" + (i + 1) + " ==========================");
					p.println("PatientID='" + (doc.getPatientId() != null && doc.getPatientId().getIdNumber() != null ? doc.getPatientId().getIdNumber() : "NULL") + "'");
					p.println("CreationTime='" + (doc.getCreationTime() != null ? doc.getCreationTime() : "NULL") + "'");
//					if (doc.getAuthors() != null) {
//						for (int j = 0; j < doc.getAuthors().size(); j++) {
//							if (((AuthorType) doc.getAuthors().get(j)).getAuthorInstitution() != null) {
//								for (int k = 0; j < ((AuthorType) doc.getAuthors().get(j)).getAuthorInstitution().size(); k++) {
//									p.println("Author='" + ((XON)((AuthorType)doc.getAuthors().get(j)).getAuthorInstitution().get(k)).getOrganizationName() != null ? ((XON)((AuthorType)doc.getAuthors().get(j)).getAuthorInstitution().get(k)).getOrganizationName() : "" + "'");
//								}
//							}
//						}
//					}
					p.println("URI='" + (doc.getUri() != null ? doc.getUri() : "NULL") + "'");
					p.println("TypeCode='" + (doc.getTypeCode() != null && doc.getTypeCode().getCode() != null ? doc.getTypeCode().getCode() : "NULL") + "'");
					p.println("ClassCode='" + (doc.getClassCode() != null && doc.getClassCode().getCode() != null ? doc.getClassCode().getCode() : "NULL") + "'");
					p.println("FormatCode='" + (doc.getFormatCode() != null && doc.getFormatCode().getCode() != null ? doc.getFormatCode().getCode() : "NULL") + "'");
					String eCode = "";
					for (int k = 0; k < (doc.getEventCode() != null ? doc.getEventCode().size() : 1); k++) {
						if (k > 0) {
							eCode += "|";
						}
						eCode += (doc.getEventCode() != null && ((CodedMetadataType)doc.getEventCode().get(k)).getSchemeName() != null ? ((CodedMetadataType)doc.getEventCode().get(k)).getSchemeName() : "NULL") + ":" + (doc.getEventCode() != null && ((CodedMetadataType)doc.getEventCode().get(k)).getCode() != null ? ((CodedMetadataType)doc.getEventCode().get(k)).getCode() : "NULL");
					}
					p.println("EventCode='" + eCode + "'");
					p.println("MimeType='" + (doc.getMimeType() != null ? doc.getMimeType() : "NULL") + "'");
					p.println("UniqueID='" + (doc.getUniqueId() != null ? doc.getUniqueId() : "NULL") + "'");
					p.println("LanguageCode='" + (doc.getLanguageCode() != null ? doc.getLanguageCode() : "NULL") + "'");
					p.println("RepositoryUniqueID='" + (doc.getRepositoryUniqueId() != null ? doc.getRepositoryUniqueId() : "NULL") + "'");
					p.println("PracticeSettingCode='" + (doc.getPracticeSettingCode() != null && doc.getPracticeSettingCode().getCode() != null ? doc.getPracticeSettingCode().getCode() : "NULL") + "'");
					p.println("HealthCareFacilityTypeCode='" + (doc.getHealthCareFacilityTypeCode() != null && doc.getHealthCareFacilityTypeCode().getCode() != null ? doc.getHealthCareFacilityTypeCode().getCode() : "NULL") + "'");
					p.println("SourcePatInfo='" + (doc.getSourcePatientInfo() != null && doc.getSourcePatientInfo().getPatientDateOfBirth() != null ? doc.getSourcePatientInfo().getPatientDateOfBirth() : "NULL") + ","
												+ (doc.getSourcePatientInfo() != null && doc.getSourcePatientInfo().getPatientSex() != null ? doc.getSourcePatientInfo().getPatientSex() : "NULL") + ","
												+ (doc.getSourcePatientInfo() != null && doc.getSourcePatientInfo().getPatientAddress() != null && doc.getSourcePatientInfo().getPatientAddress().getStreetAddress() != null ? doc.getSourcePatientInfo().getPatientAddress().getStreetAddress() : "NULL") + ","
												+ (doc.getSourcePatientInfo() != null && doc.getSourcePatientInfo().getPatientAddress() != null && doc.getSourcePatientInfo().getPatientAddress().getCity() != null ? doc.getSourcePatientInfo().getPatientAddress().getCity() : "NULL") + ","
												+ (doc.getSourcePatientInfo() != null && doc.getSourcePatientInfo().getPatientAddress() != null && doc.getSourcePatientInfo().getPatientAddress().getStateOrProvince() != null ? doc.getSourcePatientInfo().getPatientAddress().getStateOrProvince() : "NULL") + ","
												+ (doc.getSourcePatientInfo() != null && doc.getSourcePatientInfo().getPatientAddress() != null && doc.getSourcePatientInfo().getPatientAddress().getZipOrPostalCode() != null ? doc.getSourcePatientInfo().getPatientAddress().getZipOrPostalCode() : "NULL")
												+ "'");
				}
				p.close();
			} catch (FileNotFoundException e) {
				System.err.println("XDSSoapRequest: " + e);
			} catch (IOException e) {
				System.err.println("XDSSoapRequest: " + e);
			}

			//	if ( log.isDebugEnabled() ) {
			//		response.writeTo(System.out);
			//	}
		}
	}

	private void retrieveDocsTest(String queryUrl, String repoUrl, String atnaUrl, String patientId,
								  String qStartTime, String qStopTime, String useTls, String storedQueryUuid) {
		B_Consumer c = null;
		java.net.URI registryURI = null;
		java.net.URI repositoryURI = null;
		XDSQueryResponseType response = null;
		
		try {
			registryURI = new java.net.URI(queryUrl);
			repositoryURI = new java.net.URI(repoUrl);
		} catch (URISyntaxException e) {
			log.error("QUERY URI CANNOT BE SET: \n" + e.getMessage());
		}
		c = new B_Consumer(registryURI);
		
		XDSConsumerAuditor auditor = XDSConsumerAuditor.getAuditor();
		try {
			auditor.getConfig().setAuditRepositoryUri(new URI((useTls == null || useTls.equals("") || "0".equals(useTls) ? "syslog" : "tls") + "://" + atnaUrl));
			
		} catch (URISyntaxException e) {
			e.printStackTrace();
		}
		auditor.getConfig().setAuditorEnabled(true);
		auditor.getConfig().setSystemIpAddress("10.242.31.18");
		auditor.getConfig().setAuditSourceId("OTHER_CDC_State2_HW");
		auditor.auditRegistryStoredQueryEvent(RFC3881EventOutcomeCodes.SUCCESS, queryUrl, storedQueryUuid, "N/A", "N/A", patientId);
		auditor.auditActorStartEvent(RFC3881EventOutcomeCodes.SUCCESS, "OTHER_CDC_State2_HW", "ihe2014");

		StoredQuery q = null;
		
		if (! "".equals(patientId)) {
			CX queryPatientId = Hl7v2Factory.eINSTANCE.createCX();
			queryPatientId.setIdNumber(patientId.substring(0, patientId.indexOf("^")));
//			queryPatientId.setAssigningAuthorityName("IHENA");
//			queryPatientId.setAssigningAuthorityUniversalId("1.3.6.1.4.1.21367.2007.2.1");
//			queryPatientId.setAssigningAuthorityUniversalId("1.3.6.1.4.1.21367.2005.3.7.9");
//			queryPatientId.setAssigningAuthorityUniversalId("1.3.6.1.4.1.21367.2005.3.7");
//			queryPatientId.setAssigningAuthorityUniversalId("1.3.6.1.4.1.21367.2010.1.2.300");
			queryPatientId.setAssigningAuthorityUniversalId(patientId.substring(patientId.lastIndexOf("^") + 2));
			queryPatientId.setAssigningAuthorityUniversalIdType("ISO");
			// make query
			DateTimeRange creationTimeRange = null;
			try {
				creationTimeRange = new DateTimeRange(DocumentEntryConstants.CREATION_TIME, qStartTime, qStopTime);
			} catch (MalformedQueryException e) {
				e.printStackTrace();
			}
			try {
				q = new FindDocumentsQuery(queryPatientId,
						null, // no classCodes
						new DateTimeRange[]{creationTimeRange},
						null, // no practiceSettingCodes
						null, // no healthcareFacilityCodes
						null, // no eventCodes
						null, // no confidentialityCodes
						null, // no formatCodes
						null, // no author person
						new AvailabilityStatusType[]{AvailabilityStatusType.APPROVED_LITERAL});
			} catch (MalformedStoredQueryException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		
		// run query
		try {
		    FileOutputStream fos = new FileOutputStream("/tmp/soapRequest.txt");
		    PrintStream p = new PrintStream(fos);
		    StoredQueryParameterList l = q.getQueryParameters();
		    String u = q.getQueryUUID();
		    p.println("Query UUID='" + u + "'");
		    for (int i = 0; i < l.size(); i++) {
		    	p.println("Param name='" + l.get(i).getName() + "', Param value='" + l.get(i).getValue() + "'");
		    }
		    p.println("SOAP Action='" + q.getSoapAction() + "'");
		    p.close();
		} catch (FileNotFoundException e) {
		    System.err.println("XDSSoapRequest: " + e);
		} catch (IOException e) {
		    System.err.println("XDSSoapRequest: " + e);
		}
		
		try {
			response = c.invokeStoredQuery(q, false);
		} catch (Exception e) {
			log.error("Error running query: \n" + e.toString());
		}
		
		auditor.auditActorStopEvent(RFC3881EventOutcomeCodes.SUCCESS, "OTHER_CDC_State2_HW", "ihe2014");

		if (response.getStatus().equals(XDSStatusType.SUCCESS_LITERAL)) {

			try {
				FileOutputStream fos = new FileOutputStream("/tmp/soapResponse.txt");
				PrintStream p = new PrintStream(fos);
				p.println("Status='" + response.getStatus().toString() + "'");
				p.println("DocEntResp size='" + response.getDocumentEntryResponses().size() + "'");
				for (int i = 0; i < response.getAssociations().size(); i++) {
					p.println(response.getAssociations().get(i).toString());
				}
				for (int i = 0; i < response.getDocumentEntryResponses().size(); i++) {
					DocumentEntryType doc = ((DocumentEntryResponseType)response.getDocumentEntryResponses().get(i)).getDocumentEntry();
					p.println("========================== Doc Entry #" + (i + 1) + " ==========================");
					p.println("PatientID='" + (doc.getPatientId() != null && doc.getPatientId().getIdNumber() != null ? doc.getPatientId().getIdNumber() : "NULL") + "'");
					p.println("CreationTime='" + (doc.getCreationTime() != null ? doc.getCreationTime() : "NULL") + "'");
//					if (doc.getAuthors() != null) {
//						for (int j = 0; j < doc.getAuthors().size(); j++) {
//							if (((AuthorType) doc.getAuthors().get(j)).getAuthorInstitution() != null) {
//								for (int k = 0; j < ((AuthorType) doc.getAuthors().get(j)).getAuthorInstitution().size(); k++) {
//									p.println("Author='" + ((XON)((AuthorType)doc.getAuthors().get(j)).getAuthorInstitution().get(k)).getOrganizationName() != null ? ((XON)((AuthorType)doc.getAuthors().get(j)).getAuthorInstitution().get(k)).getOrganizationName() : "" + "'");
//								}
//							}
//						}
//					}
					p.println("URI='" + (doc.getUri() != null ? doc.getUri() : "NULL") + "'");
					String cCode = "";
					for (int z = 0; z < (doc.getConfidentialityCode() != null ? doc.getConfidentialityCode().size() : 1); z++) {
						if (z > 0) {
							cCode += "|";
						}
						cCode += (doc.getConfidentialityCode() != null && ((CodedMetadataType)doc.getConfidentialityCode().get(z)).getSchemeName() != null ? ((CodedMetadataType)doc.getConfidentialityCode().get(z)).getSchemeName() : "NULL") + ":" + (doc.getConfidentialityCode() != null && ((CodedMetadataType)doc.getConfidentialityCode().get(z)).getCode() != null ? ((CodedMetadataType)doc.getConfidentialityCode().get(z)).getCode() : "NULL");
					}
					p.println("TypeCode='" + (doc.getTypeCode() != null && doc.getTypeCode().getCode() != null ? doc.getTypeCode().getCode() : "NULL") + "'");
					p.println("ClassCode='" + (doc.getClassCode() != null && doc.getClassCode().getCode() != null ? doc.getClassCode().getCode() : "NULL") + "'");
					p.println("FormatCode='" + (doc.getFormatCode() != null && doc.getFormatCode().getCode() != null ? doc.getFormatCode().getCode() : "NULL") + "'");
					String eCode = "";
					for (int k = 0; k < (doc.getEventCode() != null ? doc.getEventCode().size() : 1); k++) {
						if (k > 0) {
							eCode += "|";
						}
						eCode += (doc.getEventCode() != null && ((CodedMetadataType)doc.getEventCode().get(k)).getSchemeName() != null ? ((CodedMetadataType)doc.getEventCode().get(k)).getSchemeName() : "NULL") + ":" + (doc.getEventCode() != null && ((CodedMetadataType)doc.getEventCode().get(k)).getCode() != null ? ((CodedMetadataType)doc.getEventCode().get(k)).getCode() : "NULL");
					}
					p.println("EventCode='" + eCode + "'");
					p.println("MimeType='" + (doc.getMimeType() != null ? doc.getMimeType() : "NULL") + "'");
					p.println("UniqueID='" + (doc.getUniqueId() != null ? doc.getUniqueId() : "NULL") + "'");
					p.println("LanguageCode='" + (doc.getLanguageCode() != null ? doc.getLanguageCode() : "NULL") + "'");
					p.println("RepositoryUniqueID='" + (doc.getRepositoryUniqueId() != null ? doc.getRepositoryUniqueId() : "NULL") + "'");
					p.println("PracticeSettingCode='" + (doc.getPracticeSettingCode() != null && doc.getPracticeSettingCode().getCode() != null ? doc.getPracticeSettingCode().getCode() : "NULL") + "'");
					p.println("HealthCareFacilityTypeCode='" + (doc.getHealthCareFacilityTypeCode() != null && doc.getHealthCareFacilityTypeCode().getCode() != null ? doc.getHealthCareFacilityTypeCode().getCode() : "NULL") + "'");
					p.println("SourcePatInfo='" + (doc.getSourcePatientInfo() != null && doc.getSourcePatientInfo().getPatientDateOfBirth() != null ? doc.getSourcePatientInfo().getPatientDateOfBirth() : "NULL") + ","
												+ (doc.getSourcePatientInfo() != null && doc.getSourcePatientInfo().getPatientSex() != null ? doc.getSourcePatientInfo().getPatientSex() : "NULL") + ","
												+ (doc.getSourcePatientInfo() != null && doc.getSourcePatientInfo().getPatientAddress() != null && doc.getSourcePatientInfo().getPatientAddress().getStreetAddress() != null ? doc.getSourcePatientInfo().getPatientAddress().getStreetAddress() : "NULL") + ","
												+ (doc.getSourcePatientInfo() != null && doc.getSourcePatientInfo().getPatientAddress() != null && doc.getSourcePatientInfo().getPatientAddress().getCity() != null ? doc.getSourcePatientInfo().getPatientAddress().getCity() : "NULL") + ","
												+ (doc.getSourcePatientInfo() != null && doc.getSourcePatientInfo().getPatientAddress() != null && doc.getSourcePatientInfo().getPatientAddress().getStateOrProvince() != null ? doc.getSourcePatientInfo().getPatientAddress().getStateOrProvince() : "NULL") + ","
												+ (doc.getSourcePatientInfo() != null && doc.getSourcePatientInfo().getPatientAddress() != null && doc.getSourcePatientInfo().getPatientAddress().getZipOrPostalCode() != null ? doc.getSourcePatientInfo().getPatientAddress().getZipOrPostalCode() : "NULL")
												+ "'");
					
					if (doc.getUniqueId() != null && !"".equals(doc.getUniqueId()) &&
						doc.getRepositoryUniqueId() != null && !"".equals(doc.getRepositoryUniqueId())) {

//						File f = new File("/tmp/xdsDoc_uniqueid_" + doc.getUniqueId() + ".xml");
//						if (f.exists()) {
//							continue;
//						}
							
						CX docQueryPatId = Hl7v2Factory.eINSTANCE.createCX();
						docQueryPatId.setIdNumber((doc.getPatientId() != null && doc.getPatientId().getIdNumber() != null ? doc.getPatientId().getIdNumber() : "NULL"));
//						docQueryPatId.setAssigningAuthorityName("IHENA");
//						docQueryPatId.setAssigningAuthorityUniversalId("1.3.6.1.4.1.21367.2010.1.2.300");
						docQueryPatId.setAssigningAuthorityUniversalId(patientId.substring(patientId.lastIndexOf("^") + 2));
						docQueryPatId.setAssigningAuthorityUniversalIdType("ISO");

						RetrieveDocumentSetRequestType retrDocSetReq = RetrieveFactory.eINSTANCE.createRetrieveDocumentSetRequestType();
						DocumentRequestType docReq = RetrieveFactory.eINSTANCE.createDocumentRequestType();
						docReq.setDocumentUniqueId(doc.getUniqueId());
//						docReq.setHomeCommunityId("urn:oid:1.3.6.1.4.1.21367.13.70.101");
						docReq.setRepositoryUniqueId(doc.getRepositoryUniqueId());
						c.getRepositoryMap().put(doc.getRepositoryUniqueId(), repositoryURI);
						//					       	c.setPrimaryRepositoryURI(repositoryURI);
						retrDocSetReq.getDocumentRequest().add(docReq);
						//					       	List<org.openhealthtools.ihe.xds.document.XDSDocument> documents = new ArrayList<org.openhealthtools.ihe.xds.document.XDSDocument>();

						auditor.auditRetrieveDocumentSetEvent(RFC3881EventOutcomeCodes.SUCCESS, queryUrl, new String[] {doc.getUniqueId()}, doc.getRepositoryUniqueId(), "N/A", doc.getPatientId().getIdNumber());
						auditor.auditActorStartEvent(RFC3881EventOutcomeCodes.SUCCESS, "OTHER_CDC_State2_HW", "ihe2014");

						String xml = "";
						try {
							XDSRetrieveResponseType docResp = c.retrieveDocumentSet(false, retrDocSetReq, docQueryPatId);			
							//log.info("INFO,Retrieve Document Request Status='" + (docResp.getStatus() != null ? docResp.getStatus() : "NULL") + "'");
							//log.info("INFO,Retrieved Document[0]='" + (documents != null && documents.get(0) != null ? documents.get(0).toString() : "NULL") + "'");

							auditor.auditActorStopEvent(RFC3881EventOutcomeCodes.SUCCESS, "OTHER_CDC_State2_HW", "ihe2014");

							if (docResp.getStatus().equals(XDSStatusType.SUCCESS_LITERAL)) {
								log.info("INFO,Number of docs in set: " + docResp.getAttachments().size());
								BufferedReader in = new BufferedReader(new InputStreamReader(docResp.getAttachments().get(0).getStream()));
								String str = "";
								while ((str = in.readLine()) != null) {
									xml = xml.concat(str);
								}
								in.close();
							} else {
								log.error("ERROR,Retrieve Document Set Status: " + docResp.getStatus().toString());
							}

							//log.info("INFO,Retrieved Doc='" + xml + "'");
						} catch (Exception e) {
							log.error("ERROR,Problem Retrieving Document Set: " + e);
						}

						 //&& xml.indexOf("confidentialityCode code=\"N\"") >= 0
						 
//						if (xml.length() > 0 && ("1.3.6.1.4.1.21367.13.60.461740842621".equals(doc.getUniqueId()) ||
//												 "1.3.6.1.4.1.21367.13.60.46228671059".equals(doc.getUniqueId()))) {
						if (xml.length() > 0) {
							// write out file
							File f = new File("/tmp/xdsDoc_uniqueid_" + doc.getUniqueId() + ".xml");
							if (f.exists()) {
								continue;
							}
							try {
								byte byteXml[] = xml.getBytes();
								fos = new FileOutputStream("/tmp/xdsDoc_uniqueid_" + doc.getUniqueId() + ".xml");
								fos.write(byteXml);
								fos.close();
							} catch (FileNotFoundException e) {
								System.err.println("WARN,File Not Found Exception: " + e);
							} catch (IOException e) {
								System.err.println("WARN,IO Exception: " + e);
							}
						}
					} else {
						p.println("WARNING: Document not retrieved (bad uniqueId or repositoryUniqueId)");
					}
				}
				p.close();
			} catch (FileNotFoundException e) {
				System.err.println("XDSSoapRequest: " + e);
			} catch (IOException e) {
				System.err.println("XDSSoapRequest: " + e);
			}

		} else {
			log.error("WARNING: Unsuccessful query.\n");
		}
	}
	
	private void atnaLoggingTest(String atnaUrl, String useTls) {
		//atna set-up
		PDQConsumerAuditor auditor = PDQConsumerAuditor.getAuditor();
		try {
			auditor.getConfig().setAuditRepositoryUri(new URI((useTls == null || useTls.equals("") || "0".equals(useTls) ? "syslog" : "tls") + "://" + atnaUrl));
			
		} catch (URISyntaxException e) {
			e.printStackTrace();
		}
		auditor.getConfig().setAuditorEnabled(true);
		auditor.getConfig().setSystemIpAddress("10.242.31.18");
		auditor.getConfig().setAuditSourceId("OTHER_CDC_State2_HW");

		String[] auditIDs = new String[1];
		auditIDs[0] = "patientID";
		
		auditor.auditPDQQueryEvent(RFC3881EventOutcomeCodes.SUCCESS,
								   "http://67.155.0.245:3600",
								   "XDSDEMO_ADT",
								   "XDSDEMO",
								   "OTHER_CDC_State2_HW",
								   "PdqConsumer",
								   "0404343237490344683",
								   "Q22^Find Candidates|2215052042665444508703766639234|^FARNSWORTH~^STEVE",
								   auditIDs);
	}
	
	private void hwFindDocumentsTest(String queryUrl, String repoUrl, String atnaUrl, String patID,
			  						 String qStartTime, String qStopTime, String useTls, String storedQueryUuid) throws IOException, ServletException {

    	Connection conn = null;
		FileOutputStream fos2 = new FileOutputStream("/tmp/hwTestErrLog.txt");
		PrintStream p2 = new PrintStream(fos2);
		
		// List of Zip codes
		Random rand = new Random();
    	List<String> zipCodes = new ArrayList<String>();
		File zipFile = new File("/var/lib/tomcat7/webapps/xds/WEB-INF/classes/zip_codes.txt");
		BufferedReader bufRdr = new BufferedReader(new FileReader(zipFile));
		String line = null;

		while((line = bufRdr.readLine()) != null) {
			zipCodes.add(line);
		}
		bufRdr.close(); 

		patID = "";
		
		try {
			// OHT version of creating query
			B_Consumer c = null;
			java.net.URI registryURI = null;
			java.net.URI repositoryURI = null;
			XDSQueryResponseType response = null;
			
			try {
				registryURI = new java.net.URI(queryUrl);
				repositoryURI = new java.net.URI(repoUrl);
			} catch (URISyntaxException e) {
				log.error("QUERY URI CANNOT BE SET: \n" + e.getMessage());
				p2.println("QUERY URI CANNOT BE SET: \n" + e.getMessage());
				throw new ServletException(e);
			}
			c = new B_Consumer(registryURI);
			
			XDSConsumerAuditor auditor = XDSConsumerAuditor.getAuditor();
			try {
				auditor.getConfig().setAuditRepositoryUri(new URI((useTls == null || useTls.equals("") || "0".equals(useTls) ? "syslog" : "tls") + "://" + atnaUrl));
				
			} catch (URISyntaxException e) {
				e.printStackTrace();
				p2.println("Error setting up audit repository URI: " + e.toString());
			}
			auditor.getConfig().setAuditorEnabled(true);
			auditor.getConfig().setSystemIpAddress("10.242.31.18");
			auditor.getConfig().setAuditSourceId("OTHER_CDC_State2_HW");
			
			StoredQuery q = null;
			
			if (! "".equals(patID)) {
				CX queryPatientId = Hl7v2Factory.eINSTANCE.createCX();
				queryPatientId.setIdNumber(patID.substring(0, patID.indexOf("^")));
//				queryPatientId.setAssigningAuthorityName("IHENA");
//				queryPatientId.setAssigningAuthorityUniversalId("1.3.6.1.4.1.21367.2007.2.1");
//				queryPatientId.setAssigningAuthorityUniversalId("1.3.6.1.4.1.21367.2005.3.7.9");
//				queryPatientId.setAssigningAuthorityUniversalId("1.3.6.1.4.1.21367.2010.1.2.300");
//				queryPatientId.setAssigningAuthorityUniversalId(patID.substring(patID.lastIndexOf("^") + 1));
				queryPatientId.setAssigningAuthorityUniversalId(patID.substring(patID.lastIndexOf("^") + 2));
				queryPatientId.setAssigningAuthorityUniversalIdType("ISO");
				// make query
				DateTimeRange creationTimeRange = null;
				try {
					creationTimeRange = new DateTimeRange(DocumentEntryConstants.CREATION_TIME, qStartTime, qStopTime);
				} catch (MalformedQueryException e) {
					e.printStackTrace();
					p2.println("Error creating date/time range: " + e.toString());
				}
				q = new FindDocumentsQuery(queryPatientId,
						null, // no classCodes
						new DateTimeRange[]{creationTimeRange},
						null, // no practiceSettingCodes
						null, // no healthcareFacilityCodes
						null, // no eventCodes
						null, // no confidentialityCodes
						null, // no formatCodes
						null, // no author person
						new AvailabilityStatusType[]{AvailabilityStatusType.APPROVED_LITERAL});
			} else {
				// make query
				DateTimeRange creationTimeRange = null;
				CodedMetadataType[] eventCodes;
				CodedMetadataType event1 = MetadataFactory.eINSTANCE.createCodedMetadataType();
				event1.setCode("TRID1018");
				event1.setSchemeName("RSNA2008 eventCodeList");
				eventCodes = new CodedMetadataType[]{event1}; 

				try {
					creationTimeRange = new DateTimeRange(DocumentEntryConstants.CREATION_TIME, qStartTime, qStopTime);
				} catch (MalformedQueryException e) {
					e.printStackTrace();
					p2.println("Error creating date/time range: " + e.toString());
				}
				q = new FindDocumentsForMultiplePatientsQuery(
						null, // no classCodes
						new DateTimeRange[]{creationTimeRange},
						null, // no practiceSettingCodes
						null, // no healthcareFacilityCodes
						eventCodes, // event codes required for MPQ
						null, // no confidentialityCodes
						null, // no formatCodes
						new AvailabilityStatusType[]{AvailabilityStatusType.APPROVED_LITERAL});
			}
			
			
			// run query
			try {
			    FileOutputStream fos = new FileOutputStream("/tmp/hwSoapRequest.txt");
			    PrintStream p = new PrintStream(fos);
			    StoredQueryParameterList l = q.getQueryParameters();
			    String u = q.getQueryUUID();
			    p.println("Query UUID='" + u + ";");
			    for (int i = 0; i < l.size(); i++) {
			    	p.println("Param name='" + l.get(i).getName() + "', Param value='" + l.get(i).getValue() + "'");
			    }
			    p.close();
			} catch (FileNotFoundException e) {
			    System.err.println("XDSSoapRequest: " + e);
			    p2.println("XDSSoapRequest: " + e);
			} catch (IOException e) {
			    System.err.println("XDSSoapRequest: " + e);
			    p2.println("XDSSoapRequest: " + e);
			}
			
			try {
//				response = c.invokeStoredQuery(q, false);
			} catch (Exception e) {
				log.error("Error running query: \n" + e.toString());
				p2.println("Error running query: \n" + e.toString());
				throw new ServletException(e);
			}

			auditor.auditRegistryStoredQueryEvent(RFC3881EventOutcomeCodes.SUCCESS, queryUrl, storedQueryUuid, "N/A", "N/A", patID);

			if (1 == 1) {
				conn = Util.getConnection(null, null, null, null, null);
				Statement stmt = null;
				int rs = 0;
				int dupes = 0;
				if ( conn == null ) {
					log.error("Connection is null");
					p2.println("Connection is null");
					throw new SQLException("Unable to obtain a database connection");
				}

//				try {
					for ( int j = 0; j < 1; j++ ) {
						int isDupe = 0;
						
						if (1 == 1) { //((CodedMetadataType)doc.getEventCode().get(0)).getCode() != null && ((CodedMetadataType)doc.getEventCode().get(0)).getCode().matches("HW-DOC")) {
							p2.println("Attempting retrieve of document #" + j);

							String xml = "";
							File xmlFile = new File("/var/lib/tomcat7/webapps/xds/WEB-INF/classes/HW_OTHER_OZ-SYSTEMS_2014.xml");
							bufRdr = new BufferedReader(new FileReader(xmlFile));
							line = null;

							while((line = bufRdr.readLine()) != null) {
								xml += line + "\n";
							}
							bufRdr.close(); 

							p2.println("Got document, XML file size is " + xml.length());
							
							// Check for dupes								
							String dataInsert = "INSERT parsed_docs SET unique_id = 'my_unique_id'";

							try {
//									if ( !(rr.getPatientIdList().get(i) == null || rr.getPatientIdList().get(i).equals("")) ) {
									stmt = conn.createStatement();
									rs = stmt.executeUpdate(dataInsert);
//									}
							} catch (SQLException e) {
								if (e.getErrorCode() == 1062) {
									/* Don't crap out just because of 'duplicate key' errors, add to running dupe tally */
									dupes++;
									isDupe = 1;
								} else {
									log.error(e);
									p2.println(e);
								}
							}
							
//								xml = xml.replaceAll("cda:", "");
//								xml = xml.replaceAll("hl7:", "");
							if (xml.length() > 0 && isDupe != 1) {
								Document cda = DocumentHelper.parseText(xml);
								String pid = "";
//									String fName = "";
//									String lName = "";
//									String patName = "";
								String sex = "";
								String dob = "";
								String ht = "";
								String wt = "";
								String odt = "";
								String occ41 = "";
								String occ23c = "";
								String occ23 = "";
								String race = "";
								String ethnic = "";
								String zip = "";
								float bmi = 0;
								String age = "";
								String freqSportsDrink = "";
								String freqSoda = "";
								String freqWater = "";
								String freqVeg = "";
								String freqFruit = "";
								String freqFruitJuice = "";
								String freqFastFood = "";
								String breastFed = "";
								String formulaQuant = "";
								String problemNursing = "";
								String freqPhysical = "";
								String physicalQuant = "";
								String tvQuant = "";
								String gameQuant = "";
								String bedTime = "";
								String sleepQuant = "";
								String pregnant = "";
								String readyNutrition = "";
								String readySleep = "";
								String readyExercise = "";
								String readyScreen = "";
								
								XPath xpathSelector = DocumentHelper.createXPath(
								"//*[name() = 'ClinicalDocument']/*[name() = 'recordTarget']/*[name() = 'patientRole']/*[name() = 'id']/@extension");
								Attribute attr = (Attribute) xpathSelector.selectSingleNode(cda);
								if ( attr != null && !("".equals(attr.getData().toString()) || attr.getData().toString() == null) ) {
									pid = attr.getData().toString();
								}

								xpathSelector = DocumentHelper.createXPath(
								"//*[name() = 'ClinicalDocument']/*[name() = 'recordTarget']/*[name() = 'patientRole']/*[name() = 'patient']/*[name() = 'administrativeGenderCode']/@code");
								attr = (Attribute) xpathSelector.selectSingleNode(cda);
								if ( attr != null && !(attr.getData().toString().equals("") || attr.getData().toString() == null) ) {
									sex = attr.getData().toString();
								}

								xpathSelector = DocumentHelper.createXPath(
								"//*[name() = 'ClinicalDocument']/*[name() = 'recordTarget']/*[name() = 'patientRole']/*[name() = 'patient']/*[name() = 'birthTime']/@value");
								attr = (Attribute) xpathSelector.selectSingleNode(cda);
								if ( attr != null && !(attr.getData().toString().equals("") || attr.getData().toString() == null) ) {
									dob = attr.getData().toString().substring(0, 4) + "-" +
								  	  	  attr.getData().toString().substring(4, 6) + "-" +
								  	  	  attr.getData().toString().substring(6, 8) + " 00:00:00";
								}

								xpathSelector = DocumentHelper.createXPath(
								"//*[name() = 'ClinicalDocument']/*[name() = 'recordTarget']/*[name() = 'patientRole']/*[name() = 'patient']/*[name() = 'raceCode']/@displayName");
								attr = (Attribute) xpathSelector.selectSingleNode(cda);
								if ( attr != null && !(attr.getData().toString().equals("") || attr.getData().toString() == null) ) {
									race = attr.getData().toString();
								}

								xpathSelector = DocumentHelper.createXPath(
								"//*[name() = 'ClinicalDocument']/*[name() = 'recordTarget']/*[name() = 'patientRole']/*[name() = 'patient']/*[name() = 'ethnicGroupCode']/@displayName");
								attr = (Attribute) xpathSelector.selectSingleNode(cda);
								if ( attr != null && !(attr.getData().toString().equals("") || attr.getData().toString() == null) ) {
									ethnic = attr.getData().toString();
								}

								xpathSelector = DocumentHelper.createXPath(
								"//*[name() = 'ClinicalDocument']/*[name() = 'effectiveTime']/@value");
								attr = (Attribute) xpathSelector.selectSingleNode(cda);
								if ( attr != null && !(attr.getData().toString().equals("") || attr.getData().toString() == null) ) {
									odt = attr.getData().toString().substring(0, 4) + "-" +
										  attr.getData().toString().substring(4, 6) + "-" +
										  attr.getData().toString().substring(6, 8) + " " +
										  attr.getData().toString().substring(8, 10) + ":" +
										  attr.getData().toString().substring(10, 12) + ":" +
										  attr.getData().toString().substring(12, 14);
								}

								/* Loop over section blocks and pull out observation sub-blocks */
								List<Node> sectionList = new ArrayList<Node>();
								List<Node> obsList = new ArrayList<Node>();
								xpathSelector = DocumentHelper.createXPath(
								"//*[name() = 'ClinicalDocument']/*[name() = 'component']/*[name() = 'structuredBody']/*[name() = 'component']/*[name() = 'section']");
								sectionList.addAll(xpathSelector.selectNodes(cda));

								p2.println("Section count = " + sectionList.size());
								for (int i = 0; i < sectionList.size(); i++) {
									Document tempDoc = DocumentHelper.parseText(((Node) sectionList.get(i)).asXML());
									xpathSelector = DocumentHelper.createXPath("//*[name() = 'entry']/*[name() = 'observation']");
									obsList.addAll(xpathSelector.selectNodes(tempDoc));
									xpathSelector = DocumentHelper.createXPath("//*[name() = 'entry']/*[name() = 'act']/*[name() = 'entryRelationship']/*[name() = 'observation']");
									obsList.addAll(xpathSelector.selectNodes(tempDoc));
								}
								
								/* Loop over observation blocks and pull out appropriate values */
								p2.println("Obs count = " + obsList.size());
								for (int k = 0; k < obsList.size(); k++) {
									String codeCode = "";
									String valueCode = "";
									Document tempDoc = DocumentHelper.parseText(((Node) obsList.get(k)).asXML());
									xpathSelector = DocumentHelper.createXPath(
									"//*[name() = 'code']/@code");
									attr = (Attribute) xpathSelector.selectSingleNode(tempDoc);
									if ( attr != null && !(attr.getData().toString().equals("") || attr.getData().toString() == null) ) {
										codeCode = attr.getData().toString();
									}
									xpathSelector = DocumentHelper.createXPath(
									"//*[name() = 'value']/@code");
									attr = (Attribute) xpathSelector.selectSingleNode(tempDoc);
									if ( attr != null && !(attr.getData().toString().equals("") || attr.getData().toString() == null) ) {
										valueCode = attr.getData().toString();
									}
									
									if ("3141-9".equals(codeCode)) {
										wt = valueCode;
									} else if ("3137-7".equals(codeCode)) {
										ht = valueCode;
									} else if ("61550-0".equals(codeCode)){
										freqSportsDrink = valueCode;
									} else if ("61473-5".equals(codeCode)){
										freqSoda = valueCode;
									} else if ("226354008".equals(codeCode)){
										freqWater = valueCode;
									} else if ("226448008".equals(codeCode)){
										freqVeg = valueCode;
									} else if ("61551-8".equals(codeCode)){
										freqFruit = valueCode;
									} else if ("61468-5".equals(codeCode)){
										freqFruitJuice = valueCode;
									} else if ("68510-7".equals(codeCode)){
										freqFastFood = valueCode;
									} else if ("169741004".equals(codeCode)){
										breastFed = valueCode;
									} else if ("226408000".equals(codeCode)){
										formulaQuant = valueCode;
									} else if ("50845008".equals(codeCode)){
										problemNursing = valueCode;
									} else if ("55411-3".equals(codeCode)){
										physicalQuant = valueCode;
									} else if ("68130003".equals(codeCode)){
										freqPhysical = valueCode;
									} else if ("INV888".equals(codeCode)){
										tvQuant = valueCode;
									} else if ("INV890".equals(codeCode)){
										gameQuant = valueCode;
									} else if ("65551-4".equals(codeCode)){
										bedTime = valueCode;
									} else if ("65968-0".equals(codeCode)){
										sleepQuant = valueCode;
									} else if ("427362008".equals(codeCode)){
										readyNutrition = valueCode;
									} else if ("426116001".equals(codeCode)){
										readySleep = valueCode;
									} else if ("11449-6".equals(codeCode)){
										pregnant = valueCode;
									} else if ("427448000".equals(codeCode)){
										readyExercise = valueCode;
									} else if ("425886001".equals(codeCode)){
										readyScreen = valueCode;
									}
								}
								
								/* Calculate age */
								age = birthDateToAge(dob.substring(0, 4) + dob.substring(5, 7) + dob.substring(8, 10));
								
								/* Normalize race/ethnicity */
								if (!"Hispanic".equals(ethnic)) {
								  if ("White".equals(race)) {
									  ethnic = "White";
								  } else if ("American Indian/Alaskan Native".equals(race)) {
									  ethnic = "American Indian/Alaskan Native";
								  } else if ("Black".equals(race)) {
									  ethnic = "Black";
								  } else if ("Asian".equals(race)) {
									  ethnic = "Asian";
								  } else if ("Pacific Islander".equals(race)) {
									  ethnic = "Pacific Islander";
								  } else if ("Other".equals(race)) {
									  ethnic = "Other";
								  }
								}
								
								/* Calculate BMI */
				       			if (!"".equals(wt) && !"".equals(ht) && !(Float.parseFloat(ht) == 0)) {
				       				bmi = (Float.parseFloat(wt) * 703) / (Float.parseFloat(ht) * Float.parseFloat(ht));
								}
								
								/* Pick a random Zip code from array */
								zip = zipCodes.get(rand.nextInt(552)).toString();

								dataInsert = "INSERT healthy_weight_obs SET " +
								"patient_id = " + (pid != null && !"".equals(pid) ? "'" + pid + "'" : "NULL") + ", " +
								"gender = " + (sex != null && !"".equals(sex) ? "'" + sex + "'" : "NULL") + ", " +
								"birthdate = " + (dob != null && !"".equals(dob) ? "'" + dob + "'" : "NULL") + ", " +
//									"race = " + (race != null && !"".equals(race) ? "'" + race + "'" : "NULL") + ", " +
								"ethnicity = " + (ethnic != null && !"".equals(ethnic) ? "'" + ethnic + "'" : "NULL") + ", " +
								"zip_code = " + (zip != null && !"".equals(zip) ? "'" + zip + "'" : "NULL") + ", " +
								"obs_date = " + (odt != null && !"".equals(odt) ? "'" + odt + "'" : "NULL") + ", " +
								"weight_pounds = " + (wt != null && !"".equals(wt) ? "'" + wt + "'" : "NULL") + ", " +
								"height_inches = " + (ht != null && !"".equals(ht) ? "'" + ht + "'" : "NULL") + ", " +
								"calculated_bmi = " + (bmi != 0 ? "'" + bmi + "'" : "NULL") + ", " +
								"calculated_age = " + (age != null && !"".equals(age) ? "'" + age + "'" : "NULL") + ", " +
								"occupation_41 = " + (occ41 != null && !"".equals(occ41) ? "'" + occ41 + "'" : "NULL") + ", " +
								"occupation_23_code = " + (occ23c != null && !"".equals(occ23c) ? "'" + occ23c + "'" : "NULL") + ", " +
								"freq_sports_drink = " + (freqSportsDrink != null && !"".equals(freqSportsDrink) ? "'" + freqSportsDrink + "'" : "NULL") + ", " +
								"freq_soda = " + (freqSoda != null && !"".equals(freqSoda) ? "'" + freqSoda + "'" : "NULL") + ", " +
								"freq_water = " + (freqWater != null && !"".equals(freqWater) ? "'" + freqWater + "'" : "NULL") + ", " +
								"freq_veg = " + (freqVeg != null && !"".equals(freqVeg) ? "'" + freqVeg + "'" : "NULL") + ", " +
								"freq_fruit = " + (freqFruit != null && !"".equals(freqFruit) ? "'" + freqFruit + "'" : "NULL") + ", " +
								"freq_fruit_juice = " + (freqFruitJuice != null && !"".equals(freqFruitJuice) ? "'" + freqFruitJuice + "'" : "NULL") + ", " +
								"freq_fast_food = " + (freqFastFood != null && !"".equals(freqFastFood) ? "'" + freqFastFood + "'" : "NULL") + ", " +
								"breast_fed = " + (breastFed != null && !"".equals(breastFed) ? "'" + breastFed + "'" : "NULL") + ", " +
								"formula_quantity = " + (formulaQuant != null && !"".equals(formulaQuant) ? "'" + formulaQuant + "'" : "NULL") + ", " +
								"problem_nursing = " + (problemNursing != null && !"".equals(problemNursing) ? "'" + problemNursing + "'" : "NULL") + ", " +
								"freq_physical = " + (freqPhysical != null && !"".equals(freqPhysical) ? "'" + freqPhysical + "'" : "NULL") + ", " +
								"physical_quantity = " + (physicalQuant != null && !"".equals(physicalQuant) ? "'" + physicalQuant + "'" : "NULL") + ", " +
								"tv_quantity = " + (tvQuant != null && !"".equals(tvQuant) ? "'" + tvQuant + "'" : "NULL") + ", " +
								"game_quantity = " + (gameQuant != null && !"".equals(gameQuant) ? "'" + gameQuant + "'" : "NULL") + ", " +
								"bed_time = " + (bedTime != null && !"".equals(bedTime) ? "'" + bedTime + "'" : "NULL") + ", " +
								"sleep_quantity = " + (sleepQuant != null && !"".equals(sleepQuant) ? "'" + sleepQuant + "'" : "NULL") + ", " +
								"is_pregnant = " + (pregnant != null && !"".equals(pregnant) ? "'" + pregnant + "'" : "NULL") + ", " +
								"ready_nutrition = " + (readyNutrition != null && !"".equals(readyNutrition) ? "'" + readyNutrition + "'" : "NULL") + ", " +
								"ready_sleep = " + (readySleep != null && !"".equals(readySleep) ? "'" + readySleep + "'" : "NULL") + ", " +
								"ready_exercise = " + (readyExercise != null && !"".equals(readyExercise) ? "'" + readyExercise + "'" : "NULL") + ", " +
								"ready_screen = " + (readyScreen != null && !"".equals(readyScreen) ? "'" + readyScreen + "'" : "NULL") + ", " +
								"import_source = 'my_unique_id', " +
								"import_datetime = now()";

								p2.println("About to insert into healthy_weight_obs: " + dataInsert);
								
								try {
//										if ( !(rr.getPatientIdList().get(i) == null || rr.getPatientIdList().get(i).equals("")) ) {
										stmt = conn.createStatement();
										rs = stmt.executeUpdate(dataInsert);
//										}
								} catch (SQLException e) {
									if (e.getErrorCode() == 1062) {
										/* Don't crap out just because of 'duplicate key' errors, add to running dupe tally */
										dupes++;
										isDupe = 1;
									} else {
										log.error(e);
										p2.println(e);
									}
								}
							}
						}
					}
					
//				} catch (FileNotFoundException e) {
//					System.err.println("XDSSoapRequest: " + e);
//					p2.println("XDSSoapRequest: " + e);
//				} catch (IOException e) {
//					System.err.println("XDSSoapRequest: " + e);
//					p2.println("XDSSoapRequest: " + e);
//				}
			}
		} catch (MalformedStoredQueryException e) {
			log.error(e);
			p2.println(e);
		} catch ( DocumentException e ) {
			log.error(e);
			p2.println(e);
		} catch (SQLException e) {
			log.error(e);
			p2.println(e);
		} finally {
			p2.close();
		}
	}
	
	private String alphaOnly (String str) {
		return (str.replaceAll("[^a-zA-Z0-9, ]", ""));
	}

	private String birthDateToAge (String bd) {
		if (bd.matches("\\d+")) {
			bd = bd.substring(0, 8);
			int bdYr = Integer.parseInt(bd.substring(0, 4));
			int bdMo = Integer.parseInt(bd.substring(4, 6));
			int bdDay = Integer.parseInt(bd.substring(6, 8));
			Calendar today = new GregorianCalendar();
			today.setTime (new Date());
			int ageSecs = (today.get(Calendar.YEAR) - bdYr) * 31557600 +
				(today.get(Calendar.MONTH) - bdMo) * 2629800 +
				(today.get(Calendar.DATE) - bdDay) * 86400;
			if ((((ageSecs / 3600) / 24) / 365.25) >= 0) {
				return (Double.toString(((ageSecs / 3600) / 24) / 365.25));
			} else {
				return ("");
			}
		} else {
			return ("");
		}
	}

	private void registerMyHostnameVerifier() {
		javax.net.ssl.HostnameVerifier myHv = new javax.net.ssl.HostnameVerifier()
		{
			public boolean verify(String hostName,javax.net.ssl.SSLSession session) {
				return true;
			}
		};
		javax.net.ssl.HttpsURLConnection.setDefaultHostnameVerifier(myHv);
	}
	
	public void doGet(HttpServletRequest req, HttpServletResponse resp)
		throws ServletException, IOException {
	   		doPost(req, resp);
	}
	   
	public void handleError(HttpServletRequest req, HttpServletResponse res) 
	   throws ServletException, IOException {
          // res.getOutputStream().print("ERROR");
        /*
        RequestDispatcher rd = 
            req.getRequestDispatcher(req.getContextPath() + "/" + XDSControllerServlet.ERROR_HANDLER);
            rd.forward(req, res);
           */
	}
}


