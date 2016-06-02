package sanyaaaaaa.db;

import sanyaaaaaa.model.Message;


public interface ChatDataBaseInterface {
    void add(Message message);

    void update(Message message);

    void delete(String id);

    void loadAllMessages();
}
