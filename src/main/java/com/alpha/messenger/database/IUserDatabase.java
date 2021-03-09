package com.alpha.messenger.database;

import com.alpha.messenger.persistance.model.Friend;
import com.alpha.messenger.persistance.model.Message;
import com.alpha.messenger.persistance.model.User;

import java.util.List;

public interface IUserDatabase {
    public boolean existUserByMail(String email);
    public Integer insertUser(User user);
    public boolean activateAccount(User user, String token);
    public boolean saveActivationAccountToken(User user, String token);
    public User getUserByToken(String token);
    public boolean validateUser(User user);
    public boolean validateJwt(String jwt);
    public String createJwt(User user);
    public User findByJWT(String token);
    public boolean removeJwt(String jwt);
    public Integer getIdByUser(User user);
    public List<Friend> getFriends(Integer userId);
    public boolean makeOnline(String token);
    public boolean makeOffline(String token);
    public List<Message> getMessages(String token);
    public String getPrivateKeyServer(String username);
    public String getPublicKeyClient(String username);
    String generatePairKey(String token);
    Integer updatePublicClientKey(String token, String pbKey);

    Integer store(Message message);
}