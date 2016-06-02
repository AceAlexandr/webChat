package sanyaaaaaa.storage.xml;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpression;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;

import sanyaaaaaa.model.Message;
import sanyaaaaaa.util.MessageUtil;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

public final class XMLHistoryUtil {
	private static final String STORAGE_LOCATION = "D:"+  File.separator + "history.xml"; // history.xml will be located in the home directory
	private static final String MESSAGES = "messages";//tasks
	private static final String MESSAGE = "message";//task
	private static final String ID = "id";//id
	private static final String MESSAGETEXT = "messageText";//description
	private static final String DATE = "date";//done
	private static final String USER = "user";//done
	private static final String METHOD = "method";//done

	private XMLHistoryUtil() {
	}

	public static synchronized void createStorage() throws ParserConfigurationException, TransformerException {
		DocumentBuilderFactory docFactory = DocumentBuilderFactory.newInstance();
		DocumentBuilder docBuilder = docFactory.newDocumentBuilder();

		Document doc = docBuilder.newDocument();
		Element rootElement = doc.createElement(MESSAGES);
		doc.appendChild(rootElement);

		Transformer transformer = getTransformer();

		DOMSource source = new DOMSource(doc);
		StreamResult result = new StreamResult(new File(STORAGE_LOCATION));
		transformer.transform(source, result);
	}

	public static synchronized void deleteStorage() throws ParserConfigurationException, TransformerException {
		File storage=new File(STORAGE_LOCATION);
		storage.delete();
	}

	public static synchronized void addData(Message message) throws ParserConfigurationException, SAXException, IOException, TransformerException {
		DocumentBuilderFactory documentBuilderFactory = DocumentBuilderFactory.newInstance();
		DocumentBuilder documentBuilder = documentBuilderFactory.newDocumentBuilder();
		Document document = documentBuilder.parse(STORAGE_LOCATION);
		document.getDocumentElement().normalize();

		Element root = document.getDocumentElement(); // Root <tasks> element

		Element messageElement = document.createElement(MESSAGE);
		root.appendChild(messageElement);
		messageElement.setAttribute(ID, message.getId());

		Element user = document.createElement(USER);
		user.appendChild(document.createTextNode(message.getUser()));
		messageElement.appendChild(user);

		Element date = document.createElement(DATE);
		date.appendChild(document.createTextNode(message.getDate()));
		messageElement.appendChild(date);

		Element text = document.createElement(MESSAGETEXT);
		text.appendChild(document.createTextNode(message.getMessage()));
		messageElement.appendChild(text);

		DOMSource source = new DOMSource(document);

		Transformer transformer = getTransformer();

		StreamResult result = new StreamResult(STORAGE_LOCATION);
		transformer.transform(source, result);
	}

	public static synchronized void updateData(Message task) throws ParserConfigurationException, SAXException, IOException, TransformerException, XPathExpressionException {
		DocumentBuilderFactory documentBuilderFactory = DocumentBuilderFactory.newInstance();
		DocumentBuilder documentBuilder = documentBuilderFactory.newDocumentBuilder();
		Document document = documentBuilder.parse(STORAGE_LOCATION);
		document.getDocumentElement().normalize();
		Node taskToUpdate = getNodeById(document, task.getId());

		if (taskToUpdate != null) {
			NodeList childNodes = taskToUpdate.getChildNodes();

			for (int i = 0; i < childNodes.getLength(); i++) {
				Node node = childNodes.item(i);
				if (MESSAGETEXT.equals(node.getNodeName())) {
					node.setTextContent(task.getMessage());
				}
			}
			Transformer transformer = getTransformer();
			DOMSource source = new DOMSource(document);
			StreamResult result = new StreamResult(new File(STORAGE_LOCATION));
			transformer.transform(source, result);
		} else {
			throw new NullPointerException();
		}
	}

	public static synchronized void deleteData(String id) throws ParserConfigurationException, SAXException, IOException, TransformerException, XPathExpressionException {
		DocumentBuilderFactory documentBuilderFactory = DocumentBuilderFactory.newInstance();
		DocumentBuilder documentBuilder = documentBuilderFactory.newDocumentBuilder();
		Document document = documentBuilder.parse(STORAGE_LOCATION);
		document.getDocumentElement().normalize();
		Element root = document.getDocumentElement();
		Node taskToDelete = getNodeById(document,id);

		if (taskToDelete != null) {
			root.removeChild(taskToDelete);
			Transformer transformer = getTransformer();
			DOMSource source = new DOMSource(document);
			StreamResult result = new StreamResult(new File(STORAGE_LOCATION));
			transformer.transform(source, result);
		} else {
			throw new NullPointerException();
		}
	}

	public static synchronized boolean doesStorageExist() {
		File file = new File(STORAGE_LOCATION);
		return file.exists();
	}

	public static synchronized List<Message> getMessages(int index) throws SAXException, IOException, ParserConfigurationException {
		List<Message> messages = new ArrayList<Message>();
		DocumentBuilderFactory documentBuilderFactory = DocumentBuilderFactory.newInstance();
		DocumentBuilder documentBuilder = documentBuilderFactory.newDocumentBuilder();
		Document document = documentBuilder.parse(STORAGE_LOCATION);
		document.getDocumentElement().normalize();
		Element root = document.getDocumentElement(); // Root <tasks> element
		NodeList messageList = root.getElementsByTagName(MESSAGE);
		for (int i = index; i < messageList.getLength(); i++) {
			Element taskElement = (Element) messageList.item(i);
			Message message=new Message();
			message.setId(taskElement.getAttribute(ID));
			message.setUser(taskElement.getElementsByTagName(USER).item(0).getTextContent());
			message.setDate(taskElement.getElementsByTagName(DATE).item(0).getTextContent());
			message.setMessage(taskElement.getElementsByTagName(MESSAGETEXT).item(0).getTextContent());
			messages.add(message);
		}
		return messages;
	}

	public static String getServerResponse(int index) throws ParserConfigurationException, IOException, SAXException {
		List<Message> messages = getMessages(index);
//		if(messages.size()<1){
//			return null;
//		}
		JSONObject jsonList = new JSONObject();
		JSONArray jsonArray=new JSONArray();
		for(Message o:messages){
			JSONObject jsonObject = new JSONObject();
			jsonObject.put("messageText", o.getMessage());
			jsonObject.put("date", o.getDate());
			jsonObject.put("id", o.getId().toString());
			jsonObject.put("user", o.getUser());
			jsonArray.add(jsonObject);
		}
		jsonList.put("messages", jsonArray);
		jsonList.put("token", MessageUtil.getToken(getStorageSize()));
		jsonList.put("hash", MessageUtil.getHASH());
		return jsonList.toJSONString();
	}

	public static String getServerResponse(List<Message> messages) throws ParserConfigurationException, IOException, SAXException {
		JSONObject jsonList = new JSONObject();
		JSONArray jsonArray=new JSONArray();
		for(Message o:messages){
			JSONObject jsonObject = new JSONObject();
			jsonObject.put("messageText", o.getMessage());
			jsonObject.put("date", o.getDate());
			jsonObject.put("id", o.getId().toString());
			jsonObject.put("user", o.getUser());
			jsonArray.add(jsonObject);
		}
		jsonList.put("messages", jsonArray);
		jsonList.put("token", MessageUtil.getToken(getStorageSize()));
		jsonList.put("hash", MessageUtil.getHASH());
		return jsonList.toJSONString();
	}

	public static synchronized int getStorageSize() throws SAXException, IOException, ParserConfigurationException {
		DocumentBuilderFactory documentBuilderFactory = DocumentBuilderFactory.newInstance();
		DocumentBuilder documentBuilder = documentBuilderFactory.newDocumentBuilder();
		Document document = documentBuilder.parse(STORAGE_LOCATION);
		document.getDocumentElement().normalize();
		Element root = document.getDocumentElement(); // Root <tasks> element
		NodeList messageList = root.getElementsByTagName(MESSAGE);
		return messageList.getLength();
	}

	private static Node getNodeById(Document doc, String id) throws XPathExpressionException {
		XPath xpath = XPathFactory.newInstance().newXPath();
		XPathExpression expr = xpath.compile("//" + MESSAGE + "[@id='" + id + "']");
		return (Node) expr.evaluate(doc, XPathConstants.NODE);
	}

	private static Transformer getTransformer() throws TransformerConfigurationException {
		TransformerFactory transformerFactory = TransformerFactory.newInstance();
		Transformer transformer = transformerFactory.newTransformer();
		// Formatting XML properly
		transformer.setOutputProperty(OutputKeys.INDENT, "yes");
		return transformer;
	}

}
