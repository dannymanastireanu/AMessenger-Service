package com.alpha.messenger.registration;

import com.alpha.messenger.persistance.model.User;
import org.springframework.context.ApplicationEvent;

import java.util.Locale;

public class OnRegistrationCompleteEvent extends ApplicationEvent {

    private final String appUrl;
    private final Locale locale;
    private final Integer IdUser;

    public OnRegistrationCompleteEvent(final Integer IdUser, final Locale locale, final String appUrl) {
        super(IdUser);
        this.IdUser = IdUser;
        this.locale = locale;
        this.appUrl = appUrl;
    }

    public String getAppUrl() {
        return appUrl;
    }

    public Locale getLocale() {
        return locale;
    }

    public Integer getIdUser() {
        return IdUser;
    }
}