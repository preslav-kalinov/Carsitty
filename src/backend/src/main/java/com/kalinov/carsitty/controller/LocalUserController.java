package com.kalinov.carsitty.controller;

import com.kalinov.carsitty.dto.AuthenticatedUserDto;
import com.kalinov.carsitty.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class LocalUserController {
    private final UserService userService;

    @Autowired
    public LocalUserController(UserService userService) {
        this.userService = userService;
    }

    @RequestMapping(value = "/me", method = RequestMethod.GET)
    public ResponseEntity<AuthenticatedUserDto> getUser(Authentication authentication) {
        return ResponseEntity.status(HttpStatus.OK).body(this.userService.getAuthenticatedUser(authentication));
    }

    @RequestMapping(value = "/login", method = RequestMethod.POST)
    public ResponseEntity<AuthenticatedUserDto> login(Authentication authentication) {
        return ResponseEntity.status(HttpStatus.CREATED).body(this.userService.getAuthenticatedUser(authentication));
    }
}