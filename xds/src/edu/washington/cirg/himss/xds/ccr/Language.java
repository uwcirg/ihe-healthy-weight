/*
 * Copyright 2004-2005 (C) University of Washington. All Rights Reserved.
 * Created on Dec 28, 2004
 * University of Washington, CIRG
 * $Id: Language.java,v 1.3 2005/01/26 19:45:44 ddrozd Exp $
 */
package edu.washington.cirg.himss.xds.ccr;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.dom4j.DocumentFactory;
import org.dom4j.Element;
import org.dom4j.QName;

/** Java implementation of the Continuity of Care (CCR) document object model
 * @author <a href="mailto:ddrozd@u.washington.edu">Dan Drozd</a>
 * @version $Revision: 1.3 $
 *
 */
public class Language {

	private static Log log = LogFactory.getLog(Language.class);

	private static final QName LANG_QNAME = 
		DocumentFactory.getInstance().createQName("Language", CCRConstants.XMLNS_CCR);	

	private static final QName TEXT_QNAME = 
		DocumentFactory.getInstance().createQName("Text", CCRConstants.XMLNS_CCR);	
	
	private static final QName PROFICIENCY_QNAME = 
		DocumentFactory.getInstance().createQName("Proficiency", CCRConstants.XMLNS_CCR);	
	
	private String language;
	private String proficiency;
	
	public Language(String language, String proficiency) {
		
		if ( language == null) {
			log.trace("argument language cannot be null");
			throw new IllegalArgumentException("argument language cannot be null");
		}
		if ( proficiency == null ) {
			log.trace("argument language cannot be null");
			throw new IllegalArgumentException("argument proficiency cannot be null");
		}
		this.language = language;
		this.proficiency = proficiency;
	}

	
	public Element getElement() {
		
		Element element = DocumentFactory.getInstance().createElement(LANG_QNAME);	
		Element textE = DocumentFactory.getInstance().createElement(TEXT_QNAME)
			.addText(language);
		element.add(textE);

		Element prof = DocumentFactory.getInstance().createElement(PROFICIENCY_QNAME);	
		Element textE2 = DocumentFactory.getInstance().createElement(TEXT_QNAME)
			.addText(proficiency);
		prof.add(textE2);
		element.add(prof);
		
		return element;
	}
}
