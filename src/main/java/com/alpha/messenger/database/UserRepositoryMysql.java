package com.alpha.messenger.database;

import com.alpha.messenger.persistance.model.Friend;
import com.alpha.messenger.persistance.model.Message;
import com.alpha.messenger.persistance.model.User;
import com.alpha.messenger.web.error.UserAlreadyExistException;
import com.alpha.messenger.web.util.MyJWT;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Service;

import java.security.Key;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.Base64;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;

@Service
public class UserRepositoryMysql implements IUserDatabase{

    @Autowired
    JdbcTemplate jdbcTemplate;

    @Override
    public boolean existUserByMail(String mail) {
        try {
            String sql = "SELECT EXISTS(SELECT 1 FROM Users WHERE mail = ?) ";
            return jdbcTemplate.queryForObject(sql,  new Object[] { mail }, Boolean.class );
        }catch (UserAlreadyExistException e) {
            e.printStackTrace();
        }
        return false;
    }

    @Override
    public Integer insertUser(User user) {
        try {
            this.jdbcTemplate.update("INSERT INTO Users(username, password, fullname, mail, status, image) VALUES(?, ?, ?, ?, ?, ?)",
                    user.getUsername(), user.getPassword(), user.getFullName(), user.getMail(), user.getStatusAccount(), user.getImage());
            Integer id = this.jdbcTemplate.queryForObject("select id from Users where mail=?", new Object[] {user.getMail()}, Integer.class);
            return id;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public boolean activateAccount(User user, String token) {
        return false;
    }

    @Override
    public boolean saveActivationAccountToken(User user, String token) {
        return false;
    }

    @Override
    public User getUserByToken(String token) {
        try {
            String sql = "SELECT user_id from Session where jwt=?";
            Integer id = jdbcTemplate.queryForObject(sql, new Object[]{token}, Integer.class);
            if(id != null) {
                sql = "SELECT username, fullname, mail, status, image from Users where id=?";
                List<User> users = jdbcTemplate.query(sql, new Object[]{id}, new RowMapper<User>() {
                    @Override
                    public User mapRow(ResultSet resultSet, int i) throws SQLException {
                        User tmp = new User();
                        tmp.setUsername(resultSet.getString(1));
                        tmp.setFullName(resultSet.getString(2));
                        tmp.setMail(resultSet.getString(3));
                        tmp.setStatusAccount(resultSet.getBoolean(4));
                        tmp.setImage(resultSet.getString(5));
                        return tmp;
                    }
                });
                return users.get(0);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public boolean validateUser(User user) {
        try {
            String sql = "SELECT EXISTS(SELECT 1 FROM Users WHERE (mail = ? or username=?) and password=?) ";
            return jdbcTemplate.queryForObject(sql,  new Object[] { user.getMail(), user.getUsername(), user.getPassword() }, Boolean.class );
        }catch (UserAlreadyExistException e) {
            e.printStackTrace();
        }
        return false;
    }

    @Override
    public boolean validateJwt(String jwt) {
        try {
            String sql = "SELECT EXISTS(SELECT 1 FROM Session WHERE jwt=?)";
            boolean exists = jdbcTemplate.queryForObject(sql,  new Object[] { jwt }, Boolean.class );
            MyJWT myJWT = new MyJWT(jwt);
            return !myJWT.isExpirated() && exists;
        }catch (UserAlreadyExistException e) {
            e.printStackTrace();
        }
        return false;
    }

    @Override
    public String createJwt(User user) {
        try {
            if(validateUser(user)) {
                MyJWT jwt = new MyJWT();
                jwt.generate(user);
                Integer idUser = getIdByUser(user);
                if (idUser != null) {
                    String sql = "REPLACE INTO Session(user_id, jwt, created_at) VALUES(?, ?, ?)";
                    jdbcTemplate.update(sql, idUser, jwt.getToken(), new Timestamp(new Date().getTime()));
                    return jwt.getToken();
                }
            }
        } catch (Exception e){
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public User findByJWT(String token) {
        return null;
    }

    @Override
    public boolean removeJwt(String jwt) {
        return false;
    }

    @Override
    public Integer getIdByUser(User user) {
        try {
            String sql = "SELECT id from Users where username=? or mail=?";
            Integer id = jdbcTemplate.queryForObject(sql, new Object[] {user.getUsername(), user.getMail()}, Integer.class);
            return id;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public Integer getIdByUsername(String username) {
        try {
            String sql = "SELECT id from Users where username=?";
            Integer id = jdbcTemplate.queryForObject(sql, new Object[] {username}, Integer.class);
            return id;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public List<Friend> getFriends(Integer userId) {
        List<Friend> friends = new LinkedList<>();
        try {
            String sql = "SELECT u.username, fullname, image, online, start_date from Users u, OnlineUsers o WHERE id != ? and u.username=o.username";
            friends = jdbcTemplate.query(sql, new Object[]{userId}, new RowMapper<Friend>() {
                @Override
                public Friend mapRow(ResultSet resultSet, int i) throws SQLException {
                    Friend f = new Friend();
                    f.setUsername(resultSet.getString(1));
                    f.setFullName(resultSet.getString(2));
                    f.setImage(resultSet.getString(3));
                    f.setOnlineStatus(resultSet.getString(4));
                    f.setLastSeenAt(resultSet.getString(5));
                    return f;
                }
            });
        } catch (Exception e) {
          e.printStackTrace();
        }
        return friends;
    }

    @Override
    public boolean makeOnline(String token) {
        try{
            if(validateJwt(token)){
                User user = getUserByToken(token);
                String sql = "REPLACE INTO OnlineUsers(username, online, start_date) VALUES(?, ?, ?)";
                this.jdbcTemplate.update(sql, user.getUsername(), "true", new Timestamp(new Date().getTime()));
                return true;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    @Override
    public boolean makeOffline(String token) {
        try {
            if(validateJwt(token)){
                User user = getUserByToken(token);
                String sql = "REPLACE INTO OnlineUsers(username, online, start_date) VALUES(?, ?, ?)";
                this.jdbcTemplate.update(sql, user.getUsername(), "false", new Timestamp(new Date().getTime()));
                return true;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    @Override
    public List<Message> getMessages(String token) {
        List<Message> messages = null;
        if(validateJwt(token)){
            User user = getUserByToken(token);
            String sql = "SELECT * FROM Messages";
            try{
                messages = this.jdbcTemplate.query(sql, new RowMapper<Message>() {
                    @Override
                    public Message mapRow(ResultSet resultSet, int i) throws SQLException {
                        Message message = new Message();
                        message.setFrom(resultSet.getString("from"));
                        message.setBody(resultSet.getString("message"));
                        message.setTimestamp(resultSet.getLong("timestamp"));
                        message.setType(resultSet.getString("type_message"));
                        return message;
                    }
                });
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return messages;
    }

    @Override
    public Integer store(Message message) {
        String sql = "INSERT INTO Messages(`from`, message, timestamp, type_message) VALUES(?, ?, ?, ?)";
        try{
            Integer rowModified = this.jdbcTemplate.update(sql, message.getFrom(), message.getBody(),
                    message.getTimestamp(), message.getType());
            return rowModified;
        }catch (Exception e) {
            e.printStackTrace();
            return -1;
        }
    }
}
