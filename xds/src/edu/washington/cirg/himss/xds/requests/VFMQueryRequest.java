/*
 * Copyright 2004-2008 (C) University of Washington. All Rights Reserved.
 * Created on Aug 19, 2008
 * University of Washington, CIRG
 */
package edu.washington.cirg.himss.xds.requests;

import java.io.File;
import java.net.MalformedURLException;
import java.text.FieldPosition;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.TimeZone;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.dom4j.Attribute;
import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;
import org.dom4j.Node;
import org.dom4j.io.*;
import org.jaxen.SimpleNamespaceContext;
import org.jaxen.dom4j.Dom4jXPath;
import org.w3c.dom.*;

import edu.washington.cirg.himss.xds.XDSException;
import edu.washington.cirg.himss.xds.util.XDSConstants;

/** Class representing a query request sent to an VFM Immunization registry
 * to retrieve vaccination forecast information
 * @author <a href="mailto:jsibley@u.washington.edu">Jim Sibley</a>
 *
 */
public class VFMQueryRequest {

	private static Log log = LogFactory.getLog(VFMQueryRequest.class);
	private Map<String, String> queryParams = new HashMap();
	private Document document;
	
	public VFMQueryRequest(String patientId, String oid, String sex, String dob, List admins, List codes, List names, List manufExts, List manufNames) {

		/* set parameters for the query */
//		if (patientId.indexOf("%") == -1) {
//			queryParams.put("$XDSDocumentEntryPatientId", "'" + patientId + "'");
//		}
//		queryParams.put("$XDSDocumentEntryCreationTimeFrom", qStartTime);
//		queryParams.put("$XDSDocumentEntryCreationTimeTo", qStopTime);
//		setQueryParams(queryParams);

		try {

			String uwaOid = "1.3.6.1.4.1.21367.2008.1.2.145";
			document = DocumentHelper.createDocument();
			Element rootEl = document.addElement("REPC_IN004014UV")
				.addAttribute("xmlns", "urn:hl7-org:v3")
				.addAttribute("xmlns:ns1", "urn:hl7-org:v3")
				.addAttribute("xmlns:xsi", "http://www.w3.org/2001/XMLSchema-instance")
				.addAttribute("xsi:schemaLocation", "../schema/hl7/processable/REPC_IN004014UV.xsd")
				.addAttribute("ITSVersion", "XML_1.0");
			Element rootId = rootEl.addElement("id")
				.addAttribute("root", uwaOid + "." + System.currentTimeMillis())
				.addAttribute("extension", "987654321");
			Element rootCrTime = rootEl.addElement("creationTime");
			StringBuffer creationTime = new StringBuffer();
			SimpleDateFormat pstFormat = new SimpleDateFormat("yyyyMMddHHmmss.SSSZ");
			pstFormat.setTimeZone( TimeZone.getTimeZone( "America/Los_Angeles") );
			creationTime = pstFormat.format(new Date (System.currentTimeMillis()), creationTime, new FieldPosition(0));
			rootCrTime.addAttribute("value", creationTime.toString());
			Element rootInter = rootEl.addElement("interactionId")
				.addAttribute("root", "2.16.840.1.113883.5")
				.addAttribute("extension", "REPC_IN004014UV");
			Element rootPrCode = rootEl.addElement("processingCode")
				.addAttribute("code", "P");
			Element rootPrModeCode = rootEl.addElement("processingModeCode")
				.addAttribute("code", "T");
			Element rootAckCode = rootEl.addElement("acceptAckCode")
				.addAttribute("code", "AL");
			Element rootRecv = rootEl.addElement("receiver")
				.addAttribute("typeCode", "RCV");
			Element recvDev = rootRecv.addElement("device")
				.addAttribute("determinerCode", "INSTANCE")
				.addAttribute("classCode", "DEV");
			Element recvDevId = recvDev.addElement("id")
				.addAttribute("root", "1.3.6.1.4.1.21367.2008.1.2.333");
			Element recvDevName = recvDev.addElement("name");
			recvDevName.setText("UWA");
			Element recvDevManuf = recvDev.addElement("manufacturerModelName");
			recvDevManuf.setText("2008");
			Element recvDevSoft = recvDev.addElement("softwareName");
			recvDevSoft.setText("IZRouter");
			Element rootSend = rootEl.addElement("sender")
				.addAttribute("typeCode", "SND");
			Element sendDev = rootSend.addElement("device")
				.addAttribute("determinerCode", "INSTANCE")
				.addAttribute("classCode", "DEV");
			Element sendDevId = sendDev.addElement("id")
				.addAttribute("root", uwaOid);
			Element sendDevName = sendDev.addElement("name");
			sendDevName.setText("UWA");
			Element sendDevTel = sendDev.addElement("telecom")
				.addAttribute("value", "NULL");
			Element ctrlActProc = rootEl.addElement("controlActProcess")
				.addAttribute("moodCode", "EVN")
				.addAttribute("classCode", "CACT");
			Element ctrlActProcId = ctrlActProc.addElement("id")
				.addAttribute("nullFlavor", "NA");
			Element ctrlActProcSubj = ctrlActProc.addElement("subject")
				.addAttribute("typeCode", "SUBJ");
			Element ctrlActProcSubjReg = ctrlActProcSubj.addElement("registrationEvent")
				.addAttribute("classCode", "REG")
				.addAttribute("moodCode", "EVN");
			Element ctrlActProcSubjRegSubj2 = ctrlActProcSubjReg.addElement("subject2")
				.addAttribute("typeCode", "SUBJ");
			Element ctrlActProcSubjRegSubj2Prov = ctrlActProcSubjRegSubj2.addElement("careProvisionEvent")
				.addAttribute("classCode", "PCPR")
				.addAttribute("moodCode", "EVN");
			Element ctrlActProcSubjRegSubj2ProvTarg = ctrlActProcSubjRegSubj2Prov.addElement("recordTarget")
				.addAttribute("typeCode", "RCT");
			Element ctrlActProcSubjRegSubj2ProvTargPat = ctrlActProcSubjRegSubj2ProvTarg.addElement("patient")
				.addAttribute("classCode", "PAT");
			Element ctrlActProcSubjRegSubj2ProvTargPatTemplid = ctrlActProcSubjRegSubj2ProvTargPat.addElement("templateId")
				.addAttribute("root", "1.3.6.1.4.1.21367.2008.1.2.998");
			Element ctrlActProcSubjRegSubj2ProvTargPatId = ctrlActProcSubjRegSubj2ProvTargPat.addElement("id")
				.addAttribute("root", oid + "." + System.currentTimeMillis())
				.addAttribute("extension", "1011");
			Element ctrlActProcSubjRegSubj2ProvTargPatPers = ctrlActProcSubjRegSubj2ProvTargPat.addElement("patientPerson");
			Element ctrlActProcSubjRegSubj2ProvTargPatPersGender = ctrlActProcSubjRegSubj2ProvTargPatPers.addElement("administrativeGenderCode")
				.addAttribute("codeSystem", "2.16.840.1.113883.5.1")
				.addAttribute("codeSystemName", "AdministrativeGender")
				.addAttribute("code", sex);
			String dispSex = ("M".equals(sex) ? "Male" : ("F".equals(sex) ? "Female" : "Unknown"));
			ctrlActProcSubjRegSubj2ProvTargPatPersGender.addAttribute("displayName", dispSex);
			Element ctrlActProcSubjRegSubj2ProvTargPatPersDob = ctrlActProcSubjRegSubj2ProvTargPatPers.addElement("birthTime")
				.addAttribute("value", dob);
			/* Loop for each immunization */
			for (int i = 0; i < admins.size(); i++) {
				Element ctrlActProcSubjRegSubj2ProvInf1 = ctrlActProcSubjRegSubj2Prov.addElement("pertinentInformation3")
					.addAttribute("xmlns:hl7", "urn:hl7-org:v3")
					.addAttribute("contextConductionInd", "true");
//				List pi3 = ctrlActProcSubjRegSubj2ProvInf1.content();
//				pi3.add(imms.get(i));
				Element ctrlActProcSubjRegSubj2ProvInf1Subst = ctrlActProcSubjRegSubj2ProvInf1.addElement("substanceAdministration")
					.addAttribute("moodCode", "EVN")
					.addAttribute("negationInd", "false")
					.addAttribute("classCode", "SBADM");
				Element ctrlActProcSubjRegSubj2ProvInf1SubstTemplid1 = ctrlActProcSubjRegSubj2ProvInf1Subst.addElement("templateId")
					.addAttribute("root", "2.16.840.1.113883.10.20.1.24");
				Element ctrlActProcSubjRegSubj2ProvInf1SubstTemplid2 = ctrlActProcSubjRegSubj2ProvInf1Subst.addElement("templateId")
					.addAttribute("root", "1.3.6.1.4.1.19376.1.5.3.1.4.12");
				Element ctrlActProcSubjRegSubj2ProvInf1SubstCode = ctrlActProcSubjRegSubj2ProvInf1Subst.addElement("code")
					.addAttribute("code", "IMMUNIZ")
					.addAttribute("codeSystem", "2.16.840.1.113883.5.4")
					.addAttribute("codeSystemName", "ActCode");
				Element ctrlActProcSubjRegSubj2ProvInf1SubstTxt = ctrlActProcSubjRegSubj2ProvInf1Subst.addElement("text");
				Element ctrlActProcSubjRegSubj2ProvInf1SubstStat = ctrlActProcSubjRegSubj2ProvInf1Subst.addElement("statusCode")
					.addAttribute("code", "completed");
				Element ctrlActProcSubjRegSubj2ProvInf1SubstTime = ctrlActProcSubjRegSubj2ProvInf1Subst.addElement("effectiveTime")
					.addAttribute("value", admins.get(i).toString());
				Element ctrlActProcSubjRegSubj2ProvInf1SubstCons = ctrlActProcSubjRegSubj2ProvInf1Subst.addElement("consumable")
					.addAttribute("typeCode", "CSM");
				Element ctrlActProcSubjRegSubj2ProvInf1SubstConsDesc = ctrlActProcSubjRegSubj2ProvInf1SubstCons.addElement("desc");
				Element ctrlActProcSubjRegSubj2ProvInf1SubstConsMat = ctrlActProcSubjRegSubj2ProvInf1SubstCons.addElement("administerableMaterial")
					.addAttribute("classCode", "ADMM")
					.addAttribute("determinerCode", "INSTANCE");
				Element ctrlActProcSubjRegSubj2ProvInf1SubstConsMatMat = ctrlActProcSubjRegSubj2ProvInf1SubstConsMat.addElement("administerableMaterial")
					.addAttribute("classCode", "MMAT")
					.addAttribute("determinerCode", "INSTANCE");
				Element ctrlActProcSubjRegSubj2ProvInf1SubstConsMatMatTemplid1 = ctrlActProcSubjRegSubj2ProvInf1SubstConsMatMat.addElement("templateId")
					.addAttribute("root", "1.3.6.1.4.1.19376.1.5.3.1.4.7.2");
				Element ctrlActProcSubjRegSubj2ProvInf1SubstConsMatMatTemplid2 = ctrlActProcSubjRegSubj2ProvInf1SubstConsMatMat.addElement("templateId")
					.addAttribute("root", "2.16.840.1.113883.10.20.1.53");
				Element ctrlActProcSubjRegSubj2ProvInf1SubstConsMatMatCode = ctrlActProcSubjRegSubj2ProvInf1SubstConsMatMat.addElement("code")
					.addAttribute("code", codes.get(i).toString())
					.addAttribute("codeSystem", "2.16.840.1.113883.6.59")
					.addAttribute("displayName", names.get(i).toString());
				Element ctrlActProcSubjRegSubj2ProvInf1SubstConsMatMatLot = ctrlActProcSubjRegSubj2ProvInf1SubstConsMatMat.addElement("lotNumberText");
				ctrlActProcSubjRegSubj2ProvInf1SubstConsMatMatLot.setText("xab32561-42B");
				Element ctrlActProcSubjRegSubj2ProvInf1SubstConsMatMatExp = ctrlActProcSubjRegSubj2ProvInf1SubstConsMatMat.addElement("expirationTime")
					.addAttribute("value", "20081231");
				Element ctrlActProcSubjRegSubj2ProvInf1SubstConsMatMatManuf = ctrlActProcSubjRegSubj2ProvInf1SubstConsMatMat.addElement("asMedicineManufacturer")
					.addAttribute("classCode", "MANU");
				Element ctrlActProcSubjRegSubj2ProvInf1SubstConsMatMatManufManuf = ctrlActProcSubjRegSubj2ProvInf1SubstConsMatMatManuf.addElement("manufacturer")
					.addAttribute("classCode", "ORG");
				Element ctrlActProcSubjRegSubj2ProvInf1SubstConsMatMatManufManufId = ctrlActProcSubjRegSubj2ProvInf1SubstConsMatMatManufManuf.addElement("id")
					.addAttribute("root", "2.16.840.1.113883.6.60")
					.addAttribute("extension", manufExts.get(i).toString());
				Element ctrlActProcSubjRegSubj2ProvInf1SubstConsMatMatManufManufName = ctrlActProcSubjRegSubj2ProvInf1SubstConsMatMatManufManuf.addElement("name");
				ctrlActProcSubjRegSubj2ProvInf1SubstConsMatMatManufManufName.setText(manufNames.get(i).toString());
			}
		
//			File xml = new File("/opt/tomcat/webapps/xds/WEB-INF/classes/messageICVFM.xml");
//			SAXReader reader = new SAXReader();
//			Document doc = reader.read(xml);
//			Element root = doc.getRootElement();
//			Dom4jXPath xpathSelector = new Dom4jXPath("//*[local-name() = 'REPC_IN004014VV']");
////			SimpleNamespaceContext nsContext = new SimpleNamespaceContext();
////			nsContext.addNamespace("soapenv","http://schemas.xmlsoap.org/soap/envelope/");
////			nsContext.addNamespace("SOAP-ENV","http://schemas.xmlsoap.org/soap/envelope/");
////			nsContext.addNamespace("ns5","urn:oasis:names:tc:ebxml-regrep:xsd:rim:3.0");
////			nsContext.addNamespace("ns4","urn:oasis:names:tc:ebxml-regrep:xsd:query:3.0");
////			nsContext.addNamespace("query","urn:oasis:names:tc:ebxml-regrep:xsd:query:3.0");
////			nsContext.addNamespace("rim","urn:oasis:names:tc:ebxml-regrep:xsd:rim:3.0");
////			xpathSelector.setNamespaceContext(nsContext);
//			List<Node> results = xpathSelector.selectNodes(doc);
//
//			Iterator iterator = results.iterator();
//			while (iterator.hasNext()) {
//				Node eo = (Node) iterator.next();
//				String uniquePath = eo.getUniquePath();
//
//				//log.info("INFO, eo = '" + eo.toString() + "'");				
//				
//				// Retrieve patient ID.
//				xpathSelector = new Dom4jXPath(eo.getUniquePath() + "/*[local-name() = 'id']/@root");
//				List<Node> nodeList = xpathSelector.selectNodes(eo);
//				
//				//log.info("INFO, xpath = '" + xpathSelector.toString() + "', nodeList size = '" + nodeList.size() + "'");				
//				
//				Iterator nodeIterator = nodeList.iterator();
//				String patId = "";
//				if (nodeList.size() < 1) {
//					patientIdList.add("");
//				} else {
//					while (nodeIterator.hasNext()) {
//						Attribute attr = (Attribute) nodeIterator.next();
//						patId = patId.concat(attr.getData().toString());
//						if (nodeIterator.hasNext()) {
//							patId = patId.concat("\r");
//						}
//					}
//					patientIdList.add(patId);
//				}

//		} catch (DocumentException e) {
//			log.error(e);
//		} catch (MalformedURLException e) {
//			log.error(e);
		} finally {
			
		}
	}
	
	public Document generateRequest() throws XDSException, DocumentException{

		//Document document = DocumentHelper.createDocument();
		return(document);
	}
	
//	public void setQueryParams(Map queryParams) {
//		this.queryParams = queryParams;
//	}
	
//	public FindDocumentsRequest(String id, int queryBy, String queryType ) {
//		this(queryType);
//	}
	
}


