/*
 * Copyright 2004-2007 (C) University of Washington. All Rights Reserved.
 * Created on Jan 27, 2006
 * University of Washington, CIRG
 */
package edu.washington.cirg.himss.xds.responses;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import javax.xml.soap.SOAPException;
import javax.xml.soap.SOAPMessage;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.dom4j.Attribute;
import org.dom4j.DocumentException;
import org.dom4j.Node;
import org.jaxen.JaxenException;
import org.jaxen.SimpleNamespaceContext;
import org.jaxen.dom4j.Dom4jXPath;

import edu.washington.cirg.himss.xds.util.XDSConstants;

/**
 * Class extends RegistryResponse to represent a FindDocuments query response
 * returned from an XDS ebXML registry, in response to a FindDocuments request
 * 
 * @author <a href="mailto:jsibley@u.washington.edu">Jim Sibley</a>
 * 
 */



/*
 * <?xml version="1.0" encoding="UTF-8"?> <rs:RegistryResponse status="Success"
 * xmlns:rs_v3="urn:oasis:names:tc:ebxml-regrep:xsd:rs:3.0"
 * xmlns:rs="urn:oasis:names:tc:ebxml-regrep:registry:xsd:2.1"
 * xmlns:java="http://xml.apache.org/xslt/java"
 * xmlns:rim_v3="urn:oasis:names:tc:ebxml-regrep:xsd:rim:3.0"
 * xmlns:query_v3="urn:oasis:names:tc:ebxml-regrep:xsd:query:3.0"
 * xmlns:query="urn:oasis:names:tc:ebxml-regrep:query:xsd:2.1"
 * xmlns:rim="urn:oasis:names:tc:ebxml-regrep:rim:xsd:2.1">
 * <query:AdhocQueryResponse> <query:SQLQueryResult> <rim:ExtrinsicObject
 * isOpaque="false" mimeType="text/x-cda-r2+xml"
 * objectType="urn:uuid:7edca82f-054d-47f2-a032-9b2a5b5186c1" status="Approved"
 * id="urn:uuid:2680428a-467a-583e-e03e-000874e9b3f3"> <rim:Slot
 * name="creationTime"> <rim:ValueList > <rim:Value >20060116</rim:Value>
 * </rim:ValueList> </rim:Slot> <rim:ExternalIdentifier
 * identificationScheme="urn:uuid:2e82c1f6-a085-4c72-9da3-8640a32e42ab"
 * registryObject="urn:uuid:2680428a-467a-583e-e03e-000874e9b3f3"
 * value="1.3.6.1.4.1.21367.2006.1.2.134^5660bd9df4e94b838e770bd871b831f7"
 * objectType="ExternalIdentifier"
 * id="urn:uuid:a1c7d33c-ef4f-41ff-ac49-9553eb40a430"> <rim:Name >
 * <rim:LocalizedString charset="UTF-8" value="XDSDocumentEntry.uniqueId"/>
 * </rim:Name> <rim:Description /> </rim:ExternalIdentifier>
 * </rim:ExtrinsicObject> </query:SQLQueryResult> </query:AdhocQueryResponse>
 * </rs:RegistryResponse>
 */

public class FindDocumentsResponse extends RegistryResponse {

	private static Log log = LogFactory.getLog(FindDocumentsResponse.class);

	private List patientIdList;
	private List documentUriList;
	private List mimeTypeList;
	private List formatCodeList;
	private List typeCodeList;
	private List authorInstList;
	private List creationTimeList;
	private List uniqueIdList;
	private List languageCodeList;
	private List sourcePatientInfoList;

	public FindDocumentsResponse(SOAPMessage response)
			throws DocumentException, IOException, SOAPException, JaxenException {

		super(response);
		this.patientIdList = new ArrayList();
		this.documentUriList = new ArrayList();
		this.mimeTypeList = new ArrayList();
		this.formatCodeList = new ArrayList();
		this.typeCodeList = new ArrayList();
		this.authorInstList = new ArrayList();
		this.creationTimeList = new ArrayList();
		this.uniqueIdList = new ArrayList();
		this.languageCodeList = new ArrayList();
		this.sourcePatientInfoList = new ArrayList();

		// Retrieve objects from document.
		Dom4jXPath xpathSelector = new Dom4jXPath("//*[local-name() = 'ExtrinsicObject']");
		SimpleNamespaceContext nsContext = new SimpleNamespaceContext();
		nsContext.addNamespace("soapenv","http://schemas.xmlsoap.org/soap/envelope/");
		nsContext.addNamespace("SOAP-ENV","http://schemas.xmlsoap.org/soap/envelope/");
		nsContext.addNamespace("ns5","urn:oasis:names:tc:ebxml-regrep:xsd:rim:3.0");
		nsContext.addNamespace("ns4","urn:oasis:names:tc:ebxml-regrep:xsd:query:3.0");
		nsContext.addNamespace("query","urn:oasis:names:tc:ebxml-regrep:xsd:query:3.0");
		nsContext.addNamespace("rim","urn:oasis:names:tc:ebxml-regrep:xsd:rim:3.0");
		xpathSelector.setNamespaceContext(nsContext);
		List<Node> results = xpathSelector.selectNodes(doc);

		Iterator iterator = results.iterator();
		while (iterator.hasNext()) {
			Node eo = (Node) iterator.next();
			String uniquePath = eo.getUniquePath();

			//log.info("INFO, eo = '" + eo.toString() + "'");				
			
			// Retrieve patient ID.
			xpathSelector = new Dom4jXPath(eo.getUniquePath() + "/*[local-name() = 'ExternalIdentifier'][@identificationScheme = '" + XDSConstants.UUID_PATIENT_IDENT_SCHEME_CODE + "']/@value");
			xpathSelector.setNamespaceContext(nsContext);
			List<Node> nodeList = xpathSelector.selectNodes(eo);
			
			//log.info("INFO, xpath = '" + xpathSelector.toString() + "', nodeList size = '" + nodeList.size() + "'");				
			
			Iterator nodeIterator = nodeList.iterator();
			String patId = "";
			if (nodeList.size() < 1) {
				patientIdList.add("");
			} else {
				while (nodeIterator.hasNext()) {
					Attribute attr = (Attribute) nodeIterator.next();
					patId = patId.concat(attr.getData().toString());
					if (nodeIterator.hasNext()) {
						patId = patId.concat("\r");
					}
				}
				patientIdList.add(patId);
			}

			// Retrieve URI values.
			xpathSelector = new Dom4jXPath(eo.getUniquePath() + "/*[local-name() = 'Slot'][@name = 'URI']/*[local-name()='ValueList']/*[local-name()='Value']");
			xpathSelector.setNamespaceContext(nsContext);

			nodeList = xpathSelector.selectNodes(eo);
			nodeIterator = nodeList.iterator();
			String docUri = "";
			if (nodeList.size() < 1) {
				documentUriList.add("");
			} else {
				while (nodeIterator.hasNext()) {
					Node node = (Node) nodeIterator.next();
					docUri = docUri.concat(node.getText().startsWith("1|") ? node.getText().substring(2) : node.getText().startsWith("2|") ? node.getText().substring(2) : node.getText());
					//if (nodeIterator.hasNext()) {
						//docUri = docUri.concat("\r");
					//}
				}
				documentUriList.add(docUri);
			}

			// Retrieve MIME type.
			xpathSelector = new Dom4jXPath(eo.getUniquePath() + "/@mimeType");
			xpathSelector.setNamespaceContext(nsContext);

			nodeList = xpathSelector.selectNodes(eo);
			nodeIterator = nodeList.iterator();
			String mimeType = "";
			if (nodeList.size() < 1) {
				mimeTypeList.add("");
			} else {
				while (nodeIterator.hasNext()) {
					Attribute attr = (Attribute) nodeIterator.next();
					mimeType = mimeType.concat(attr.getData().toString());
					if (nodeIterator.hasNext()) {
						mimeType = mimeType.concat("\r");
					}
				}
				mimeTypeList.add(mimeType);
			}

			// Retrieve format code.
			xpathSelector = new Dom4jXPath(eo.getUniquePath() + "/*[local-name() = 'Classification'][@classificationScheme = '" + XDSConstants.UUID_FORMAT_CODE + "']/*[local-name()='Name']/*[local-name()='LocalizedString']/@value");
			xpathSelector.setNamespaceContext(nsContext);

			nodeList = xpathSelector.selectNodes(eo);
			nodeIterator = nodeList.iterator();
			String formatCode = "";
			if (nodeList.size() < 1) {
				formatCodeList.add("");
			} else {
				while (nodeIterator.hasNext()) {
					Attribute attr = (Attribute) nodeIterator.next();
					formatCode = formatCode.concat(attr.getData()
							.toString());
					if (nodeIterator.hasNext()) {
						formatCode = formatCode.concat("\r");
					}
				}
				formatCodeList.add(formatCode);
				log.info("Found formatCode: '" + formatCode.toString() + "'");
			}

			// Retrieve type (really event) code.
			xpathSelector = new Dom4jXPath(eo.getUniquePath() + "/*[local-name() = 'Classification'][@classificationScheme = '" + XDSConstants.UUID_EVENT_CODE_IDENT_SCHEME_CODE + "']/*[local-name()='Name']/*[local-name()='LocalizedString']/@value");
			xpathSelector.setNamespaceContext(nsContext);

			nodeList = xpathSelector.selectNodes(eo);
			nodeIterator = nodeList.iterator();
			String typeCode = "";
			if (nodeList.size() < 1) {
				typeCodeList.add("");
				log.info("INFO,No event codes found");
			} else {
				while (nodeIterator.hasNext()) {
					Attribute attr = (Attribute) nodeIterator.next();
					String tempStr = attr.getData().toString();
					typeCode = typeCode.concat(tempStr);
					if (nodeIterator.hasNext()) {
						typeCode = typeCode.concat("\r");
					}
				}

				typeCodeList.add(typeCode);
				log.info("INFO,Adding event code: " + typeCode);
			}
//			while (nodeIterator.hasNext()) {
//				Attribute attr = (Attribute) nodeIterator.next();
//				formatCode = formatCode.concat(attr.getData()
//						.toString());
//				if (nodeIterator.hasNext()) {
//					formatCode = formatCode.concat("\r");
//				}
//			}
//			formatCodeList.add(formatCode);
			
			// Retrieve author institution.
			xpathSelector = new Dom4jXPath(eo.getUniquePath() + "/*[local-name() = 'Classification'][@classificationScheme = '" + XDSConstants.UUID_AUTHOR_IDENT_SCHEME_CODE + "']/*[local-name()='Slot'][@name = 'authorInstitution']/*[local-name()='ValueList']/*[local-name()='Value']");
			xpathSelector.setNamespaceContext(nsContext);

			nodeList = xpathSelector.selectNodes(eo);
			nodeIterator = nodeList.iterator();
			String authInst = "";
			if (nodeList.size() < 1) {
				authorInstList.add("");
			} else {
				while (nodeIterator.hasNext()) {
					Node node = (Node) nodeIterator.next();
					String tempStr = node.getText();
					if (tempStr.indexOf("^") != -1) {
						authInst = authInst.concat(tempStr.substring(0, tempStr.indexOf("^", 0)));
					} else {
						authInst = authInst.concat(tempStr);
					}
					if (nodeIterator.hasNext()) {
						authInst = authInst.concat("\r");
					}
				}
				authorInstList.add(authInst);
				log.info("INFO: AuthInst = '" + authInst + "'");
			}

			// Retrieve creation time.
			xpathSelector = new Dom4jXPath(eo.getUniquePath() + "/*[local-name() = 'Slot'][@name = 'creationTime']/*[local-name()='ValueList']/*[local-name()='Value']");
			xpathSelector.setNamespaceContext(nsContext);

			nodeList = xpathSelector.selectNodes(eo);
			nodeIterator = nodeList.iterator();
			String creationTime = "";
			if (nodeList.size() < 1) {
				creationTimeList.add("");
			} else {
				while (nodeIterator.hasNext()) {
					Node node = (Node) nodeIterator.next();
					String tempStr = node.getText();
					while (tempStr.length() < 14) {
						tempStr = tempStr.concat("0");
					}
					creationTime = creationTime.concat(tempStr);
					if (nodeIterator.hasNext()) {
						creationTime = creationTime.concat("\r");
					}
				}
				creationTimeList.add(creationTime);
			}

			// Retrieve identification scheme code.
			xpathSelector = new Dom4jXPath(eo.getUniquePath() + "/*[local-name() = 'ExternalIdentifier'][@identificationScheme = '" + XDSConstants.UUID_UNIQUE_ID_IDENT_SCHEME_CODE + "']/@value");
			xpathSelector.setNamespaceContext(nsContext);

			nodeList = xpathSelector.selectNodes(eo);
			nodeIterator = nodeList.iterator();
			String uniqId = "";
			if (nodeList.size() < 1) {
				uniqueIdList.add("");
			} else {
				while (nodeIterator.hasNext()) {
					Attribute attr = (Attribute) nodeIterator.next();
					uniqId = uniqId.concat(attr.getData().toString());
					if (nodeIterator.hasNext()) {
						uniqId = uniqId.concat("\r");
					}
				}
				uniqueIdList.add(uniqId);
			}

			// Retrieve language code.
			xpathSelector = new Dom4jXPath(eo.getUniquePath() + "/*[local-name() = 'Slot'][@name = 'languageCode']/*[local-name()='ValueList']/*[local-name()='Value']");
			xpathSelector.setNamespaceContext(nsContext);

			nodeList = xpathSelector.selectNodes(eo);
			nodeIterator = nodeList.iterator();
			String langCode = "";
			if (nodeList.size() < 1) {
				languageCodeList.add("");
			} else {
				while (nodeIterator.hasNext()) {
					Node node = (Node) nodeIterator.next();
					langCode = langCode.concat(node.getText());
					if (nodeIterator.hasNext()) {
						langCode = langCode.concat("\r");
					}
				}
				languageCodeList.add(langCode);
			}

			// Retrieve source patient info.
			xpathSelector = new Dom4jXPath(eo.getUniquePath() + "/*[local-name() = 'Slot'][@name = 'sourcePatientInfo']/*[local-name()='ValueList']/*[local-name()='Value']");
			xpathSelector.setNamespaceContext(nsContext);

			nodeList = xpathSelector.selectNodes(eo);
			nodeIterator = nodeList.iterator();
			String patInfo = "";
			if (nodeList.size() < 1) {
				sourcePatientInfoList.add("");
			} else {
				while (nodeIterator.hasNext()) {
					Node node = (Node) nodeIterator.next();
					patInfo = patInfo.concat(node.getText());
					if (nodeIterator.hasNext()) {
						patInfo = patInfo.concat("\r");
					}
				}
				sourcePatientInfoList.add(patInfo);
			}
		}

	}

//	public List getObjectRefList() {
//		return objectRefList;
//	}

//	public List getExternalLinkList() {
//		return externalLinkList;
//	}

	public List getPatientIdList() {
		return patientIdList;
	}

	public List getDocumentUriList() {
		return documentUriList;
	}

	public List getMimeTypeList() {
		return mimeTypeList;
	}

	public List getFormatCodeList() {
		return formatCodeList;
	}

	public List getTypeCodeList() {
		return typeCodeList;
	}

	public List getAuthorInstList() {
		return authorInstList;
	}

	public List getCreationTimeList() {
		return creationTimeList;
	}

	public List getUniqueIdList() {
		return uniqueIdList;
	}

	public List getLanguageCodeList() {
		return languageCodeList;
	}

	public List getSourcePatientInfoList() {
		return sourcePatientInfoList;
	}

}
