/*
 * Copyright 2004-2009 (C) University of Washington. All Rights Reserved.
 * Created on Aug 13, 2008
 * University of Washington, CIRG
 */
package edu.washington.cirg.himss.servlet;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.InputStream;
import java.io.IOException;
import java.io.PrintWriter;
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
import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.jaxen.JaxenException;
//import org.eclipse.ohf.ihe.atna.agent.AtnaAgentFactory;
//import org.eclipse.ohf.ihe.common.hl7v2.message.PixPdqMessageUtilities;
//import org.eclipse.ohf.ihe.common.hl7v2.message.PixPdqMessageException;
//import org.eclipse.ohf.ihe.common.hl7v2.mllpclient.ClientException;
//import org.eclipse.ohf.ihe.common.mllp.*;
//import org.eclipse.ohf.ihe.pdq.consumer.*;
import org.openhealthtools.ihe.atna.auditor.PDQConsumerAuditor;
import org.openhealthtools.ihe.atna.auditor.codes.rfc3881.RFC3881EventCodes.RFC3881EventOutcomeCodes;
import org.openhealthtools.ihe.common.hl7v2.message.PixPdqMessageUtilities;
import org.openhealthtools.ihe.common.hl7v2.message.PixPdqMessageException;
import org.openhealthtools.ihe.common.hl7v2.mllpclient.ClientException;
import org.openhealthtools.ihe.common.mllp.*;
import org.openhealthtools.ihe.pdq.consumer.*;

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
public class PDQFindPatientsServlet extends HttpServlet {

	private static Log log = LogFactory.getLog(PDQFindPatientsServlet.class);
	private static boolean LOG_PERFORMANCE_DATA = false; 

	private String DATASOURCE_NAME;
	
	public PDQFindPatientsServlet() {
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
	
		String pidList = req.getParameter(XDSControllerServlet.PDQ_PID_LIST_PARAM);
		String pixUrl = req.getParameter(XDSControllerServlet.PIX_MANAGER_URL_PARAM);
		String atnaUrl = req.getParameter(XDSControllerServlet.ATNA_URL_PARAM);
		String patName = req.getParameter(XDSControllerServlet.PDQ_PAT_NAME_PARAM);
		String patDob = req.getParameter(XDSControllerServlet.PDQ_PAT_DOB_PARAM);
		String patAddr = req.getParameter(XDSControllerServlet.PDQ_PAT_ADDR_PARAM);
		String useTls = req.getParameter(XDSControllerServlet.USE_TLS_PARAM);
		String patSex = req.getParameter(XDSControllerServlet.PDQ_PAT_SEX_PARAM);
		String patAcct = req.getParameter(XDSControllerServlet.PDQ_PAT_ACCT_PARAM);
		
		if ( (pidList == null || pidList.equals("")) &&
			 (patName == null || patName.equals("")) &&
			 (patDob == null || patDob.equals("")) &&
			 (patAddr == null || patAddr.equals("")) &&
			 (patSex == null || patSex.equals("")) &&
			 (patAcct == null || patAcct.equals("")) ) {
			throw new ServletException("no query params passed to servlet");
    	}
    	if ( pixUrl == null || pixUrl.equals("") ) {
    		throw new ServletException("pixUrl param not passed to servlet");
    	}
    	if ( atnaUrl == null || atnaUrl.equals("") ) {
    		throw new ServletException("atnaUrl param not passed to servlet");
    	}
    	if ( !( useTls == null || useTls.equals("") || "0".equals(useTls) ) ) {
            // set the system security properties
            System.setProperty("javax.net.ssl.keyStore", XDSConstants.TLS_KEYSTORE);
            System.setProperty("javax.net.ssl.keyStorePassword", XDSConstants.TLS_KEYSTORE_PASS);
            System.setProperty("javax.net.ssl.trustStore", XDSConstants.TLS_CERTFILE);
            System.setProperty("javax.net.ssl.trustStorePassword", XDSConstants.TLS_CERTFILE_PASS);
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
			if ( LOG_PERFORMANCE_DATA ) { //Start GeneratePDQQuery
				startTime=System.currentTimeMillis();
			}

			PdqConsumer pdqQuery = null;
			PdqConsumerResponse pdqResponse = null;
//			Message pdqResponse = null;
			
			//atna set-up
//			AtnaAgentFactory.getAtnaAgent().setInitiatingUser("ihe2009");
//			AtnaAgentFactory.getAtnaAgent().setAuditSourceId("UWA/SAIC");
//			AtnaAgentFactory.getAtnaAgent().setAuditRepository(new URI("mllp", null, atnaUrl.substring(0, atnaUrl.indexOf(":")), Integer.valueOf(atnaUrl.substring(atnaUrl.indexOf(":") + 1)).intValue(), null, null, null));
			PDQConsumerAuditor auditor = PDQConsumerAuditor.getAuditor();
			try {
				auditor.getConfig().setAuditRepositoryUri(new URI("http://" + atnaUrl));
			} catch (URISyntaxException e) {
				e.printStackTrace();
			}
			auditor.getConfig().setAuditorEnabled(true);
			auditor.getConfig().setSystemIpAddress("140.142.56.21");
			auditor.getConfig().setAuditSourceId("UWA/SAIC");

			//pdqQuery set-up
			try {
//				InputStream cpStream = new FileInputStream(XDSConstants.CONFORMANCE_PROFILE_FILE);
//				pdqQuery = new PdqConsumer(cpStream);
				pdqQuery = new PdqConsumer();
				//cpStream.close();
		    	if ( !( useTls == null || useTls.equals("") || "0".equals(useTls) ) ) {
		    		MLLPDestination mllps = new MLLPDestination(new URI("mllps", null, pixUrl.substring(0, pixUrl.indexOf(":")), Integer.valueOf(pixUrl.substring(pixUrl.indexOf(":") + 1)).intValue(), null, null, null));
		    		MLLPDestination.setUseATNA(true); 
					pdqQuery.setMLLPDestination(mllps); 
//					SecurityDomain domain = new SecurityDomain("domainXY", props); 
//					ConfigurationManager.registerDefaultSecurityDomain(domain);
		    	} else {
		    		MLLPDestination mllp = new MLLPDestination(new URI("mllp", null, pixUrl.substring(0, pixUrl.indexOf(":")), Integer.valueOf(pixUrl.substring(pixUrl.indexOf(":") + 1)).intValue(), null, null, null)); 
					MLLPDestination.setUseATNA(true); 
					pdqQuery.setMLLPDestination(mllp); 
		    	}
//				pdqQuery.setMLLPDestination(new MLLPDestination(new URI("mllp", null, pixUrl.substring(0, pixUrl.indexOf(":")), Integer.valueOf(pixUrl.substring(pixUrl.indexOf(":") + 1)).intValue(), null, null, null)));
				pdqQuery.setMaxVerifyEvent(4);
				//pdqQuery.setMLLPDestination(createSecureMLLP());		
//			} catch (FileNotFoundException e) {
//				throw new ServletException(e);
			} catch (ClientException e) {
				throw new ServletException(e);
			} catch (URISyntaxException e) {
				throw new ServletException(e);
			}

			//PDQ QUERY
			//MSH|^~\&|OHFConsumer1|OHFFacility1|OTHER_KIOSK|HIMSSSANDIEGO|20070108141845-0800||QBP^Q22^QBP_Q21|0404343237490344683|P|2.5
			//QPD|Q22^Find Candidates|2215052042665444508703766639234|@PID.5.1^MOORE~@PID.5.2^CHIP
			//RCP|I|10^RD
			//
			//MSH|^~\&|OTHER_KIOSK|HIMSSSANDIEGO|OHFConsumer1|OHFFacility1|20070108161844-0600||RSP^K22|1168294724767696ibmo|P|2.5
			//MSA|AA|0404343237490344683
			//QAK|2215052042665444508703766639234|OK
			//QPD|Q22^Find Candidates|2215052042665444508703766639234|^MOORE~^CHIP
			//PID|||PDQ113XX01^^^HIMSS2005&1.3.6.1.4.1.21367.2005.1.1&ISO||MOORE^CHIP||19380224|M||WH|10 Pinetree^^Webster^MO^63119||^PRN^PH
			//QRI|189.0	
			
			PdqConsumerDemographicQuery msg = pdqQuery.createDemographicQuery();
			msg.changeDefaultSendingApplication("UWAPdqConsumer", "", "");
			msg.changeDefaultSendingFacility("UWA/SAIC", "", "");
			msg.changeDefaultReceivingApplication("XDSDEMO_ADT", "", "");
			msg.changeDefaultRecievingFacility("XDSDEMO", "", "");
			msg.changeDefaultMessageQueryName("Q22", "Find Candidates", "", "", "", "");
			log.info("ClientPdqQueryTest: adding pidList - '" + pidList + "'");
			if (! (pidList == null || "".equals(pidList)) ) {
				String[] pidParts = pidList.split("^");
				if (pidParts.length == 4) {
					msg.addQueryPatientID(pidParts[0], pidParts[1], pidParts[2], pidParts[3]);
				}
			}
			log.info("ClientPdqQueryTest: adding patName - '" + patName + "'");
			if (! (patName == null || "".equals(patName)) ) {
				String[] nameParts = patName.split("\\^");
				if (nameParts.length > 0 && ! (nameParts[0] == null || "".equals(nameParts[0])) ) {
					msg.addQueryPatientNameFamilyName(nameParts[0]);
				}
				if (nameParts.length > 1 && ! (nameParts[1] == null || "".equals(nameParts[1])) ) {
					msg.addQueryPatientNameGivenName(nameParts[1]);
				}
			}
			log.info("ClientPdqQueryTest: adding patDob - '" + patDob + "'");
			if (! (patDob == null || "".equals(patDob)) ) {
				msg.addQueryPatientDateOfBirth(patDob);
			}
			log.info("ClientPdqQueryTest: adding patAddr - '" + patAddr + "'");
			if (! (patAddr == null || "".equals(patAddr)) ) {
				String[] addrParts = patAddr.split("^");
				if (addrParts.length > 0 && ! (addrParts[0] == null || "".equals(addrParts[0])) ) {
					msg.addQueryPatientAddressStreetAddress(addrParts[0]);
				}
				if (addrParts.length > 1 && ! (addrParts[1] == null || "".equals(addrParts[1])) ) {
					msg.addQueryPatientAddressOtherDesignation(addrParts[1]);
				}
				if (addrParts.length > 2 && ! (addrParts[2] == null || "".equals(addrParts[2])) ) {
					msg.addQueryPatientAddressCity(addrParts[2]);
				}
				if (addrParts.length > 3 && ! (addrParts[3] == null || "".equals(addrParts[3])) ) {
					msg.addQueryPatientAddressStateOrProvince(addrParts[3]);
				}
				if (addrParts.length > 4 && ! (addrParts[4] == null || "".equals(addrParts[4])) ) {
					msg.addQueryPatientAddressZipOrPostalCode(addrParts[4]);
				}
				if (addrParts.length > 5 && ! (addrParts[5] == null || "".equals(addrParts[5])) ) {
					msg.addQueryPatientAddressCountry(addrParts[5]);
				}
				if (addrParts.length > 6 && ! (addrParts[6] == null || "".equals(addrParts[6])) ) {
					msg.addQueryPatientAddressType(addrParts[6]);
				}
				if (addrParts.length > 7 && ! (addrParts[7] == null || "".equals(addrParts[7])) ) {
					msg.addQueryPatientAddressOtherGeographicDesignation(addrParts[7]);
				}
				if (addrParts.length > 8 && ! (addrParts[8] == null || "".equals(addrParts[8])) ) {
					msg.addQueryPatientAddressCountyParishCode(addrParts[8]);
				}
				if (addrParts.length > 9 && ! (addrParts[9] == null || "".equals(addrParts[9])) ) {
					msg.addQueryPatientAddressCensusTract(addrParts[9]);
				}
			}
			log.info("ClientPdqQueryTest: adding patSex - '" + patSex + "'");
			if (! (patSex == null || "".equals(patSex)) ) {
				//msg.addOptionalDemographicSearch("PID-8",patSex);
				msg.addQueryPatientSex(patSex);
			}
			log.info("ClientPdqQueryTest: adding pidAcct - '" + patAcct + "'");
			if (! (patAcct == null || "".equals(patAcct)) ) {
				String[] acctParts = patAcct.split("^");
				if (acctParts.length == 4) {
					msg.addQueryPatientID(acctParts[0], acctParts[1], acctParts[2], acctParts[3]);
				}
			}
			msg.addOptionalQuantityLimit(10);
			log.info("ClientPdqQueryTest: query - " + PixPdqMessageUtilities.msgToString(msg));

			if ( LOG_PERFORMANCE_DATA) { //End GeneratePDQQuery
				stopTime = System.currentTimeMillis();
				log.info("TIMER,Generate PDQ Query Request," + (stopTime-startTime) + " ms");
				startTime=System.currentTimeMillis();
	    	}//Start ProcessPDQResponse
			
			pdqResponse = pdqQuery.sendDemographicQuery(msg, true);
			String patID[] = new String[10];
			String patientID = "";
			
			// prepare response to caller
			resp.setContentType("text/html");
			PrintWriter out = resp.getWriter();
			//out.print("<HTML>\n<BODY>\n");
			
//			if (pdqResponse.getContinuationPointer() == null) {
//				out.print("<H4>No patients found matching search criteria</H4>\n");
//			} else {
//				//out.print("<H2>Patient search results:</H2>\n");
			if (! "OK".equals(pdqResponse.getQueryStatus(Boolean.FALSE))) {
				out.print("<H2>No patients found matching search criteria</H2>\n");
				log.info("INFO,PDQ response status='" + pdqResponse.getQueryStatus(Boolean.FALSE) + "'");
			} else {
				out.print("<TABLE style=\"border: 1px solid #0000FF\" WIDTH=\"80%\">\n<TR>\n<TH style=\"border: 1px solid #0000FF\" width=\"10%\">Action</TH>\n<TH style=\"border: 1px solid #0000FF\" width=\"35%\">Name</TH>\n<TH style=\"border: 1px solid #0000FF\" width=\"15%\">DOB</TH>\n<TH style=\"border: 1px solid #0000FF\" width=\"10%\">Gender</TH>\n<TH style=\"border: 1px solid #0000FF\" width=\"30%\">Addr.</TH>\n</TR>\n");
			}
			
			int patCnt = 0;
//			while (pdqResponse.getContinuationPointer() != null) {
			if ("OK".equals(pdqResponse.getQueryStatus(Boolean.FALSE))) {
				try {
					log.info("ClientPdqQueryTest: response - " + PixPdqMessageUtilities.msgToString(pdqResponse));
				} catch (PixPdqMessageException e) {
					log.info("ClientPdqQueryTest: response - ");
				}
			
				//status
				log.info("Query Status: " + pdqResponse.getQueryStatus(true));
				log.info("ResponseACK Code: " + pdqResponse.getResponseAckCode(false));
				log.info("ResponseACK Desc: " + pdqResponse.getResponseAckCode(true));
			
				//header echo
				try {
					log.info("Sending App: " + pdqResponse.getSendingApplication()[0]);
					log.info("Sending Fac: " + pdqResponse.getSendingFacility()[0]);
					log.info("Receiving App: " + pdqResponse.getReceivingApplication()[0]);
					log.info("Receiving Fac: " + pdqResponse.getReceivingFacility()[0]);
				} catch (PixPdqMessageException e) {
					throw new PdqConsumerException(e);
				}
				
				log.info("Control ID: " + pdqResponse.getControlId());
			
				//check for error?
				if (pdqResponse.hasError()) {
				
					//multiple errors returned either as segments or repeats (not both)
					int segCnt = pdqResponse.getErrorCountbySegment();
					int rptCnt = pdqResponse.getErrorCountbyRepeat();
					log.info("Errors returned: " + "seg-" + segCnt + " rpt-" + rptCnt);
				
					if (segCnt > 0) {
						for (int i=0; i < segCnt; i++) {
							String errLoc[] = pdqResponse.getErrorLocation(i, 0);
							String errCode[] = pdqResponse.getErrorCode(i);
							log.info("  Error location: " + errLoc[0] + "^" + errLoc[1] + "^" + errLoc[2] + "^" + errLoc[3] + "^" + errLoc[4] + "^" + errLoc[5]);
							log.info("  Error code: " + errCode[0]);
							log.info("  Error severity: " + pdqResponse.getErrorSeverity(i, true));
						}
					}
					else if (rptCnt > 0) {
						for (int i=0; i < rptCnt; i++) {
							String errLoc[] = pdqResponse.getErrorLocation(0, i);
							String errCode[] = pdqResponse.getErrorCode(0);
							log.info("  Error location: " + errLoc[0] + "^" + errLoc[1] + "^" + errLoc[2] + "^" + errLoc[3] + "^" + errLoc[4] + "^" + errLoc[5]);
							log.info("  Error code: " + errCode[0]);
							log.info("  Error severity: " + pdqResponse.getErrorSeverity(0, true));
						}
					}
				}
			
				//check for patients?
				log.info("Patients returned: " + pdqResponse.getPatientCount());
				//out.print("<TR>\n<TD style=\"border: 1px solid #0000FF\" width=\"10%\"><A HREF=\"vfmFindDocs.php?patID=" + pdqResponse.getPatientIdentifier(patCnt, 0)[0] + "^^^%261.3.6.1.4.1.21367.2005.3.7%26ISO\">" + patCnt + "</A></TD>\n<TD style=\"border: 1px solid #0000FF\" width=\"35%\">" + pdqResponse.getPatientNameFamilyName(patCnt, 0) + ", " + pdqResponse.getPatientNameGivenName(patCnt, 0) + "</TD>\n<TD style=\"border: 1px solid #0000FF\" width=\"15%\">" + pdqResponse.getPatientDateOfBirth(patCnt) + "</TD>\n<TD style=\"border: 1px solid #0000FF\" width=\"10%\">" + pdqResponse.getPatientSex(patCnt, Boolean.FALSE) + "</TD>\n<TD style=\"border: 1px solid #0000FF\" width=\"30%\">" + pdqResponse.getPatientAddressStreetAddress(patCnt, 0) + "<BR>" + pdqResponse.getPatientAddressOtherDesignation(patCnt, 0) + ("".equals(pdqResponse.getPatientAddressOtherDesignation(patCnt, 0)) ? "" : "<BR>") + pdqResponse.getPatientAddressCity(patCnt, 0) + ", " + pdqResponse.getPatientAddressStateOrProvince(patCnt, 0) + "&nbsp;&nbsp;" + pdqResponse.getPatientAddressZipOrPostalCode(patCnt, 0) + "&nbsp;&nbsp;" + pdqResponse.getPatientAddressCountry(patCnt, 0) + "</TD>\n</TR>");
			
				String auditIDs[] = new String[pdqResponse.getPatientCount()];
				int count = 0;
				for (int i = 0; i < pdqResponse.getPatientCount(); i++) {

					patID = pdqResponse.getPatientIdentifier(i, 0);
					if (!"IHENA".equals(patID[1])) {
						continue;
					}
					auditIDs[i] = patID[0] + "^" + patID[1];
					String patientName[] = pdqResponse.getPatientName(i, 0);
					log.info("  Patient: " + patID[0] + "^" + patID[1] + "-" + pdqResponse.getPatientNameFamilyName(i, 0) + ", " + patientName[1]);
					String patientAddr[] = pdqResponse.getPatientAddress(i, 0);
					log.info("    Address: " + patientAddr[0] + ", " + patientAddr[1] + ", " + patientAddr[2] + ", " + patientAddr[3]);
					log.info("      Phone: " + pdqResponse.getPatientPhoneHomeUnformattedTelephoneNumber(i, 0));
					out.print("<TR" + (count % 2 == 1 ? " STYLE=\"background-color: PowderBlue;\"" : "") + ">\n<TD VALIGN=\"middle\"><FORM METHOD=\"GET\" ACTION=\"vfmFindDocs.php\"><INPUT TYPE=\"hidden\" NAME=\"patID\" VALUE=\"" + patID[0] + "^^^\"><BR><INPUT TYPE=\"submit\" STYLE=\"color: black; background: white; border: 2px solid blue;\" VALUE=\"Find Immunization Documents\"></FORM></TD>\n<TD VALIGN=\"middle\">" + pdqResponse.getPatientNameFamilyName(i, 0) + ", " + pdqResponse.getPatientNameGivenName(i, 0) + "</TD>\n<TD VALIGN=\"middle\">" + pdqResponse.getPatientDateOfBirth(i).substring(0, 4) + "-" + pdqResponse.getPatientDateOfBirth(i).substring(4, 6) + "-" + pdqResponse.getPatientDateOfBirth(i).substring(6) + "</TD>\n<TD VALIGN=\"middle\">" + pdqResponse.getPatientSex(i, Boolean.FALSE) + "</TD>\n<TD VALIGN=\"middle\">" + patientAddr[0] + ", " + patientAddr[1] + ", " + patientAddr[2] + ", " + patientAddr[3] + "</TD>\n</TR>");
					log.info("Patient IDs returned for patient " + (i+1) + " is " + pdqResponse.getPatientIdentifierCount(i));
					if (pdqResponse.getPatientIdentifierCount(i) > 0) {
						//out.print("<TR>\n<TD COLSPAN=\"5\">\n<TABLE>\n");
						for (int j=0; j < pdqResponse.getPatientIdentifierCount(i); j++) {
							String[] id = pdqResponse.getPatientIdentifier(i, j);
							int cnt=j+1;
							log.info("  " + cnt + " - Patient ID number: " + id[0]);
							log.info("  " + cnt + " - Patient ID assigningAuthority: " + id[1]);
							log.info("  " + cnt + " - Patient ID universalID: " + id[2]);
							log.info("  " + cnt + " - Patient ID universalIDType: " + id[3]);
							patientID = id[0] + "^^^";
							//out.print(id[0] + "^^^" + id[1] + "&" + id[2] + "&" + id[3]);
						}
						//out.print("</TABLE>\n</TD>\n</TR>\n");
					}
					count++;
				}
				auditor.auditPDQQueryEvent(RFC3881EventOutcomeCodes.SUCCESS, "http://67.155.0.245:3600", pdqResponse.getReceivingFacility()[0], pdqResponse.getReceivingApplication()[0], pdqResponse.getSendingFacility()[0], pdqResponse.getSendingApplication()[0], pdqResponse.getMessageControlID(), pdqResponse.getQueryTag(), auditIDs);
				msg.addOptionalContinuationPointer(pdqResponse);
				pdqResponse = pdqQuery.sendDemographicQuery(msg, true);
				patCnt++;
			}
//			OutputStream outs = resp.getOutputStream();
//			response.writeTo(outs);//We forward on SOAP Message to caller
//			}
			out.print("</TABLE>\n"); //</BODY>\n</HTML>\n");
			out.flush();
			
			if ( LOG_PERFORMANCE_DATA) { //End ProcessPDQResponse
				stopTime = System.currentTimeMillis();
				log.info("TIMER,Process PDQ Query Response," + (stopTime-startTime) + " ms");
				startTime=System.currentTimeMillis();
	    	}
		} catch ( IOException e ) {
			log.error(e);
			handleError(req, resp);
		} catch (PdqConsumerException e) {
			log.error(e);
			handleError(req, resp);
		} catch (PixPdqMessageException e) {
			log.error(e);
			handleError(req, resp);
//		} catch (URISyntaxException e) {
//			log.error(e);
//			handleError(req, resp);
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


