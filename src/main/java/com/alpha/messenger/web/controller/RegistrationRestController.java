package com.alpha.messenger.web.controller;

import com.alpha.messenger.persistance.model.User;
import com.alpha.messenger.registration.OnRegistrationCompleteEvent;
import com.alpha.messenger.service.IUserService;
import com.alpha.messenger.web.dto.UserDto;
import com.alpha.messenger.web.error.UserAlreadyExistException;
import com.alpha.messenger.web.util.GenericResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;

@CrossOrigin(origins = "http://localhost:4200")
@RestController
public class RegistrationRestController {

    @Autowired
    private IUserService userService;

    @Autowired
    private ApplicationEventPublisher eventPublisher;

    @PostMapping("/user/registration")
    public GenericResponse registerUserAccount(@RequestBody @Valid final UserDto accountDto, final HttpServletRequest request) {
        String result = "succes";
        GenericResponse genericResponse = null;
        try {
            final Integer registeredId = userService.registerNewUserAccount(accountDto);
//            eventPublisher.publishEvent(new OnRegistrationCompleteEvent(registeredId, request.getLocale(), getAppUrl(request)));
            genericResponse = new GenericResponse("success: " + registeredId);
        } catch (UserAlreadyExistException e) {
            System.out.println("User exist!!");
            genericResponse = new GenericResponse("User exist!!", e.getMessage());
        }
        return genericResponse;
    }


    @GetMapping("/registrationConfirm")
    public GenericResponse confirmRegistration(final HttpServletRequest request, @RequestParam("token") String token) {
        if(userService.validateVerificationToken(token) != null)
            return new GenericResponse("Successfully registration");
        return new GenericResponse("Fail");
    }


    private String getAppUrl(HttpServletRequest request) {
        return "http://" + request.getServerName() + ":" + request.getServerPort() + request.getContextPath();
    }
}