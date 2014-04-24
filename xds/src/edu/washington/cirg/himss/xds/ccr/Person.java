/*
 * Copyright 2004-2005 (C) University of Washington. All Rights Reserved.
 * Created on Dec 28, 2004
 * University of Washington, CIRG
 * $Id: Person.java,v 1.5 2005/02/09 01:51:11 ddrozd Exp $
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
 * @version $Revision: 1.5 $
 *
 */
public class Person extends Actor implements Cloneable {

	private static Log log = LogFactory.getLog(Person.class);

	private static final QName PERSON_QNAME = 
		DocumentFactory.getInstance().createQName("Person", CCRConstants.XMLNS_CCR);	

	private static final QName NAME_QNAME = 
		DocumentFactory.getInstance().createQName("Name", CCRConstants.XMLNS_CCR);	
	private static final QName DOB_QNAME = 
		DocumentFactory.getInstance().createQName("DateOfBirth", CCRConstants.XMLNS_CCR);	
	private static final QName GENDER_QNAME = 
		DocumentFactory.getInstance().createQName("Gender", CCRConstants.XMLNS_CCR);	
	
	
	private Name birthName;
	private Name currentName;
	private ApproximateDateTime dateOfBirth;
	private String gender;
	private List languageList;
	
	public Object clone() throws CloneNotSupportedException {
		return super.clone();
	}
	
	public Person(Name currentName, ApproximateDateTime dateOfBirth,
			String gender, List languageList) {
		super();
		this.currentName = currentName;
		this.dateOfBirth = dateOfBirth;
		this.gender = gender;
		this.languageList = languageList;
	}
	
	public Person(Name currentName) {
		this();
		this.currentName = currentName;
	}
	
	public Person() {
		super();
		languageList = new ArrayList();
	}
	
	public Name getCurrentName() {
		return currentName;
	}
	
	
	public void setDateOfBirth(ApproximateDateTime dateOfBirth) {
		this.dateOfBirth = dateOfBirth;
	}
	
	public void setGender(String gender) {
		this.gender = gender;
	}
	
	public void setBirthName(Name birthName) {
		this.birthName = birthName;
	}
	
	public void addLanguage(Language lang) {
		languageList.add(lang);
	}
	
	public Element getElement() {
		
		Element personElement = DocumentFactory.getInstance().createElement(PERSON_QNAME);	

		Element nameElement = DocumentFactory.getInstance().createElement(NAME_QNAME);
		if ( birthName != null ) {
			nameElement.add(birthName.getElement());
		} else {
			nameElement.addElement(Name.BIRTHNAME_QNAME);
		}
		
		nameElement.add(currentName.getElement());
		personElement.add(nameElement);
		
		if ( dateOfBirth != null ) {
			Element dobElement = 
				DocumentFactory.getInstance().createElement(DOB_QNAME);
			dobElement.add(dateOfBirth.getElement());
			personElement.add(dobElement);
		}
		if ( gender != null && ! gender.trim().equals("")) {
			Element genderElement = 
				DocumentFactory.getInstance().createElement(GENDER_QNAME);
			genderElement.addText(gender);
			personElement.add(genderElement);			
		}
		
		Iterator iterator = languageList.iterator();
		
		while ( iterator.hasNext()) {
			Language language = (Language)iterator.next();
			personElement.add(language.getElement());
		}
		
		if ( isActor ) {
			Element actorElement = 
				DocumentFactory.getInstance().createElement(ACTOR_QNAME);

			actorElement.add(ccrDataObjectId.getElement());
			actorElement.add(personElement);
			return actorElement;
		} else {
			return personElement;
		}
	}
	
}
