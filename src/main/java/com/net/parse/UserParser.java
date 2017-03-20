package com.net.parse;

import java.io.InputStream;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.apache.log4j.Logger;
import org.springframework.stereotype.Component;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import com.net.util.LogUtil;

@Component("userParser")
public class UserParser {

	private static Logger logger = LogUtil.getLogger();

	private static DocumentBuilderFactory documentBuilderFactory;

	private static final String SCHEMA_FILE = "UserInfo.xsd";

	private static DocumentBuilder db;

	static {
		try {
			documentBuilderFactory = DocumentBuilderFactory.newInstance();
			documentBuilderFactory.setAttribute("http://java.sun.com/xml/jaxp/properties/schemaLanguage",
					"http://www.w3.org/2001/XMLSchema");
			documentBuilderFactory.setAttribute("http://java.sun.com/xml/jaxp/properties/schemaSource",
					UserParser.class.getResourceAsStream(SCHEMA_FILE));

			documentBuilderFactory.setNamespaceAware(true);
			documentBuilderFactory.setValidating(true);

			db = documentBuilderFactory.newDocumentBuilder();
		} catch (ParserConfigurationException e) {
			e.printStackTrace();
		}

	}

	/**
	 * urlPath look like /com/net/util/xml/Config.xml
	 */
	public UserInfo parserXml(final String urlPath) {
		try {
			if (logger.isDebugEnabled()) {
				logger.debug("Parsing xml at resource path " + urlPath);
			}

			final ParserValidationHandler errorHandler = new ParserValidationHandler();
			db.setErrorHandler(errorHandler);

			InputStream inputStream = UserParser.class.getResourceAsStream(urlPath);
			Document document = db.parse(inputStream);

			if (errorHandler.hasParsingErrors()) {
				errorHandler.logParsingErrors(logger);
				throw new SAXException("Unable to parse xml file: " + urlPath);
			}

			document.getDocumentElement().normalize();

			UserInfo userInfo = new UserInfo();

			final NodeList nodeList = document.getElementsByTagName("User");

			parseUsers(userInfo, nodeList);

			return userInfo;

		} catch (Exception ex) {
			ex.printStackTrace();
		}
		return null;
	}

	private void parseUsers(UserInfo userInfo, NodeList nodeList) {
		if (nodeList != null && nodeList.getLength() > 0) {
			for (int i = 0; i < nodeList.getLength(); i++) {
				final Node fstNode = nodeList.item(i);

				if (fstNode.getNodeType() == Node.ELEMENT_NODE) {
					final Element elObject = (Element) fstNode;
					if (elObject.getNodeName().equals("User")) {
						final String id = elObject.getAttribute("id");
						userInfo.setId(Integer.valueOf(id));
					}
				}
			}
		}

	}

}
