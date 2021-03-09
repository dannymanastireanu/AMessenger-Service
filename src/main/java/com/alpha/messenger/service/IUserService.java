package com.alpha.messenger.service;


import com.alpha.messenger.persistance.model.User;
import com.alpha.messenger.web.dto.UserDto;
import com.alpha.messenger.web.error.UserAlreadyExistException;

public interface IUserService {
    public Integer registerNewUserAccount(UserDto accountDto) throws UserAlreadyExistException;
    public String validateVerificationToken(String token);
    public void createVerificationTokenForUser(User user, String token);
    public String createJwt(User user);
    public boolean validateJwt(String jwt);
    public boolean validateUser(User user);
    public boolean logoutUser(String jwt);
}