/*
 * Copyright 2004-2005 (C) University of Washington. All Rights Reserved.
 * Created on Jan 20, 2005
 * University of Washington, CIRG
 * $Id: XDSTest.java,v 1.2 2005/02/11 20:11:51 ddrozd Exp $
 */
package edu.washington.cirg.himss.xds.test;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Properties;

import javax.xml.soap.SOAPMessage;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.dom4j.Document;

import edu.washington.cirg.himss.xds.XDSConfigurationException;
import edu.washington.cirg.himss.xds.ccr.ContinuityOfCareRecord;
import edu.washington.cirg.himss.xds.requests.AdhocQueryRequest;
import edu.washington.cirg.himss.xds.requests.RequestHelper;
import edu.washington.cirg.himss.xds.requests.RetrieveExternalLinkRequest;
import edu.washington.cirg.himss.xds.requests.RetrieveExtrinsicObjectRequest;
import edu.washington.cirg.himss.xds.requests.RetrieveObjectRefRequest;
import edu.washington.cirg.himss.xds.responses.AdhocQueryResponse;
import edu.washington.cirg.himss.xds.responses.RegistryResponse;
import edu.washington.cirg.himss.xds.soap.XDSSoapRequest;
import edu.washington.cirg.himss.xds.util.XDSConstants;
import edu.washington.cirg.himss.xds.util.XDSUtil;
import edu.washington.cirg.util.Util;

/** JUnit test framework used for 2005 ConnectAThon tests
 * @author <a href="mailto:ddrozd@u.washington.edu">Dan Drozd</a>
 * @version $Revision: 1.2 $
 *
 */
public class XDSTest extends TestCase {

	private static Log log = LogFactory.getLog(XDSTest.class);

	
	
	protected String success = "Success";
	protected Properties props;
	protected String queryUrl;
	protected String repositoryUrl;
	protected String server;
	protected String db;
	protected String user;
	protected String pass;
	protected String patientId;
	protected String parentObjectId;
	protected String associationType;
	protected String folderName;
	protected String jdbcDriverClassName;
	
	/**
	 * @param arg0
	 */
	public XDSTest(String arg0) {
		super(arg0);
	}	
	protected void setUp() {
		props = new Properties();
		try {
			props.load(new FileInputStream("test.properties"));
			XDSConstants.loadConstants();
		} catch ( XDSConfigurationException e ) {
			log.fatal("Unable to load properties file");
			System.exit(1);
		} catch (FileNotFoundException e) {
			log.fatal(e);
			System.exit(1);
		} catch (IOException e) {
			log.fatal(e);
			System.exit(1);
		}
		
		//objectId = props.getProperty("ObjectId");
		//patientId = props.getProperty("PatientId");
		repositoryUrl = props.getProperty("RepositoryUrl");
		queryUrl = props.getProperty("QueryUrl");
		server = props.getProperty("Server");
		db = props.getProperty("Database");
		user = props.getProperty("User");
		pass = props.getProperty("Password");
		patientId = props.getProperty("PatientId");
		parentObjectId = props.getProperty("ParentObjectId");
		associationType = props.getProperty("AssociationType");
		folderName = props.getProperty("FolderName");
		jdbcDriverClassName = props.getProperty("JDBCDriverClassName");
		
	}
	
	protected void tearDown() {
		
	}
	
	
	public static Test suite() {
		TestSuite suite = new TestSuite();
		suite.addTest(new XDSTest("testPrintCCR"));
		//suite.addTest(new XDSTest("testPrintMetaData"));
		//suite.addTest(new XDSTest("test11802"));		
		//suite.addTest(new XDSTest("test11803"));
		return suite;
	}
	
	
	public void testPrintMetaData() {
		Connection conn = null;
		ContinuityOfCareRecord ccr = null;
		
		try {
			conn = Util.getConnection(jdbcDriverClassName, server, db, user, pass);
			assertTrue(conn != null);
			ccr = new ContinuityOfCareRecord(patientId, XDSConstants.SYMBOLIC_DOC_NAME_ROOT, conn);
			Document ccrDoc = ccr.getDocument();
			//Validate against the schema
			Util.validate(XDSConstants.CCR_MSV_COMPILED_SCHEMA, ccrDoc);
		} catch (Exception e) {
			log.error(e);
			assertTrue(false);
		} finally {
			try {
				Util.closeConnection(conn);
			} catch ( SQLException sqle ) {
				log.error(sqle);
			}
		}
		
		List xdsFileList = new ArrayList();
		xdsFileList.add(ccr);
		
		try {
			Document doc = 
				RequestHelper.generateProvideDocRequest(patientId, 
						ccr.getPatient().getPerson().getCurrentName(), 
						xdsFileList, 
						associationType, 
						parentObjectId, 
						folderName);
			XDSUtil.printDOM(System.err, doc);
		} catch ( Exception e ) {
			log.error(e);
			assertTrue(false);
		}
		
	}
	
	
	public void testPrintCCR() {
		Connection conn = null;
		ContinuityOfCareRecord ccr = null;
		try {
			conn = Util.getConnection(jdbcDriverClassName, server, db, user, pass);
			assertTrue(conn != null);
			ccr = new ContinuityOfCareRecord(patientId, XDSConstants.SYMBOLIC_DOC_NAME_ROOT, conn);
            Document ccrDoc = ccr.getDocument();

            XDSUtil.printDOM(System.err, ccrDoc);
			//Validate against the schema
			Util.validate(XDSConstants.CCR_MSV_COMPILED_SCHEMA, ccrDoc);
			log.info("No validation errors");
		} catch (Exception e) {
			log.error(e);
			assertTrue(false);
		} finally {
			try {
				Util.closeConnection(conn);
			} catch ( SQLException sqle ) {
				log.error(sqle);
			}
		}
		
		
	}
	
	public void test11741() {
		
		Connection conn = null;
		ContinuityOfCareRecord ccr = null;
		
		try {
			conn = Util.getConnection(jdbcDriverClassName, server, db, user, pass);
			assertTrue(conn != null);
			ccr = new ContinuityOfCareRecord(patientId, XDSConstants.SYMBOLIC_DOC_NAME_ROOT, conn);
			Document ccrDoc = ccr.getDocument();
			//Validate against the schema
			Util.validate(XDSConstants.CCR_MSV_COMPILED_SCHEMA, ccrDoc);
		} catch (Exception e) {
			log.error(e);
			assertTrue(false);
		} finally {
			try {
				Util.closeConnection(conn);
			} catch ( SQLException sqle ) {
				log.error(sqle);
			}
		}
		
		List xdsFileList = new ArrayList();
		xdsFileList.add(ccr);
		
		try {
			Document doc = 
				RequestHelper.generateProvideDocRequest(patientId, ccr.getPatient().getPerson().getCurrentName(), xdsFileList, null, null, null);
			if ( log.isDebugEnabled() ) {
				XDSUtil.printDOM(System.out, doc);
			}
		} catch ( Exception e ) {
			log.error(e);
			assertTrue(false);
		}

	}
	
	public void test11746() {
		Connection conn = null;
		ContinuityOfCareRecord ccr = null;
		String docUniqueId = null;
		try {
			conn = Util.getConnection(jdbcDriverClassName, server, db, user, pass);
			assertTrue(conn != null);
			ccr = 
				new ContinuityOfCareRecord(patientId, XDSConstants.SYMBOLIC_DOC_NAME_ROOT, conn);
			Document ccrDoc = ccr.getDocument();
			if ( log.isDebugEnabled() ) {
				XDSUtil.printDOM(System.err, ccrDoc);
			}
			//Validate against the schema
			Util.validate(XDSConstants.CCR_MSV_COMPILED_SCHEMA, ccrDoc);
		} catch (Exception e) {
			log.error(e);
			assertTrue(false);
		}

		List xdsFileList = new ArrayList();
		xdsFileList.add(ccr);

		try {

			Document reqDocument = 
				RequestHelper.generateProvideDocRequest(patientId, ccr.getPatient().getPerson().getCurrentName(), xdsFileList, null, null, null);
			
			if ( log.isDebugEnabled() ) {
				XDSUtil.printDOM(System.err, reqDocument);
			}
			
			XDSSoapRequest soapReq = new XDSSoapRequest(reqDocument, xdsFileList, repositoryUrl, "1.1");

			if ( log.isDebugEnabled() ) {
				soapReq.printSOAPMessage(System.err);
			}

			SOAPMessage response = soapReq.send();
			if ( log.isDebugEnabled() ) {
				response.writeTo(System.err);
			}
			RegistryResponse rr = new RegistryResponse(response);
			assertTrue(rr.wasSuccessful());
			
			if ( ! rr.wasSuccessful() ) {
				for ( Iterator iter = rr.getErrorList().iterator(); iter.hasNext();) {
					log.error((String)iter.next());
				}
			} else {
				if ( log.isDebugEnabled() ) {
					log.debug("Message successfully sent");
				}
			}
			
		} catch ( Exception e ) {
			log.error(e);
			assertTrue(false);
		}

	}

	
	public void test11752() {
		Connection conn = null;

		try {
			conn = Util.getConnection(jdbcDriverClassName, server, db, user, pass);
			assertTrue(conn != null);

			ContinuityOfCareRecord ccr = 
				new ContinuityOfCareRecord(patientId, XDSConstants.SYMBOLIC_DOC_NAME_ROOT, conn);
			Document doc = ccr.getDocument();
			//Validate against the schema

			Util.validate(XDSConstants.CCR_MSV_COMPILED_SCHEMA, doc);
			if ( log.isDebugEnabled()) {
				XDSUtil.printDOM(System.out, doc);
			}
		} catch ( Exception e ) {
			log.error(e);
			assertTrue(false);
		}
	}
	
	
	public void test11782() {
		String objectId = "NIST-1234^^^&1.3.6.1.4.1.21367.2005.1.1&ISO^PI";
		try {
			RetrieveExternalLinkRequest req =
				new RetrieveExternalLinkRequest(objectId, RetrieveExternalLinkRequest.BY_EO_ID, AdhocQueryRequest.LEAF_CLASS);

			Document reqDocument = req.generateRequest();
			XDSSoapRequest soapReq = new XDSSoapRequest(reqDocument, queryUrl, "1.1");
			SOAPMessage response = soapReq.sendMessage();
			AdhocQueryResponse qresponse = new AdhocQueryResponse(response);
			assertTrue(qresponse.wasSuccessful());
			Iterator iterator = qresponse.getExternalLinkList().iterator();
			String s;
			while ( iterator.hasNext() ) {
				s = (String)iterator.next();
				String[] sa = s.split("/");
				String localFile = sa[sa.length-1];
				XDSUtil.httpGetFile(s, localFile);
				File fLocalFile = new File(localFile);
				assertTrue(fLocalFile.exists());
				fLocalFile.delete();
			}
		} catch ( Exception e ) {
			log.error(e);
			assertTrue(false);
		}

	}
	
	public void test11801() {
		String objectId = "NIST-5678^^^&1.3.6.1.4.1.21367.2005.1.1&ISO^PI";
		try {
			RetrieveObjectRefRequest req = new RetrieveObjectRefRequest(objectId);
			Document reqDocument = req.generateRequest();
			XDSSoapRequest soapReq = new XDSSoapRequest(reqDocument, queryUrl, "1.1");
			SOAPMessage response = soapReq.sendMessage();
			AdhocQueryResponse qresponse = new AdhocQueryResponse(response);
			assertTrue(qresponse.wasSuccessful());
			
			Iterator iterator = qresponse.getErrorList().iterator();
			String error;
			while ( iterator.hasNext() ) {
				error = (String)iterator.next();
				log.error(error);
			}
		} catch ( Exception e ) {
			log.error(e);
			assertTrue(false);
		}	
	}
	
	public void test11802() {
		String[] objectRefArray = {"urn:uuid:60811113-7de8-4acb-bef9-f462c65cdca2", 
				"urn:uuid:f53fef14-713e-4f13-a295-dc37d131621d"};
		try {
			RetrieveExtrinsicObjectRequest req = 
				new RetrieveExtrinsicObjectRequest(objectRefArray, RetrieveExtrinsicObjectRequest.BY_EO_ID, AdhocQueryRequest.LEAF_CLASS);
			Document reqDocument = req.generateRequest();
			XDSSoapRequest soapReq = new XDSSoapRequest(reqDocument, queryUrl, "1.1");
			SOAPMessage response = soapReq.sendMessage();
			AdhocQueryResponse qresponse = new AdhocQueryResponse(response);
			assertTrue(qresponse.wasSuccessful());
			
			Iterator iterator = qresponse.getErrorList().iterator();
			String error;
			while ( iterator.hasNext() ) {
				error = (String)iterator.next();
				log.error(error);
			}
		} catch ( Exception e ) {
			log.error(e);
			assertTrue(false);
		}
	}
	
	
	public void test11803() {
		//String patientId = "NIST-5678^^^&1.3.6.1.4.1.21367.2005.1.1&ISO^PI"; 
		String patientId = "UWA-jsibley^^^&1.3.6.1.4.1.21367.2005.1.1&ISO^PI"; 
		try {
			RetrieveExtrinsicObjectRequest req = 
				new RetrieveExtrinsicObjectRequest(patientId, RetrieveExtrinsicObjectRequest.BY_PATIENT_ID, AdhocQueryRequest.LEAF_CLASS);
			Document reqDocument = req.generateRequest();
			XDSUtil.printDOM(System.err, reqDocument);
			XDSSoapRequest soapReq = new XDSSoapRequest(reqDocument, queryUrl, "1.1");
			SOAPMessage response = soapReq.sendMessage();
			AdhocQueryResponse qresponse = new AdhocQueryResponse(response);
			assertTrue(qresponse.wasSuccessful());
			Iterator iterator = qresponse.getErrorList().iterator();
			String error;
			while ( iterator.hasNext() ) {
				error = (String)iterator.next();
				log.error(error);
			}
		} catch ( Exception e ) {
			log.error(e);
			assertTrue(false);
		}
	}
	
	
	public void test11804() {
		String objectId = "urn:uuid:60811113-7de8-4acb-bef9-f462c65cdca2"; 
		try {
			RetrieveExternalLinkRequest req =
				new RetrieveExternalLinkRequest(objectId, RetrieveExternalLinkRequest.BY_EO_ID, AdhocQueryRequest.LEAF_CLASS);

			Document reqDocument = req.generateRequest();
			XDSSoapRequest soapReq = new XDSSoapRequest(reqDocument, queryUrl, "1.1");
			SOAPMessage response = soapReq.sendMessage();
			AdhocQueryResponse qresponse = new AdhocQueryResponse(response);
			assertTrue(qresponse.wasSuccessful());
			Iterator iterator = qresponse.getErrorList().iterator();
			String error;
			while ( iterator.hasNext() ) {
				error = (String)iterator.next();
				log.error(error);
			}

		} catch ( Exception e ) {
			log.error(e);
			assertTrue(false);
		}
	}
	
	public void test11805() {
		String objectId = "urn:uuid:60811113-7de8-4acb-bef9-f462c65cdca2"; 
		try {
			RetrieveExternalLinkRequest req =
				new RetrieveExternalLinkRequest(objectId, RetrieveExternalLinkRequest.BY_EO_ID, AdhocQueryRequest.LEAF_CLASS);
			Document reqDocument = req.generateRequest();
			XDSSoapRequest soapReq = new XDSSoapRequest(reqDocument, queryUrl, "1.1");
			SOAPMessage response = soapReq.sendMessage();
			AdhocQueryResponse qresponse = new AdhocQueryResponse(response);
			assertTrue(qresponse.wasSuccessful() == true);
			Iterator iterator = qresponse.getErrorList().iterator();
			String error;
			while ( iterator.hasNext() ) {
				error = (String)iterator.next();
				log.error(error);
			}
			iterator = qresponse.getExternalLinkList().iterator();
			String fileUrl;
			while ( iterator.hasNext() ) {
				fileUrl = (String)iterator.next();
				String[] sa = fileUrl.split("/");
				String localFile = sa[sa.length-1];
				XDSUtil.httpGetFile(fileUrl, localFile);
				File fLocalFile = new File(localFile);
				assertTrue(fLocalFile.exists());
				fLocalFile.delete();
			}
			
		} catch ( Exception e ) {
			log.error(e);
			assertTrue(false);
		}
	}	
}