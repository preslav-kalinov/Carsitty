package com.kalinov.carsitty.service;

import com.kalinov.carsitty.dao.*;
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
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Service
public class PartService {
    private final PartDao partDao;
    private final CarDao carDao;
    private final LogDao logDao;
    private final CategoryDao categoryDao;
    private final SaleDao saleDao;
    private final ModelMapperUtil modelMapper;

    @Autowired
    public PartService(PartDao partDao, CarDao carDao, LogDao logDao,
                       CategoryDao categoryDao, SaleDao saleDao, ModelMapperUtil modelMapper) {
        this.partDao = partDao;
        this.carDao = carDao;
        this.logDao = logDao;
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
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "No parts found");
        }

        return this.partDao.findAll();
    }

    //operation to add new part
    public Part createPart(PartDto partDto, User user) {
        this.validatePartData(partDto);
        Category category = this.categoryDao.findById(partDto.getCategoryId()).get();
        Car car = this.carDao.findById(partDto.getCarId()).get();

        Part part = modelMapper.map(partDto, Part.class);
        part.setCategory(category);
        part.setCar(car);
        part.setUser(user);

        return this.partDao.save(part);
    }

    //checks for the id
    private void validatePartId(Long id) {
        Log log = new Log();
        if (this.partDao.count() == 0) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "No parts found");
        }

        if (id == null) {
            log.setErrorMessage("ID cannot be empty");
            this.logDao.save(log);
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "ID cannot be empty");
        }

        if (!this.partDao.existsById(id)) {
            log.setErrorMessage("This id does not exist: provided " + id);
            this.logDao.save(log);
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, String.format("Part with ID '%d' not found", id));
        }
    }

    private void validatePartData(PartDto partDto) {
        Log log = new Log();
        //--------checks if the data is null---------
        if (partDto.getName() == null) {
            log.setErrorMessage("Name cannot be empty");
            this.logDao.save(log);
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Name cannot be empty");
        }

        if (partDto.getCarId() == null) {
            log.setErrorMessage("Car cannot be empty");
            this.logDao.save(log);
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "You need to choose a car");
        }

        if (partDto.getCategoryId() == null) {
            log.setErrorMessage("Category cannot be empty");
            this.logDao.save(log);
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "You need to choose part category");
        }

        if (partDto.getQuantity() == null) {
            log.setErrorMessage("Quantity cannot be empty");
            this.logDao.save(log);
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Quantity cannot be empty");
        }

        if (partDto.getPrice() == null) {
            log.setErrorMessage("Price cannot be empty");
            this.logDao.save(log);
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Price cannot be empty");
        }
        //---------------------------------------------

        Matcher partNameMatcher = Pattern.compile(Part.nameFormat).matcher(partDto.getName());

        //check for invalid part name
        if (!partNameMatcher.matches()) {
            log.setErrorMessage("Invalid part name");
            this.logDao.save(log);
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Part name must be between 1 and 1024 characters");
        }

        //check for cars existence
        if (this.carDao.count() == 0) {
            log.setErrorMessage("No cars found");
            this.logDao.save(log);
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "No cars found");
        }

        //check for particular car existence
        if (!this.carDao.existsById(partDto.getCarId())) {
            log.setErrorMessage("This car does not exist: provided " + partDto.getCarId());
            this.logDao.save(log);
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "This car does not exist");
        }

        //check for category existence
        if (categoryDao.count() == 0) {
            log.setErrorMessage("No categories found");
            this.logDao.save(log);
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "No categories found");
        }

        //check for particular category existence
        if (!this.categoryDao.existsById(partDto.getCategoryId())) {
            log.setErrorMessage("This category does not exist: provided " + partDto.getCategoryId());
            this.logDao.save(log);
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "This category does not exist");
        }

        //check if quantity is a positive number
        if (partDto.getQuantity() <= 0) {
            log.setErrorMessage("Quantity must be a positive number: provided " + partDto.getQuantity());
            this.logDao.save(log);
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Quantity must be a positive number");
        }

        if (partDto.getPrice().compareTo(BigDecimal.ZERO) <= 0) {
            log.setErrorMessage("Price must be a positive number: provided " + partDto.getPrice());
            this.logDao.save(log);
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Price must be a positive number");
        }
    }

    //operation to edit existing part
    public void updatePart(Long id, PartDto partDto, User user) {
        this.validatePartId(id);
        this.validatePartData(partDto);
        Category category = categoryDao.findById(partDto.getCategoryId()).get();
        Car car = carDao.findById(partDto.getCarId()).get();

        Part part = partDao.getReferenceById(id);
        part.setName(partDto.getName());
        part.setQuantity(partDto.getQuantity());
        part.setPrice(partDto.getPrice());
        part.setCategory(category);
        part.setCar(car);
        part.setUser(user);

        this.partDao.save(part);
    }

    //operation to delete part by id
    public void deletePart(Long id) {
        this.validatePartId(id);
        this.partDao.deleteById(id);
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

    //availability check
    private void validatePartSale(SaleDto saleDto, Part part) {
        Long partQuantity = part.getQuantity();
        Log log = new Log();
        if (saleDto.getSoldQuantity() == null) {
            log.setErrorMessage("Sold quantity cannot be empty");
            this.logDao.save(log);
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Sold quantity cannot be empty");
        }
        if (saleDto.getSoldQuantity() <= 0 || saleDto.getSoldQuantity() > partQuantity) {
            log.setErrorMessage("You can sell between 1 and " + partQuantity + " of this product");
            this.logDao.save(log);
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