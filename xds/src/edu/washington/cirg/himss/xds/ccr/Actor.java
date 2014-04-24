/*
 * Copyright 2004-2005 (C) University of Washington. All Rights Reserved.
 * Created on Dec 28, 2004
 * University of Washington, CIRG
 * $Id: Actor.java,v 1.3 2005/01/26 19:45:44 ddrozd Exp $
 */
package edu.washington.cirg.himss.xds.ccr;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.dom4j.DocumentFactory;
import org.dom4j.QName;


/** Java implementation of the Continuity of Care (CCR) document object model
 * @author <a href="mailto:ddrozd@u.washington.edu">Dan Drozd</a>
 * @version $Revision: 1.3 $
 *
 */
public abstract class Actor extends CCRDataObject {

	private static Log log = LogFactory.getLog(Actor.class);
		
	protected static final QName ACTOR_QNAME = 
		DocumentFactory.getInstance().createQName("Actor", CCRConstants.XMLNS_CCR);	
	protected static final QName SPECIALTY_QNAME = 
		DocumentFactory.getInstance().createQName("Specialty", CCRConstants.XMLNS_CCR);	
	
	protected Specialty specialty;
	protected List telephoneList;
	protected List addressList;
	protected boolean isActor = false;

	public Actor() {
		telephoneList = new ArrayList();
		addressList = new ArrayList();
	}
	
	public Actor(Specialty specialty) {
		this();
		this.specialty = specialty;
	}
	
	public void addSpecialty(Specialty specialty) {
		this.specialty = specialty;
	}
	
	public void setIsActor(boolean isActor) {
		this.isActor = isActor;
	}
	
	public void addTelephoneNumber(TelephoneNumber number) {
		telephoneList.add(number);
	}
	
	public void addAddress(Address address) {
		addressList.add(address);
	}
	
	public List getTelephoneNumberList() {
		return telephoneList;
	}
	
	public List getAddressList() {
		return addressList;
	}
	
}