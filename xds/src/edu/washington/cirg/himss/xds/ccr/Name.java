/*
 * Copyright 2004-2005 (C) University of Washington. All Rights Reserved.
 * Created on Dec 28, 2004
 * University of Washington, CIRG
 * $Id: Name.java,v 1.4 2005/02/09 01:51:11 ddrozd Exp $
 */
package edu.washington.cirg.himss.xds.ccr;

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
public class Name {
	
	private static Log log = LogFactory.getLog(Name.class);

	
	public static final int BIRTHNAME_TYPE = 1;
	public static final int CURRENTNAME_TYPE = 2;
	
	public static final QName BIRTHNAME_QNAME = 
		DocumentFactory.getInstance().createQName("BirthName", CCRConstants.XMLNS_CCR);	
	
	private static final QName CURRENTNAME_QNAME = 
		DocumentFactory.getInstance().createQName("CurrentName", CCRConstants.XMLNS_CCR);	

	private static final QName GIVEN_NAME_QNAME = 
		DocumentFactory.getInstance().createQName("Given", CCRConstants.XMLNS_CCR);	
	
	private static final QName MIDDLE_NAME_QNAME = 
		DocumentFactory.getInstance().createQName("Middle", CCRConstants.XMLNS_CCR);	
	
	private static final QName FAMILY_NAME_QNAME = 
		DocumentFactory.getInstance().createQName("Family", CCRConstants.XMLNS_CCR);	

	private static final QName NICK_NAME_QNAME = 
		DocumentFactory.getInstance().createQName("NickName", CCRConstants.XMLNS_CCR);	

	private static final QName TITLE_QNAME = 
		DocumentFactory.getInstance().createQName("Title", CCRConstants.XMLNS_CCR);	

	private static final QName SUFFIX_QNAME = 
		DocumentFactory.getInstance().createQName("Suffix", CCRConstants.XMLNS_CCR);	

	
	private int nameType;
	private String givenName;
	private String middleName;
	private String familyName;
	private String suffix;
	private String title;
	private String nickName;

    
    public Name(int nameType) {
        this.nameType = nameType;
        this.givenName = null;
        this.middleName = null;
        this.familyName = null;
        this.suffix = null;
        this.title = null;
        this.nickName = null;
    }
    
	/**
	 * @param nameType
	 * @param givenName
	 * @param middleName
	 * @param familyName
	 * @param suffix
	 * @param title
	 * @param nickName
	 */
	public Name(int nameType, String givenName, String middleName, 
			String familyName, String suffix, String title, String nickName) 
		throws IllegalArgumentException {
		
		if ( nameType != BIRTHNAME_TYPE && 
				nameType != CURRENTNAME_TYPE ) {
			throw new IllegalArgumentException("Invalid name type specified " + nameType);
		}
		this.nameType = nameType;	
		this.givenName = givenName;
		this.middleName = middleName;
		this.familyName = familyName;
		this.suffix = suffix;
		this.title = title;
		this.nickName = nickName;
	}
	
	public String getGivenName() {
		return givenName;
	}
	public String getFamilyName() {
		return familyName;
	}
	public String getMiddleName() {
		return middleName;
	}
	public String getTitle() {
		return title;
	}
	public String getSuffix() {
		return suffix;
	}
	
	
	public Element getElement() {

		Element name = null;
		
		if ( nameType == BIRTHNAME_TYPE ) {
			name = 
				DocumentFactory.getInstance().createElement(BIRTHNAME_QNAME);
		} else if ( nameType == CURRENTNAME_TYPE ) {
			name = 
				DocumentFactory.getInstance().createElement(CURRENTNAME_QNAME);
		} else {
			log.error("We should not get here.  Bad nameType.");
		}

		if ( givenName != null && ! givenName.equals("")) {
			name.addElement(GIVEN_NAME_QNAME)
				.addText(givenName);
		}
		
		if ( middleName != null && ! middleName.equals("") ) {
			name.addElement(MIDDLE_NAME_QNAME)
			.addText(middleName);
		}
		
		if ( familyName != null && ! familyName.equals("") ) {
			name.addElement(FAMILY_NAME_QNAME)
			.addText(familyName);
		}
		
		if ( nickName != null && ! nickName.equals("") ) {
			name.addElement(NICK_NAME_QNAME)
			.addText(nickName);
		}
		if ( title != null && ! title.equals("") ) {
			name.addElement(TITLE_QNAME)
			.addText(title);
		}
		if ( suffix != null && ! suffix.equals("") ) {
			name.addElement(SUFFIX_QNAME)
			.addText(suffix);
		}
		
		return name;
	}
}
