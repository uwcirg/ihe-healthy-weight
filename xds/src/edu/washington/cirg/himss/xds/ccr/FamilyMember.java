/*
 * Copyright 2005 (C) University of Washington. All Rights Reserved.
 * Clinical Informatics Research Group (CIRG)
 * Created on Feb 8, 2005
 * $Id: FamilyMember.java,v 1.3 2005/02/13 17:54:55 ddrozd Exp $
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

/** 
 * @author <a href="mailto:ddrozd@u.washington.edu">Dan Drozd</a>
 * @version $Revision: 1.3 $
 *
 */
public class FamilyMember extends CCRDataObject {

    private static final QName FAMILY_MEMBER_QNAME = 
        DocumentFactory.getInstance().createQName("FamilyMember", CCRConstants.XMLNS_CCR);
    private static final QName RELATIONS_QNAME = 
        DocumentFactory.getInstance().createQName("Relations", CCRConstants.XMLNS_CCR);   
    private static final QName RELATION_QNAME = 
        DocumentFactory.getInstance().createQName("Relation", CCRConstants.XMLNS_CCR);   

    
    private static Log log = LogFactory.getLog(FamilyMember.class);


    private Actor actor;
    private List relationList;
    
    
    public FamilyMember(Actor actor, String relation ) {
        this.actor = actor;
        relationList = new ArrayList();
        relationList.add(relation);
    }
    
    public void addRelation(String relation) {
        relationList.add(relation);
    }
    
    /* (non-Javadoc)
     * @see edu.washington.cirg.himss.xds.ccr.CCRDataObject#getElement()
     */
    public Element getElement() {
        
        Element element = 
            DocumentFactory.getInstance().createElement(FAMILY_MEMBER_QNAME);
        element.add(ccrDataObjectId.getElement());

	if ( actor != null ) {
          element.add(actor.getElement());
	}

	if ( relationList.size() > 0 ) {
          Element relationsElement =
            DocumentFactory.getInstance().createElement(RELATIONS_QNAME);
          Iterator iterator = relationList.iterator();
        
          while ( iterator.hasNext() ) {
            String relation = (String)iterator.next();
            if ( relation != null ) {
                relationsElement.addElement(RELATION_QNAME).addText(relation);
            }
          }
          element.add(relationsElement);
	}

        return element;
    }

}


