/*
 * Copyright 2004-2009 (C) University of Washington. All Rights Reserved.
 * Created on Aug 13, 2008
 * University of Washington, CIRG
 */
package edu.washington.cirg.himss.servlet;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintStream;
import java.io.PrintWriter;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;
import java.sql.Connection;
import java.text.FieldPosition;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.TimeZone;

import javax.mail.MessagingException;
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
import org.dom4j.Node;
import org.dom4j.XPath;
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
import org.openhealthtools.ihe.xds.consumer.storedquery.FindDocumentsForMultiplePatientsQuery;
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
import org.openhealthtools.ihe.xds.response.XDSResponseType;
import org.openhealthtools.ihe.xds.response.XDSRetrieveResponseType;
import org.openhealthtools.ihe.xds.response.XDSStatusType;

import edu.washington.cirg.himss.xds.XDSConfigurationException;
import edu.washington.cirg.himss.xds.XDSException;
import edu.washington.cirg.himss.xds.requests.VFMQueryRequest;
import edu.washington.cirg.himss.xds.soap.XDSSoapRequest;
import edu.washington.cirg.himss.xds.util.XDSConstants;

/** This servlet acts as a wrapper around classes that perform a patient demographic
 * query (PDQ) to retrieve a global patient identifier (GID), then generate XDS Registry
 * query transactions.  These requests query an ebXML Registry for documents matching 
 * the GID and return a list of matching documents.
 * @author <a href="mailto:jsibley@u.washington.edu">Jim Sibley</a>
 *
 */
public class VFMFindDocumentsServlet extends HttpServlet {

	private static Log log = LogFactory.getLog(VFMFindDocumentsServlet.class);
	private static boolean LOG_PERFORMANCE_DATA = false; 

	private String DATASOURCE_NAME;
	
	public VFMFindDocumentsServlet() {
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
	
		String queryUrl = req.getParameter(XDSControllerServlet.TARGET_URL_PARAM);
		String vfmUrl = req.getParameter(XDSControllerServlet.VFM_URL_PARAM);
		String vfmCardUrl = req.getParameter(XDSControllerServlet.VFM_CARD_URL_PARAM);
		String atnaUrl = req.getParameter(XDSControllerServlet.ATNA_URL_PARAM);
		String patID = req.getParameter(XDSControllerServlet.PATIENT_ID_PARAM);
		String timeSlot = req.getParameter(XDSControllerServlet.TIME_SLOT_PARAM);
		String qStartTime = req.getParameter(XDSControllerServlet.START_TIME_PARAM);
		String qStopTime = req.getParameter(XDSControllerServlet.STOP_TIME_PARAM);
		String usePH = req.getParameter(XDSControllerServlet.USE_PH_PARAM);
		String storedQueryUuid = XDSConstants.UUID_FIND_DOCUMENTS_STORED_QUERY_CODE;
		String useTls = req.getParameter(XDSControllerServlet.USE_TLS_PARAM);
//		String repoUrl = "http://xds-ibm.lgs.com:9080/IBMXDSRepository/XDSb/SOAP12/Repository";
		String repoUrl = XDSConstants.REPO_URL;
		String yCardsDir = XDSConstants.YCARDS_DIR;
		
		if ( patID == null || "".equals(patID) ) {
			throw new ServletException("patientID param not passed to servlet");
    	}
    	if ( queryUrl == null || "".equals(queryUrl) ) {
    		throw new ServletException("targetUrl param not passed to servlet");
    	}
    	if ( vfmUrl == null || "".equals(vfmUrl) ) {
    		throw new ServletException("vfmUrl param not passed to servlet");
    	}
    	if ( vfmCardUrl == null || "".equals(vfmCardUrl) ) {
    		throw new ServletException("vfmCardUrl param not passed to servlet");
    	}
    	if ( atnaUrl == null || "".equals(atnaUrl) ) {
    		throw new ServletException("atnaUrl param not passed to servlet");
    	}
    	if ( timeSlot == null || "".equals(timeSlot) ) {
    		throw new ServletException("timeSlot param not passed to servlet");
    	}
    	if ( qStartTime == null || "".equals(qStartTime) ) {
    		throw new ServletException("qStartTime param not passed to servlet");
    	}
    	if ( qStopTime == null || "".equals(qStopTime) ) {
    		throw new ServletException("qStopTime param not passed to servlet");
    	}
    	if ( !( usePH == null || "".equals(usePH) || "0".equals(usePH) ) ) {
    		// change the stored query uuid if we want the "public health" version of the query
    		storedQueryUuid = XDSConstants.UUID_PH_FIND_DOCUMENTS_STORED_QUERY_CODE;
    		// nuke the pid to trigger the Public Health version of the Find Documents query
    		patID = "";
    	}
    	if ( !( useTls == null || "".equals(useTls) || "0".equals(useTls) ) ) {
            // set the system security properties
            System.setProperty("javax.net.ssl.keyStore", XDSConstants.TLS_KEYSTORE);
            System.setProperty("javax.net.ssl.keyStorePassword", XDSConstants.TLS_KEYSTORE_PASS);
            System.setProperty("javax.net.ssl.trustStore", XDSConstants.TLS_CERTFILE);
            System.setProperty("javax.net.ssl.trustStorePassword", XDSConstants.TLS_CERTFILE_PASS);
//            repoUrl = "https://xds-ibm.lgs.com:9443/IBMXDSRepository/XDSb/SOAP12/Repository";
            repoUrl = XDSConstants.SECURE_REPO_URL;
    	}
    	
    	Connection conn = null;
//		Random rand = new Random();
//    	List chiefComplaints = new ArrayList();
//		File file = new File("/opt/tomcat/webapps/xds/WEB-INF/classes/chief_complaint_pool.txt");
//		BufferedReader bufRdr  = new BufferedReader(new FileReader(file));
//		String line = null;
//
//		/* read each line of text file */
//		while((line = bufRdr.readLine()) != null) {
//			chiefComplaints.add(line);
//		}
//		 
//		/* close the file */
//		bufRdr.close(); 
		
		// Translate pid if ListDocs is desired
//		if ( !( listDocs == null || listDocs.equals("") || "0".equals(listDocs) ) ) {
//			File file2 = new File("/home/CIRG/jsibley/himss2008/allscripts_code_xref.txt");
//			BufferedReader bufRdr2 = new BufferedReader(new FileReader(file2));
//			String line2 = null;
//
//			/* read each line of text file */
//			while((line2 = bufRdr2.readLine()) != null) {
//				if (line2.indexOf(":" + patientId + ":") != -1) {
//					patientId = line2.substring(0, line2.indexOf(":"));
//					break;
//				}
//			}
//			 
//			/* close the file */
//			bufRdr2.close();
//		}

		try {
			if ( LOG_PERFORMANCE_DATA ) { //Start GenerateRequest
				startTime=System.currentTimeMillis();
			}
//			
//			FindDocumentsStoredQueryRequest xdsreq =
//				new FindDocumentsStoredQueryRequest(patID, timeSlot, qStartTime, qStopTime, AdhocQueryRequest.LEAF_CLASS, storedQueryUuid);
//			Document reqDocument = xdsreq.generateRequest();
//	    	if ( LOG_PERFORMANCE_DATA) { //End GenerateRequest
//				stopTime = System.currentTimeMillis();
//				log.info("TIMER,Generate XDS Query Request," + (stopTime-startTime) + " ms");
//				startTime=System.currentTimeMillis();
//	    	}//Start GenerateSOAPRequest
//	    	
//	    	//XDSUtil.printDOM(System.err, reqDocument);
//	    	
//			XDSSoapRequest soapReq = 
//				new XDSSoapRequest(reqDocument, queryUrl, "1.1");
//
//			if ( LOG_PERFORMANCE_DATA) { //End GenerateSOAPRequest
//				stopTime = System.currentTimeMillis();
//				log.info("TIMER,Generate Query SOAP Request," + (stopTime-startTime) + " ms");
//				startTime=System.currentTimeMillis();
//	    	}//Start SendSOAPRequest
//
//			
//			SOAPMessage response = soapReq.sendMessage();
//	    	
//			if ( LOG_PERFORMANCE_DATA) {//End SendSOAPRequest
//				stopTime = System.currentTimeMillis();
//				log.info("TIMER,Send Query SOAP Request," + (stopTime-startTime) + " ms");
//			}
			
//			if ( log.isDebugEnabled() ) {
//				response.writeTo(System.out);
//			}
			
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
			auditor.auditRegistryStoredQueryEvent(RFC3881EventOutcomeCodes.SUCCESS, queryUrl, storedQueryUuid, "N/A", "N/A", patID);
			auditor.auditActorStartEvent(RFC3881EventOutcomeCodes.SUCCESS, "UW/SAIC", "ihe2009");
			
			StoredQuery q = null;
			
			if (! "".equals(patID)) {
				CX queryPatientId = Hl7v2Factory.eINSTANCE.createCX();
				queryPatientId.setIdNumber(patID.substring(0, patID.indexOf("^")));
				queryPatientId.setAssigningAuthorityName("IHENA");
//				queryPatientId.setAssigningAuthorityUniversalId("1.3.6.1.4.1.21367.2007.2.1");
//				queryPatientId.setAssigningAuthorityUniversalId("1.3.6.1.4.1.21367.2005.3.7.9");
				queryPatientId.setAssigningAuthorityUniversalId("1.3.6.1.4.1.21367.2010.1.2.300");
//				queryPatientId.setAssigningAuthorityUniversalId(patID.substring(patID.lastIndexOf("^") + 1));
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
				event1.setCode("MPQ-eventcode-1");
				event1.setSchemeName("MPQ Testing");
				eventCodes = new CodedMetadataType[]{event1}; 

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
			    FileOutputStream fos = new FileOutputStream("/tmp/vfmSoapRequest.txt");
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

			auditor.auditActorStopEvent(RFC3881EventOutcomeCodes.SUCCESS, "UW/SAIC", "ihe2009");

			if ( LOG_PERFORMANCE_DATA) {//End SendSOAPRequest
				stopTime = System.currentTimeMillis();
				Calendar cal = Calendar.getInstance();
			    SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
				log.info("TIMER," + sdf.format(cal.getTime()).toString() + ",Send Query SOAP Request," + (stopTime-startTime) + " ms");
				startTime = System.currentTimeMillis();
			}

			if (response.getStatus().equals(XDSStatusType.SUCCESS_LITERAL)) {

				try {
					FileOutputStream fos = new FileOutputStream("/tmp/vfmSoapResponse.txt");
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
							p.println("Author='" + (doc.getAuthors() != null && ((AuthorType)doc.getAuthors().get(j)).getAuthorInstitution() != null && ((XON)((AuthorType)doc.getAuthors().get(j)).getAuthorInstitution()).getOrganizationName() != null ? ((XON)((AuthorType)doc.getAuthors().get(j)).getAuthorInstitution()).getOrganizationName() : "NULL") + "'");
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
//				if ( !( listDocs == null || listDocs.equals("") || "0".equals(listDocs) ) ) {
//					/* Output something to the user */
//					resp.setContentType("text/html");
//					PrintWriter out = resp.getWriter();
//					out.print("<HTML>\n<BODY>\n");

//					if ( ! rr.wasSuccessful() ) {
//						out.print("<H2>Application Error</H2>\n");
//						String error;
//						for ( Iterator iter = rr.getErrorList().iterator(); iter.hasNext();) {
//							error = (String)iter.next();
//							log.error(error);
//							out.print("<H4>" + error + "</H4>\n");
//						}

			/* Parse SOAP response into a set of lists */
//			FindDocumentsResponse rr = new FindDocumentsResponse(response);
//			if ( LOG_PERFORMANCE_DATA ) {
//				stopTime = System.currentTimeMillis();
//				log.info("TIMER,Parse FindDocuments SOAP Response Message," + (stopTime-startTime) + " ms");				
//				startTime = System.currentTimeMillis();
//			}

				/* Start output to user */
				resp.setContentType("text/html");
				PrintWriter out = resp.getWriter();
				out.print("<HTML>\n<BODY>\n");

//				if ( ! rr.wasSuccessful() ) {
//					out.print("<H2>Application Error</H2>\n");
//					String error;
//					for ( Iterator iter = rr.getErrorList().iterator(); iter.hasNext();) {
//						error = (String)iter.next();
//						log.error(error);
//						out.print("<H4>" + error + "</H4>\n");
//					}
//				} else {
					//				SimpleDateFormat myformat = new SimpleDateFormat("d MMM yyyy HH:mm:ss");
					//				//out.print("<H2>" + myformat.format(new Date()) + ": " + rr.getPatientIdList().size() + " documents found for patientId '" + patID + "'.</H2>\n");
					//				out.print("<table border=\"1\">\n");
					//				out.print("<tr><th width=\"100\">Date Saved</th><th width=\"100\">Type Code</th><th width=\"100\">Link</th><th width=\"100\">Format Code</th><th width=\"100\">Language Code</th><th width=\"100\">Mime Type</th><th>Unique ID</th></tr>");
					//				for ( int i = 0; i <= rr.getPatientIdList().size() - 1; i++ ) {
					//					out.print("<tr><td align=\"center\" width=\"100\">" + rr.getCreationTimeList().get(i).toString() + "</td>\n");
					//					out.print("<td align=\"center\" width=\"200\">" + rr.getTypeCodeList().get(i).toString() + "</td>\n");
					////					if (rr.getDocumentUriList().get(i).toString().indexOf("https:") == -1) {
					//					out.print("<td align=\"center\" width=\"100\"><a href=\"" + rr.getDocumentUriList().get(i).toString() + "\">Document Link</a></td>\n");
					////					} else {
					////					out.print("<td align=\"center\" width=\"100\">&nbsp;</td>\n");
					////					}
					//					out.print("<td align=\"center\" width=\"200\">" + rr.getFormatCodeList().get(i).toString() + "</td>\n");
					//					out.print("<td align=\"center\" width=\"100\">" + rr.getLanguageCodeList().get(i).toString() + "</td>\n");
					//					out.print("<td align=\"center\" width=\"100\">" + rr.getMimeTypeList().get(i).toString() + "</td>\n");
					////					out.print("<td>" + rr.getTypeCodeList().get(i).toString() + "</td>\n");
					////					out.print("<H4>Patient ID (" + rr.getPatientIdList().size() + "):<br> " + rr.getPatientIdList().toString() + "</H4>\n");
					////					out.print("<H4>Source Patient Info (" + rr.getSourcePatientInfoList().size() + "):<br> " + rr.getSourcePatientInfoList().toString() + "</H4>\n");
					//					out.print("<td align=\"center\" width=\"200\">" + rr.getUniqueIdList().get(i).toString() + "</td></tr>\n");
					//				}
					//				out.print("</table>\n");

					/* Nab all Immunization docs, parse them and build a query for the VFM */
					String patientID = "";
					String fName = "";
					String lName = "";
					String patName = "";
//					String oid = "1.3.6.1.4.1.21367.2008.1.2.190";
					String oid = "1.3.6.1.4.1.21367.2010.1.2.300";
					String sex = "";
					String dob = "";
					List immList = new ArrayList();
					List datesList = new ArrayList();
					List codesList = new ArrayList();
					List namesList = new ArrayList();
					List manufExtList = new ArrayList();
					List manufNameList = new ArrayList();
					//				datesList.add("20070705152819");
					//				datesList.add("20070705152819");
					//				codesList.add("02");
					//				codesList.add("20");
					//				namesList.add("OPV");
					//				namesList.add("DTap");

					for ( int i = 0; i <= response.getDocumentEntryResponses().size() - 1; i++ ) {
						DocumentEntryType doc = ((DocumentEntryResponseType)response.getDocumentEntryResponses().get(i)).getDocumentEntry();
						if ("urn:ihe:pcc:irc:2008".equals((doc.getFormatCode() != null && doc.getFormatCode().getCode() != null ? doc.getFormatCode().getCode() : "NULL")) ||
							"urn:ihe:pcc:ic:2007".equals((doc.getFormatCode() != null && doc.getFormatCode().getCode() != null ? doc.getFormatCode().getCode() : "NULL")) ||
							"1.3.6.1.4.1.19376.1.5.3.1.1.18.1.2".equals((doc.getFormatCode() != null && doc.getFormatCode().getCode() != null ? doc.getFormatCode().getCode() : "NULL")) ||
							"1.3.6.1.4.1.19376.1.5.3.1.1.3".equals((doc.getFormatCode() != null && doc.getFormatCode().getCode() != null ? doc.getFormatCode().getCode() : "NULL"))) {
//							String uri = doc.getUri();
							try {
//								if ( uri.toLowerCase().startsWith("https://") &&
//										(useTls == null || useTls.equals(""))) {
									// set the system security properties
//									System.setProperty("javax.net.ssl.keyStore", XDSConstants.TLS_KEYSTORE);
//									System.setProperty("javax.net.ssl.keyStorePassword", XDSConstants.TLS_KEYSTORE_PASS);
//									System.setProperty("javax.net.ssl.trustStore", XDSConstants.TLS_CERTFILE);
//									System.setProperty("javax.net.ssl.trustStorePassword", XDSConstants.TLS_CERTFILE_PASS);
									//					            System.setProperty("javax.net.ssl.serverAuth", "false");
									//					            System.setProperty("javax.net.ssl.sslProtocol", "TLS");
									//								String hostPort = uri.substring(8, uri.indexOf("/", 8));
									//								uri = uri.replace(hostPort, nonTlsHostPort(hostPort));
									//								uri = uri.replace("https://", "http://");
									//								return;
//								}

//								if ( uri.indexOf("10.242.0.54") != -1 ) {
//									uri = uri.replace("10.242.0.54", "dejarnette1.ihe.net");
//								}

								// Create a URL for the desired page
//								URL url = new URL(uri);
								
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
//						       	log.info("Going for uniqueID='" + doc.getRepositoryUniqueId() + "', repoURI='" + repositoryURI + "'");
						       	retrDocSetReq.getDocumentRequest().add(docReq);
//						       	List<org.openhealthtools.ihe.xds.document.XDSDocument> documents = new ArrayList<org.openhealthtools.ihe.xds.document.XDSDocument>();

								auditor.auditRetrieveDocumentSetEvent(RFC3881EventOutcomeCodes.SUCCESS, "mllp://xds-ibm.lgs.com:514", new String[] {doc.getUniqueId()}, doc.getRepositoryUniqueId(), "N/A", doc.getPatientId().getIdNumber());
								auditor.auditActorStartEvent(RFC3881EventOutcomeCodes.SUCCESS, "UW/SAIC", "ihe2009");

								if ( LOG_PERFORMANCE_DATA ) {
									stopTime = System.currentTimeMillis();
									log.info("TIMER,Generate Retrieve Document Set Query [Doc #" + (i + 1) + "]," + (stopTime-startTime) + " ms");				
									startTime = System.currentTimeMillis();
								}

//								log.info("INFO,Trying to retrieve imm doc for pid:" + doc.getPatientId().getIdNumber());
								String xml = "";
						       	try {
						       		XDSRetrieveResponseType docResp = c.retrieveDocumentSet(false, retrDocSetReq, docQueryPatId);			
//								    log.info("INFO,Retrieve Document Request Status='" + (docResp.getStatus() != null ? docResp.getStatus() : "NULL") + "'");
//								    if (docResp.getStatus() != null && "Success".equals(docResp.getStatus())) {
//								    	log.info("INFO,Retrieved Document[0]='" + (documents != null && documents.get(0) != null ? documents.get(0).toString() : "NULL") + "'");

										auditor.auditActorStopEvent(RFC3881EventOutcomeCodes.SUCCESS, "UW/SAIC", "ihe2009");

										BufferedReader in = new BufferedReader(new InputStreamReader(docResp.getAttachments().get(0).getStream()));
										String str = "";
										while ((str = in.readLine()) != null) {
											xml = xml.concat(str);
										}
										in.close();
//								    }
									//log.info("INFO,Retrieved Doc='" + xml + "'");
						       	} catch (Exception e) {
						       		log.error("ERROR,Problem Retrieving Document Set: " + e);
						       		handleError(req, resp);
						       	}

								if ( LOG_PERFORMANCE_DATA ) {
									stopTime = System.currentTimeMillis();
									log.info("TIMER,Send Retrieve Document Set Query [Doc #" + i + "]," + (stopTime-startTime) + " ms");				
									startTime = System.currentTimeMillis();
								}

								// Read all the text returned by the server
//								BufferedReader in = new BufferedReader(new InputStreamReader(url.openStream()));
//								String str = "";
//								String xml = "";
//								while ((str = in.readLine()) != null) {
//									xml = xml.concat(str);
//								}
//								in.close();

								xml = xml.replaceAll("cda:", "");
								xml = xml.replaceAll("hl7:", "");
								//log.info(xml);
								Document immDoc = DocumentHelper.parseText(xml);

								XPath xpathSelector = DocumentHelper.createXPath(
								"//*[name() = 'ClinicalDocument']/*[name() = 'recordTarget']/*[name() = 'patientRole']/*[name() = 'id']/@extension");
								Attribute attr = (Attribute) xpathSelector.selectSingleNode(immDoc);
								if ( attr != null && !(attr.getData().toString().equals("") || attr.getData().toString() == null) ) {
									patientID = attr.getData().toString();
									try {
										byte byteXml[] = xml.getBytes();
										FileOutputStream fos = new FileOutputStream("/tmp/icParsed_patientid_" + alphaOnly(patientID.indexOf("&") != -1 ? patientID.substring(0, patientID.indexOf("&")) : patientID) + "_" + doc.getUniqueId() + ".xml");
										fos.write(byteXml);
										fos.close();
									} catch (FileNotFoundException e) {
										System.err.println("WARN,File Not Found Exception: " + e);
									} catch (IOException e) {
										System.err.println("WARN,IO Exception: " + e);
									}
								}

								xpathSelector = DocumentHelper.createXPath(
								"//*[name() = 'ClinicalDocument']/*[name() = 'recordTarget']/*[name() = 'patientRole']/*[name() = 'patient']/*[name() = 'name']/*[name() = 'given']");
								Node node = (Node) xpathSelector.selectSingleNode(immDoc);
								if ( node != null && !(node.getText().equals("") || node.getText() == null) ) {
									fName = node.getText();
								}

								xpathSelector = DocumentHelper.createXPath(
								"//*[name() = 'ClinicalDocument']/*[name() = 'recordTarget']/*[name() = 'patientRole']/*[name() = 'patient']/*[name() = 'name']/*[name() = 'family']");
								node = (Node) xpathSelector.selectSingleNode(immDoc);
								if ( node != null && !(node.getText().equals("") || node.getText() == null) ) {
									lName = node.getText();
								}

								patName = lName + "^" + fName;

								xpathSelector = DocumentHelper.createXPath(
								"//*[name() = 'ClinicalDocument']/*[name() = 'recordTarget']/*[name() = 'patientRole']/*[name() = 'patient']/*[name() = 'administrativeGenderCode']/@code");
								attr = (Attribute) xpathSelector.selectSingleNode(immDoc);
								if ( attr != null && !(attr.getData().toString().equals("") || attr.getData().toString() == null) ) {
									sex = attr.getData().toString();
								}

								xpathSelector = DocumentHelper.createXPath(
								"//*[name() = 'ClinicalDocument']/*[name() = 'recordTarget']/*[name() = 'patientRole']/*[name() = 'patient']/*[name() = 'birthTime']/@value");
								attr = (Attribute) xpathSelector.selectSingleNode(immDoc);
								if ( attr != null && !(attr.getData().toString().equals("") || attr.getData().toString() == null) ) {
									dob = attr.getData().toString();
								}

								xpathSelector = DocumentHelper.createXPath(
								"//*[name() = 'ClinicalDocument']/*[name() = 'component']/*[name() = 'structuredBody']/*[name() = 'component']/*[name() = 'section']/*[name() = 'entry']/*[name() = 'substanceAdministration']");
								immList.addAll(xpathSelector.selectNodes(immDoc));

							} catch (MalformedURLException e) {
								log.info("WARN,Malformed URL Exception Parsing IC Document: " + e.toString());
							} catch (IOException e) {
								log.info("WARN,IO Exception Parsing IC Document: " + e.toString());
							}
						}
						if ( LOG_PERFORMANCE_DATA ) {
							stopTime = System.currentTimeMillis();
							log.info("TIMER,Parsed Retrieved Document Set [Doc #" + i + "]," + (stopTime-startTime) + " ms");				
							startTime = System.currentTimeMillis();
						}
					}
					log.info("Total imms found = " + immList.size());

					if (immList.size() < 1) {
						out.print("<H2>No immunization records found for this patient.</H2>\n");
						out.flush();
						throw new ServletException("No immunization records found.");
					}

					/* For yellow card later */
					String vux = "MSH|^~\\&|||||||VXU^V04|19970522MA53|P|2.3.1\rPID|||" + alphaOnly(patientID.indexOf("&") != -1 ? patientID.substring(0, patientID.indexOf("&")) : patientID) + "^^^^SS||" + patName + "|BOUVIER^^^^^^M|" + dob + "|" + sex + "|||^^^^MA^^^BLD\r";

					/* Pull out dates, codes and names for all immunization records found */
					for (int i = 0; i < immList.size(); i++) {
						String tmpDate = "";
						String tmpCode = "";
						String tmpName = "";
						String tmpManufExt = "";
						String tmpManufName = "";
						//log.info("This is: " + ((Node) immList.get(i)).asXML());
						Document tempDoc = DocumentHelper.parseText(((Node) immList.get(i)).asXML());
						XPath xpathSelector = DocumentHelper.createXPath(
						"//*[name() = 'substanceAdministration']/*[name() = 'effectiveTime']/@value");
						Attribute attr = (Attribute) xpathSelector.selectSingleNode(tempDoc);
						if ( attr != null && !(attr.getData().toString().equals("") || attr.getData().toString() == null) ) {
//							datesList.add(attr.getData().toString());
							tmpDate = attr.getData().toString();
						}

						xpathSelector = DocumentHelper.createXPath(
						"//*[name() = 'substanceAdministration']/*[name() = 'consumable']/*[name() = 'manufacturedProduct']/*[name() = 'manufacturedLabeledDrug']/*[name() = 'code']/@code");
						attr = (Attribute) xpathSelector.selectSingleNode(tempDoc);
						if ( attr != null && !(attr.getData().toString().equals("") || attr.getData().toString() == null) ) {
//							codesList.add(attr.getData().toString());
							tmpCode = attr.getData().toString();
						} else {
							xpathSelector = DocumentHelper.createXPath(
							"//*[name() = 'substanceAdministration']/*[name() = 'consumable']/*[name() = 'manufacturedProduct']/*[name() = 'manufacturedMaterial']/*[name() = 'code']/@code");
							attr = (Attribute) xpathSelector.selectSingleNode(tempDoc);
							if ( attr != null && !(attr.getData().toString().equals("") || attr.getData().toString() == null) ) {
//								codesList.add(attr.getData().toString());
								tmpCode = attr.getData().toString();
							}						
						}

						xpathSelector = DocumentHelper.createXPath(
						"//*[name() = 'substanceAdministration']/*[name() = 'consumable']/*[name() = 'manufacturedProduct']/*[name() = 'manufacturedLabeledDrug']/*[name() = 'code']/@displayName");
						attr = (Attribute) xpathSelector.selectSingleNode(tempDoc);
						if ( attr != null && !(attr.getData().toString().equals("") || attr.getData().toString() == null) ) {
//							namesList.add(attr.getData().toString());
							tmpName = attr.getData().toString();
						} else {
							xpathSelector = DocumentHelper.createXPath(
							"//*[name() = 'substanceAdministration']/*[name() = 'consumable']/*[name() = 'manufacturedProduct']/*[name() = 'manufacturedMaterial']/*[name() = 'code']/@displayName");
							attr = (Attribute) xpathSelector.selectSingleNode(tempDoc);
							if ( attr != null && !(attr.getData().toString().equals("") || attr.getData().toString() == null) ) {
//								namesList.add(attr.getData().toString());
								tmpName = attr.getData().toString();
							}						
						}

						xpathSelector = DocumentHelper.createXPath(
						"//*[name() = 'substanceAdministration']/*[name() = 'consumable']/*[name() = 'manufacturedProduct']/*[name() = 'manufacturedLabeledDrug']/*[name() = 'asMedicineManufacturer']/*[name() = 'manufacturer']/*[name() = 'id']/@extension");
						attr = (Attribute) xpathSelector.selectSingleNode(tempDoc);
						if ( attr != null && !(attr.getData().toString().equals("") || attr.getData().toString() == null) ) {
//							manufExtList.add(attr.getData().toString());
							tmpManufExt = attr.getData().toString();
						} else {
//							manufExtList.add("");
							tmpManufExt = "";
						}

						xpathSelector = DocumentHelper.createXPath(
						"//*[name() = 'substanceAdministration']/*[name() = 'consumable']/*[name() = 'manufacturedProduct']/*[name() = 'manufacturedLabeledDrug']/*[name() = 'asMedicineManufacturer']/*[name() = 'manufacturer']/*[name() = 'name']");
						Node node = (Node) xpathSelector.selectSingleNode(tempDoc);
						if ( node != null && !(node.getText().equals("") || node.getText() == null) ) {
//							manufNameList.add(node.getText());
							tmpManufName = node.getText();
						} else {
//							manufNameList.add("");
							tmpManufName = "";
						}
						
						if (!"".equals(tmpDate) && !"".equals(tmpCode) && !"".equals(tmpName)) {
							datesList.add(tmpDate);
							codesList.add(tmpCode);
							namesList.add(tmpName);
							manufExtList.add(tmpManufExt);
							manufNameList.add(tmpManufName);
						}
					}

					log.info("INFO,datesList=" + datesList.size() + ", codesList=" + codesList.size() + ", namesList=" + namesList.size() + ", manufExtList=" + manufExtList.size() + ", manufNameList=" + manufNameList.size());
					/* Do VFM query */
					VFMQueryRequest vfmreq =
						new VFMQueryRequest(patientID, oid, sex, dob, datesList, codesList, namesList, manufExtList, manufNameList);
					Document vfmDocument = vfmreq.generateRequest();
					if ( LOG_PERFORMANCE_DATA) { //End GenerateRequest
						stopTime = System.currentTimeMillis();
						log.info("TIMER,Generate VFM Query Request," + (stopTime-startTime) + " ms");
						startTime=System.currentTimeMillis();
					}//Start GenerateSOAPRequest

					//XDSUtil.printDOM(System.err, reqDocument);

					XDSSoapRequest vfmSoapReq = 
						new XDSSoapRequest(vfmDocument, vfmUrl, "1.2");

					if ( LOG_PERFORMANCE_DATA) { //End GenerateSOAPRequest
						stopTime = System.currentTimeMillis();
						log.info("TIMER,Generate VFM Query SOAP Request," + (stopTime-startTime) + " ms");
						startTime=System.currentTimeMillis();
					}//Start SendSOAPRequest


					SOAPMessage vfmResponse = vfmSoapReq.sendVFMMessage();

					if ( LOG_PERFORMANCE_DATA) {//End SendSOAPRequest
						stopTime = System.currentTimeMillis();
						log.info("TIMER,Sent VFM Query SOAP Request," + (stopTime-startTime) + " ms");
						startTime = System.currentTimeMillis();
					}

					/* Break VFM response into historical and forecast lists */
					ByteArrayOutputStream xml = new ByteArrayOutputStream();
					vfmResponse.writeTo(xml);
					Document immDoc = DocumentHelper.parseText(xml.toString().substring(xml.toString().indexOf("<S:Body>") + 8, xml.toString().indexOf("</S:Body>")));

					List vfmImmList = new ArrayList();
					List histDates = new ArrayList();
					List histCodes = new ArrayList();
					List histNames = new ArrayList();
					List histDoses = new ArrayList();
					List histTexts = new ArrayList();
					List foreDates = new ArrayList();
					List foreCodes = new ArrayList();
					List foreNames = new ArrayList();
					List foreDoses = new ArrayList();
					List foreTexts = new ArrayList();
					String date = "";
					String code = "";
					String name = "";
					String dose = "";
					String type = "";
					String text = "";

					XPath xpathSelector = DocumentHelper.createXPath(
					"//*[name() = 'REPC_IN004014UV']/*[name() = 'controlActProcess']/*[name() = 'subject']/*[name() = 'registrationEvent']/*[name() = 'subject2']/*[name() = 'careProvisionEvent']/*[name() = 'pertinentInformation3']/*[name() = 'substanceAdministration']");
					vfmImmList.addAll(xpathSelector.selectNodes(immDoc));

					for (int i = 0; i < vfmImmList.size(); i++) {
						//log.info("This is: " + ((Node) vfmImmList.get(i)).asXML());
						Document tempDoc = DocumentHelper.parseText(((Node) vfmImmList.get(i)).asXML());
						xpathSelector = DocumentHelper.createXPath(
						"//*[name() = 'substanceAdministration']/*[name() = 'effectiveTime']/@value");
						Attribute attr = (Attribute) xpathSelector.selectSingleNode(tempDoc);
						if ( attr != null && !(attr.getData().toString().equals("") || attr.getData().toString() == null) ) {
							date = attr.getData().toString();
						}

						xpathSelector = DocumentHelper.createXPath(
						"//*[name() = 'substanceAdministration']/*[name() = 'consumable']/*[name() = 'administerableMaterial']/*[name() = 'administerableMaterial']/*[name() = 'code']/@code");
						attr = (Attribute) xpathSelector.selectSingleNode(tempDoc);
						if ( attr != null && !(attr.getData().toString().equals("") || attr.getData().toString() == null) ) {
							code = attr.getData().toString();
						}

						xpathSelector = DocumentHelper.createXPath(
						"//*[name() = 'substanceAdministration']/*[name() = 'consumable']/*[name() = 'administerableMaterial']/*[name() = 'administerableMaterial']/*[name() = 'code']/@displayName");
						attr = (Attribute) xpathSelector.selectSingleNode(tempDoc);
						if ( attr != null && !(attr.getData().toString().equals("") || attr.getData().toString() == null) ) {
							name = attr.getData().toString();
						}

						xpathSelector = DocumentHelper.createXPath(
						"//*[name() = 'substanceAdministration']/*[name() = 'entryRelationship']/*[name() = 'observation']/*[name() = 'value']/@value");
						attr = (Attribute) xpathSelector.selectSingleNode(tempDoc);
						if ( attr != null && !(attr.getData().toString().equals("") || attr.getData().toString() == null) ) {
							dose = attr.getData().toString();
						}

						xpathSelector = DocumentHelper.createXPath(
						"//*[name() = 'substanceAdministration']/*[name() = 'statusCode']/@code");
						attr = (Attribute) xpathSelector.selectSingleNode(tempDoc);
						if ( attr != null && !(attr.getData().toString().equals("") || attr.getData().toString() == null) ) {
							type = attr.getData().toString();
						}

						xpathSelector = DocumentHelper.createXPath(
						"//*[name() = 'substanceAdministration']/*[name() = 'text']");
						Node node = (Node) xpathSelector.selectSingleNode(tempDoc);
						if ( node != null && !(node.getText().equals("") || node.getText() == null) ) {
							text = node.getText();
						}

						//log.info("Got date: " + date + ", code: " + code + ", name: " + name + ", dose: " + dose + ", type: " + type);

						int pos = 0;
						if ("completed".equals(type)) {
							if (histDates.size() < 1) {
								//log.info("Adding as initial hist entry - date: " + date + ", code: " + code + ", name: " + name + ", dose: " + dose);
								histDates.add(date);
								histCodes.add(code);
								histNames.add(name);
								histDoses.add(dose);
								histTexts.add(text);
							} else {
								for (int j = 0; j < histDates.size(); j++) {
									if (histDates.get(j).toString().compareTo(date) < 0) {
										pos = j + 1;
										continue;
									} else {
										pos = j;
										break;
									}
								}
								//log.info("Adding hist at position " + pos + " - date: " + date + ", code: " + code + ", name: " + name + ", dose: " + dose);
								histDates.add(pos, date);
								histCodes.add(pos, code);
								histNames.add(pos, name);
								histDoses.add(pos, dose);
								histTexts.add(pos, text);
							}
						} else if ("forecast".equals(type)) {
							if (foreDates.size() < 1) {
								//log.info("Adding as initial fore entry - date: " + date + ", code: " + code + ", name: " + name + ", dose: " + dose);
								foreDates.add(date);
								foreCodes.add(code);
								foreNames.add(name);
								foreDoses.add(dose);
								foreTexts.add(text);
							} else {
								for (int j = 0; j < foreDates.size(); j++) {
									if (foreDates.get(j).toString().compareTo(date) < 0) {
										pos = j + 1;
										continue;
									} else {
										pos = j;
										break;
									}
								}
								//log.info("Adding fore at position " + pos + " - date: " + date + ", code: " + code + ", name: " + name + ", dose: " + dose);
								foreDates.add(pos, date);
								foreCodes.add(pos, code);
								foreNames.add(pos, name);
								foreDoses.add(pos, dose);
								foreTexts.add(pos, text);
							}
						}
					}

					if ( LOG_PERFORMANCE_DATA ) {
						stopTime = System.currentTimeMillis();
						log.info("TIMER,Parsed VFM Response," + (stopTime-startTime) + " ms");				
						startTime = System.currentTimeMillis();
					}

					/* Output dose history */
					out.print("<H4>Patient Date of Birth: " + dob.substring(0, 4) + "-" + dob.substring(4, 6) + "-" + dob.substring(6, 8) + "&nbsp;&nbsp;&nbsp;&nbsp;Gender: " + ("M".equals(sex) ? "Male" : "F".equals(sex) ? "Female" : "Unknown") + "</H4>");
					out.print("<H3><U>Immunization History</U></H3>\n");
					out.print("<TABLE style=\"border: 1px solid #0000FF\" WIDTH=\"70%\">\n<TR>\n<TH style=\"border: 1px solid #0000FF\">Vaccine (CVX)</TH>\n<TH style=\"border: 1px solid #0000FF\">Date</TH>\n<TH style=\"border: 1px solid #0000FF\">Dose #</TH>\n</TR>\n");

					//				for (int i = 0; i < immList.size(); i++) {
					//					log.info("This is: " + ((Node) immList.get(i)).asXML());
					//					Document tempDoc = DocumentHelper.parseText(((Node) immList.get(i)).asXML());
					//					xpathSelector = DocumentHelper.createXPath(
					//					"//*[name() = 'substanceAdministration']/*[name() = 'effectiveTime']/@value");
					//					Attribute attr = (Attribute) xpathSelector.selectSingleNode(tempDoc);
					//					if ( attr != null && !(attr.getData().toString().equals("") || attr.getData().toString() == null) ) {
					//						date = attr.getData().toString();
					//					}
					//					
					//					xpathSelector = DocumentHelper.createXPath(
					//					"//*[name() = 'substanceAdministration']/*[name() = 'consumable']/*[name() = 'manufacturedProduct']/*[name() = 'manufacturedLabeledDrug']/*[name() = 'code']/@code");
					//					attr = (Attribute) xpathSelector.selectSingleNode(tempDoc);
					//					if ( attr != null && !(attr.getData().toString().equals("") || attr.getData().toString() == null) ) {
					//						code = attr.getData().toString();
					//					}
					//
					//					xpathSelector = DocumentHelper.createXPath(
					//					"//*[name() = 'substanceAdministration']/*[name() = 'consumable']/*[name() = 'manufacturedProduct']/*[name() = 'manufacturedLabeledDrug']/*[name() = 'code']/@displayName");
					//					attr = (Attribute) xpathSelector.selectSingleNode(tempDoc);
					//					if ( attr != null && !(attr.getData().toString().equals("") || attr.getData().toString() == null) ) {
					//						name = attr.getData().toString();
					//					}
					//					
					//					log.info("Got date: " + date + ", code: " + code + ", name: " + name);
					//					
					//					List tempCodes = (List) ((ArrayList) histCodes).clone();
					//					List tempDates = (List) ((ArrayList) histDates).clone();
					//					int codeInd = tempCodes.indexOf(code);
					//					Boolean dupe = Boolean.FALSE;
					//					while (codeInd != -1) {
					//						if (date.equals(tempDates.get(codeInd).toString())) {
					//							log.info("Skipping duplicate");
					//							dupe = Boolean.TRUE;
					//							break;
					//						}
					//						tempCodes.remove(code);
					//						tempDates.remove(codeInd);
					//						codeInd = tempCodes.indexOf(code);
					//					}
					//					
					//					if (dupe) {
					//						continue;
					//					}
					//					
					//					int pos = 0;
					//					if (histDates.size() < 1) {
					//						log.info("Adding as initial entry - date: " + date + ", code: " + code + ", name: " + name);
					//						histDates.add(date);
					//						histCodes.add(code);
					//						histNames.add(name);
					//					} else {
					//						for (int j = 0; j < histDates.size(); j++) {
					//							if (histDates.get(j).toString().compareTo(date) < 0) {
					//								pos = j + 1;
					//								continue;
					//							} else {
					//								pos = j;
					//								break;
					//							}
					//						}
					//						log.info("Adding in position " + pos + " - date: " + date + ", code: " + code + ", name: " + name);
					//						histDates.add(pos, date);
					//						histCodes.add(pos, code);
					//						histNames.add(pos, name);				
					//					}
					//				}

					List combos = new ArrayList();
					for (int i = 0; i < histDates.size(); i++) {
						out.print("<TR>\n<TD style=\"border: 1px solid #0000FF\">" + histTexts.get(i).toString() + "/" + histNames.get(i).toString() + " (" + histCodes.get(i).toString() + ")</TD>\n<TD style=\"border: 1px solid #0000FF\">" + histDates.get(i).toString().substring(0, 4) + "-" + histDates.get(i).toString().substring(4, 6) + "-" + histDates.get(i).toString().substring(6, 8) + "</TD>\n<TD style=\"border: 1px solid #0000FF\">" + ("-1".equals(histDoses.get(i).toString()) ? "<font color=\"red\">Invalid</font>" : "-2".equals(histDoses.get(i).toString()) ? "Duplicate" : "-99".equals(histDoses.get(i).toString()) ? "Booster" : histDoses.get(i).toString()) + "</TD>\n</TR>\n");
						if (! ("-1".equals(histDoses.get(i).toString()) || "-2".equals(histDoses.get(i).toString()))) {
							Boolean flag = Boolean.FALSE;
							for (int j = 0; j < combos.size(); j++) {
								if (combos.get(j).toString().equals(histCodes.get(i).toString())) {
									flag = Boolean.TRUE;
								}
							}
							if (!flag) {
								vux += "RXA|0|" + histDoses.get(i).toString() + "|" + histDates.get(i).toString() + "|" + histDates.get(i).toString() + "|" + histCodes.get(i).toString() + "^" + histNames.get(i).toString() + "^CVX\r"; //|999|||||||||MRK12345||MSD^MERCK^MVX\r";
							}
						}
						if ("104".equals(histCodes.get(i).toString()) || "110".equals(histCodes.get(i).toString()) || "120".equals(histCodes.get(i).toString())) {
							combos.add(histCodes.get(i).toString());
						}
					}
					out.print("</TABLE>\n");

					/* Output vaccine forecast */
					out.print("<H3><U>Immunization Recommendations</U></H3>\n");
					out.print("<TABLE style=\"border: 1px solid #0000FF\" WIDTH=\"70%\">\n");

					//				List substances = new ArrayList();
					//				
					//				xpathSelector = DocumentHelper.createXPath(
					//				"//*[name() = 'REPC_IN004014UV']/*[name() = 'controlActProcess']/*[name() = 'subject']/*[name() = 'registrationEvent']/*[name() = 'subject2']/*[name() = 'careProvisionEvent']/*[name() = 'pertinentInformation3']/*[name() = 'substanceAdministration']");
					//				substances.addAll(xpathSelector.selectNodes(immDoc));
					//				
					//				date = "";
					//				code = "";
					//				name = "";
					//				dose = "";
					//				
					//				for (int i = 0; i < substances.size(); i++) {
					//					log.info("This is: " + ((Node) substances.get(i)).asXML());
					//					Document tempDoc = DocumentHelper.parseText(((Node) substances.get(i)).asXML());
					//					xpathSelector = DocumentHelper.createXPath(
					//					"//*[name() = 'substanceAdministration']/*[name() = 'effectiveTime']/@value");
					//					Attribute attr = (Attribute) xpathSelector.selectSingleNode(tempDoc);
					//					if ( attr != null && !(attr.getData().toString().equals("") || attr.getData().toString() == null) ) {
					//						date = attr.getData().toString();
					//					}
					//					
					//					xpathSelector = DocumentHelper.createXPath(
					//					"//*[name() = 'substanceAdministration']/*[name() = 'consumable']/*[name() = 'administerableMaterial']/*[name() = 'administerableMaterial']/*[name() = 'code']/@code");
					//					attr = (Attribute) xpathSelector.selectSingleNode(tempDoc);
					//					if ( attr != null && !(attr.getData().toString().equals("") || attr.getData().toString() == null) ) {
					//						code = attr.getData().toString();
					//					}
					//
					//					xpathSelector = DocumentHelper.createXPath(
					//					"//*[name() = 'substanceAdministration']/*[name() = 'consumable']/*[name() = 'administerableMaterial']/*[name() = 'administerableMaterial']/*[name() = 'code']/@displayName");
					//					attr = (Attribute) xpathSelector.selectSingleNode(tempDoc);
					//					if ( attr != null && !(attr.getData().toString().equals("") || attr.getData().toString() == null) ) {
					//						name = attr.getData().toString();
					//					}
					//
					//					xpathSelector = DocumentHelper.createXPath(
					//					"//*[name() = 'substanceAdministration']/*[name() = 'entryRelationship']/*[name() = 'observation']/*[name() = 'value']/@value");
					//					attr = (Attribute) xpathSelector.selectSingleNode(tempDoc);
					//					if ( attr != null && !(attr.getData().toString().equals("") || attr.getData().toString() == null) ) {
					//						dose = attr.getData().toString();
					//					}
					//
					//					log.info("Got date: " + date + ", code: " + code + ", name: " + name + ", dose: " + dose);
					//					
					//					int pos = 0;
					//					if (foreDates.size() < 1) {
					//						log.info("Adding as initial entry - date: " + date + ", code: " + code + ", name: " + name + ", dose: " + dose);
					//						foreDates.add(date);
					//						foreCodes.add(code);
					//						foreNames.add(name);
					//						foreDoses.add(dose);
					//					} else {
					//						for (int j = 0; j < foreDates.size(); j++) {
					//							if (foreDates.get(j).toString().compareTo(date) < 0) {
					//								pos = j + 1;
					//								continue;
					//							} else {
					//								pos = j;
					//								break;
					//							}
					//						}
					//						log.info("Adding at position " + pos + " - date: " + date + ", code: " + code + ", name: " + name + ", dose: " + dose);
					//						foreDates.add(pos, date);
					//						foreCodes.add(pos, code);
					//						foreNames.add(pos, name);
					//						foreDoses.add(pos, dose);
					//					}
					//				}

					Boolean overdueHeader = Boolean.FALSE;
					Boolean forecastHeader = Boolean.FALSE;
					StringBuffer currentTime = new StringBuffer();
					SimpleDateFormat pstFormat = new SimpleDateFormat("yyyyMMdd");
					pstFormat.setTimeZone( TimeZone.getTimeZone( "America/Los_Angeles") );
					currentTime = pstFormat.format(new Date (System.currentTimeMillis()), currentTime, new FieldPosition(0));

					for (int i = 0; i < foreDates.size(); i++) {
						if (foreDates.get(i).toString().compareTo(currentTime.toString()) < 0) {
							if (! overdueHeader) {
								out.print("<TR>\n<TD style=\"border: 1px solid #0000FF\" colspan=\"3\"><font color=\"red\"><b>OVERDUE IMMUNIZATIONS:</b></font></TD>\n</TR>\n");
								out.print("<TR>\n<TD style=\"border: 1px solid #0000FF\" ALIGN=\"center\"><B>Vaccine (CVX)</B></TD>\n<TD style=\"border: 1px solid #0000FF\" ALIGN=\"center\"><B>Recommended Date</B></TD>\n<TD style=\"border: 1px solid #0000FF\" ALIGN=\"center\"><B>Dose #</B></TD>\n</TR>\n");
								overdueHeader = Boolean.TRUE;
							}
						} else {
							if (! forecastHeader) {
								out.print("<TR>\n<TD style=\"border: 1px solid #0000FF\" colspan=\"3\"><b>FUTURE IMMUNIZATIONS:</b></TD>\n</TR>\n");
								out.print("<TR>\n<TD style=\"border: 1px solid #0000FF\" ALIGN=\"center\"><B>Vaccine (CVX)</B></TD>\n<TD style=\"border: 1px solid #0000FF\" ALIGN=\"center\"><B>Recommended Date</B></TD>\n<TD style=\"border: 1px solid #0000FF\" ALIGN=\"center\"><B>Dose #</B></TD>\n</TR>\n");
								forecastHeader = Boolean.TRUE;
							}					
						}
						out.print("<TR>\n<TD style=\"border: 1px solid #0000FF\">" + foreTexts.get(i).toString() + "/" + foreNames.get(i).toString() + " (" + foreCodes.get(i).toString() + ")</TD>\n<TD style=\"border: 1px solid #0000FF\">" + foreDates.get(i).toString().substring(0, 4) + "-" + foreDates.get(i).toString().substring(4, 6) + "-" + foreDates.get(i).toString().substring(6, 8) + "</TD>\n<TD style=\"border: 1px solid #0000FF\">" + foreDoses.get(i).toString() + "</TD>\n</TR>\n");
					}
					out.print("</TABLE>\n");

					if ( LOG_PERFORMANCE_DATA ) {
						stopTime = System.currentTimeMillis();
						log.info("TIMER,Generated HTML Output & yCard Query," + (stopTime-startTime) + " ms");				
						startTime = System.currentTimeMillis();
					}

					/* Get yellow card PDF */

					// Construct data
					String cardData = URLEncoder.encode("requestFormat", "UTF-8") + "=" + URLEncoder.encode("CAYellowCard", "UTF-8");
					cardData += "&" + URLEncoder.encode("facilityName", "UTF-8") + "=" + URLEncoder.encode("River County Public Health", "UTF-8");
					cardData += "&" + URLEncoder.encode("facilityAddress", "UTF-8") + "=" + URLEncoder.encode("123 Fake St.", "UTF-8");
					cardData += "&" + URLEncoder.encode("facilityCity", "UTF-8") + "=" + URLEncoder.encode("River City", "UTF-8");
					cardData += "&" + URLEncoder.encode("facilityState", "UTF-8") + "=" + URLEncoder.encode("Riversota", "UTF-8");
					cardData += "&" + URLEncoder.encode("facilityZip", "UTF-8") + "=" + URLEncoder.encode("99999", "UTF-8");
					cardData += "&" + URLEncoder.encode("facilityPhone", "UTF-8") + "=" + URLEncoder.encode("555-867-5309", "UTF-8");
					cardData += "&" + URLEncoder.encode("data", "UTF-8") + "=" + URLEncoder.encode(vux, "UTF-8");

					log.info("VUX = " + vux);
					log.info("Card Data = " + cardData);

					// Send data
					int BUF_SIZE = 1024;
					String fileTail = alphaOnly(patientID.indexOf("&") != -1 ? patientID.substring(0, patientID.indexOf("&")) : patientID) + "_" + System.currentTimeMillis();
					// This path should obviously live in a properties file.  Honeydew path was:
					// /home/websites/honeydew.cirg.washington.edu/vfm2009/ycards/
					String fileName = yCardsDir + "/" + fileTail + ".pdf";
					URL url = new URL(vfmCardUrl);
					URLConnection cardConn = url.openConnection();
					cardConn.setDoOutput(true);
					OutputStreamWriter wr = new OutputStreamWriter(cardConn.getOutputStream());
					wr.write(cardData);
					wr.flush();
					File outFile = new File(fileName);
					FileOutputStream cardOut = new FileOutputStream(outFile);
					BufferedInputStream bis = new BufferedInputStream(cardConn.getInputStream(), BUF_SIZE);
					BufferedOutputStream bos = new BufferedOutputStream(cardOut);
					byte[] buff = new byte[BUF_SIZE];
					int bytesRead;

					if ( log.isDebugEnabled() ) {
						log.debug("Getting yellow card");
					}

					while ( -1 != (bytesRead = bis.read(buff, 0, buff.length)) ) {
						bos.write(buff, 0, bytesRead);
					}
					bos.flush();

					if ( log.isDebugEnabled() ) {
						log.debug("Retrieved yellow card and saved to " + fileTail);
					}

					bis.close();
					bos.close();

					if ( LOG_PERFORMANCE_DATA ) {
						stopTime = System.currentTimeMillis();
						log.info("TIMER,Received yCard," + (stopTime-startTime) + " ms");				
						startTime = System.currentTimeMillis();
					}

					/* Add yellow card button to output */
					out.print("<P>\n<FORM ACTION=\"" + yCardsDir.substring(yCardsDir.lastIndexOf("/") + 1) + "/" + fileTail + ".pdf\" METHOD=\"GET\">\n");
					out.print("<INPUT TYPE=\"submit\" VALUE=\"Print Yellow Card\">\n</FORM>\n");

					//				XDSSoapRequest vfmCardSoapReq = 
					//					new XDSSoapRequest(vfmDocument, vfmCardUrl, "1.2");
					//
					//				if ( LOG_PERFORMANCE_DATA) { //End GenerateSOAPRequest
					//					stopTime = System.currentTimeMillis();
					//					log.info("TIMER,Generate VFM Query SOAP Request," + (stopTime-startTime) + " ms");
					//					startTime=System.currentTimeMillis();
					//				}//Start SendSOAPRequest
					//
					//
					//				SOAPMessage vfmCardResponse = vfmCardSoapReq.sendVFMCardMessage();
					//
					//				if ( LOG_PERFORMANCE_DATA) {//End SendSOAPRequest
					//					stopTime = System.currentTimeMillis();
					//					log.info("TIMER,Send VFM Query SOAP Request," + (stopTime-startTime) + " ms");
					//				}
//				}

				/* Finalize output */
				out.print("</BODY>\n</HTML>\n");
				out.flush();
			}
		} catch (MalformedStoredQueryException e) {
			log.error(e);
			handleError(req, resp);
		} catch ( DocumentException e ) {
			log.error(e);
			handleError(req, resp);
		} catch ( XDSException e ) {
			log.error(e);
			handleError(req, resp);	   		
		} catch ( SOAPException e ) {
			log.error(e);
			handleError(req, resp);
		} catch ( IOException e ) {
			log.error(e);
			handleError(req, resp);
		} catch (MessagingException e) {
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


