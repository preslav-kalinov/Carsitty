package com.kalinov.carsitty.controller;

import com.kalinov.carsitty.RoleEnum;
import com.kalinov.carsitty.dto.UpdatedUserDto;
import com.kalinov.carsitty.dto.UserDto;
import com.kalinov.carsitty.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

@RestController
@RequestMapping("/users")
public class UsersController {
    private final UserService userService;

    @Autowired
    public UsersController(UserService userService) {
        this.userService = userService;
    }

    @RequestMapping(method = RequestMethod.GET)
    public ResponseEntity<List<UserDto>> getUsers() {
        return ResponseEntity.status(HttpStatus.OK).body(this.userService.getAllUsers());
    }

    @RequestMapping(value = "/{username}", method = RequestMethod.GET)
    public ResponseEntity<UserDto> getUser(@PathVariable String username) {
        return ResponseEntity.status(HttpStatus.OK).body(this.userService.getUser(username));
    }

    @RequestMapping(value = "/employees", consumes = MediaType.APPLICATION_JSON_VALUE, method = RequestMethod.POST)
    public ResponseEntity<Void> createEmployee(@Valid @RequestBody UserDto userDto) throws NoSuchMethodException, MethodArgumentNotValidException {
        this.userService.createUser(userDto, RoleEnum.Employee);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    @RequestMapping(value = "/managers", consumes = MediaType.APPLICATION_JSON_VALUE, method = RequestMethod.POST)
    public ResponseEntity<Void> createManager(@Valid @RequestBody UserDto userDto) throws MethodArgumentNotValidException, NoSuchMethodException {
        this.userService.createUser(userDto, RoleEnum.Manager);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    @RequestMapping(value = "/employees/{username}", consumes = MediaType.APPLICATION_JSON_VALUE, method = RequestMethod.PATCH)
    public ResponseEntity<Void> updateEmployee(@PathVariable String username, @Valid @RequestBody UpdatedUserDto updatedUserDto) throws NoSuchMethodException, MethodArgumentNotValidException {
        this.userService.updateUser(username, RoleEnum.Employee, updatedUserDto);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }

    @RequestMapping(value = "/managers/{username}", consumes = MediaType.APPLICATION_JSON_VALUE, method = RequestMethod.PATCH)
    public ResponseEntity<Void> updateManager(@PathVariable String username, @Valid @RequestBody UpdatedUserDto updatedUserDto) throws NoSuchMethodException, MethodArgumentNotValidException {
        this.userService.updateUser(username, RoleEnum.Manager, updatedUserDto);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }

    @RequestMapping(value = "/employees/{username}", method = RequestMethod.DELETE)
    public ResponseEntity<Void> deleteEmployee(@PathVariable String username) {
        this.userService.deleteUser(username, RoleEnum.Employee);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }

    @RequestMapping(value = "/managers/{username}", method = RequestMethod.DELETE)
    public ResponseEntity<Void> deleteManager(@PathVariable String username) {
        this.userService.deleteUser(username, RoleEnum.Manager);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }
}