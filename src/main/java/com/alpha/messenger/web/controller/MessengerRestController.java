package com.alpha.messenger.web.controller;

import com.alpha.messenger.database.IUserDatabase;
import com.alpha.messenger.persistance.model.Friend;
import com.alpha.messenger.persistance.model.Message;
import com.alpha.messenger.persistance.model.User;
import com.alpha.messenger.service.IUserService;
import com.alpha.messenger.web.util.Encryption;
import com.alpha.messenger.web.util.MyJWT;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import java.util.LinkedList;
import java.util.List;

@CrossOrigin(origins = "http://localhost:4200")
@RestController
public class MessengerRestController {

    @Autowired
    private IUserDatabase userService;

    @Autowired
    private SimpMessagingTemplate smtp;

    @MessageMapping("/chat/{to}")
    public void sendMessage(@DestinationVariable String to, Message message) {
        // broadcast
        if(to.equals("all")) {
            Integer id = userService.getIdByUsername(message.getFrom());
            List<Friend> friends = userService.getFriends(id);
            friends.forEach(f -> {
                smtp.convertAndSend("/topic/messages/" + f.getUsername(), message);
            });
            userService.store(message);
        }
    }

    @GetMapping("/user/friends")
    @ResponseBody
    public ResponseEntity<List<Friend>> getFriends(final HttpServletRequest request) {
        String token = request.getHeader("authorization").split(" ")[1];
        if (userService.validateJwt(token)) {
            User user = userService.getUserByToken(token);
            if(user != null) {
                Integer id = userService.getIdByUser(user);
                List<Friend> friends = userService.getFriends(id);
                return new ResponseEntity<List<Friend>>(friends, HttpStatus.OK);
            }
        }
        return new ResponseEntity<>(null, HttpStatus.BAD_REQUEST);
    }

    @GetMapping("/user/messages")
    @ResponseBody
    public ResponseEntity<?> getMessages(final HttpServletRequest request) {
        try{
            String token = request.getHeader("authorization").split(" ")[1];
            if(this.userService.validateJwt(token)) {
                List<Message> messages = userService.getMessages(token);
                return new ResponseEntity<>(messages, HttpStatus.OK);
            }
        }catch (Exception e) {
            e.printStackTrace();
        }
        return new ResponseEntity<>(null, HttpStatus.BAD_REQUEST);
    }

    @GetMapping("/user")
    @ResponseBody
    public ResponseEntity<?> getUser(final HttpServletRequest request) {
        try{
            String token = request.getHeader("authorization").split(" ")[1];
            if(this.userService.validateJwt(token)) {
                User user = userService.getUserByToken(token);
                return new ResponseEntity<>(user, HttpStatus.OK);
            }
        }catch (Exception e) {
            e.printStackTrace();
        }
        return new ResponseEntity<>(null, HttpStatus.BAD_REQUEST);
    }

    @PostMapping("/user/messenger/onlinestatus")
    @ResponseBody
    public ResponseEntity<?> changeOnlineStatus(final HttpServletRequest request) {
        try {
            String token = request.getHeader("authorization").split(" ")[1];
            if(this.userService.makeOnline(token)) {
                return ResponseEntity.ok().build();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return ResponseEntity.status(204).build();
    }

    @PostMapping(value = "/user/messenger/offlinestatus")
    public ResponseEntity<?> changeOfflineStatus(final HttpServletRequest request) {
        try {
            String token = request.getHeader("authorization").split(" ")[1];
            if(this.userService.makeOffline(token)) {
                return ResponseEntity.ok().build();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return ResponseEntity.status(204).build();
    }
}