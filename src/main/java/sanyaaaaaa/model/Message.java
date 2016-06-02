package sanyaaaaaa.model;



import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

public class Message {
    private String id;
    private String user;
    private String message;
    private String date;
   //sdf
    public Message(){}

    public Message(String id, String user, String message, String date) {
        this.id = id;
        this.user = user;
        this.message = message;
        this.date = date;
    }

    public void setUniqueID(String s){
        Date date = new Date();
        DateFormat idDateFormat = new SimpleDateFormat("ddMMHHmmss");

        this.id=idDateFormat.format(date)+s;
    }


    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public void setDate() {
        DateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
        Date date = new Date();
        this.date=dateFormat.format(date);
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {

        this.id=id;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getUser() {
        return user;
    }

    public void setUser(String user) {
        this.user = user;
    }



    @Override
    public String toString() {
        String str;
        str="date:"+this.getDate()+", user:"+this.getUser()+", id:" +this.getId()+", messageText:"+this.getMessage();
        return str;
    }
}
