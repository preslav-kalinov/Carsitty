package com.kalinov.carsitty.service;

import com.kalinov.carsitty.dao.*;
import com.kalinov.carsitty.dto.NewPartDto;
import com.kalinov.carsitty.dto.PartDto;
import com.kalinov.carsitty.dto.SaleDto;
import com.kalinov.carsitty.entity.*;
import com.kalinov.carsitty.util.ModelMapperUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.math.BigDecimal;
import java.sql.Date;
import java.time.LocalDate;
import java.time.ZoneOffset;
import java.util.List;

@Service
public class PartService {
    private final PartDao partDao;
    private final CarDao carDao;
    private final LogService logService;
    private final CategoryDao categoryDao;
    private final SaleDao saleDao;
    private final ModelMapperUtil modelMapper;

    @Autowired
    public PartService(PartDao partDao, CarDao carDao, LogService logService,
                       CategoryDao categoryDao, SaleDao saleDao, ModelMapperUtil modelMapper) {
        this.partDao = partDao;
        this.carDao = carDao;
        this.logService = logService;
        this.categoryDao = categoryDao;
        this.saleDao = saleDao;
        this.modelMapper = modelMapper;
    }

    //operation to get a part by id
    public PartDto getPart(Long id) {
        this.validatePartId(id);
        return this.modelMapper.map(this.partDao.findById(id).get(), PartDto.class);
    }

    //operation to get all parts
    public List<Part> getAllParts()  {
        if (this.partDao.count() == 0) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "There are no available parts");
        }

        return this.partDao.findAll();
    }

    //operation to add new part
    public Part createPart(NewPartDto newPartDto, User user) {
        this.validatePartData(newPartDto);
        Category category = this.categoryDao.findById(newPartDto.getCategoryId()).get();
        Car car = this.carDao.findById(newPartDto.getCarId()).get();

        Part part = new Part();
        part.setName(newPartDto.getName());
        part.setQuantity(newPartDto.getQuantity());
        part.setPrice(newPartDto.getPrice());
        part.setCategory(category);
        part.setCar(car);
        part.setUser(user);

        this.logService.logCreatedPart(part.getId(), part.getName());

        return this.partDao.save(part);
    }

    //operation to edit existing part
    public void updatePart(Long id, NewPartDto newPartDto, User user) {
        this.validatePartId(id);
        this.validatePartData(newPartDto);
        Category category = categoryDao.findById(newPartDto.getCategoryId()).get();
        Car car = carDao.findById(newPartDto.getCarId()).get();

        Part part = partDao.getReferenceById(id);
        part.setName(newPartDto.getName());
        part.setQuantity(newPartDto.getQuantity());
        part.setPrice(newPartDto.getPrice());
        part.setCategory(category);
        part.setCar(car);
        part.setUser(user);

        this.logService.logUpdatedPart(part.getId(), part.getName());

        this.partDao.save(part);
    }

    //operation to delete part by id
    public void deletePart(Long id) {
        this.validatePartId(id);
        String partName = this.partDao.findById(id).get().getName();
        this.partDao.deleteById(id);
        this.logService.logDeletedPart(id, partName);
    }

    //operation to sell a part
    public Part sellPart(SaleDto saleDto, Long id, User user) {
        this.validatePartId(id);
        Part part = partDao.findById(id).get();
        this.validatePartSale(saleDto, part);
        Long partQuantity = part.getQuantity();
        partQuantity -= saleDto.getSoldQuantity();
        part.setQuantity(partQuantity);
        this.partDao.save(part);

        Sale sale = modelMapper.map(saleDto, Sale.class);
        BigDecimal soldQuantity = BigDecimal.valueOf(saleDto.getSoldQuantity());
        BigDecimal saleProfit = soldQuantity.multiply(part.getPrice());

        sale.setPart(part);
        sale.setSaleProfit(saleProfit);
        sale.setUser(user);
        sale.setSaleDate(Date.valueOf(LocalDate.now(ZoneOffset.UTC)));
        this.saleDao.save(sale);

        return part;
    }

    //checks for the id
    private void validatePartId(Long id) {
        if (this.partDao.count() == 0) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Parts not found");
        }

        if (id == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "ID cannot be empty");
        }

        if (!this.partDao.existsById(id)) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, String.format("Part with ID '%d' not found", id));
        }
    }

    private void validatePartData(NewPartDto newPartDto) {
        //check for particular car existence
        if (!this.carDao.existsById(newPartDto.getCarId())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, String.format("The car with ID '%d' does not exist", newPartDto.getCarId()));
        }

        //check for particular category existence
        if (!this.categoryDao.existsById(newPartDto.getCategoryId())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, String.format("The category with ID '%d' does not exist", newPartDto.getCategoryId()));
        }
    }

    //availability check
    private void validatePartSale(SaleDto saleDto, Part part) {
        Long partQuantity = part.getQuantity();

        if (saleDto.getSoldQuantity() <= 0 || saleDto.getSoldQuantity() > partQuantity) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "You can sell between 1 and " + partQuantity + " of this product");
        }
    }

    public List<Category> getAllCategories() {
        if (this.categoryDao.count() == 0) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "No categories found");
        }
        return this.categoryDao.findAll();
    }

    public List<Car> getAllCars() {
        if (this.carDao.count() == 0) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "No cars found");
        }
        return this.carDao.findAll();
    }
}