/*
 * Copyright 2004-2005 (C) University of Washington. All Rights Reserved.
 * Created on Dec 21, 2004
 * University of Washington, CIRG
 * $Id: ProvideAndRegisterDocRequest.java,v 1.5 2005/01/26 18:59:47 ddrozd Exp $
 */
package edu.washington.cirg.himss.xds.requests;




import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.DocumentFactory;
import org.dom4j.Element;
import org.dom4j.QName;

import edu.washington.cirg.ebxml.Association;
import edu.washington.cirg.ebxml.EbXMLConstants;
import edu.washington.cirg.ebxml.ObjectRef;
import edu.washington.cirg.himss.xds.XDSDocument;
import edu.washington.cirg.himss.xds.XDSException;
import edu.washington.cirg.himss.xds.XDSFolder;
import edu.washington.cirg.himss.xds.XDSSubmissionSet;
import edu.washington.cirg.himss.xds.util.XDSConstants;
import edu.washington.cirg.himss.xds.util.XDSUtil;

/** Class representing a Provide and Register Document request 
 * to be sent to the XDS ebXML repository
 * @author <a href="mailto:ddrozd@u.washington.edu">Dan Drozd</a>
 * @version $Revision: 1.5 $
 *
 */
public class ProvideAndRegisterDocRequest implements XDSRequest {

	private static Log log = LogFactory.getLog(ProvideAndRegisterDocRequest.class);
	
	public static final QName SUBMIT_OBJECT_REQ_QNAME = 
		DocumentFactory.getInstance().createQName("SubmitObjectsRequest", EbXMLConstants.XMLNS_RS);
	public static final QName LEAF_REGISTRY_OBJ_LIST_QNAME = 
		DocumentFactory.getInstance().createQName("LeafRegistryObjectList", EbXMLConstants.XMLNS_RIM );

	private int docCount = 0;
	
	private String patientId;
	private XDSSubmissionSet xdsSubmissionSet;
	private List xdsDocumentList;
	private Map mFolders;
	private String submissionTime;
	private String authorPerson;
	private String authorDepartment;
	private String authorInstitution;
	private String description;
	private Document document;
	
	
	public void createFolder(String name, String comments) throws XDSException {
		
		XDSFolder folder = new XDSFolder(name, comments, patientId, new String[] {XDSConstants.CCR_FOLDER_CODE}, xdsSubmissionSet);
		mFolders.put(name, folder);
		xdsSubmissionSet.addFolder(folder);
	}
	
	public void addXDSDocument(String folderName, XDSDocument xdsdoc ) 
		throws XDSException {
		
		this.addXDSDocument(xdsdoc);
		XDSFolder folder = (XDSFolder)mFolders.get(folderName);
		if ( folder == null ) {
			throw new XDSException("Folder [ " + folderName + " ] does not exist.  Create it.");
		} else {
			folder.addXDSFile(xdsdoc.getXDSFile());
		}
	}
	
	public void addXDSDocument(XDSDocument xdsdoc) {
		xdsDocumentList.add(xdsdoc);
	}
	
	public ProvideAndRegisterDocRequest(String patientId, List xdsDocumentList)
		throws XDSException {
		this(patientId);
		this.xdsDocumentList = xdsDocumentList;
	}
	
	
	public void setAuthorPerson(String authorPerson) {
		this.authorPerson = authorPerson;
		xdsSubmissionSet.setAuthorPerson(authorPerson);
	}
	public void setAuthorDepartment(String authorDepartment) {
		this.authorDepartment = authorDepartment;
		xdsSubmissionSet.setAuthorDepartment(authorDepartment);
	}
	public void setAuthorInstitution(String authorInstitution) {
		this.authorInstitution = authorInstitution;
		xdsSubmissionSet.setAuthorInstitution(authorInstitution);
	}
	public void setDescription(String description) {
		this.description = description;
		xdsSubmissionSet.setDescription(description);
	}
	
	public ProvideAndRegisterDocRequest(String patientId) throws XDSException {
		this.patientId = patientId;
		this.xdsDocumentList = new ArrayList();
		this.mFolders = new HashMap();
		this.submissionTime = XDSUtil.currentXdsDate(XDSUtil.XDS_FORMAT);
		
		
		xdsSubmissionSet = 
			new XDSSubmissionSet(description, XDSConstants.OID_ROOT,
					submissionTime, authorDepartment, 
					authorInstitution, authorPerson, 
					XDSConstants.CCR_SS_CONTENT_TYPE_CODE);
	}
	
	
	public Document generateRequest()
		throws DocumentException, XDSException {
		
		Iterator iterator = xdsDocumentList.iterator();
		while ( iterator.hasNext() ) {
			XDSDocument xdsdoc = (XDSDocument)iterator.next();
			xdsSubmissionSet.addDocument(xdsdoc);
		}
		iterator = null;
		
 		Element root = 
			DocumentFactory.getInstance().createElement(SUBMIT_OBJECT_REQ_QNAME);
		document = DocumentFactory.getInstance().createDocument(root);
	
		Element LeafRegistryObjectList = 
			root.addElement(LEAF_REGISTRY_OBJ_LIST_QNAME);
		
		//Create a list of external identifiers and create the <ObjectRef> tags
		//that refer to them
		List externalIdentifierList = 
			XDSConstants.getExternalIdentifierUUIDs();
		iterator = externalIdentifierList.iterator();
		while ( iterator.hasNext() ) {
			ObjectRef objref = 
				new ObjectRef((String)iterator.next());
			LeafRegistryObjectList.add(objref.getElement());
		}
		
 
		//Begin processing documents
		List xdsDocList = 
			xdsSubmissionSet.getXDSDocumentList();
		
		if ( xdsDocList != null && xdsDocList.size() > 0 ) {
			
			iterator = xdsDocList.iterator();
			
			while ( iterator.hasNext() ) {
				XDSDocument xdsdoc = (XDSDocument)iterator.next();
				Element docElement = xdsdoc.getElement();
				LeafRegistryObjectList.add(docElement);
				//We add an association if we're doing a RPLC, APND, or XFRM
				if ( xdsdoc.docHasAssociation() ) {
					String relationshipType = xdsdoc.getDocAssociationType();
					String parentDocId = xdsdoc.getParentDocId();
					ObjectRef objref = new ObjectRef(parentDocId);
					Association association = 
						new Association(relationshipType, 
								xdsdoc.getSymbolicDocName(), 
								parentDocId, null, null);
					LeafRegistryObjectList.add(objref.getElement());
					LeafRegistryObjectList.add(association.getElement());
				}
				
			}
			iterator = null;
		}
 
		//Begin processing submission set
		List ssElementList =
			xdsSubmissionSet.getElementList();
		iterator = ssElementList.iterator();
		while ( iterator.hasNext() ) {
			LeafRegistryObjectList.add((Element)iterator.next());
		}

		
		iterator = null;
		//Begin processing folders
		List xdsFolderList = xdsSubmissionSet.getXDSFolderList();
		
		if ( xdsFolderList != null && xdsFolderList.size() > 0 ) {
		
			iterator = xdsFolderList.iterator();
			while ( iterator.hasNext() ) {
				XDSFolder folder = (XDSFolder)iterator.next();
				List folderElementList = folder.getElementList();
				Iterator i2 = folderElementList.iterator();
				while ( i2.hasNext() ) {
					LeafRegistryObjectList.add((Element)i2.next());
				}
			}
		}//End processing folders
				
		return document;
	}


}
