package sanyaaaaaa.util;

import sanyaaaaaa.model.Message;
import sanyaaaaaa.storage.xml.XMLHistoryUtil;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.xml.sax.SAXException;

import javax.xml.parsers.ParserConfigurationException;
import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

public final class MessageUtil {
    public static final String TOKEN = "token";
    public static final String MESSAGE = "message";
    public static final String MESSAGES = "messages";
    private static final String TN = "TN";
    private static final String EN = "EN";
    private static final String ID = "id";
    private static final String METHOD = "method";
    private static final String DATE = "date";
    private static final String USER = "user";
    public static final String MESSAGETEXT = "messageText";

    public static String getHASH() {
        return new String(HASH);
    }

    private static String HASH="";

    public static void updateHash() throws ParserConfigurationException, SAXException, IOException {
        Date date = new Date();
        DateFormat idDateFormat = new SimpleDateFormat("ddMMHHmmss");
        HASH=idDateFormat.format(date)+Integer.toString(XMLHistoryUtil.getStorageSize() + 1);
    }


    private MessageUtil() {
    }

    public static String getToken(int index) {
        Integer number = index * 8 + 11;
        return TN + number + EN;
    }

    public static int getIndex(String token) {
        return (Integer.valueOf(token.substring(2, token.length() - 2)) - 11) / 8;
    }

    public static JSONObject stringToJson(String data) throws ParseException {
        JSONParser parser = new JSONParser();
        return (JSONObject) parser.parse(data.trim());
    }

    public static Message jsonToMessage(JSONObject json, String method) throws ParserConfigurationException, SAXException, IOException {
        if("POST".equals(method)) {
            Message message = new Message();
            message.setUniqueID(Integer.toString(XMLHistoryUtil.getStorageSize() + 1));
            message.setMessage((String) json.get(MESSAGETEXT));
            message.setUser((String) json.get(USER));
            message.setDate();
            if (message.getId() != null && message.getMessage() != null && message.getUser() != null) {
                return message;
            }
        } else if("PUT".equals(method)){
            Message message = new Message();
            message.setMessage((String) json.get(MESSAGETEXT));
            message.setId((String) json.get(ID));
            if (message.getId() != null && message.getMessage() != null) {
                return message;
            }
        } else if("DELETE".equals(method)){
            Message message = new Message();
            message.setMessage((String) json.get(MESSAGETEXT));
            message.setId((String) json.get(ID));
            return message;
        }
        return null;
    }
}
