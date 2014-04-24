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
import java.text.ParsePosition;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
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
import org.dom4j.Node;
import org.dom4j.XPath;

import edu.washington.cirg.himss.xds.util.XDSConstants;

/**
 * Class to handle parsing an XDS-LAB document
 * 
 * @author <a href="mailto:jsibley@u.washington.edu">Jim Sibley</a>
 * 
 */

public class XDSLABDocumentParse {

	private static Log log = LogFactory.getLog(XDSLABDocumentParse.class);

	private String patientId;
	private String effTime;
	private String labType;
	private String state;
	private String zip;

	public XDSLABDocumentParse(String xml)
	throws DocumentException, IOException {
		
		this.patientId = new String("");
		this.effTime = new String("");
		this.labType = new String("");
		this.state = new String("");
		this.zip = new String("");
		
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
			
//			if ( uri.indexOf("10.242.0.54") != -1 ) {
//				uri.replace("10.242.0.54", "dejarnette1.ihe.net");
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
				try {
					byte byteXml[] = xml.getBytes();
				    FileOutputStream fos = new FileOutputStream("/tmp/xdsLabParsed_patientid_" + alphanumOnly(patientId) + ".xml");
				    fos.write(byteXml);
				    fos.close();
				} catch (FileNotFoundException e) {
				    System.err.println("WARN,File Not Found Exception: " + e);
				} catch (IOException e) {
				    System.err.println("WARN,IO Exception: " + e);
				}
			}

			xpathSelector = DocumentHelper.createXPath(
				"//*[name() = 'ClinicalDocument']/*[name() = 'effectiveTime']/@value");
			attr = (Attribute) xpathSelector.selectSingleNode(doc);
			if ( attr != null && !(attr.getData().toString().equals("") || attr.getData().toString() == null) ) {
//				Calendar israelCal = new GregorianCalendar(TimeZone.getTimeZone("Israel"));
//				israelCal.set(Integer.parseInt(attr.getData().toString().substring(0, 4)),
//					    Integer.parseInt(attr.getData().toString().substring(4, 6)) - 1,
//					    Integer.parseInt(attr.getData().toString().substring(6, 8)),
//					    Integer.parseInt(attr.getData().toString().substring(8, 10)),
//					    Integer.parseInt(attr.getData().toString().substring(10, 12)),
//					    Integer.parseInt(attr.getData().toString().substring(12, 14)));
//				
//				Calendar local = new GregorianCalendar(TimeZone.getTimeZone("New_York"));
//			    local.setTimeInMillis(israelCal.getTimeInMillis());
//
//			    effTime = Integer.toString(local.get(Calendar.YEAR)) + "-" + (Integer.toString(local.get(Calendar.MONTH) + 1)) + "-" +
//			    		  Integer.toString(local.get(Calendar.DATE)) + " " + Integer.toString(local.get(Calendar.HOUR_OF_DAY)) + ":" +
//			    		  Integer.toString(local.get(Calendar.MINUTE)) + ":" + Integer.toString(local.get(Calendar.SECOND));
			    
				SimpleDateFormat israelFormat = new SimpleDateFormat("yyyyMMddHHmmss");
				israelFormat.setTimeZone( TimeZone.getTimeZone( "Israel") );

				SimpleDateFormat estFormat = new SimpleDateFormat( "yyyyMMddHHmmss" );
				estFormat.setTimeZone( TimeZone.getTimeZone( "America/New_York" ));

				Date israelDate = israelFormat.parse(attr.getData().toString().substring(0, 14), new ParsePosition(0));
				effTime = estFormat.format( israelDate );

			}

			xpathSelector = DocumentHelper.createXPath(
				"//*[name() = 'ClinicalDocument']/*[name() = 'component']/*[name() = 'structuredBody']/*[name() = 'component']/*[name() = 'section']/*[name() = 'entry']/*[name() = 'act']/*[name() = 'entryRelationship']/*[name() = 'organizer']/*[name() = 'component']");
			//attr = (Attribute) xpathSelector.selectSingleNode(doc);
			List fluNodes = xpathSelector.selectNodes(doc);
			int i = fluNodes.size() - 1;
			Document tempDoc = DocumentHelper.parseText(((Node) fluNodes.get(i)).asXML());
			xpathSelector = DocumentHelper.createXPath(
			"//*[name() = 'component']/*[name() = 'observation']/*[name() = 'value']/@displayName");
			attr = (Attribute) xpathSelector.selectSingleNode(tempDoc);
			if ( attr != null && !(attr.getData().toString().equals("") || attr.getData().toString() == null) ) {
				labType = attr.getData().toString();
			}

			xpathSelector = DocumentHelper.createXPath(
				"//*[name() = 'ClinicalDocument']/*[name() = 'recordTarget']/*[name() = 'patientRole']/*[name() = 'addr']/*[name() = 'state']");
			Node node = (Node) xpathSelector.selectSingleNode(doc);
			if ( node != null && !(node.getText().equals("") || node.getText() == null) ) {
				state = node.getText();
			}

			xpathSelector = DocumentHelper.createXPath(
				"//*[name() = 'ClinicalDocument']/*[name() = 'recordTarget']/*[name() = 'patientRole']/*[name() = 'addr']/*[name() = 'postalCode']");
			node = xpathSelector.selectSingleNode(doc);
			if ( node != null && !(node.getText().equals("") || node.getText() == null) ) {
				zip = node.getText();
			}

//			String xmlStr = doc.toString();
//			String addrStr = xmlStr.substring(xmlStr.indexOf("<addr>", 0) + 7, xmlStr.indexOf("</addr>", 0)).trim();
//			String addrSplit[] = addrStr.split(",");
//			Map<String, String> states = new HashMap();
//			states.put("ALABAMA", "AL");
//			states.put("ALASKA", "AK");
//			states.put("AMERICAN SAMOA", "AS");
//			states.put("ARIZONA", "AZ");
//			states.put("ARKANSAS", "AR");
//			states.put("CALIFORNIA", "CA");
//			states.put("COLORADO", "CO");
//			states.put("CONNECTICUT", "CT");
//			states.put("DELAWARE", "DE");
//			states.put("DISTRICT OF COLUMBIA", "DC");
//			states.put("FEDERATED STATES OF MICRONESIA", "FM");
//			states.put("FLORIDA", "FL");
//			states.put("GEORGIA", "GA");
//			states.put("GUAM", "GU");
//			states.put("HAWAII", "HI");
//			states.put("IDAHO", "ID");
//			states.put("ILLINOIS", "IL");
//			states.put("INDIANA", "IN");
//			states.put("IOWA", "IA");
//			states.put("KANSAS", "KS");
//			states.put("KENTUCKY", "KY");
//			states.put("LOUISIANA", "LA");
//			states.put("MAINE", "ME");
//			states.put("MARSHALL ISLANDS", "MH");
//			states.put("MARYLAND", "MD");
//			states.put("MASSACHUSETTS", "MA");
//			states.put("MICHIGAN", "MI");
//			states.put("MINNESOTA", "MN");
//			states.put("MISSISSIPPI", "MS");
//			states.put("MISSOURI", "MO");
//			states.put("MONTANA", "MT");
//			states.put("NEBRASKA", "NE");
//			states.put("NEVADA", "NV");
//			states.put("NEW HAMPSHIRE", "NH");
//			states.put("NEW JERSEY", "NJ");
//			states.put("NEW MEXICO", "NM");
//			states.put("NEW YORK", "NY");
//			states.put("NORTH CAROLINA", "NC");
//			states.put("NORTH DAKOTA", "ND");
//			states.put("NORTHERN MARIANA ISLANDS", "MP");
//			states.put("OHIO", "OH");
//			states.put("OKLAHOMA", "OK");
//			states.put("OREGON", "OR");
//			states.put("PALAU", "PW");
//			states.put("PENNSYLVANIA", "PA");
//			states.put("PUERTO RICO", "PR");
//			states.put("RHODE ISLAND", "RI");
//			states.put("SOUTH CAROLINA", "SC");
//			states.put("SOUTH DAKOTA", "SD");
//			states.put("TENNESSEE", "TN");
//			states.put("TEXAS", "TX");
//			states.put("UTAH", "UT");
//			states.put("VERMONT", "VT");
//			states.put("VIRGIN ISLANDS", "VI");
//			states.put("VIRGINIA", "VA");
//			states.put("WASHINGTON", "WA");
//			states.put("WEST VIRGINIA", "WV");
//			states.put("WISCONSIN", "WI");
//			states.put("WYOMING", "WY");
//			for (int i = 0; i < addrSplit.length; i++) {
//				
//			}
			
//		} catch (MalformedURLException e) {
//			log.info("WARN,Malformed URL Exception Parsing XDS-LAB Document: " + uri + " : " + e.toString());
//		} catch (IOException e) {
//			log.info("WARN,IO Exception Parsing XDS-LAB Document: " + uri + " : " + e.toString());
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

	public String getTypeCode() {
		return labType;
	}

	public String getAddrState() {
		return state;
	}
	
	public String getAddrZip() {
		return zip;
	}
}
