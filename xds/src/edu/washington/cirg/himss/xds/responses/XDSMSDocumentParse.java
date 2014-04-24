/*
 * Copyright 2004-2009 (C) University of Washington. All Rights Reserved.
 * Created on Feb 9, 2006
 * University of Washington, CIRG
 */
package edu.washington.cirg.himss.xds.responses;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.dom4j.Attribute;
import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.DocumentHelper;
import org.dom4j.Node;
import org.dom4j.XPath;

import edu.washington.cirg.himss.xds.util.XDSConstants;

/**
 * Class to handle parsing an XDS-MS document
 * 
 * @author <a href="mailto:jsibley@u.washington.edu">Jim Sibley</a>
 * 
 */

public class XDSMSDocumentParse {

	private static Log log = LogFactory.getLog(XDSMSDocumentParse.class);

	private String patientId;
	private String effTime;
	private String birthDate;
	private String gender;
	private String givenName;
	private String familyName;
	private String strAddr;
	private String city;
	private String state;
	private String zip;
	private String facility;
	private List ccTitles;
	private List ccTexts;
	private String type;
	private String docUri;

	public XDSMSDocumentParse(String xml)
	throws DocumentException, IOException {
		
		this.patientId = new String("");
		this.effTime = new String("");
		this.birthDate = new String("");
		this.gender = new String("");
		this.givenName = new String("");
		this.familyName = new String("");
		this.strAddr = new String("");
		this.city = new String("");
		this.state = new String("");
		this.zip = new String("");
		this.facility = new String("");
		this.ccTitles = new ArrayList();
		this.ccTexts = new ArrayList();
		this.type = new String("");
//		this.docUri = new String(uri);
		
//		try {
//			if ( uri.toLowerCase().startsWith("https://") &&
//					(tls == null || tls.equals(""))) {
//	            // set the system security properties
//	            System.setProperty("javax.net.ssl.keyStore", XDSConstants.TLS_KEYSTORE);
//	            System.setProperty("javax.net.ssl.keyStorePassword", XDSConstants.TLS_KEYSTORE_PASS);
//	            System.setProperty("javax.net.ssl.trustStore", XDSConstants.TLS_CERTFILE);
//	            System.setProperty("javax.net.ssl.trustStorePassword", XDSConstants.TLS_CERTFILE_PASS);
//	            System.setProperty("javax.net.ssl.serverAuth", "false");
//	            System.setProperty("javax.net.ssl.sslProtocol", "TLS");
//				String hostPort = uri.substring(8, uri.indexOf("/", 8));
//				uri = uri.replace(hostPort, nonTlsHostPort(hostPort));
//				uri = uri.replace("https://", "http://");
//				return;
//			}
			
			// Create a URL for the desired page
//			URL url = new URL(uri);
			
			// Read all the text returned by the server
//			BufferedReader in = new BufferedReader(new InputStreamReader(url.openStream()));
//			String str = "";
//			String xml = "";
//			while ((str = in.readLine()) != null) {
//				xml = xml.concat(str);
//			}
//			in.close();
			
			Document doc = DocumentHelper.parseText(xml);
			
			XPath xpathSelector = DocumentHelper.createXPath(
				"//*[name() = 'ClinicalDocument']/*[name() = 'recordTarget']/*[name() = 'patientRole']/*[name() = 'id']/@extension");
			Attribute attr = (Attribute) xpathSelector.selectSingleNode(doc);
			if ( attr != null && !(attr.getData().toString().equals("") || attr.getData().toString() == null) ) {
				patientId = attr.getData().toString();
//				try {
//					byte byteXml[] = xml.getBytes();
//				    FileOutputStream fos = new FileOutputStream("/tmp/xdsMsParsed_patientid_" + alphanumOnly(patientId) + ".xml");
//				    fos.write(byteXml);
//				    fos.close();
//				} catch (FileNotFoundException e) {
//				    System.err.println("WARN,File Not Found Exception: " + e);
//				} catch (IOException e) {
//				    System.err.println("WARN,IO Exception: " + e);
//				}
			}

			xpathSelector = DocumentHelper.createXPath(
				"//*[name() = 'ClinicalDocument']/*[name() = 'effectiveTime']/@value");
			attr = (Attribute) xpathSelector.selectSingleNode(doc);
			if ( attr != null && !(attr.getData().toString().equals("") || attr.getData().toString() == null) ) {
				effTime = attr.getData().toString().substring(0, 8);
				if (attr.getData().toString().length() > 9) {
					effTime = effTime.concat(attr.getData().toString().substring(8, 10));
				}
				if (attr.getData().toString().length() > 11) {
					effTime = effTime.concat(attr.getData().toString().substring(10, 12));
				}
				if (attr.getData().toString().length() > 13) {
					effTime = effTime.concat(attr.getData().toString().substring(12, 14));
				}
			}

			xpathSelector = DocumentHelper.createXPath(
				"//*[name() = 'ClinicalDocument']/*[name() = 'recordTarget']/*[name() = 'patientRole']/*[name() = 'patient']/*[name() = 'birthTime']/@value");
			attr = (Attribute) xpathSelector.selectSingleNode(doc);
			if ( attr != null && !(attr.getData().toString().equals("") || attr.getData().toString() == null) ) {
				if (attr.getData().toString().matches("\\d{8}")) {
					birthDate = attr.getData().toString().substring(0, 8);
				}
			}

			xpathSelector = DocumentHelper.createXPath(
				"//*[name() = 'ClinicalDocument']/*[name() = 'recordTarget']/*[name() = 'patientRole']/*[name() = 'patient']/*[name() = 'administrativeGenderCode']/@code");
			attr = (Attribute) xpathSelector.selectSingleNode(doc);
			if ( attr != null && !(attr.getData().toString().equals("") || attr.getData().toString() == null) ) {
				gender = attr.getData().toString();
			}

			xpathSelector = DocumentHelper.createXPath(
				"//*[name() = 'ClinicalDocument']/*[name() = 'recordTarget']/*[name() = 'patientRole']/*[name() = 'patient']/*[name() = 'name']/*[name() = 'given']");
			Node node = (Node) xpathSelector.selectSingleNode(doc);
			if ( node != null && !(node.getText().equals("") || node.getText() == null) ) {
				givenName = node.getText();
			}

			xpathSelector = DocumentHelper.createXPath(
				"//*[name() = 'ClinicalDocument']/*[name() = 'recordTarget']/*[name() = 'patientRole']/*[name() = 'patient']/*[name() = 'name']/*[name() = 'family']");
			node = xpathSelector.selectSingleNode(doc);
			if ( node != null && !(node.getText().equals("") || node.getText() == null) ) {
				familyName = node.getText();
			}

			xpathSelector = DocumentHelper.createXPath(
				"//*[name() = 'ClinicalDocument']/*[name() = 'recordTarget']/*[name() = 'patientRole']/*[name() = 'addr']/*[name() = 'streetAddressLine']");
			node = xpathSelector.selectSingleNode(doc);
			if ( node != null && !(node.getText().equals("") || node.getText() == null) ) {
				strAddr = node.getText();
			}

			xpathSelector = DocumentHelper.createXPath(
				"//*[name() = 'ClinicalDocument']/*[name() = 'recordTarget']/*[name() = 'patientRole']/*[name() = 'addr']/*[name() = 'city']");
			node = xpathSelector.selectSingleNode(doc);
			if ( node != null && !(node.getText().equals("") || node.getText() == null) ) {
				city = node.getText();
			}

			xpathSelector = DocumentHelper.createXPath(
				"//*[name() = 'ClinicalDocument']/*[name() = 'recordTarget']/*[name() = 'patientRole']/*[name() = 'addr']/*[name() = 'state']");
			node = xpathSelector.selectSingleNode(doc);
			if ( node != null && !(node.getText().equals("") || node.getText() == null) ) {
				state = node.getText();
			}

			xpathSelector = DocumentHelper.createXPath(
				"//*[name() = 'ClinicalDocument']/*[name() = 'recordTarget']/*[name() = 'patientRole']/*[name() = 'addr']/*[name() = 'postalCode']");
			node = xpathSelector.selectSingleNode(doc);
			if ( node != null && !(node.getText().equals("") || node.getText() == null) ) {
				zip = node.getText();
			}

			xpathSelector = DocumentHelper.createXPath(
				"//*[name() = 'ClinicalDocument']/*[name() = 'custodian']/*[name() = 'assignedCustodian']/*[name() = 'representedCustodianOrganization']/*[name() = 'name']");
			node = xpathSelector.selectSingleNode(doc);
			if ( node != null && !(node.getText().equals("") || node.getText() == null) ) {
				facility = node.getText();
			}

			xpathSelector = DocumentHelper.createXPath(
				"//*[name() = 'ClinicalDocument']/*[name() = 'code']/@code");
			attr = (Attribute) xpathSelector.selectSingleNode(doc);
			if ( attr != null && !(attr.getData().toString().equals("") || attr.getData().toString() == null) ) {
				type = attr.getData().toString();
			}

			/* Glom some text together to feed to CoCo */
			xpathSelector = DocumentHelper.createXPath(
				"//*[name() = 'ClinicalDocument']/*[name() = 'component']/*[name() = 'structuredBody']/*[name() = 'component']/*[name() = 'section']/*[name() = 'title']");
			List nodeList = xpathSelector.selectNodes(doc);
			Iterator nodeIterator = nodeList.iterator();
//			String title = "";
//			if (nodeList.size() < 1) {
//				ccTitles.add("");
//			} else {
				while (nodeIterator.hasNext()) {
					node = (Node) nodeIterator.next();
//					title = title.concat(node.getText());
//					if (nodeIterator.hasNext()) {
//						title = title.concat("\r");
//					}
//				}
					ccTitles.add(node.getText());
				}
//			}
			
			xpathSelector = DocumentHelper.createXPath(
				"//*[name() = 'ClinicalDocument']/*[name() = 'component']/*[name() = 'structuredBody']/*[name() = 'component']/*[name() = 'section']/*[name() = 'text']");
			nodeList = xpathSelector.selectNodes(doc);
			nodeIterator = nodeList.iterator();
//			String text = "";
//			if (nodeList.size() < 1) {
//				ccTexts.add("");
//			} else {
				while (nodeIterator.hasNext()) {
					node = (Node) nodeIterator.next();
//					text = text.concat(node.getText());
//					if (nodeIterator.hasNext()) {
//						text = text.concat("\r");
//					}
//				}
					ccTexts.add(node.getText());
				}	
//			}
//		} catch (MalformedURLException e) {
//			log.info("WARN,Malformed URL Exception Parsing XDS-MS Document: " + uri + " : " + e.toString());
//		} catch (IOException e) {
//			log.info("WARN,IO Exception Parsing XDS-MS Document: " + uri + " : " + e.toString());
//		}
	}
	
	private String nonTlsHostPort (String hostPort) {
		if (hostPort.equals("hxti2.ihe.net")) {
			return "hxti2.ihe.net:8080";
		} else if (hostPort.equals("")) {
			return "";
		} else {
			return "";
		}
	}
	
	private String alphanumOnly (String str) {
		return (str.replaceAll("[^a-zA-Z0-9]", ""));
	}
	
	public String getPatientId() {
		return patientId;
	}

	public String getEffectiveTime() {
		return effTime;
	}

	public String getBirthDate() {
		return birthDate;
	}

	public String getGender() {
		return gender;
	}

	public String getGivenName() {
		return givenName;
	}

	public String getFamilyName() {
		return familyName;
	}

	public String getAddrStreet() {
		return strAddr;
	}

	public String getAddrCity() {
		return city;
	}

	public String getAddrState() {
		return state;
	}

	public String getAddrZip() {
		return zip;
	}
	
	public String getFacility() {
		return facility;
	}

	public List getCcTitles() {
		return ccTitles;
	}

	public List getCcTexts() {
		return ccTexts;
	}

	public String getType() {
		return type;
	}

	public String getDocumentUri() {
		return docUri;
	}

}
