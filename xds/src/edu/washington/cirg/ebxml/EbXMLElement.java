/*
 * Copyright 2004-2005 (C) University of Washington. All Rights Reserved.
 * Created on Dec 21, 2004
 * University of Washington, CIRG
 * $Id: EbXMLElement.java,v 1.3 2005/02/11 20:10:22 ddrozd Exp $
 */
package edu.washington.cirg.ebxml;

import org.dom4j.Element;

/** A lightweight implementation of standard ebXML classes
 * @author <a href="mailto:ddrozd@u.washington.edu">Dan Drozd</a>
 * @version $Revision: 1.3 $
 *
 */
public interface EbXMLElement {
	
	/**<p><code>getElement</code> builds and will return a {@link org.dom4j.Element}.
	 * 
	 */
	public Element getElement();
	
}
