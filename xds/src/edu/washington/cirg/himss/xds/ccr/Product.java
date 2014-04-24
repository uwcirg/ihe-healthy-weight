/*
 * Copyright 2004-2005 (C) University of Washington. All Rights Reserved.
 * Created on Dec 28, 2004
 * University of Washington, CIRG
 * $Id: Product.java,v 1.4 2005/02/09 01:51:11 ddrozd Exp $
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
public class Product extends CCRDataObject {

	private static Log log = LogFactory.getLog(Product.class);

	protected static final QName PRODUCT_QNAME = 
		DocumentFactory.getInstance().createQName("Product", CCRConstants.XMLNS_CCR);	
	protected static final QName PRODUCT_NAME_QNAME = 
		DocumentFactory.getInstance().createQName("ProductName", CCRConstants.XMLNS_CCR);	
	protected static final QName PRODUCT_TYPE_QNAME = 
		DocumentFactory.getInstance().createQName("ProductType", CCRConstants.XMLNS_CCR);	
	protected static final QName BRAND_NAME_QNAME = 
		DocumentFactory.getInstance().createQName("BrandName", CCRConstants.XMLNS_CCR);	
    protected static final QName QUANTITY_QNAME = 
        DocumentFactory.getInstance().createQName("Quantity", CCRConstants.XMLNS_CCR);  
    protected static final QName DOSE_STRENGTH_QNAME = 
        DocumentFactory.getInstance().createQName("DoseStrength", CCRConstants.XMLNS_CCR);  

	public static final String MEDICATION_PRODUCT = "Medication";
	public static final String IMMUNIZATION_PRODUCT = "Immunization";
	
	
	private String productType;
	private String productName;
	private String brandName;
	private int quantity = -1;
	private List reactionList;
	private CCRDateTime ccrDateTime;
    private String doseStrength;

	/**
	 * @param productType
	 * @param productName
	 * @param brandName
     * @param doseStrength
	 */
	public Product(String productType, String productName,
			String brandName, String doseStrength) {
		this.productType = productType;
		this.productName = productName;
		this.brandName = brandName;
        this.doseStrength = doseStrength;
		reactionList = new ArrayList();
	}

	/**
	 * @param productType
	 * @param productName
	 */
	public Product(String productType, String productName) {
		this.productType = productType;
		this.productName = productName;
		reactionList = new ArrayList();
	}

	/**
	 * @param productType
	 * @param productName
	 * @param brandName
	 * @param reaction
     * @param doseStrength
	 */
	public Product(String productType, String productName,
			String brandName, Reaction reaction, String doseStrength) {
		this(productType, productName, brandName, doseStrength);
		this.addReaction(reaction);
	}
	
	public void setQuantity(int quantity) {
		this.quantity = quantity;
	}
	
	public void setDateTime(CCRDateTime ccrDateTime) {
		this.ccrDateTime = ccrDateTime;
	}

	
	public void addReaction(Reaction reaction) {
		reactionList.add(reaction);
	}
	
	public Element getElement() {
		Element element = 
			DocumentFactory.getInstance().createElement(PRODUCT_QNAME);
		element.add(ccrDataObjectId.getElement());
		if ( ccrDateTime != null ) {
			element.add(ccrDateTime.getElement());
		}
		
		if ( productType != null ) {
			element.addElement(PRODUCT_TYPE_QNAME).addText(productType);
		}
		if ( productName != null ) {
			element.addElement(PRODUCT_NAME_QNAME).addText(productName);
		}
		if ( brandName != null && ! brandName.equals("")) {
			element.addElement(BRAND_NAME_QNAME).addText(brandName);
		}
        if ( doseStrength != null ) {
            Element doseStrengthElement = 
                DocumentFactory.getInstance().createElement(DOSE_STRENGTH_QNAME);
            doseStrengthElement.addElement(CCRConstants.VALUE_QNAME)
                .addText(String.valueOf(doseStrength));
            element.add(doseStrengthElement);
        }
		
		if ( quantity != -1 ) {
			Element quantityElement = 
				DocumentFactory.getInstance().createElement(QUANTITY_QNAME);
			quantityElement.addElement(CCRConstants.VALUE_QNAME)
				.addText(String.valueOf(quantity));
			element.add(quantityElement);
		}
		
		
		Iterator iterator = reactionList.iterator();
		while ( iterator.hasNext() ) {
			Reaction reaction = (Reaction)iterator.next();
			element.add(reaction.getElement());
		}

		return element;
	}
}
