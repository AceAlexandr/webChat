package sanyaaaaaa.db;

import sanyaaaaaa.model.Message;
import sanyaaaaaa.storage.xml.XMLHistoryUtil;
import org.apache.log4j.Logger;
import org.xml.sax.SAXException;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.TransformerException;
import java.io.IOException;
import java.sql.*;


public class ChatDataBase implements ChatDataBaseInterface {
    private static Logger logger = Logger.getLogger(ChatDataBase.class.getName());

    @Override
    public void add(Message message) {
        Connection connection = null;
        PreparedStatement preparedStatement = null;
        try {
            connection = ConnectionManager.getConnection();
            preparedStatement = connection.prepareStatement("SELECT * FROM chat.users WHERE name=?");
            preparedStatement.setString(1,message.getUser());
            ResultSet userResultSet=preparedStatement.executeQuery();
            if(userResultSet.next()) {
                message.setUser(userResultSet.getString("name"));
            } else {
                preparedStatement = connection.prepareStatement("INSERT INTO users (id, name ) VALUES (?, ?)");
                preparedStatement.setString(1, message.getId());
                preparedStatement.setString(2,message.getUser());
                preparedStatement.executeUpdate();
            }
            connection = ConnectionManager.getConnection();
            preparedStatement = connection.prepareStatement("INSERT INTO messages (id, text, date, user_id ) VALUES (?, ?, ?, ?)");
            preparedStatement.setString(1, message.getId());
            preparedStatement.setString(2,message.getMessage());
            preparedStatement.setString(3, message.getDate());
            preparedStatement.setString(4, "1");
            preparedStatement.executeUpdate();
        } catch (SQLException e) {
            logger.error(e);
        } finally {
            try {
                if (preparedStatement != null) {
                    preparedStatement.close();
                }
                if (connection != null) {
                    connection.close();
                }
            } catch (SQLException e) {
                logger.error(e);
            }
        }
    }

    @Override
    public void update(Message message) {
        Connection connection = null;
        PreparedStatement preparedStatement = null;
        try {
            connection = ConnectionManager.getConnection();
            preparedStatement = connection.prepareStatement("UPDATE chat.messages SET text=? WHERE id=?;");
            preparedStatement.setString(1, message.getMessage());
            preparedStatement.setString(2, message.getId());
            preparedStatement.executeUpdate();
        } catch (SQLException e) {
            logger.error(e);
        } finally {
            try {
                if (preparedStatement != null) {
                    preparedStatement.close();
                }
                if (connection != null) {
                    connection.close();
                }
            } catch (SQLException e) {
                logger.error(e);
            }
        }
    }

    @Override
    public void delete(String id) {
//        DELETE FROM `chat`.`messages` WHERE `id`='123';
        Connection connection = null;
        PreparedStatement preparedStatement = null;
        try {
            connection = ConnectionManager.getConnection();
            preparedStatement = connection.prepareStatement("DELETE FROM chat.messages WHERE id=?;");
            preparedStatement.setString(1, id);
            preparedStatement.executeUpdate();
        } catch (SQLException e) {
            logger.error(e);
        } finally {
            try {
                if (preparedStatement != null) {
                    preparedStatement.close();
                }
                if (connection != null) {
                    connection.close();
                }
            } catch (SQLException e) {
                logger.error(e);
            }
        }
    }

    @Override
    public void loadAllMessages() {
        Message message=new Message();
        Connection connection = null;
        Statement statement = null;
        PreparedStatement preparedStatement = null;
        ResultSet resultSet = null;
        ResultSet userResultSet = null;
        try {
            connection = ConnectionManager.getConnection();
            statement = connection.createStatement();
            resultSet = statement.executeQuery("SELECT * FROM chat.messages");
            while (resultSet.next()) {
//                SELECT * FROM chat.users WHERE id=1;
                String userID=resultSet.getString("user_id");
                preparedStatement = connection.prepareStatement("SELECT * FROM chat.users WHERE id=?;");
                preparedStatement.setString(1,userID);
                userResultSet=preparedStatement.executeQuery();
                userResultSet.next();
                message.setUser(userResultSet.getString("name"));

                message.setId(resultSet.getString("id"));
                message.setMessage(resultSet.getString("text"));
                message.setDate(resultSet.getString("date"));
                XMLHistoryUtil.addData(message);
            }
        } catch (SQLException e) {
            logger.error(e);
        } catch (ParserConfigurationException | TransformerException | SAXException | IOException e) {
            e.printStackTrace();
        } finally {
            try {
                if (resultSet != null) {
                    resultSet.close();
                }
                if (statement != null) {
                    statement.close();
                }
                if (connection != null) {
                    connection.close();
                }

            }  catch (SQLException e) {
            logger.error(e);
        }
        }

    }
}
