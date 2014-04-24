/*
 * Copyright 2004-2005 (C) University of Washington. All Rights Reserved.
 * Created on Dec 28, 2004
 * University of Washington, CIRG
 * $Id: Body.java,v 1.4 2005/02/09 01:51:11 ddrozd Exp $
 */
package edu.washington.cirg.himss.xds.ccr;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.dom4j.DocumentFactory;
import org.dom4j.Element;
import org.dom4j.QName;

/** Java implementation of the Continuity of Care (CCR) document object model
 * @author <a href="mailto:ddrozd@u.washington.edu">Dan Drozd</a>
 * @version $Revision: 1.4 $
 *
 */
public class Body {
	
	private static Log log = LogFactory.getLog(Body.class);
	
	private static final QName BODY_QNAME = 
		DocumentFactory.getInstance().createQName("Body", CCRConstants.XMLNS_CCR);	
	private static final QName INSURANCE_QNAME = 
		DocumentFactory.getInstance().createQName("Insurance", CCRConstants.XMLNS_CCR);	
	private static final QName ADVANCE_DIRECTIVES_QNAME = 
		DocumentFactory.getInstance().createQName("AdvanceDirectives", CCRConstants.XMLNS_CCR);	
	private static final QName SUPPORT_QNAME = 
		DocumentFactory.getInstance().createQName("Support", CCRConstants.XMLNS_CCR);	
	private static final QName PROBLEMS_QNAME = 
		DocumentFactory.getInstance().createQName("Problems", CCRConstants.XMLNS_CCR);	
	private static final QName SOCIAL_HISTORY_QNAME = 
		DocumentFactory.getInstance().createQName("SocialHistory", CCRConstants.XMLNS_CCR);	
	private static final QName ALERTS_QNAME = 
		DocumentFactory.getInstance().createQName("Alerts", CCRConstants.XMLNS_CCR);	
	private static final QName MEDICATIONS_QNAME = 
		DocumentFactory.getInstance().createQName("Medications", CCRConstants.XMLNS_CCR);	
	private static final QName IMMUNIZATIONS_QNAME = 
		DocumentFactory.getInstance().createQName("Immunizations", CCRConstants.XMLNS_CCR);	
	private static final QName RESULTS_QNAME = 
		DocumentFactory.getInstance().createQName("Results", CCRConstants.XMLNS_CCR);	
	private static final QName FAMILY_HISTORY_QNAME = 
		DocumentFactory.getInstance().createQName("FamilyHistory", CCRConstants.XMLNS_CCR);	

	
	private List insuranceList;//Insurer
	private List advanceDirectiveList;//AdvanceDirective
	private List supportList;//Support
	private List problemList;//Problem
	private List socialHistoryList;//RiskFactor
	private List alertList;//Alert
	private List medicationList;//Medication
	private List immunizationList;
	private List resultList;
	private List familyHistoryList;
	
	
	public Body() {
		
		this.insuranceList = new ArrayList();
		this.advanceDirectiveList = new ArrayList();
		this.supportList = new ArrayList();
		this.problemList = new ArrayList();
		this.socialHistoryList = new ArrayList();
		this.alertList = new ArrayList();
		this.medicationList = new ArrayList();
		this.immunizationList = new ArrayList();
		this.resultList = new ArrayList();
		this.familyHistoryList = new ArrayList();
	}
	
	public void setCCRDataObjectIds(ContinuityOfCareRecord ccr) {
		List[] listArray = {supportList, problemList, socialHistoryList};

		
		
		Iterator iterator;
		
		iterator = insuranceList.iterator();
		while ( iterator.hasNext() ) {
			Insurer ins = (Insurer)iterator.next();
			ccr.setCCRDataObjectId(ins);
			InsuranceProvider provider = ins.getProvider();
			ccr.setCCRDataObjectId(provider);
		}
		iterator = null;

		iterator = advanceDirectiveList.iterator();
		while ( iterator.hasNext() ) {
			AdvanceDirective ad = (AdvanceDirective)iterator.next();
			ccr.setCCRDataObjectId(ad);
			Directive directive = ad.getDirective();
			ccr.setCCRDataObjectId(directive);
		}
		iterator = null;

		iterator = alertList.iterator();
		while ( iterator.hasNext() ) {
			Alert alert = (Alert)iterator.next();
			ccr.setCCRDataObjectId(alert);
			Agent agent = alert.getAgent();
			ccr.setCCRDataObjectId(agent);
			if ( agent instanceof ProductAgent ) {
				ProductAgent pa = (ProductAgent)agent;
				List products = pa.getProducts();
				Iterator i2 = products.iterator();
				while ( i2.hasNext() ) {
					Product product = (Product)i2.next();
					ccr.setCCRDataObjectId(product);
				}
				i2 = null;
			}
		}
		iterator = null;

		iterator = medicationList.iterator();
		while ( iterator.hasNext() ) {
			Medication med = (Medication)iterator.next();
			ccr.setCCRDataObjectId(med);
			Product product = med.getProduct();
			ccr.setCCRDataObjectId(product);
		}
		iterator = null;

		iterator = immunizationList.iterator();
		while ( iterator.hasNext() ) {
			Immunization imm = (Immunization)iterator.next();
			ccr.setCCRDataObjectId(imm);
			Product product = imm.getProduct();
			ccr.setCCRDataObjectId(product);
		}
		iterator = null;
		
		
		for ( int i = 0; i < listArray.length; i++ ) {
			iterator = listArray[i].iterator();
			while ( iterator.hasNext() ) {
				CCRDataObject ccrdo = (CCRDataObject)iterator.next();
				ccr.setCCRDataObjectId(ccrdo);
			}
			iterator = null;
		}
		
		iterator = resultList.iterator();
		while ( iterator.hasNext() ) {
			Result result = (Result)iterator.next();
			ccr.setCCRDataObjectId(result);
			Test test = result.getTest();
			ccr.setCCRDataObjectId(test);
		}

		iterator = familyHistoryList.iterator();
		while ( iterator.hasNext() ) {
			History history = (History)iterator.next();
			ccr.setCCRDataObjectId(history);
			Problem problem = history.getProblem();
			ccr.setCCRDataObjectId(problem);
            FamilyMember member = history.getFamilyMember();
            ccr.setCCRDataObjectId(member);
		}

		
	}
	
	public void addAdvanceDirective(AdvanceDirective ad) {
		advanceDirectiveList.add(ad);
	}
	public void addProblem(Problem problem) {
		problemList.add(problem);
	}
	public void addRiskFactor(RiskFactor rf) {
		socialHistoryList.add(rf);
	}
	public void addMedication(Medication med) {
		medicationList.add(med);
	}
	public void addAlert(Alert alert) {
		alertList.add(alert);
	}
	public void addInsurer(Insurer insurer) {
		insuranceList.add(insurer);
	}

	public void addImmunization(Immunization imm) {
		immunizationList.add(imm);
	}
	
	public void addSupportProvider(SupportProvider sp) {
		supportList.add(sp);
	}

	public void addResult(Result result) {
		resultList.add(result);
	}
	
	public void addHistory(History history) {
		familyHistoryList.add(history);
	}
	
	
	public void setInsuranceList(List insuranceList) {
		this.insuranceList = insuranceList;
	}
	public void setSupportProviderList(List supportList) {
		this.supportList = supportList;
	}
	public void setProblemList(List problemList) {
		this.problemList = problemList;
	}
	public void setAlertList(List alertList) {
		this.alertList = alertList;
	}
	public void setMedicationList(List medicationList) {
		this.medicationList = medicationList;
	}
	public void setImmunizationList(List immunizationList) {
		this.immunizationList = immunizationList;
	}
	public void setResultList(List resultList) {
		this.resultList = resultList;
	}
	public void setFamilyHistoryList(List familyHistoryList) {
		this.familyHistoryList = familyHistoryList;
	}
	
	public Element getElement() {

		Element element = 
			DocumentFactory.getInstance().createElement(BODY_QNAME);
		if ( insuranceList.size() > 0 ) {
			Element insuranceElement =
				DocumentFactory.getInstance().createElement(INSURANCE_QNAME);
			addListElements(element, insuranceElement, insuranceList);
		}
		if (  advanceDirectiveList.size() > 0 ) {
			Element advDirectivesElement =
				DocumentFactory.getInstance().createElement(ADVANCE_DIRECTIVES_QNAME);
			addListElements(element, advDirectivesElement, advanceDirectiveList);
		}
		if ( supportList.size() > 0 ) {
			Element supportElement =
				DocumentFactory.getInstance().createElement(SUPPORT_QNAME);
			addListElements(element, supportElement, supportList);
		}
		if ( problemList.size() > 0 ) {
			Element problemsElement =
				DocumentFactory.getInstance().createElement(PROBLEMS_QNAME);
			addListElements(element, problemsElement, problemList);
		}
		if ( familyHistoryList.size() > 0 ) {
			Element familyHistoryElement =
				DocumentFactory.getInstance().createElement(FAMILY_HISTORY_QNAME);
			addListElements(element, familyHistoryElement, familyHistoryList);
		}
		if ( socialHistoryList.size() > 0 ) {
			Element socialHistoryElement =
				DocumentFactory.getInstance().createElement(SOCIAL_HISTORY_QNAME);
			addListElements(element, socialHistoryElement, socialHistoryList);
		}
		if ( alertList.size() > 0 ) {
			Element alertsElement =
				DocumentFactory.getInstance().createElement(ALERTS_QNAME);
			addListElements(element, alertsElement, alertList);
		}
		if ( medicationList.size() > 0 ) {
			Element medicationsElement =
				DocumentFactory.getInstance().createElement(MEDICATIONS_QNAME);
			addListElements(element, medicationsElement, medicationList);
		}

		if ( immunizationList.size() > 0 ) {
			Element immunizationsElement =
				DocumentFactory.getInstance().createElement(IMMUNIZATIONS_QNAME);
			addListElements(element, immunizationsElement, immunizationList);
		}
		
		if ( resultList.size() > 0 ) {
			Element resultsElement =
				DocumentFactory.getInstance().createElement(RESULTS_QNAME);
			addListElements(element, resultsElement, resultList);
		}

		return element;
	}
	
	private void addListElements(Element parent, Element child, List list) {
		Iterator iterator = list.iterator();
		while ( iterator.hasNext() ) {
			CCRDataObject ccrdo = (CCRDataObject)iterator.next();
			child.add(ccrdo.getElement());
		}
		parent.add(child);
	}
	

}
