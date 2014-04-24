/*
 * Copyright 2004-2005 (C) University of Washington. All Rights Reserved.
 * Created on Dec 28, 2004
 * University of Washington, CIRG
 * $Id: ContinuityOfCareRecord.java,v 1.6 2005/02/21 16:43:25 ddrozd Exp $
 */
package edu.washington.cirg.himss.xds.ccr;

import java.io.PrintStream;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.DocumentFactory;
import org.dom4j.Element;
import org.dom4j.QName;
import org.dom4j.io.OutputFormat;
import org.dom4j.io.XMLWriter;

import edu.washington.cirg.himss.xds.XDSException;
import edu.washington.cirg.himss.xds.XDSFile;
import edu.washington.cirg.himss.xds.util.IdentifierManager;
import edu.washington.cirg.himss.xds.util.XDSConstants;



/** Java implementation of the Continuity of Care (CCR) document object model
 * @author <a href="mailto:ddrozd@u.washington.edu">Dan Drozd</a>
 * @version $Revision: 1.6 $
 *
 */


public class ContinuityOfCareRecord extends XDSFile {

	private static Log log = LogFactory.getLog(ContinuityOfCareRecord.class);

	private static final QName CCR_QNAME = 
		DocumentFactory.getInstance().createQName("ContinuityOfCareRecord", CCRConstants.XMLNS_CCR);	
	private static final QName SOURCE_QNAME = 
		DocumentFactory.getInstance().createQName("Source", CCRConstants.XMLNS_CCR);
	
	
	private ExactDateTime ccrCreationDateTime;
	private Patient patient;
	private List actorList;
	private Recipient recipient;
	private Purpose purpose;
	private Body body;
	private int dataObjectIdCount = 1;
	private Object lock = new Object();
	
	private Document document;
	private String docUniqueId;
	

	/**
	 * @param patient
	 * @param actorList
	 * @param recipient
	 * @param purpose
	 * @param body
	 */
	public ContinuityOfCareRecord(Patient patient, List actorList, 
			Recipient recipient, Purpose purpose, Body body, String uuid) {
		this(uuid);
		this.patient = patient;
		setCCRDataObjectId(patient);
		this.actorList = actorList;
		Iterator iterator = actorList.iterator();
		while ( iterator.hasNext() ) {
			Actor a = (Actor)iterator.next();
			setCCRDataObjectId(a);
		}
		this.recipient = recipient;
		setCCRDataObjectId(recipient);
		this.purpose = purpose;
		this.body = body;
		body.setCCRDataObjectIds(this);

	}
	
	public ContinuityOfCareRecord(String uuid) {
		super(XDSConstants.CCR_DE_MIME_TYPE, uuid);
		ccrCreationDateTime = 
			new ExactDateTime(new Date(), "CCR Creation DateTime");	
		this.docUniqueId = IdentifierManager.getUniqueId();
	}
	
	public Patient getPatient() {
		return patient;
	}
	
	public String getDocUniqueId() {
		return docUniqueId;
	}
	
	public ContinuityOfCareRecord(String patientId, String uuid, Connection conn) 
	throws SQLException, XDSException, DocumentException {
		this(uuid);
		try {			
			this.patient = new Patient(patientId, conn);
			setCCRDataObjectId(patient);
			
			this.actorList = new ArrayList();
			Person self = null;
			try {
				/*
				 * Make a shallow copy, we need to add a CCRObjectId to this
				 * person as Actor, but not the original
				 */
				
				self = (Person)patient.getPerson().clone();
			} catch ( CloneNotSupportedException e ) {
				//We should not get here, as Person implements Cloneable
				log.error(e);
				throw new XDSException(e);
			}
			//We are always actors in our own CCR
			InformationSystem uwpchr = 
				new InformationSystem(XDSConstants.CCR_INFO_SYSTEM_NAME, 
						XDSConstants.CCR_INFO_SYSTEM_TYPE, 
						XDSConstants.CCR_INFO_SYSTEM_VERSION);
			
			Organization org =
				new Organization(XDSConstants.CCR_ORGANIZATION_NAME,
						new Specialty(XDSConstants.CCR_ORGANIZATION_SPECIALTY, null));
			
			actorList.add(self);
			actorList.add(uwpchr);
			actorList.add(org);
			Iterator iterator = actorList.iterator();
			while ( iterator.hasNext() ) {
				Actor a = (Actor)iterator.next();
				a.setIsActor(true);
				setCCRDataObjectId(a);
			}
			this.recipient = new Recipient(new InformationSystem(CCRConstants.RECIPIENT_SYSTEM_NAME, null, null));
			setCCRDataObjectId(recipient);
			this.purpose = new Purpose(XDSConstants.CCR_PURPOSE_DESCRIPTION, 
					new Code(XDSConstants.CCR_PURPOSE_CODE_SYSTEM, 
							XDSConstants.CCR_PURPOSE_CODE_VALUE, 
							XDSConstants.CCR_PURPOSE_CODE_VERSION));
			
			this.body = new Body();
			List insuranceList = Insurer.getInsurers(patientId, conn);
			body.setInsuranceList(insuranceList);
			//We don't currently store advance directives
			//List advanceDirectiveList = AdvanceDirective.getDirectives(patientId, conn);
			//body.setAdvanceDirectiveList(advanceDirectiveList);
			List supportProviderList = SupportProvider.getSupportProviders(patientId, conn);
			body.setSupportProviderList(supportProviderList);
			List problemList = Problem.getProblems(patientId, conn);
			body.setProblemList(problemList);
			List alertList = Alert.getAlerts(patientId, conn);
			body.setAlertList(alertList);
			List medList = Medication.getMedications(patientId, conn);
			body.setMedicationList(medList);
			List immunizationList = Immunization.getImmunizations(patientId, conn);
			body.setImmunizationList(immunizationList);
			List resultList = Result.getResults(patientId, conn);
			body.setResultList(resultList);
			List familyHistoryList = History.getHistory(patientId, conn);
			body.setFamilyHistoryList(familyHistoryList);
			body.setCCRDataObjectIds(this);
			generateDocument();
		} catch ( SQLException sqle ) {
			log.error(sqle);
			throw sqle;
		} catch ( XDSException xdse ) {
			log.error(xdse);
			throw xdse;
		} catch ( DocumentException de ) {
			log.error(de);
			throw de;
		}
	}
	
	public void addActor(Actor actor) {
		actorList.add(actor);
		setCCRDataObjectId(actor);
	}

	public Document getDocument() {
		return document;
	}
	
	private void generateDocument()
		throws DocumentException {
				
		Element root = 
			DocumentFactory.getInstance().createElement(CCR_QNAME)
			.addAttribute("xmlns:xsi", "http://www.w3.org/2001/XMLSchema-instance")
			//.addAttribute("xsi:schemaLocation", "urn:astm-org:CCR IHE-CCR-DemoSchema.20040929.xsd")
			.addAttribute("SCode","")
			.addAttribute("ID", docUniqueId)
			.addAttribute("Status","");
		
		root.add(ccrCreationDateTime.getElement());
		root.add(patient.getElement());

		
		
		Element sourceElement = 
			DocumentFactory.getInstance().createElement(SOURCE_QNAME);
		
		Iterator iterator = actorList.iterator();
		
		while ( iterator.hasNext() ) {
			Actor actor = (Actor)iterator.next();
			sourceElement.add(actor.getElement());
		}
		root.add(sourceElement);
		iterator = null;
		root.add(recipient.getElement());
		root.add(purpose.getElement());

		root.add(body.getElement());
		


		document = DocumentFactory.getInstance().createDocument(root);
		

		super.setDataSource(new CCRDataSource(document));
	}
	
	public void printRequest(PrintStream out) {

		try {//this is for debugging....
//			 Pretty print the document to System.out
				OutputFormat format = OutputFormat.createPrettyPrint();
				XMLWriter xmlwriter = new XMLWriter( System.out, format );
				xmlwriter.write( document );
			} catch ( Exception e ) {
				e.printStackTrace();
			}
	}
	
	public void setCCRDataObjectId(CCRDataObject dataObject) {
		
		String idStr = "AA";
		int idCount;

		synchronized ( lock ) {
			idCount = dataObjectIdCount;
			dataObjectIdCount++;
		}
		
		if ( idCount < 10 ) {
			idStr += "000" + idCount;
		} else if ( idCount < 100 ) {
			idStr += "00" + idCount;			
		} else if ( idCount < 1000 ) {
			idStr += "0" + idCount;
		} else {
			idStr += idCount;
		}
		
		dataObject.setCCRDataObjectId(idStr);

	}
}
