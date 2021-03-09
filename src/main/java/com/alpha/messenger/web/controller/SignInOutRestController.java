package com.alpha.messenger.web.controller;

import com.alpha.messenger.persistance.model.User;
import com.alpha.messenger.service.IUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.function.EntityResponse;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;

@CrossOrigin(origins = "http://localhost:4200")
@RestController
public class SignInOutRestController {

    @Autowired
    private IUserService userService;


    @PostMapping("/user/login")
    public ResponseEntity<String> login(@Valid @RequestBody final User user, final HttpServletRequest request) {
        String token = "ERROR";
        try {
            if(userService.validateUser(user))
                token = userService.createJwt(user);
            if (token.isEmpty()) {
                token = "ERROR";
                return new ResponseEntity<>(token, HttpStatus.BAD_REQUEST);
            }
            return new ResponseEntity<>("Bearer " + token, HttpStatus.CREATED);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return new ResponseEntity<>(token, HttpStatus.BAD_REQUEST);
    }

    @PostMapping("/user/logout")
    public ResponseEntity<String> logout(final HttpServletRequest request) {
        String message = "";
        try {
            String token = request.getHeader("authorization").split(" ")[1];
            if(userService.validateJwt(token)){
                userService.logoutUser(token);
                message = "Successfully logout";
                return new ResponseEntity<>(message, HttpStatus.OK);
            }
        } catch (Exception e) {
            e.printStackTrace();
            message = "fail";
        }
        return new ResponseEntity<>(message, HttpStatus.BAD_REQUEST);
    }

    @GetMapping("/user/authorization")
    @ResponseBody
    public ResponseEntity<?> authorization(final HttpServletRequest request) {
        try {
            String token = request.getHeader("authorization").split(" ")[1];
            if (userService.validateJwt(token)) {
                return ResponseEntity.ok().build();
            }
            return ResponseEntity.status(401).build();

        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(500).build();
        }
    }
}