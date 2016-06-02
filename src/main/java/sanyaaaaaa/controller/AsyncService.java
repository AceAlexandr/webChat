package sanyaaaaaa.controller;

import sanyaaaaaa.model.Message;
import sanyaaaaaa.storage.xml.XMLHistoryUtil;
import sanyaaaaaa.util.MessageUtil;
import sanyaaaaaa.util.ServletUtil;
import org.xml.sax.SAXException;

import javax.servlet.AsyncContext;
import javax.servlet.http.HttpServletResponse;
import javax.xml.parsers.ParserConfigurationException;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;


public class AsyncService implements Runnable {
    private AsyncContext aContext;
    private String hash;
//    private String hash="-1";
    public AsyncService(AsyncContext a){
        this.aContext=a;
        this.hash=MessageUtil.getHASH();
    }

    @Override
    public void run() {
        String token= aContext.getRequest().getParameter(MessageUtil.TOKEN);
        int index = MessageUtil.getIndex(token);
        boolean flag=true;
        List<Message> messages=null;
        while(flag){
            try {
                messages = XMLHistoryUtil.getMessages(index);
                if(messages.size()>0||!(this.hash).equals(MessageUtil.getHASH())){
                    flag=false;
                } else {
                    Thread.sleep(1500);
                }
            } catch (SAXException |IOException |ParserConfigurationException |InterruptedException e) {
                e.printStackTrace();
            }
        }
        try {
            String mesResp=XMLHistoryUtil.getServerResponse(index);
            HttpServletResponse response= (HttpServletResponse) aContext.getResponse();
            response.setContentType(ServletUtil.APPLICATION_JSON);
            PrintWriter out = response.getWriter();
            out.print(mesResp);
            out.flush();
            aContext.complete();
            out.close();
        } catch (SAXException |IOException |ParserConfigurationException  e) {
            e.printStackTrace();
        }

    }
}
