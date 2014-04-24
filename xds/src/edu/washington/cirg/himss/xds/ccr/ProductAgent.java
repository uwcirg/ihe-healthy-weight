/*
 * Copyright 2004-2005 (C) University of Washington. All Rights Reserved.
 * Created on Dec 28, 2004
 * University of Washington, CIRG
 * $Id: ProductAgent.java,v 1.2 2005/01/26 19:45:44 ddrozd Exp $
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
 * @version $Revision: 1.2 $
 *
 */
public class ProductAgent extends Agent {

	private static Log log = LogFactory.getLog(ProductAgent.class);

	private static final QName PRODUCTS_QNAME = 
		DocumentFactory.getInstance().createQName("Products", CCRConstants.XMLNS_CCR);	

	
	private List productList;
	private String desc;
	
	public ProductAgent(Product product, String desc) {
		this.desc = desc;
		productList = new ArrayList();
		this.addProduct(product);
	}
	
	public void addProduct(Product product) {
		productList.add(product);
	}
	
	public List getProducts() {
		return productList;
	}
	

	public Element getElement() {
		Element element = 
			DocumentFactory.getInstance().createElement(AGENT_QNAME);
		
		Element productsElement = 
			DocumentFactory.getInstance().createElement(PRODUCTS_QNAME);

		productsElement.add(ccrDataObjectId.getElement());

		if ( desc != null ) {
			Element descElement = 
				DocumentFactory.getInstance().createElement(CCRConstants.DESC_QNAME);
			descElement.addElement(CCRConstants.TEXT_QNAME).addText(desc);
			productsElement.add(descElement);
		}

		Iterator iterator = productList.iterator();
		while ( iterator.hasNext() ) {
			Product product = (Product)iterator.next();
			productsElement.add(product.getElement());
		}
		element.add(productsElement);
		
		return element;
	}
}
