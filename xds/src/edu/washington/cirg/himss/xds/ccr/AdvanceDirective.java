/*
 * Copyright 2004-2005 (C) University of Washington. All Rights Reserved.
 * Created on Dec 28, 2004
 * University of Washington, CIRG
 * $Id: AdvanceDirective.java,v 1.3 2005/02/09 01:51:11 ddrozd Exp $
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
public class AdvanceDirective extends CCRDataObject {
	
	private static Log log = LogFactory.getLog(AdvanceDirective.class);

	private static final QName ADVANCE_DIRECTIVE_QNAME = 
		DocumentFactory.getInstance().createQName("AdvanceDirective", CCRConstants.XMLNS_CCR);	
	
	private Directive directive;
	

	public AdvanceDirective(String directiveString) {
		this.directive = new Directive(directiveString);
	}
	
	public Directive getDirective() {
		return directive;
	}
	
	public Element getElement() {

		Element element = 
			DocumentFactory.getInstance().createElement(ADVANCE_DIRECTIVE_QNAME);
		element.add(ccrDataObjectId.getElement());
		element.add(directive.getElement());
		
		return element;
	}
}