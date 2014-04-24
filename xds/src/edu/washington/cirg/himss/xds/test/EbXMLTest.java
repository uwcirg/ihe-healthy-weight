/*
 * Created on Jan 19, 2005
 * University of Washington, CIRG
 */


package edu.washington.cirg.himss.xds.test;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Properties;

import javax.activation.DataHandler;
import javax.xml.registry.BulkResponse;
import javax.xml.registry.BusinessLifeCycleManager;
import javax.xml.registry.BusinessQueryManager;
import javax.xml.registry.Connection;
import javax.xml.registry.ConnectionFactory;
import javax.xml.registry.JAXRException;
import javax.xml.registry.JAXRResponse;
import javax.xml.registry.RegistryService;
import javax.xml.registry.infomodel.Classification;
import javax.xml.registry.infomodel.ClassificationScheme;
import javax.xml.registry.infomodel.Concept;
import javax.xml.registry.infomodel.ExternalIdentifier;
import javax.xml.registry.infomodel.ExtrinsicObject;
import javax.xml.registry.infomodel.RegistryObject;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.dom4j.Document;
import org.dom4j.DocumentFactory;
import org.dom4j.Element;

import edu.washington.cirg.himss.xds.ccr.CCRDataSource;
import edu.washington.cirg.himss.xds.util.XDSConstants;

/**
 * @author ddrozd
 *
 */
public class EbXMLTest {
	private static Log log = LogFactory.getLog(EbXMLTest.class);

	private Connection conn;
	private BusinessLifeCycleManager blcm;
	private BusinessQueryManager bqm;
	private RegistryService rs;
	private Document doc;
	private Map mConcepts;
	private Map mClassificationSchemes;
	
	
	private static final String XDS_DOCUMENT_ENTRY_PATIENT_ID = "XDSDocument.patientId";
	private static final String XDS_DOCUMENT_ENTRY_UNIQUE_ID = "XDSDocument.uniqueId";
	
	
	private static final String myPatientId = "UWA-TEST01^^&&38383.48984.5884";
	private static final String myDEUniqueId = "19293.394995";
	
	
	public static void main(String[] args) throws Exception {
		
		EbXMLTest t = new EbXMLTest();
		t.doIt();
	}
	
	/**
	 * 
	 */
	public EbXMLTest() throws JAXRException {
		super();		
		Properties props = new Properties();
		props.setProperty("javax.xml.registry.queryManagerURL", "http://hcxw2k1.nist.gov:8080/xdsServices/registry/soap/portals/query");
		props.setProperty("javax.xml.registry.lifeCycleManagerURL", "http://hcxw2k1.nist.gov:8080/xdsServices/registry/soap/portals/repository");
		props.setProperty("javax.xml.registry.ConnectionFactoryClass", "com.sun.xml.registry.ebxml.ConnectionFactoryImpl");

		try {
		ConnectionFactory factory =
			ConnectionFactory.newInstance();
		factory.setProperties(props);
		this.conn =
			(com.sun.xml.registry.ebxml.ConnectionImpl)factory.createConnection();
		this.rs = 
			conn.getRegistryService();
		this.bqm = 
			rs.getBusinessQueryManager();
		this.blcm = 
			rs.getBusinessLifeCycleManager();
		} catch (JAXRException e ) {
			log.error(e);
			throw e; 
		}
		System.out.println("Got connection");
	
		mConcepts = new HashMap();
		mClassificationSchemes = new HashMap();
		doc = generateDoc();
		init();
	}
	
	
	public void doIt() {
		
		try {
			//RegistryObject ro = 
			//bqm.getRegistryObject("urn:uuid:987d2071-1c96-42a1-b35e-f021b7376a13");
			//System.out.println(ro.toXML());
			
			//Collection objects = new ArrayList();
			//Key k = blcm.createKey("urn:uuid:987d2071-1c96-42a1-b35e-f021b7376a13");
			//objects.add(k);
			//BulkResponse response = bqm.getRegistryObjects(objects, LifeCycleManager.EXTRINSIC_OBJECT);
			
			ExtrinsicObject docExtrinsicObject = getDocumentEntryObject(doc);
			
			
			System.out.println(docExtrinsicObject.toXML());
			
			/*
			Concept cFolder = (Concept)mConcepts.get("XDSFolder");
			if ( cFolder == null ) {
				log.error("Null folder concept????");
			}
			Classification c = blcm.createClassification(cFolder);
			c.setName(blcm.createInternationalString("MyFolderNow"));
			Concept cHasMemberConcept = (Concept)mConcepts.get("HasMember");
			Association assoc = blcm.createAssociation(docExtrinsicObject, cHasMemberConcept);
			c.addAssociation(assoc);
			
			System.out.println(c.getName());
			System.out.println(c.getValue());
			//System.out.println(c.toXML());
			*/
			conn.close();
			
		} catch (JAXRException e) {
			log.error(e);
		}
	}
	
	
	public ExtrinsicObject getDocumentEntryObject(Document doc) throws JAXRException{

		DataHandler handler = new DataHandler(new CCRDataSource(doc));
		
		ExtrinsicObject eo = blcm.createExtrinsicObject(handler);
		eo.setMimeType("application/x-ccr-xml");
		eo.setKey(blcm.createKey("theDocument"));
		eo.setName(blcm.createInternationalString("test 1234"));
		
		Concept concept = (Concept)mConcepts.get(XDSConstants.XDS_DOCUMENT_ENTRY_ELEMENT);
		Classification classification = blcm.createClassification(concept);
		Collection classifications = new ArrayList();
		classifications.add(classification);
		eo.setClassifications(classifications);
		
		
		ClassificationScheme classScheme = 
			(ClassificationScheme)mClassificationSchemes.get(XDS_DOCUMENT_ENTRY_PATIENT_ID);
		if ( classScheme != null ) {
			ExternalIdentifier eiPatientId = 
				blcm.createExternalIdentifier(classScheme, XDS_DOCUMENT_ENTRY_PATIENT_ID, myPatientId);
			eo.addExternalIdentifier(eiPatientId);
		} else {
			log.error("Null classificationScheme returned");
		}
		ClassificationScheme classScheme2 = 
			(ClassificationScheme)mClassificationSchemes.get(XDS_DOCUMENT_ENTRY_UNIQUE_ID);
		ExternalIdentifier eiUniqueId = 
			blcm.createExternalIdentifier(classScheme2, XDS_DOCUMENT_ENTRY_UNIQUE_ID, myDEUniqueId);
		eo.addExternalIdentifier(eiUniqueId);
		
		
		return eo;
	}

	
	public Document generateDoc() {
		Element root = 
			DocumentFactory.getInstance().createElement("ContinuityOfCareRecord")
			.addAttribute("xmlns:xsi", "http://www.w3.org/2001/XMLSchema-instance")
			.addAttribute("xsi:schemaLocation", "urn:astm-org:CCR IHE-CCR-DemoSchema.20040929.xsd")
			.addAttribute("SCode","")
			.addAttribute("ID", "4994959.6969997")
			.addAttribute("Status","");
		Document doc = DocumentFactory.getInstance().createDocument(root);
		
		return doc;
	}
	
	
	public void handleError(BulkResponse response) throws JAXRException {

		if ( response != null ) {
			System.out.println("Error getting response");
			Collection exceptions = response.getExceptions();
			Iterator i = exceptions.iterator();
			while ( i.hasNext()) {
				Exception e = (Exception)i.next();
				e.printStackTrace();
			}
			
		}
	}
	
	
	private void init() throws JAXRException {
		
		Collection names = new ArrayList();
		names.add(XDSConstants.XDS_FOLDER_ELEMENT);
		names.add(XDSConstants.XDS_DOCUMENT_ENTRY_ELEMENT);
		names.add(XDSConstants.XDS_SUBMISSION_SET_ELEMENT);
		names.add(XDSConstants.XDS_DOCUMENT_ENTRY_STUB_ELEMENT);
		names.add(XDSConstants.XDS_ASSOCIATION_TYPE_APND);
		names.add(XDSConstants.XDS_ASSOCIATION_TYPE_RPLC);
		names.add(XDSConstants.XDS_ASSOCIATION_TYPE_XFRM);
		names.add(XDSConstants.UUID_HAS_MEMBER_ASSN);
		
		BulkResponse response = bqm.findConcepts(null,names,null,null,null);

		Collection c = response.getCollection();
		Iterator i = c.iterator();
		while ( i.hasNext() ) {
			Concept concept = (Concept)i.next();
			mConcepts.put(concept.getName().toString(), concept);
		}
		names = null;
		response = null;
		i = null;
		c = null;
		
		names = new ArrayList();
//		names.add(XDSConstants.XDS_DOCUMENT_ENTRY_PATIENT_ID_ELEMENT);
//	names.add(XDSConstants.XDS_DOCUMENT_ENTRY_UNIQUE_ID_ELEMENT);
		names.add(XDS_DOCUMENT_ENTRY_PATIENT_ID);
		names.add(XDS_DOCUMENT_ENTRY_UNIQUE_ID);
		names.add(XDSConstants.XDS_SUBMISSION_SET_SOURCE_ID_ELEMENT);
		names.add(XDSConstants.XDS_SUBMISSION_SET_UNIQUE_ID_ELEMENT);
		names.add(XDSConstants.XDS_FOLDER_UNIQUE_ID_ELEMENT);
		names.add(XDSConstants.XDS_FOLDER_PATIENT_ID_ELEMENT);
		
		response = bqm.findClassificationSchemes(null,names,null,null);

		c = response.getCollection();
		i = c.iterator();
		while ( i.hasNext() ) {
			ClassificationScheme cs = (ClassificationScheme)i.next();
			mClassificationSchemes.put(cs.getName().toString(), cs);
		}
		
		
	}
	
	public RegistryObject getOne(BulkResponse response) throws JAXRException {

		if (response.getStatus() != JAXRResponse.STATUS_SUCCESS ) {
			handleError(response);
		}
		
		Collection responseObjects = response.getCollection();
		
		if ( responseObjects.size() == 0 ) {
			return null;
		} else if ( responseObjects.size() > 1 ) {
			return null;
		}
		
		Iterator i = responseObjects.iterator();
		RegistryObject obj = null;
		
		obj = (RegistryObject)i.next();
		return obj;
	}
}


