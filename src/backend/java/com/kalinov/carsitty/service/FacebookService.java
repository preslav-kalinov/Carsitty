package com.kalinov.carsitty.service;

import com.restfb.FacebookClient;
import com.restfb.Parameter;
import com.restfb.types.FacebookType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class FacebookService {
    private final FacebookClient facebookClient;

    @Value("${restfb.page.id}")
    private String pageId;

    @Autowired
    public FacebookService(FacebookClient facebookClient) {
        this.facebookClient = facebookClient;
    }

    public FacebookType postTextToFacebook(String message) {
        return this.facebookClient.publish(
                String.format("%s/feed", this.pageId),
                FacebookType.class,
                Parameter.with("message", message)
        );
    }
}