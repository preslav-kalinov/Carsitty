package com.kalinov.carsitty.controller;

import com.kalinov.carsitty.dao.UserDao;
import com.kalinov.carsitty.dto.*;
import com.kalinov.carsitty.entity.Car;
import com.kalinov.carsitty.entity.Category;
import com.kalinov.carsitty.entity.Part;
import com.kalinov.carsitty.entity.User;
import com.kalinov.carsitty.service.LogService;
import com.kalinov.carsitty.service.PartService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import javax.mail.MessagingException;
import javax.validation.Valid;
import java.io.IOException;
import java.util.List;

@RestController
@RequestMapping("/parts")
public class PartsController {
    private final PartService partService;
    private final LogService logService;
    private final UserDao userDao;

    @Autowired
    public PartsController (PartService partService, LogService logService, UserDao userDao) {
        this.partService = partService;
        this.logService = logService;
        this.userDao = userDao;
    }

    @RequestMapping(method = RequestMethod.GET)
    public ResponseEntity getParts() {
        return ResponseEntity.status(HttpStatus.OK).body(this.partService.getAllParts());
    }

    @RequestMapping(value = "/{partId}", method = RequestMethod.GET)
    public ResponseEntity<PartDto> getPart(@PathVariable Long partId) throws IOException {
        return ResponseEntity.status(HttpStatus.OK).body(this.partService.getPart(partId));
    }

    @RequestMapping(value = "/categories", method = RequestMethod.GET)
    public ResponseEntity getCategories() {
        List<Category> categoryList = this.partService.getAllCategories();
        return ResponseEntity.status(HttpStatus.OK).body(categoryList);
    }

    @RequestMapping(value = "/cars", method = RequestMethod.GET)
    public ResponseEntity getCars() {
        List<Car> carList = this.partService.getAllCars();
        return ResponseEntity.status(HttpStatus.OK).body(carList);
    }

    @RequestMapping(consumes = MediaType.APPLICATION_JSON_VALUE, method = RequestMethod.POST)
    public ResponseEntity<Part> createPart(@Valid @RequestBody NewPartDto newPartDto, Authentication authentication) throws IOException {
        User user = userDao.getUsersByUsername(authentication.getName()).get(0);
        return ResponseEntity.status(HttpStatus.CREATED).body(this.partService.createPart(newPartDto, user));
    }

    @RequestMapping(value = "/{partId}", consumes = MediaType.APPLICATION_JSON_VALUE, method = RequestMethod.PUT)
    public ResponseEntity editPart(@Valid @RequestBody NewPartDto newPartDto, @PathVariable Long partId, Authentication authentication) throws IOException {
        User user = userDao.getUsersByUsername(authentication.getName()).get(0);
        this.partService.updatePart(partId, newPartDto, user);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }

    @RequestMapping(value = "/{partId}", method = RequestMethod.DELETE)
    public ResponseEntity deletePart(@PathVariable Long partId, Authentication authentication) throws IOException {
        User user = userDao.getUsersByUsername(authentication.getName()).get(0);
        partService.deletePart(partId, user);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }

    @RequestMapping(value = "/{partId}/sale", consumes = MediaType.APPLICATION_JSON_VALUE, method = RequestMethod.POST)
    public ResponseEntity sellPart(@Valid @RequestBody SaleDto saleDto, @PathVariable Long partId, Authentication authentication) throws MessagingException, IOException {
        User user = userDao.getUsersByUsername(authentication.getName()).get(0);
        return ResponseEntity.status(HttpStatus.CREATED).body(this.partService.sellPart(saleDto, partId, user));
    }

    @RequestMapping(value = "/logs", method = RequestMethod.GET)
    public ResponseEntity<List<LogDto>> getPartLogs() {
        return ResponseEntity.status(HttpStatus.OK).body(this.logService.getLogs());
    }
}