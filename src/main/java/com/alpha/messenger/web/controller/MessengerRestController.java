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
        if(message.getType().equals("text")) {
            String pvtKey = this.userService.getPrivateKeyServer(message.getFrom());
            if(pvtKey != null) {
                String decryptMessage = Encryption.decryptRSA(message.getBody(), pvtKey);
                message.setBody(decryptMessage);
                Integer result = this.userService.store(message);
                if(result != -1){
                    System.out.println("Successfully stored messages");
                }
                String pbKey = this.userService.getPublicKeyClient(message.getFrom());
                String encryptedMessage = Encryption.encryptRSA(message.getBody(), pbKey);
                message.setBody(encryptedMessage);
                smtp.convertAndSend("/topic/messages/" + to, message);
            } else {
                System.out.println("Null private key");
            }
        } else if(message.getType().equals("image")) {
            Integer result = this.userService.store(message);
            if(result != -1){
                System.out.println("Successfully stored messages");
            }
            smtp.convertAndSend("/topic/messages/" + to, message);
        } else{
            System.out.println("IDK format");
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

    @PostMapping("/user/pubKey")
    @ResponseBody
    public ResponseEntity<?> getPublicServerKey(final HttpServletRequest request) {
        try {
            String token = request.getHeader("authorization").split(" ")[1];
            String pbKey = this.userService.generatePairKey(token);
            if (!pbKey.isEmpty())
                return new ResponseEntity<>(pbKey, HttpStatus.CREATED);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
    }

    @PostMapping("/pubKey")
    @ResponseBody
    public ResponseEntity<?> setPublicClientKey(final HttpServletRequest request, @RequestBody String pbKey) {
        try {
            String token = request.getHeader("authorization").split(" ")[1];
            Integer result = this.userService.updatePublicClientKey(token, pbKey);
            if (result != -1)
                return new ResponseEntity<>(HttpStatus.CREATED);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return new ResponseEntity<>(HttpStatus.NOT_FOUND);
    }

}