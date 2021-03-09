package com.alpha.messenger.service;

import com.alpha.messenger.persistance.model.User;
import com.alpha.messenger.database.IUserDatabase;
import com.alpha.messenger.web.dto.UserDto;
import com.alpha.messenger.web.error.UserAlreadyExistException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class UserService implements IUserService {

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private IUserDatabase userDB;


    @Override
    public Integer registerNewUserAccount(UserDto accountDto) throws UserAlreadyExistException {
        final User user = new User();
        if(!userDB.existUserByMail(accountDto.getEmail())) {
            user.setUsername(accountDto.getUsername());
            user.setMail(accountDto.getEmail());
            user.setPassword(passwordEncoder.encode(accountDto.getPassword()));
            if(accountDto.getStatus() == null)
                user.setStatusAccount(false);
            user.setFullName(accountDto.getFullName());
            return userDB.insertUser(user);
        }
        throw new UserAlreadyExistException();
    }

    @Override
    public String validateVerificationToken(String token) {
        try{
            User user = userDB.getUserByToken(token);
            if(user != null)
                userDB.activateAccount(user, token);
            return token;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public void createVerificationTokenForUser(User user, String token) {
        userDB.saveActivationAccountToken(user, token);
    }

    @Override
    public String createJwt(User user) {
        return userDB.createJwt(user);
    }

    @Override
    public boolean validateJwt(String jwt) {
        return userDB.validateJwt(jwt);
    }

    @Override
    public boolean validateUser(User user) {
        return userDB.validateUser(user);
    }

    @Override
    public boolean logoutUser(String jwt) {
        return userDB.removeJwt(jwt);
    }


}