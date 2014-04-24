/*
 * Copyright 2004-2005 (C) University of Washington. All Rights Reserved.
 * Created on Dec 28, 2004
 * University of Washington, CIRG
 * $Id: Directive.java,v 1.2 2005/01/26 19:45:44 ddrozd Exp $
 */
package edu.washington.cirg.himss.xds.ccr;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.dom4j.DocumentFactory;
import org.dom4j.Element;
import org.dom4j.QName;



/** Java implementation of the Continuity of Care (CCR) document object model
 * @author <a href="mailto:ddrozd@u.washington.edu">Dan Drozd</a>
 * @version $Revision: 1.2 $
 *
 */

public class Directive extends CCRDataObject {

	private static Log log = LogFactory.getLog(Directive.class);
	
	private static final QName DIRECTIVE_QNAME = 
		DocumentFactory.getInstance().createQName("Directive", CCRConstants.XMLNS_CCR);		
	private static final QName DESC_QNAME = 
		DocumentFactory.getInstance().createQName("Description", CCRConstants.XMLNS_CCR);	

	
	private String directiveString;
	
	public Directive(String directiveString) {
		this.directiveString = directiveString;
	}
	
	
	public Element getElement() {

		Element element = 
			DocumentFactory.getInstance().createElement(DIRECTIVE_QNAME);
		element.add(ccrDataObjectId.getElement());
		if ( directiveString != null ) {
			Element desc = element.addElement(DESC_QNAME);
			desc.addElement(CCRConstants.TEXT_QNAME)
				.addText(directiveString);
		}
		return element;
	}
}
