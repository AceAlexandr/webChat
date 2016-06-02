package sanyaaaaaa.controller;


import java.io.IOException;
import java.util.concurrent.ScheduledThreadPoolExecutor;

import javax.servlet.AsyncContext;
import javax.servlet.AsyncEvent;
import javax.servlet.AsyncListener;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.xpath.XPathExpressionException;

import sanyaaaaaa.db.ChatDataBase;
import sanyaaaaaa.db.ChatDataBaseInterface;
import org.apache.log4j.Logger;

import sanyaaaaaa.model.Message;
import sanyaaaaaa.util.MessageUtil;

import sanyaaaaaa.storage.xml.XMLHistoryUtil;
import sanyaaaaaa.util.ServletUtil;
import org.json.simple.JSONObject;
import org.json.simple.parser.ParseException;
import org.xml.sax.SAXException;

@WebServlet(name = "MessageServlet",urlPatterns = {"/chat"},asyncSupported = true)
public class MessageServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;
	private static Logger logger = Logger.getLogger(MessageServlet.class.getName());
	private ChatDataBaseInterface dataBase;

	@Override
	public void init() throws ServletException {
		try {
			if(!XMLHistoryUtil.doesStorageExist()) {
				XMLHistoryUtil.createStorage();
			} else {
				XMLHistoryUtil.deleteStorage();
				XMLHistoryUtil.createStorage();
			}
			this.dataBase=new ChatDataBase();
			dataBase.loadAllMessages();
		} catch (ParserConfigurationException | TransformerException e) {
			e.printStackTrace();
		}
	}

	@Override
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		logger.info("doGet");
		String token = request.getParameter(MessageUtil.TOKEN);
		logger.info("token " + token);
		if (token != null && !"".equals(token)) {
			int index = MessageUtil.getIndex(token);
			logger.info("Index " + index);
			AsyncContext ac=request.startAsync();
			ac.addListener(new AsyncListener() {
				@Override
				public void onComplete(AsyncEvent asyncEvent) throws IOException {
					System.out.println("Async complete");
				}
				@Override
				public void onTimeout(AsyncEvent asyncEvent) throws IOException {
					System.out.println("Time out");
				}
				@Override
				public void onError(AsyncEvent asyncEvent) throws IOException {
					System.err.println("Async eror");
				}
				@Override
				public void onStartAsync(AsyncEvent asyncEvent) throws IOException {
					System.out.println("Starting async...");
				}
			});
//			String messages = null;
//			try {
//				messages = XMLHistoryUtil.getServerResponse(index);
//			} catch (ParserConfigurationException | SAXException e) {
//				e.printStackTrace();
//			}
//			if(tasks!=null) {
//				response.setContentType(ServletUtil.APPLICATION_JSON);
//				PrintWriter out = response.getWriter();
//				out.print(messages);
//				out.flush();
//			} else {
//				JSONObject jsonList = new JSONObject();
//				jsonList.put("hash", MessageUtil.getHASH());
//				tasks= jsonList.toJSONString();
//				response.setContentType(ServletUtil.APPLICATION_JSON);
//				response.setStatus(304);
//				PrintWriter out = response.getWriter();
//				out.print(tasks);
//				out.flush();
//			}
			ScheduledThreadPoolExecutor executer = new ScheduledThreadPoolExecutor(10);
			executer.execute(new AsyncService(ac));
//			AsyncService as=new AsyncService(ac);
			System.out.println("Servlet completed request handling");
		} else {
			response.sendError(HttpServletResponse.SC_BAD_REQUEST, "'token' parameter needed");
		}
	}

	@Override
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		logger.info("doPost");
		String data = ServletUtil.getMessageBody(request);
		logger.info(data);
		try {
			JSONObject json =  MessageUtil.stringToJson(data);
			Message message =  MessageUtil.jsonToMessage(json,"POST");
			XMLHistoryUtil.addData(message);
			response.setStatus(HttpServletResponse.SC_OK);
			dataBase.add(message);
			System.out.println(message.getDate()+"  "+message.getUser()+":"+message.getMessage());
		} catch (ParseException | ParserConfigurationException | SAXException | TransformerException e) {
			logger.error(e);
			response.sendError(HttpServletResponse.SC_BAD_REQUEST);
		}
	}

	@Override
	protected void doPut(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		logger.info("doPut");
		String data = ServletUtil.getMessageBody(request);
		logger.info(data);
		try {
			JSONObject json =  MessageUtil.stringToJson(data);
			Message message =  MessageUtil.jsonToMessage(json, "PUT");
			try {
				XMLHistoryUtil.updateData(message);
				response.setStatus(HttpServletResponse.SC_OK);
				MessageUtil.updateHash();
				dataBase.update(message);
			} catch (NullPointerException e){
				response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Task does not exist");
			}
		} catch (ParseException | ParserConfigurationException | SAXException | TransformerException | XPathExpressionException e) {
			logger.error(e);
			response.sendError(HttpServletResponse.SC_BAD_REQUEST);
		}
	}

	@Override
	protected void doDelete(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		logger.info("doDelete");
		String data = ServletUtil.getMessageBody(request);
		logger.info(data);
		try {
			JSONObject json =  MessageUtil.stringToJson(data);
			Message message =  MessageUtil.jsonToMessage(json, "DELETE");
			String id = message.getId();
			try {
				XMLHistoryUtil.deleteData(id);
				MessageUtil.updateHash();
				dataBase.delete(id);
				response.setStatus(HttpServletResponse.SC_OK);
			} catch (NullPointerException e){
				response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Task does not exist");
			}
		} catch (ParseException| ParserConfigurationException | SAXException | TransformerException | XPathExpressionException  e) {
			logger.error(e);
			response.sendError(HttpServletResponse.SC_BAD_REQUEST);
		}
	}

//	private void loadHistory() throws SAXException, IOException, ParserConfigurationException, TransformerException  {
//		if (XMLHistoryUtil.doesStorageExist()) {
//			MessageStorage.addAll(XMLHistoryUtil.getMessages());
//			MessageStorage.updateHash();
//		} else {
//			XMLHistoryUtil.createStorage();
//			addStubData();
//			MessageStorage.updateHash();
//		}
//	}
	
	private void addStubData() throws ParserConfigurationException, TransformerException {

		Message[] stubTasks = {
				new Message("0","testUser","test Message","10.04-20:61"),
		};
		for (Message task : stubTasks) {
			try {
				XMLHistoryUtil.addData(task);
			} catch (ParserConfigurationException | SAXException | IOException | TransformerException e) {
				logger.error(e);
			}
		}
	}

}
