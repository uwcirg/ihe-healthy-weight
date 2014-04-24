/*
 * Copyright 2004-2009 (C) University of Washington. All Rights Reserved.
 * Created on Jan 17, 2006
 * University of Washington, CIRG
 */
package edu.washington.cirg.himss.servlet;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.PrintStream;
import java.io.PrintWriter;
import java.net.URI;
import java.net.URISyntaxException;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
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

import javax.microedition.io.Connector;
import javax.microedition.io.HttpConnection;
import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.Element;
import org.dom4j.Node;
import org.dom4j.io.SAXReader;
import org.jaxen.JaxenException;
import org.jaxen.dom4j.Dom4jXPath;
import org.openhealthtools.ihe.atna.auditor.XDSConsumerAuditor;
import org.openhealthtools.ihe.atna.auditor.codes.rfc3881.RFC3881EventCodes.RFC3881EventOutcomeCodes;
import org.openhealthtools.ihe.common.hl7v2.CX;
import org.openhealthtools.ihe.common.hl7v2.Hl7v2Factory;
import org.openhealthtools.ihe.common.hl7v2.XON;
import org.openhealthtools.ihe.xds.consumer.B_Consumer;
import org.openhealthtools.ihe.xds.consumer.query.DateTimeRange;
import org.openhealthtools.ihe.xds.consumer.query.MalformedQueryException;
import org.openhealthtools.ihe.xds.consumer.retrieve.DocumentRequestType;
import org.openhealthtools.ihe.xds.consumer.retrieve.RetrieveDocumentSetRequestType;
import org.openhealthtools.ihe.xds.consumer.retrieve.RetrieveFactory;
import org.openhealthtools.ihe.xds.consumer.storedquery.FindDocumentsQuery;
import org.openhealthtools.ihe.xds.consumer.storedquery.MalformedStoredQueryException;
import org.openhealthtools.ihe.xds.consumer.storedquery.FindDocumentsForMultiplePatientsQuery;
import org.openhealthtools.ihe.xds.consumer.storedquery.StoredQuery;
import org.openhealthtools.ihe.xds.consumer.storedquery.StoredQueryParameterList;
import org.openhealthtools.ihe.xds.metadata.AuthorType;
import org.openhealthtools.ihe.xds.metadata.AvailabilityStatusType;
import org.openhealthtools.ihe.xds.metadata.CodedMetadataType;
import org.openhealthtools.ihe.xds.metadata.DocumentEntryType;
import org.openhealthtools.ihe.xds.metadata.MetadataFactory;
import org.openhealthtools.ihe.xds.metadata.constants.DocumentEntryConstants;
import org.openhealthtools.ihe.xds.response.DocumentEntryResponseType;
import org.openhealthtools.ihe.xds.response.XDSQueryResponseType;
import org.openhealthtools.ihe.xds.response.XDSRetrieveResponseType;
import org.openhealthtools.ihe.xds.response.XDSStatusType;

import edu.washington.cirg.himss.xds.XDSConfigurationException;
import edu.washington.cirg.himss.xds.responses.XDSMSDocumentParse;
import edu.washington.cirg.himss.xds.util.XDSConstants;
import edu.washington.cirg.util.Util;

/** This servlet acts as a wrapper around classes that generate XDS Registry query
 * transactions.  These requests query an ebXML Registry for documents matching 
 * particular criteria and return a list of matching documents.
 * @author <a href="mailto:jsibley@u.washington.edu">Jim Sibley</a>
 *
 */
public class FindDocumentsServlet extends HttpServlet {

	private static Log log = LogFactory.getLog(FindDocumentsServlet.class);
	private static boolean LOG_PERFORMANCE_DATA = false; 

	private String DATASOURCE_NAME;
	
	public FindDocumentsServlet() {
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
		String repoUrl = XDSConstants.REPO_URL;
		
//		if ( patientId == null || patientId.equals("") ) {
//			throw new ServletException("patientId param not passed to servlet");
//    	}
    	if ( queryUrl == null || queryUrl.equals("") ) {
    		throw new ServletException("targetUrl param not passed to servlet");
    	}
    	if ( atnaUrl == null || atnaUrl.equals("") ) {
    		throw new ServletException("atnaUrl param not passed to servlet");
    	}
    	if ( timeSlot == null || timeSlot.equals("") ) {
    		throw new ServletException("timeSlot param not passed to servlet");
    	}
    	if ( qStartTime == null || qStartTime.equals("") ) {
    		throw new ServletException("startTime param not passed to servlet");
    	}
    	if ( qStopTime == null || qStopTime.equals("") ) {
    		throw new ServletException("stopTime param not passed to servlet");
    	}
    	if ( !( usePH == null || "".equals(usePH) || "0".equals(usePH) ) ) {
    		// change the stored query uuid if we want the "public health" version of the query
    		storedQueryUuid = XDSConstants.UUID_PH_FIND_DOCUMENTS_STORED_QUERY_CODE;
    		// nuke the pid to trigger the Public Health version of the Find Documents query
    		patientId = "";
    	}
    	if ( !( useTls == null || useTls.equals("") || "0".equals(useTls) ) ) {
            // set the system security properties
            System.setProperty("javax.net.ssl.keyStore", XDSConstants.TLS_KEYSTORE);
            System.setProperty("javax.net.ssl.keyStorePassword", XDSConstants.TLS_KEYSTORE_PASS);
            System.setProperty("javax.net.ssl.trustStore", XDSConstants.TLS_CERTFILE);
            System.setProperty("javax.net.ssl.trustStorePassword", XDSConstants.TLS_CERTFILE_PASS);
//            repoUrl = "https://xds-ibm.lgs.com:9443/IBMXDSRepository/XDSb/SOAP12/Repository";
            repoUrl = XDSConstants.SECURE_REPO_URL;
        }
    	
    	Connection conn = null;
		Random rand = new Random();
    	List chiefComplaints = new ArrayList();
		File file = new File("/var/lib/tomcat7/webapps/xds/WEB-INF/classes/chief_complaint_pool.txt");
		BufferedReader bufRdr  = new BufferedReader(new FileReader(file));
		String line = null;

		/* read each line of text file */
		while((line = bufRdr.readLine()) != null) {
			chiefComplaints.add(line);
		}
		 
		/* close the file */
		bufRdr.close(); 
		
		// Translate pid if ListDocs is desired
		if ( !( listDocs == null || listDocs.equals("") || "0".equals(listDocs) ) ) {
			File file2 = new File("/home/CIRG/jsibley/himss2008/allscripts_code_xref.txt");
			BufferedReader bufRdr2 = new BufferedReader(new FileReader(file2));
			String line2 = null;

			/* read each line of text file */
			while((line2 = bufRdr2.readLine()) != null) {
				if (line2.indexOf(":" + patientId + ":") != -1) {
					patientId = line2.substring(0, line2.indexOf(":"));
					break;
				}
			}
			 
			/* close the file */
			bufRdr2.close();
		}

		try {
			if ( LOG_PERFORMANCE_DATA ) { //Start GenerateRequest
				startTime=System.currentTimeMillis();
			}

//			FindDocumentsStoredQueryRequest xdsreq =
//				new FindDocumentsStoredQueryRequest(patientId, timeSlot, qStartTime, qStopTime, AdhocQueryRequest.LEAF_CLASS, storedQueryUuid);
//			Document reqDocument = xdsreq.generateRequest();
//	    	if ( LOG_PERFORMANCE_DATA) { //End GenerateRequest
//				stopTime = System.currentTimeMillis();
//				log.info("TIMER,Generate XDS Query Request," + (stopTime-startTime) + " ms");
//				startTime=System.currentTimeMillis();
//	    	}//Start GenerateSOAPRequest
	    	
	    	//XDSUtil.printDOM(System.err, reqDocument);
	    	
//			XDSSoapRequest soapReq = 
//				new XDSSoapRequest(reqDocument, queryUrl, "1.1");

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
				throw new ServletException(e);
			}
			c = new B_Consumer(registryURI);
			
			XDSConsumerAuditor auditor = XDSConsumerAuditor.getAuditor();
			try {
				auditor.getConfig().setAuditRepositoryUri(new URI("syslog://" + atnaUrl));
			} catch (URISyntaxException e) {
				e.printStackTrace();
			}
			auditor.getConfig().setAuditorEnabled(true);
			auditor.getConfig().setSystemIpAddress("140.142.56.21");
			auditor.getConfig().setAuditSourceId("UWA/SAIC");
			auditor.auditRegistryStoredQueryEvent(RFC3881EventOutcomeCodes.SUCCESS, queryUrl, storedQueryUuid, "N/A", "N/A", patientId);
			auditor.auditActorStartEvent(RFC3881EventOutcomeCodes.SUCCESS, "UW/SAIC", "ihe2010");
			
			StoredQuery q = null;
			
			if (! "".equals(patientId)) {
				CX queryPatientId = Hl7v2Factory.eINSTANCE.createCX();
				queryPatientId.setIdNumber(patientId.substring(0, patientId.indexOf("^")));
				queryPatientId.setAssigningAuthorityName("IHENA");
//				queryPatientId.setAssigningAuthorityUniversalId("1.3.6.1.4.1.21367.2007.2.1");
//				queryPatientId.setAssigningAuthorityUniversalId("1.3.6.1.4.1.21367.2005.3.7.9");
				queryPatientId.setAssigningAuthorityUniversalId("1.3.6.1.4.1.21367.2010.1.2.300");
//				queryPatientId.setAssigningAuthorityUniversalId(patientId.substring(patientId.lastIndexOf("^") + 1));
				queryPatientId.setAssigningAuthorityUniversalIdType("ISO");
				// make query
				DateTimeRange creationTimeRange = null;
				try {
					creationTimeRange = new DateTimeRange(DocumentEntryConstants.CREATION_TIME, qStartTime, qStopTime);
				} catch (MalformedQueryException e) {
					e.printStackTrace();
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
				event1.setCode("55014007");
				event1.setSchemeName("SNOMED-CT");
				CodedMetadataType event2 = MetadataFactory.eINSTANCE.createCodedMetadataType();
				event2.setCode("407479009");
				event2.setSchemeName("SNOMED-CT");
				CodedMetadataType event3 = MetadataFactory.eINSTANCE.createCodedMetadataType();
				event3.setCode("420362005");
				event3.setSchemeName("SNOMED-CT");
				CodedMetadataType event4 = MetadataFactory.eINSTANCE.createCodedMetadataType();
				event4.setCode("421539000");
				event4.setSchemeName("SNOMED-CT");
				CodedMetadataType event5 = MetadataFactory.eINSTANCE.createCodedMetadataType();
				event5.setCode("420508007");
				event5.setSchemeName("SNOMED-CT");
				CodedMetadataType event6 = MetadataFactory.eINSTANCE.createCodedMetadataType();
				event6.setCode("421264001");
				event6.setSchemeName("SNOMED-CT");
				CodedMetadataType event7 = MetadataFactory.eINSTANCE.createCodedMetadataType();
				event7.setCode("407480007");
				event7.setSchemeName("SNOMED-CT");
				CodedMetadataType event8 = MetadataFactory.eINSTANCE.createCodedMetadataType();
				event8.setCode("407482004");
				event8.setSchemeName("SNOMED-CT");
				eventCodes = new CodedMetadataType[]{event1, event2, event3, event4, event5, event6, event7, event8}; 

				try {
					creationTimeRange = new DateTimeRange(DocumentEntryConstants.CREATION_TIME, qStartTime, qStopTime);
				} catch (MalformedQueryException e) {
					e.printStackTrace();
				}
//				q = new PublicHealthFindDocumentsQuery(
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
			
			
			if ( LOG_PERFORMANCE_DATA) { //End GenerateSOAPRequest
				stopTime = System.currentTimeMillis();
				Calendar cal = Calendar.getInstance();
			    SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
				log.info("TIMER," + sdf.format(cal.getTime()).toString() + ",Generate Query SOAP Request," + (stopTime-startTime) + " ms");
				startTime=System.currentTimeMillis();
	    	}//Start SendSOAPRequest

//			SOAPMessage response = soapReq.sendMessage();

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

			auditor.auditActorStopEvent(RFC3881EventOutcomeCodes.SUCCESS, "UW/SAIC", "ihe2010");

			if ( LOG_PERFORMANCE_DATA) {//End SendSOAPRequest
				stopTime = System.currentTimeMillis();
				Calendar cal = Calendar.getInstance();
			    SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
				log.info("TIMER," + sdf.format(cal.getTime()).toString() + ",Send Query SOAP Request," + (stopTime-startTime) + " ms");
				startTime = System.currentTimeMillis();
			}

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
						for (int j = 0; j < (doc.getAuthors() != null ? doc.getAuthors().size() : 1); j++) {
							p.println("Author='" + (doc.getAuthors().get(j) != null && ((XON)((AuthorType)doc.getAuthors().get(j)).getAuthorInstitution().get(j)).getOrganizationName() != null ? ((XON)((AuthorType)doc.getAuthors().get(j)).getAuthorInstitution().get(j)).getOrganizationName() : "NULL") + "'");
						}
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

				/* Parse SOAP response into a set of lists */
				//	FindDocumentsResponse rr = new FindDocumentsResponse(response);
//				FindDocumentsResponse rr = null;
				if ( LOG_PERFORMANCE_DATA ) {
					stopTime = System.currentTimeMillis();
					log.info("TIMER,Parse FindDocuments SOAP Response Message," + (stopTime-startTime) + " ms");				
					startTime = System.currentTimeMillis();
				}

				// Just list documents if told to do so
				if ( !( listDocs == null || listDocs.equals("") || "0".equals(listDocs) ) ) {
					/* Output something to the user */
					resp.setContentType("text/html");
					PrintWriter out = resp.getWriter();
					out.print("<HTML>\n<BODY>\n");

//					if ( ! rr.wasSuccessful() ) {
//						out.print("<H2>Application Error</H2>\n");
//						String error;
//						for ( Iterator iter = rr.getErrorList().iterator(); iter.hasNext();) {
//							error = (String)iter.next();
//							log.error(error);
//							out.print("<H4>" + error + "</H4>\n");
//						}
					if (!response.getStatus().equals(XDSStatusType.SUCCESS_LITERAL)) {
						log.error("WARNING: Unsuccessful query.\n");
					} else {
						SimpleDateFormat myformat = new SimpleDateFormat("d MMM yyyy HH:mm:ss");
//						out.print("<H2>" + myformat.format(new Date()) + ": " + rr.getPatientIdList().size() + " documents found for patientId '" + patientId + "'.</H2>\n");
						out.print("<H2>" + myformat.format(new Date()) + ": " + response.getDocumentEntryResponses().size() + " documents found for patientId '" + patientId + "'.</H2>\n");
						out.print("<table border=\"1\">\n");
						out.print("<tr><th width=\"100\">Date Saved</th><th width=\"100\">Type Code</th><th width=\"100\">Link</th><th width=\"100\">Format Code</th><th width=\"100\">Language Code</th><th width=\"100\">Mime Type</th></tr>");
						for ( int i = 0; i <= response.getDocumentEntryResponses().size() - 1; i++ ) {
							DocumentEntryType doc = ((DocumentEntryResponseType)response.getDocumentEntryResponses().get(i)).getDocumentEntry();
//							out.print("<tr><td align=\"center\" width=\"100\">" + rr.getCreationTimeList().get(i).toString() + "</td>\n");
//							out.print("<td align=\"center\" width=\"200\">" + rr.getTypeCodeList().get(i).toString() + "</td>\n");
							out.print("<tr><td align=\"center\" width=\"100\">" + doc.getCreationTime() + "</td>\n");
							out.print("<td align=\"center\" width=\"200\">" + doc.getTypeCode().getCode() + "</td>\n");
							//if (rr.getDocumentUriList().get(i).toString().indexOf("https:") == -1) {
//							out.print("<td align=\"center\" width=\"100\"><a href=\"" + rr.getDocumentUriList().get(i).toString() + "\">Document Link</a></td>\n");
							out.print("<td align=\"center\" width=\"100\"><a href=\"" + doc.getUri() + "\">Document Link</a></td>\n");
							//} else {
							//	out.print("<td align=\"center\" width=\"100\">&nbsp;</td>\n");
							//}
//							out.print("<td align=\"center\" width=\"200\">" + rr.getFormatCodeList().get(i).toString() + "</td>\n");
//							out.print("<td align=\"center\" width=\"100\">" + rr.getLanguageCodeList().get(i).toString() + "</td>\n");
//							out.print("<td align=\"center\" width=\"100\">" + rr.getMimeTypeList().get(i).toString() + "</td></tr>\n");
							out.print("<td align=\"center\" width=\"200\">" + doc.getFormatCode().getCode() + "</td>\n");
							out.print("<td align=\"center\" width=\"100\">" + doc.getLanguageCode() + "</td>\n");
							out.print("<td align=\"center\" width=\"100\">" + doc.getMimeType() + "</td></tr>\n");
							//out.print("<td>" + rr.getTypeCodeList().get(i).toString() + "</td>\n");
							//out.print("<H4>Patient ID (" + rr.getPatientIdList().size() + "):<br> " + rr.getPatientIdList().toString() + "</H4>\n");
							//out.print("<H4>Source Patient Info (" + rr.getSourcePatientInfoList().size() + "):<br> " + rr.getSourcePatientInfoList().toString() + "</H4>\n");
							//out.print("<H4>Unique ID (" + rr.getUniqueIdList().size() + "):<br> " + rr.getUniqueIdList().toString() + "</H4>\n");
						}
						out.print("</table>\n");
					}
					out.print("</BODY>\n</HTML>\n");
					out.flush();
				} else {
					/* Stuff metadata into the database */
					log.info("DSN = '" + DATASOURCE_NAME + "'");

//					conn = Util.getConnection(DATASOURCE_NAME);
					conn = Util.getConnection(null, null, null, null, null);
					Statement stmt = null;
					int rs = 0;

					if ( conn == null ) {
						log.error("Connection is null");
						throw new SQLException("Unable to obtain a database connection");
					}

//					log.info("Conn info: " + conn.getClientInfo() + ", " + conn.toString());

					int dupes = 0;
					log.info("Found " + response.getDocumentEntryResponses().size() + " documents");
					for ( int i = 0; i <= response.getDocumentEntryResponses().size() - 1; i++ ) {
						DocumentEntryType doc = ((DocumentEntryResponseType)response.getDocumentEntryResponses().get(i)).getDocumentEntry();
						int isDupe = 0;

						// HXTI is converting to GMT, so we need to convert back to EST
						String creationTime = (doc.getCreationTime() != null ? doc.getCreationTime() : "NULL");
						if (creationTime.indexOf("-") != -1) {
							creationTime = creationTime.substring(0, creationTime.indexOf("-"));
						}
						while (creationTime.length() < 14) {
							creationTime += "0";
						}
						if (queryUrl.toLowerCase().indexOf("hxti1") != -1) {
							//Calendar gmtCal = new GregorianCalendar(TimeZone.getTimeZone("Greenwich"));
							//gmtCal.set(Integer.parseInt(creationTime.substring(0, 4)),
							//Integer.parseInt(creationTime.substring(4, 6)) - 1,
							//Integer.parseInt(creationTime.substring(6, 8)),
							//Integer.parseInt(creationTime.substring(8, 10)),
							//Integer.parseInt(creationTime.substring(10, 12)),
							//Integer.parseInt(creationTime.substring(12, 14)));

							//Calendar local = new GregorianCalendar(TimeZone.getTimeZone("New_York"));
							//local.setTimeInMillis(gmtCal.getTimeInMillis());

							//creationTime = Integer.toString(local.get(Calendar.YEAR)) + "-" + (Integer.toString(local.get(Calendar.MONTH) + 1)) + "-" +
							//Integer.toString(local.get(Calendar.DATE)) + " " + Integer.toString(local.get(Calendar.HOUR_OF_DAY)) + ":" +
							//Integer.toString(local.get(Calendar.MINUTE)) + ":" + Integer.toString(local.get(Calendar.SECOND));

							SimpleDateFormat gmtFormat = new SimpleDateFormat("yyyyMMddHHmmss");
							gmtFormat.setTimeZone( TimeZone.getTimeZone( "GMT") );

							SimpleDateFormat estFormat = new SimpleDateFormat( "yyyyMMddHHmmss" );
							estFormat.setTimeZone( TimeZone.getTimeZone( "America/New_York" ));

							Date gmtDate = gmtFormat.parse(creationTime, new ParsePosition(0));
							creationTime = estFormat.format( gmtDate );
						}

						// Look for event codes (for lab docs mostly)
						String eCode = "";
						for (int k = 0; k < (doc.getEventCode() != null ? doc.getEventCode().size() : 1); k++) {
							if (k > 0) {
								eCode += "|";
							}
							eCode += (doc.getEventCode() != null && ((CodedMetadataType)doc.getEventCode().get(k)).getSchemeName() != null ? ((CodedMetadataType)doc.getEventCode().get(k)).getSchemeName() : "NULL") + ":" + (doc.getEventCode() != null && ((CodedMetadataType)doc.getEventCode().get(k)).getCode() != null ? ((CodedMetadataType)doc.getEventCode().get(k)).getCode() : "NULL");
						}
						
						/* If it looks like a lab doc, pull out lab code and translate it to text */
						String labCode = "";
						String labText = "";
						if (doc.getTypeCode() != null && doc.getTypeCode().getCode() != null &&
							doc.getClassCode() != null && doc.getClassCode().getCode() != null &&
							("28570-0".equals(doc.getTypeCode().getCode()) ||
							 "Laboratory Report".equalsIgnoreCase(doc.getTypeCode().getCode()) ||
							 "Pathology Procedure".equalsIgnoreCase(doc.getClassCode().getCode()) ||
							 ("11488-4".equals(doc.getTypeCode().getCode()) &&
							  "Consult".equalsIgnoreCase(doc.getClassCode().getCode()) &&
							  "HL7/Lab 2.5".equals(doc.getFormatCode().getCode())))) {

							int phadIsolateType = -1;
							if (eCode.indexOf("PHAD_IsolateType:") != -1) {
								phadIsolateType = Integer.parseInt(eCode.substring(eCode.indexOf("PHAD_IsolateType:") + 17, eCode.indexOf("PHAD_IsolateType:") + 18));
							}
							if (phadIsolateType == -1) {
								/* StarLIMS isn't sending PHAD_IsolateType, assume PHIN Influenza codes */
								phadIsolateType = 4;
							}
							
							/* Read lab type from metadata */
							if (eCode.lastIndexOf("SNOMED-CT:") != -1) {
								labCode = eCode.substring(eCode.lastIndexOf("SNOMED-CT:") + 10);
							}

							/* Translate coded lab value into text */
							ClassLoader loader = Thread.currentThread ().getContextClassLoader();
							InputStream in = loader.getResourceAsStream("phiadIsolateType" + phadIsolateType + "Codes.xml");
							SAXReader reader = new SAXReader();
							Document xmlDoc;
							try {
								xmlDoc = reader.read(in);
							} catch ( DocumentException de ) {
								log.error(de);
								throw de;
							}

							try {
								Dom4jXPath xpathSelector = new Dom4jXPath("//*[local-name() = 'Code']");
								List<Node> results = xpathSelector.selectNodes(xmlDoc);
								Iterator iterator = results.iterator();
								while (iterator.hasNext()) {
									Element child = (Element)iterator.next();
									if (labCode.equals(child.attributeValue("code")) && labCode.length() > 0) {
										if (labText.length() > 0) {
											labText += "|";
										}
										labText += child.attributeValue("display");
									}
								}
							} catch ( JaxenException e ) {
								log.error(e);
								handleError(req, resp);
							}
							log.info("INFO,labCode = '" + labCode + "', labText = '" + labText + "'");
						}
						
//						if (eventCode.indexOf("\r") != -1) {
//							String [] temp = eventCode.split("\r");
//							for (int k = 0 ; k < temp.length ; k++) {
//								if (temp[k].indexOf("Influenza A H1:N1") != -1) {
//									eventCode = "1";
//									break;
//								} else if (temp[k].indexOf("Influenza A H1:N2") != -1) {
//									eventCode = "2";
//									break;
//								} else if (temp[k].indexOf("Influenza A H3:N2") != -1) {
//									eventCode = "3";
//									break;
//								} else if (temp[k].indexOf("Influenza A H5:N1") != -1) {
//									eventCode = "4";
//									break;
//								} else if (temp[k].indexOf("Influenza B") != -1) {
//									eventCode = "5";
//									break;
//								} else if (temp[k].indexOf("Influenza C") != -1) {
//									eventCode = "6";
//									break;
//								} else if (temp[k].indexOf("Influenza") != -1) {
//									eventCode = "7";
//								}
//							}
//						}

						log.info("INFO,creationTime for metadata," + creationTime);				

						String dataInsert = "INSERT metadata SET " +
						"patientId = '" + (doc.getPatientId() != null && doc.getPatientId().getIdNumber() != null ? doc.getPatientId().getIdNumber() : "NULL") + "', " +
						"documentUri = '" + (doc.getUri() != null ? doc.getUri() : "NULL") + "', " +
						"mimeType = '" + (doc.getMimeType() != null ? doc.getMimeType() : "NULL") + "', " +
						"formatCode = '" + (doc.getFormatCode() != null && doc.getFormatCode().getCode() != null ? doc.getFormatCode().getCode() : "NULL") + "', " +
						"typeCode = '" + labText + "', " +
						"creationTime = '" + creationTime + "', " +
						"uniqueId = '" + (doc.getUniqueId() != null ? doc.getUniqueId() : "NULL") + "', " +
						"languageCode = '" + (doc.getLanguageCode() != null ? doc.getLanguageCode() : "NULL") + "', " +
						"sourcePatientInfo = '"
											+ "PID-7|" + (doc.getSourcePatientInfo() != null && doc.getSourcePatientInfo().getPatientDateOfBirth() != null ? doc.getSourcePatientInfo().getPatientDateOfBirth() : "NULL")
											+ "\rPID-8|" + (doc.getSourcePatientInfo() != null && doc.getSourcePatientInfo().getPatientSex() != null ? doc.getSourcePatientInfo().getPatientSex() : "NULL")
											+ "\rPID-11|" + (doc.getSourcePatientInfo() != null && doc.getSourcePatientInfo().getPatientAddress() != null && doc.getSourcePatientInfo().getPatientAddress().getStreetAddress() != null ? doc.getSourcePatientInfo().getPatientAddress().getStreetAddress() : "NULL")
											+ "^" + (doc.getSourcePatientInfo() != null && doc.getSourcePatientInfo().getPatientAddress() != null && doc.getSourcePatientInfo().getPatientAddress().getOtherDesignation() != null ? doc.getSourcePatientInfo().getPatientAddress().getOtherDesignation() : "NULL")
											+ "^" + (doc.getSourcePatientInfo() != null && doc.getSourcePatientInfo().getPatientAddress() != null && doc.getSourcePatientInfo().getPatientAddress().getCity() != null ? doc.getSourcePatientInfo().getPatientAddress().getCity() : "NULL")
											+ "^" + (doc.getSourcePatientInfo() != null && doc.getSourcePatientInfo().getPatientAddress() != null && doc.getSourcePatientInfo().getPatientAddress().getStateOrProvince() != null ? doc.getSourcePatientInfo().getPatientAddress().getStateOrProvince() : "NULL")
											+ "^" + (doc.getSourcePatientInfo() != null && doc.getSourcePatientInfo().getPatientAddress() != null && doc.getSourcePatientInfo().getPatientAddress().getZipOrPostalCode() != null ? doc.getSourcePatientInfo().getPatientAddress().getZipOrPostalCode() : "NULL")
											+ "^" + (doc.getSourcePatientInfo() != null && doc.getSourcePatientInfo().getPatientAddress() != null && doc.getSourcePatientInfo().getPatientAddress().getCountry() != null ? doc.getSourcePatientInfo().getPatientAddress().getCountry() : "NULL")
											+ "^" + (doc.getSourcePatientInfo() != null && doc.getSourcePatientInfo().getPatientAddress() != null && doc.getSourcePatientInfo().getPatientAddress().getCountyParishCode() != null ? doc.getSourcePatientInfo().getPatientAddress().getCountyParishCode() : "NULL")
											+ "'";

						try {
//							if ( !(rr.getPatientIdList().get(i) == null || rr.getPatientIdList().get(i).equals("")) ) {
								stmt = conn.createStatement();
								rs = stmt.executeUpdate(dataInsert);
//							}
						} catch (SQLException e) {
							if (e.getErrorCode() == 1062) {
								/* Don't crap out just because of 'duplicate key' errors, add to running dupe tally */
								dupes++;
								isDupe = 1;
							} else {
								log.error(e);
								handleError(req, resp);
							}
						}

						// 2/20/2008 - JS: facility is now best retrieved somewhere else than 'Source Patient Info'
						String facility = "";
						for (int k = 0; k < (doc.getAuthors() != null ? doc.getAuthors().size() : 1); k++) {
							if (k > 0) {
								facility += "\r";
							}
							facility += (doc.getAuthors().get(k) != null && ((AuthorType)doc.getAuthors().get(k)).getAuthorInstitution() != null && ((XON)((AuthorType)doc.getAuthors().get(k)).getAuthorInstitution()).getOrganizationName() != null ? ((XON)((AuthorType)doc.getAuthors().get(k)).getAuthorInstitution()).getOrganizationName() : "NULL");
						}
						log.info("INFO: Facility = '" + facility + "'");

//						String spi = rr.getSourcePatientInfoList().get(i).toString();
						String spi = "PID-7|" + (doc.getSourcePatientInfo() != null && doc.getSourcePatientInfo().getPatientDateOfBirth() != null ? doc.getSourcePatientInfo().getPatientDateOfBirth() : "NULL")
							+ "\rPID-8|" + (doc.getSourcePatientInfo() != null && doc.getSourcePatientInfo().getPatientSex() != null ? doc.getSourcePatientInfo().getPatientSex() : "NULL")
							+ "\rPID-11|" + (doc.getSourcePatientInfo() != null && doc.getSourcePatientInfo().getPatientAddress() != null && doc.getSourcePatientInfo().getPatientAddress().getStreetAddress() != null ? doc.getSourcePatientInfo().getPatientAddress().getStreetAddress() : "NULL")
							+ "^" + (doc.getSourcePatientInfo() != null && doc.getSourcePatientInfo().getPatientAddress() != null && doc.getSourcePatientInfo().getPatientAddress().getOtherDesignation() != null ? doc.getSourcePatientInfo().getPatientAddress().getOtherDesignation() : "NULL")
							+ "^" + (doc.getSourcePatientInfo() != null && doc.getSourcePatientInfo().getPatientAddress() != null && doc.getSourcePatientInfo().getPatientAddress().getCity() != null ? doc.getSourcePatientInfo().getPatientAddress().getCity() : "NULL")
							+ "^" + (doc.getSourcePatientInfo() != null && doc.getSourcePatientInfo().getPatientAddress() != null && doc.getSourcePatientInfo().getPatientAddress().getStateOrProvince() != null ? doc.getSourcePatientInfo().getPatientAddress().getStateOrProvince() : "NULL")
							+ "^" + (doc.getSourcePatientInfo() != null && doc.getSourcePatientInfo().getPatientAddress() != null && doc.getSourcePatientInfo().getPatientAddress().getZipOrPostalCode() != null ? doc.getSourcePatientInfo().getPatientAddress().getZipOrPostalCode() : "NULL")
							+ "^" + (doc.getSourcePatientInfo() != null && doc.getSourcePatientInfo().getPatientAddress() != null && doc.getSourcePatientInfo().getPatientAddress().getCountry() != null ? doc.getSourcePatientInfo().getPatientAddress().getCountry() : "NULL")
							+ "^" + (doc.getSourcePatientInfo() != null && doc.getSourcePatientInfo().getPatientAddress() != null && doc.getSourcePatientInfo().getPatientAddress().getCountyParishCode() != null ? doc.getSourcePatientInfo().getPatientAddress().getCountyParishCode() : "NULL");
						String age = "";
						String gender = "";
						String state = "";
						String zip = "";

						if (isDupe != 1) {
							/* Pick apart 'Source Patient Info' and use EW's visit format in another table */		
							//if (spi.indexOf("PID-5|") != -1) {
							//facility = spi.substring(spi.indexOf("PID-5|") + 6, spi.indexOf("^", spi.indexOf("PID-5|") + 6));		
							//}
							if (spi.indexOf("PID-7|") != -1) {
								age = birthDateToAge(spi.substring(spi.indexOf("PID-7|") + 6, spi.indexOf("PID-7|") + 14));
							}
							if (spi.indexOf("PID-8|") != -1) {
								gender = spi.substring(spi.indexOf("PID-8|") + 6, spi.indexOf("PID-8|") + 7);	    			
							}
							if (spi.indexOf("PID-11|") != -1) {
								int begin = spi.indexOf("PID-11|") + 7;
								int end = 0;
								if (spi.indexOf("^", begin) != -1) {
									begin = spi.indexOf("^", begin);
									if (spi.indexOf("^", begin + 1) != -1) {
										begin = spi.indexOf("^", begin + 1);
										if (spi.indexOf("^", begin + 1) != -1) {
											begin = spi.indexOf("^", begin + 1);
											if (spi.indexOf("^", begin + 1) != -1) {
												state = spi.substring(begin + 1, spi.indexOf("^", begin + 1));
												begin = spi.indexOf("^", begin + 1);
												if (spi.indexOf("^", begin + 1) != -1) {
													end = spi.indexOf("^", begin + 1);
													if ((end - begin) > 1 && 
															(spi.substring(begin + 1, end).matches("[\\d-]+"))) {
														zip = spi.substring(begin + 1, end);
													}
												}
											}
										}
									}
								}
							}
							dataInsert = "INSERT odin_himss.metadataVisits SET " +
							"encounter = '" + (doc.getPatientId() != null && doc.getPatientId().getIdNumber() != null ? doc.getPatientId().getIdNumber() : "NULL")
//											+ "-" + (doc.getCreationTime() != null ? doc.getCreationTime() : "NULL")
											+ "', " +
							"facility = '" + facility + "', " +
							"zip = '" + zip + "', " +
							//"zip = '10001', " +
							"age = '" + age + "', " +
							"gender = '" + gender + "', " +
							"date = '" + creationTime + "', " +
							"cc = ''"
							//"disposition = '', " +
							//"classification = ''"
							;

							try {
								stmt = conn.createStatement();
								rs = stmt.executeUpdate(dataInsert);
							} catch (SQLException e) {
								log.info("INFO,Attempted metadataVisits insert='" + dataInsert + ":");
								log.info("WARN,SQL Exception Inserting into metadataVisits Table: " + e.toString());
							}

//							String uri = doc.getUri();
						        	
							/* If XDS-MS document, parse and store data in a separate database table */
							if ("text/xml".equals((doc.getMimeType() != null ? doc.getMimeType() : "NULL")) &&
								("IHE/PCC/MS/1.0".equals((doc.getFormatCode() != null && doc.getFormatCode().getCode() != null ? doc.getFormatCode().getCode() : "NULL")) ||
								 "XDS-MS".equals((doc.getFormatCode() != null && doc.getFormatCode().getCode() != null ? doc.getFormatCode().getCode() : "NULL")) ||
								 "1.3.6.1.4.1.19376.1.5.3.1.1.2".equals((doc.getFormatCode() != null && doc.getFormatCode().getCode() != null ? doc.getFormatCode().getCode() : "NULL")) ||
								 "CDAR2/IHE 1.0".equals((doc.getFormatCode() != null && doc.getFormatCode().getCode() != null ? doc.getFormatCode().getCode() : "NULL"))) &&
								doc.getUniqueId() != null && !"".equals(doc.getUniqueId()) &&
								doc.getRepositoryUniqueId() != null && !"".equals(doc.getRepositoryUniqueId())) {
								if ( LOG_PERFORMANCE_DATA ) {
									stopTime = System.currentTimeMillis();
									log.info("TIMER,Parsing XDS-MS Document," + (stopTime-startTime) + " ms");				
									startTime = System.currentTimeMillis();
								}

								int isDocDupe = 0;

								CX docQueryPatId = Hl7v2Factory.eINSTANCE.createCX();
								docQueryPatId.setIdNumber((doc.getPatientId() != null && doc.getPatientId().getIdNumber() != null ? doc.getPatientId().getIdNumber() : "NULL"));
								docQueryPatId.setAssigningAuthorityName("IHENA");
								docQueryPatId.setAssigningAuthorityUniversalId("1.3.6.1.4.1.21367.2010.1.2.300");
								docQueryPatId.setAssigningAuthorityUniversalIdType("ISO");

						        RetrieveDocumentSetRequestType retrDocSetReq = RetrieveFactory.eINSTANCE.createRetrieveDocumentSetRequestType();
						        DocumentRequestType docReq = RetrieveFactory.eINSTANCE.createDocumentRequestType();
					        	docReq.setDocumentUniqueId(doc.getUniqueId());
						       	docReq.setRepositoryUniqueId(doc.getRepositoryUniqueId());
						       	c.getRepositoryMap().put(doc.getRepositoryUniqueId(), repositoryURI);
//						       	c.setPrimaryRepositoryURI(repositoryURI);
						       	retrDocSetReq.getDocumentRequest().add(docReq);
//						       	List<org.openhealthtools.ihe.xds.document.XDSDocument> documents = new ArrayList<org.openhealthtools.ihe.xds.document.XDSDocument>();

								auditor.auditRetrieveDocumentSetEvent(RFC3881EventOutcomeCodes.SUCCESS, queryUrl, new String[] {doc.getUniqueId()}, doc.getRepositoryUniqueId(), "N/A", doc.getPatientId().getIdNumber());
								auditor.auditActorStartEvent(RFC3881EventOutcomeCodes.SUCCESS, "UW/SAIC", "ihe2010");

								String xml = "";
						       	try {
						       		XDSRetrieveResponseType docResp = c.retrieveDocumentSet(false, retrDocSetReq, docQueryPatId);			
								    //log.info("INFO,Retrieve Document Request Status='" + (docResp.getStatus() != null ? docResp.getStatus() : "NULL") + "'");
								    //log.info("INFO,Retrieved Document[0]='" + (documents != null && documents.get(0) != null ? documents.get(0).toString() : "NULL") + "'");

									auditor.auditActorStopEvent(RFC3881EventOutcomeCodes.SUCCESS, "UW/SAIC", "ihe2010");

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
						       		handleError(req, resp);
						       	}

						       	if (xml.length() > 0) {
						       		// write out file
									try {
										byte byteXml[] = xml.getBytes();
									    FileOutputStream fos = new FileOutputStream("/tmp/xdsMsParsed_uniqueid_" + doc.getUniqueId() + ".xml");
									    fos.write(byteXml);
									    fos.close();
									} catch (FileNotFoundException e) {
									    System.err.println("WARN,File Not Found Exception: " + e);
									} catch (IOException e) {
									    System.err.println("WARN,IO Exception: " + e);
									}

						       		XDSMSDocumentParse dp = new XDSMSDocumentParse(xml);
						       		dataInsert = "INSERT xdsMsParse SET " +
						       		"patientId = '" + dp.getPatientId() + "', " +
						       		"effectiveTime = '" + dp.getEffectiveTime() + "', " +
						       		"birthDate = " + (dp.getBirthDate().equals("") ? "NULL, " : "'" + dp.getBirthDate() + "', ") +
						       		"gender = '" + dp.getGender() + "', " +
						       		"nameGiven = '" + dp.getGivenName() + "', " +
						       		"nameFamily = '" + dp.getFamilyName() + "', " +
						       		"addrStreet = '" + dp.getAddrStreet() + "', " +
						       		"addrCity = '" + dp.getAddrCity() + "', " +
						       		"addrState = '" + dp.getAddrState() + "', " +
						       		"addrZip = '" + dp.getAddrZip() + "', " +
						       		"documentUri = '" + (doc.getUniqueId() != null ? doc.getUniqueId() : "NULL") + "'";

						       		try {
						       			if ( !(dp.getPatientId() == null || dp.getPatientId().equals("")) ) {
						       				stmt = conn.createStatement();
						       				rs = stmt.executeUpdate(dataInsert);
						       			}
						       		} catch (SQLException e) {
						       			isDocDupe = 1;
						       			log.info("INFO,Attempted xdsMsParse insert='" + dataInsert + ":");
						       			log.info("WARN,SQL Exception Inserting into xdsMsParse Table: " + e.toString());
						       		}

						       		/* Mark document as 'beenParsed' in the metadata table */
						       		dataInsert = "UPDATE metadata SET beenParsed = '1' WHERE patientId = '" +
						       		doc.getPatientId().getIdNumber() + "' AND uniqueId = '" +
						       		doc.getUniqueId() + "'";
						       		try {
						       			if ( !(dp.getPatientId() == null || dp.getPatientId().equals("")) ) {
						       				stmt = conn.createStatement();
						       				rs = stmt.executeUpdate(dataInsert);
						       			}
						       		} catch (SQLException e) {
						       			log.info("WARN,SQL Exception Updating metadata Table: " + e.toString());
						       		}

						       		if (isDocDupe != 1) {
						       			// If it's one of Sondra's CDA documents, save to case report manager
						       			if ("CDAR2/IHE 1.0".equals((doc.getFormatCode() != null && doc.getFormatCode().getCode() != null ? doc.getFormatCode().getCode() : "NULL")) &&
						       				"28570-0".equals(doc.getTypeCode().getCode()) &&
						       				"Pathology Procedure".equalsIgnoreCase(doc.getClassCode().getCode())) {
						       				labCode = "";
						       				labText = "";
						       				String url = "http://ihe-rfd.cphi.washington.edu/HIMSS_rfd2010/CdaReceiver.cgi";
						       				HttpConnection hc = null;
						       				InputStream is = null;
						       				ByteArrayOutputStream reqBos = new ByteArrayOutputStream();
						       				ByteArrayOutputStream respBos = new ByteArrayOutputStream();
						       				byte[] res = null;
						       				byte[] postBytes = null;
						       				String boundary = "----------V2ymHFg03ehbqgZCaKO6jy";
						       				String boundaryMessage = "--" + boundary + "\r\nContent-Disposition: form-data; " +
						       										 "name=\"instanceData\"; filename=\"xdsMsParsed_uniqueid_" +
						       										 doc.getUniqueId() + ".xml\"\r\nContent-Type: text/xml\r\n\r\n";
						       				String endBoundary = "\r\n--" + boundary + "--\r\n";
						       				reqBos.write(boundaryMessage.getBytes());
						       				reqBos.write(xml.getBytes());
						       				reqBos.write(endBoundary.getBytes());
						       				postBytes = reqBos.toByteArray();
						       				reqBos.close();
						       				
						       				try
						       				{
						       					hc = (HttpConnection) Connector.open(url);
						       					hc.setRequestProperty("Content-Type", "multipart/form-data; boundary=" + boundary);
						       					hc.setRequestMethod(HttpConnection.POST);
						       					OutputStream dout = hc.openOutputStream();
						       					dout.write(postBytes);
						       					dout.close();
						       					int ch;
						       					is = hc.openInputStream();
						       					while ((ch = is.read()) != -1)
						       					{
						       						respBos.write(ch);
						       					}
						       					res = respBos.toByteArray();
						       				}
						       				catch(Exception e)
						       				{
						       					e.printStackTrace();
						       				}
						       				finally
						       				{
						       					try
						       					{
						       						if(respBos != null)
						       							respBos.close();
						       		 
						       						if(is != null)
						       							is.close();
						       		 
						       						if(hc != null)
						       							hc.close();
						       					}
						       					catch(Exception e2)
						       					{
						       						e2.printStackTrace();
						       					}
						       				}
						       				log.info("INFO,Posted file to case report manager");
						       			}
						       					       			
						       			/* Use EW's visit format in another table */
						       			age = "";
						       			String cc = "";

						       			age = birthDateToAge(dp.getBirthDate());

						       			/* Pick a random CC from array */
						       			cc = chiefComplaints.get(rand.nextInt(29900)).toString();

						       			//									log.info("INFO,Found " + (dp.getCcTitles().size()) + " potential CC fields");
						       			//
						       			//									for ( int j = 0; j <= dp.getCcTitles().size() - 1; j++ ) {
						       			//										log.info("INFO,CC Title=" + dp.getCcTitles().get(j).toString() + ", CC Text=" + dp.getCcTexts().get(j).toString());
						       			//										if (dp.getCcTitles().get(j).toString().equals("History of Present Illness")) {
						       			//											cc = cc.concat(alphaOnly(dp.getCcTexts().get(j).toString()));
						       			//										}
						       			//									}

						       			dataInsert = "INSERT odin_himss.xdsMsParseVisits SET " +
						       			"encounter = '" + dp.getPatientId() + "', " +
						       			"facility = '" + dp.getFacility() + "', " +
						       			"zip = '" + dp.getAddrZip() + "', " +
						       			"age = '" + age + "', " +
						       			"gender = '" + dp.getGender() + "', " +
						       			"date = '" + creationTime + "', " +
						       			"cc = '" + cc + "'"
						       			//"disposition = '', " +
						       			//"classification = ''"
						       			;

						       			try {
						       				if ( !(dp.getPatientId() == null || dp.getPatientId().equals("")) ) {
						       					stmt = conn.createStatement();
						       					rs = stmt.executeUpdate(dataInsert);
						       				}
						       			} catch (SQLException e) {
						       				log.info("INFO,Attempted xdsMsParseVisits insert='" + dataInsert + ":");
						       				log.info("WARN,SQL Exception Inserting into xdsMsParseVisits Table: " + e.toString());
						       			}
						       		}
						       	}
							}

							/* If XDS-LAB document, parse and store data in a separate database table */
							
							/* JS 3/20/2009: No longer need to retrieve and parse XDS-LAB documents since */
							/* all the data we're interested in can be found in the metadata. */
							
							if ("text/xml".equals((doc.getMimeType() != null ? doc.getMimeType() : "NULL")) &&
								("CDAR2/IHE 1.0".equals((doc.getFormatCode() != null && doc.getFormatCode().getCode() != null ? doc.getFormatCode().getCode() : "NULL")) ||
								 "HL7/Lab 2.5".equals((doc.getFormatCode() != null && doc.getFormatCode().getCode() != null ? doc.getFormatCode().getCode() : "NULL"))) &&
								doc.getUniqueId() != null && !"".equals(doc.getUniqueId()) &&
								doc.getRepositoryUniqueId() != null && !"".equals(doc.getRepositoryUniqueId()) &&
								!"".equals(labCode) && !"".equals(labText)) {
								if ( LOG_PERFORMANCE_DATA ) {
									stopTime = System.currentTimeMillis();
									log.info("TIMER,Parsing XDS-LAB Document," + (stopTime-startTime) + " ms");				
									startTime = System.currentTimeMillis();
								}

								int isDocDupe = 0;

//								CX docQueryPatId = Hl7v2Factory.eINSTANCE.createCX();
//								docQueryPatId.setIdNumber((doc.getPatientId() != null && doc.getPatientId().getIdNumber() != null ? doc.getPatientId().getIdNumber() : "NULL"));
//								docQueryPatId.setAssigningAuthorityName("IHENA");
//								docQueryPatId.setAssigningAuthorityUniversalId("1.3.6.1.4.1.21367.2009.1.2.300");
//								docQueryPatId.setAssigningAuthorityUniversalIdType("ISO");
//
//						        RetrieveDocumentSetRequestType retrDocSetReq = RetrieveFactory.eINSTANCE.createRetrieveDocumentSetRequestType();
//						        DocumentRequestType docReq = RetrieveFactory.eINSTANCE.createDocumentRequestType();
//					        	docReq.setDocumentUniqueId(doc.getUniqueId());
//						       	docReq.setRepositoryUniqueId(doc.getRepositoryUniqueId());
//						       	c.setPrimaryRepositoryURI(repositoryURI);
//						       	retrDocSetReq.getDocumentRequest().add(docReq);
//						       	List<org.openhealthtools.ihe.xds.document.Document> documents = new ArrayList<org.openhealthtools.ihe.xds.document.Document>();
//
//								auditor.auditRetrieveDocumentSetEvent(RFC3881EventOutcomeCodes.SUCCESS, "mllp://xds-ibm.lgs.com:514", new String[] {doc.getUniqueId()}, doc.getRepositoryUniqueId(), "N/A", doc.getPatientId().getIdNumber());
//								auditor.auditActorStartEvent(RFC3881EventOutcomeCodes.SUCCESS, "UW/SAIC", "ihe2009");
//
//								String xml = "";
//						       	try {
//						       		XDSResponseType docResp = c.retrieveDocumentSet(retrDocSetReq, documents, docQueryPatId);			
//								    log.info("INFO,Retreive Document Request Status='" + (docResp.getStatus() != null ? docResp.getStatus() : "NULL") + "'");
//								    log.info("INFO,Retrieved Document[0]='" + (documents != null && documents.get(0) != null ? documents.get(0).toString() : "NULL") + "'");
//
//								    auditor.auditActorStopEvent(RFC3881EventOutcomeCodes.SUCCESS, "UW/SAIC", "ihe2009");
//
//									BufferedReader in = new BufferedReader(new InputStreamReader(documents.get(0).getDocumentData()));
//									String str = "";
//									while ((str = in.readLine()) != null) {
//										xml = xml.concat(str);
//									}
//									in.close();
//
//									//log.info("INFO,Retrieved Doc='" + xml + "'");
//						       	} catch (Exception e) {
//						       		log.error("ERROR,Problem Retrieving Document Set: " + e);
//						       		handleError(req, resp);
//						       	}
//
//								XDSLABDocumentParse lp = new XDSLABDocumentParse(xml);
								dataInsert = "INSERT xdsLabData SET " +
								"pid = '" + (doc.getPatientId() != null && doc.getPatientId().getIdNumber() != null ? doc.getPatientId().getIdNumber() : "NULL") + "', " +
								"labDate = '" + creationTime + "', " +
								"labType = '" + (labText.indexOf("|") != -1 ? labText.substring(0, labText.indexOf("|")) : labText) + "', " +
								"labState = '" + state + "', " +
								"labZip = '" + zip + "'";

								try {
//									if ( !(lp.getPatientId() == null || lp.getPatientId().equals("")) ) {
										stmt = conn.createStatement();
										rs = stmt.executeUpdate(dataInsert);
//									}
								} catch (SQLException e) {
									isDocDupe = 1;
									log.info("WARN,SQL Exception Inserting into xdsLabData Table: " + e.toString());
								}

								/* Mark document as 'beenParsed' in the metadata table */
//								dataInsert = "UPDATE metadata SET beenParsed = '1' WHERE patientId = '" +
//								doc.getPatientId().getIdNumber() + "' AND uniqueId = '" +
//								doc.getUniqueId() + "'";
//								try {
//									if ( !(lp.getPatientId() == null || lp.getPatientId().equals("")) ) {
//										stmt = conn.createStatement();
//										rs = stmt.executeUpdate(dataInsert);
//									}
//								} catch (SQLException e) {
//									log.info("WARN,SQL Exception Updating metadata Table: " + e.toString());
//								}

								if (isDocDupe != 1) {
									/* Use EW's visit format in another table */
									dataInsert = "INSERT odin_himss.xdsLabVisits SET " +
									"pid = '" + (doc.getPatientId() != null && doc.getPatientId().getIdNumber() != null ? doc.getPatientId().getIdNumber() : "NULL") + "', " +
									"labDate = '" + creationTime + "', " +
									"labType = '" + (labText.indexOf("|") != -1 ? labText.substring(0, labText.indexOf("|")) : labText) + "', " +
									"labState = '" + state + "', " +
									"labZip = '" + zip + "'";

									try {
//										if ( !(lp.getPatientId() == null || lp.getPatientId().equals("")) ) {
											stmt = conn.createStatement();
											rs = stmt.executeUpdate(dataInsert);
//										}
									} catch (SQLException e) {
										log.info("WARN,SQL Exception Inserting into xdsLabVisits Table: " + e.toString());
									}
								}
							}
						} else {
							int isDocDupe = 0;
							String birthdate = "";

							if (spi.indexOf("PID-7|") != -1) {
								birthdate = spi.substring(spi.indexOf("PID-7|") + 6, spi.indexOf("PID-7|") + 14);
							}

							//XDSMSDocumentParse dp = new XDSMSDocumentParse(rr.getDocumentUriList().get(i).toString().trim(), useTls);
							dataInsert = "INSERT xdsMsParse SET " +
							"patientId = '" + (doc.getPatientId() != null && doc.getPatientId().getIdNumber() != null ? doc.getPatientId().getIdNumber() : "NULL") + "', " +
							"effectiveTime = '" + creationTime + "', " +
							"birthDate = " + (birthdate.matches("\\d{8}") ? "'" + birthdate + "', " : "NULL, ") +
							"gender = '" + gender + "', " +
							//"nameGiven = '" + dp.getGivenName() + "', " +
							//"nameFamily = '" + dp.getFamilyName() + "', " +
							//"addrStreet = '" + dp.getAddrStreet() + "', " +
							//"addrCity = '" + dp.getAddrCity() + "', " +
							//"addrState = '" + dp.getAddrState() + "', " +
							//"addrZip = '" + dp.getAddrZip() + "', " +
							"documentUri = '" + (doc.getUri() != null ? doc.getUri() : "NULL") + "'";

							try {
								if ( !(doc.getPatientId().getIdNumber() == null || "".equals(doc.getPatientId().getIdNumber())) ) {
									stmt = conn.createStatement();
									rs = stmt.executeUpdate(dataInsert);
								}
							} catch (SQLException e) {
								isDocDupe = 1;
								log.info("WARN,SQL Exception Inserting into xdsMsParse Table: " + e.toString());
							}

							/* Mark document as 'beenParsed' in the metadata table */
							//dataInsert = "UPDATE metadata SET beenParsed = '1' WHERE patientId = '" +
							//rr.getPatientIdList().get(i) + "' AND uniqueId = '" +
							//rr.getUniqueIdList().get(i) + "'";
							//try {
							//if ( !(dp.getPatientId() == null || dp.getPatientId().equals("")) ) {
							//stmt = conn.createStatement();
							//rs = stmt.executeUpdate(dataInsert);
							//}
							//} catch (SQLException e) {
							//	log.info("WARN,SQL Exception Updating metadata Table: " + e.toString());
							//}

							if (isDocDupe != 1) {
								/* Use EW's visit format in another table */
								//String age = "";
								String cc = "";

								/* Pick a random CC from array */
								cc = chiefComplaints.get(rand.nextInt(29900)).toString();

								age = birthDateToAge(birthdate);

								log.info("INFO,Inserting CC: " + cc);

								//for ( int j = 0; j <= dp.getCcTitles().size() - 1; j++ ) {
								//log.info("INFO,CC Title=" + dp.getCcTitles().get(j).toString() + ", CC Text=" + dp.getCcTexts().get(j).toString());
								//if (dp.getCcTitles().get(j).toString().equals("History of Present Illness")) {
								//cc = cc.concat(alphaOnly(dp.getCcTexts().get(j).toString()));
								//}
								//}

								dataInsert = "INSERT odin_himss.xdsMsParseVisits SET " +
								"encounter = '" + (doc.getPatientId() != null && doc.getPatientId().getIdNumber() != null ? doc.getPatientId().getIdNumber() : "NULL") + "', " +
								"facility = '" + facility + "', " +
								"zip = '" + zip + "', " +
								"age = '" + age + "', " +
								"gender = '" + gender + "', " +
								"date = '" + creationTime + "', " +
								"cc = '" + cc + "'"
								//"disposition = '', " +
								//"classification = ''"
								;

								try {
									if ( !(doc.getPatientId().getIdNumber() == null || "".equals(doc.getPatientId().getIdNumber())) ) {
										stmt = conn.createStatement();
										rs = stmt.executeUpdate(dataInsert);
									}
								} catch (SQLException e) {
									log.info("INFO,Attempted xdsMsParseVisits insert='" + dataInsert + "'");
									log.info("WARN,SQL Exception Inserting into xdsMsParseVisits Table: " + e.toString());
								}
							}
						}
					}

					/* Output something to the user */
					resp.setContentType("text/html");
					PrintWriter out = resp.getWriter();
					out.print("<HTML>\n<BODY>\n");

					if (!response.getStatus().equals(XDSStatusType.SUCCESS_LITERAL)) {
						out.print("<H2>Unsuccessful Query!</H2>\n");
//						String error;
//						for ( Iterator iter = rr.getErrorList().iterator(); iter.hasNext();) {
//							error = (String)iter.next();
//							log.error(error);
//							out.print("<H4>" + error + "</H4>\n");
//						}
					} else {
						SimpleDateFormat myformat = new SimpleDateFormat("d MMM yyyy HH:mm:ss");
						out.print("<H2>" + myformat.format(new Date()) + ": FindDocuments query successfully sent to registry: " + queryUrl + "</H2>\n");
						out.print("<H4>Documents found: " + response.getDocumentEntryResponses().size() + "<br>Duplicates: " + dupes + "</H4>\n");
//						out.print("<H4>Creation Time (" + rr.getCreationTimeList().size() + "):<br> " + rr.getCreationTimeList().toString() + "</H4>\n");
//						out.print("<H4>Document URI (" + rr.getDocumentUriList().size() + "):<br> " + rr.getDocumentUriList().toString() + "</H4>\n");
//						out.print("<H4>Format Code (" + rr.getFormatCodeList().size() + "):<br> " + rr.getFormatCodeList().toString() + "</H4>\n");
//						out.print("<H4>Language Code (" + rr.getLanguageCodeList().size() + "):<br> " + rr.getLanguageCodeList().toString() + "</H4>\n");
//						out.print("<H4>Mime Type (" + rr.getMimeTypeList().size() + "):<br> " + rr.getMimeTypeList().toString() + "</H4>\n");
//						out.print("<H4>Patient ID (" + rr.getPatientIdList().size() + "):<br> " + rr.getPatientIdList().toString() + "</H4>\n");
//						out.print("<H4>Source Patient Info (" + rr.getSourcePatientInfoList().size() + "):<br> " + rr.getSourcePatientInfoList().toString() + "</H4>\n");
//						out.print("<H4>Type Code (" + rr.getTypeCodeList().size() + "):<br> " + rr.getTypeCodeList().toString() + "</H4>\n");
//						out.print("<H4>Unique ID (" + rr.getUniqueIdList().size() + "):<br> " + rr.getUniqueIdList().toString() + "</H4>\n");
					}
					out.print("</BODY>\n</HTML>\n");
					out.flush();

					//OutputStream outs = resp.getOutputStream();
					//response.writeTo(outs);//We forward on SOAP Message to caller
				}
			} else {
				log.error("WARNING: Unsuccessful query.\n");
			}
//		} catch (JaxenException e) {
//			log.error(e);
//			handleError(req, resp);
		} catch ( DocumentException e ) {
			log.error(e);
			handleError(req, resp);
//		} catch (NamingException e) {
//			log.error(e);
//			handleError(req, resp);
		} catch (SQLException e) {
			log.error(e);
			handleError(req, resp);
//		} catch ( XDSException e ) {
//			log.error(e);
//			handleError(req, resp);	   		
//		} catch ( SOAPException e ) {
//			log.error(e);
//			handleError(req, resp);
		} catch ( IOException e ) {
			log.error(e);
			handleError(req, resp);
//		} catch (MessagingException e) {
//			log.error(e);
//			handleError(req, resp);
		} catch (MalformedStoredQueryException e) {
			log.error(e);
			handleError(req, resp);
		} finally {
			if ( LOG_PERFORMANCE_DATA ) {
				stopTime = System.currentTimeMillis();
				log.info("TIMER,Query Request Total," + (stopTime-reqStart) + " ms");
			}
		}
	}
	
	private String alphaOnly (String str) {
		return (str.replaceAll("[^a-zA-Z0-9, ]", ""));
	}

	private String birthDateToAge (String bd) {
		if (bd.matches("\\d{8}")) {
			bd = bd.substring(0, 8);
			int bdYr = Integer.parseInt(bd.substring(0, 4));
			int bdMo = Integer.parseInt(bd.substring(4, 6));
			int bdDay = Integer.parseInt(bd.substring(6, 8));
			Calendar today = new GregorianCalendar();
			today.setTime (new Date());
			int ageSecs = (today.get(Calendar.YEAR) - bdYr) * 31557600 +
				(today.get(Calendar.MONTH) - bdMo) * 2629800 +
				(today.get(Calendar.DATE) - bdDay) * 86400;
			if (Math.floor(((ageSecs / 3600) / 24) / 365.25) >= 0) {
				return (Double.toString(Math.floor(((ageSecs / 3600) / 24) / 365.25)));
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


