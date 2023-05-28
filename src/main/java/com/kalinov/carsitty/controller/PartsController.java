package com.kalinov.carsitty.controller;

import com.kalinov.carsitty.dao.UserDao;
import com.kalinov.carsitty.dto.PartDto;
import com.kalinov.carsitty.dto.SaleDto;
import com.kalinov.carsitty.entity.Car;
import com.kalinov.carsitty.entity.Category;
import com.kalinov.carsitty.entity.User;
import com.kalinov.carsitty.service.PartService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

@RestController
@RequestMapping("/parts")
public class PartsController {
    private final PartService partService;
    private final UserDao userDao;

    @Autowired
    public PartsController (PartService partService, UserDao userDao) {
        this.partService = partService;
        this.userDao = userDao;
    }

    @RequestMapping(method = RequestMethod.GET)
    public ResponseEntity getParts() {
        return ResponseEntity.status(HttpStatus.OK).body(this.partService.getAllParts());
    }

    @RequestMapping(value = "/{partId}", method = RequestMethod.GET)
    public ResponseEntity<PartDto> getPart(@PathVariable Long partId) {
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
    public ResponseEntity createPart(@Valid @RequestBody PartDto partDto, Authentication authentication) {
        User user = userDao.getUsersByUsername(authentication.getName()).get(0);
        return ResponseEntity.status(HttpStatus.CREATED).body(this.partService.createPart(partDto, user));
    }

    @RequestMapping(value = "/{partId}", consumes = MediaType.APPLICATION_JSON_VALUE, method = RequestMethod.PUT)
    public ResponseEntity editPart(@Valid @RequestBody PartDto partDto, @PathVariable Long partId, Authentication authentication) {
        User user = userDao.getUsersByUsername(authentication.getName()).get(0);
        this.partService.updatePart(partId, partDto, user);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }

    @RequestMapping(value = "/{partId}", method = RequestMethod.DELETE)
    public ResponseEntity deletePart(@PathVariable Long partId) {
        partService.deletePart(partId);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }

    @RequestMapping(value = "/{partId}/sale", consumes = MediaType.APPLICATION_JSON_VALUE, method = RequestMethod.POST)
    public ResponseEntity sellPart(@RequestBody SaleDto saleDto, @PathVariable Long partId, Authentication authentication) {
        User user = userDao.getUsersByUsername(authentication.getName()).get(0);
        return ResponseEntity.status(HttpStatus.CREATED).body(this.partService.sellPart(saleDto, partId, user));
    }
}