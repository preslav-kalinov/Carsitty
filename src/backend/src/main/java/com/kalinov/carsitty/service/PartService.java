package com.kalinov.carsitty.service;

import com.kalinov.carsitty.RoleEnum;
import com.kalinov.carsitty.dao.*;
import com.kalinov.carsitty.dto.NewPartDto;
import com.kalinov.carsitty.dto.PartDto;
import com.kalinov.carsitty.dto.SaleDto;
import com.kalinov.carsitty.entity.*;
import com.kalinov.carsitty.util.ModelMapperUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import javax.mail.MessagingException;
import java.io.IOException;
import java.math.BigDecimal;
import java.sql.Date;
import java.time.LocalDate;
import java.time.ZoneOffset;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class PartService {
    @Value("${part.limit}")
    private Long partLimit;

    private final PartDao partDao;
    private final CarDao carDao;
    private final UserDao userDao;
    private final LogService logService;
    private final CategoryDao categoryDao;
    private final SaleDao saleDao;
    private final ModelMapperUtil modelMapper;
    private final MailService mailService;
    private final FileService fileService;

    @Autowired
    public PartService(PartDao partDao, CarDao carDao, UserDao userDao, LogService logService, CategoryDao categoryDao,
                       SaleDao saleDao, ModelMapperUtil modelMapper, MailService mailService, FileService fileService) {
        this.partDao = partDao;
        this.carDao = carDao;
        this.userDao = userDao;
        this.logService = logService;
        this.categoryDao = categoryDao;
        this.saleDao = saleDao;
        this.modelMapper = modelMapper;
        this.mailService = mailService;
        this.fileService = fileService;
    }

    //operation to get a part by id
    public PartDto getPart(Long id) throws IOException {
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
    public Part createPart(NewPartDto newPartDto, User user) throws IOException {
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

        this.logService.logCreatedPart(part.getName());
        this.fileService.writeToFile("Part with name '" + part.getName() +
                "' was successfully created by user '" + user.getUsername() + "'");

        return this.partDao.save(part);
    }

    //operation to edit existing part
    public void updatePart(Long partId, NewPartDto newPartDto, User user) throws IOException {
        this.validatePartId(partId);
        this.validatePartData(newPartDto);
        Category category = categoryDao.findById(newPartDto.getCategoryId()).get();
        Car car = carDao.findById(newPartDto.getCarId()).get();

        Part part = partDao.getReferenceById(partId);
        part.setName(newPartDto.getName());
        part.setQuantity(newPartDto.getQuantity());
        part.setPrice(newPartDto.getPrice());
        part.setCategory(category);
        part.setCar(car);

        this.logService.logUpdatedPart(part.getId(), part.getName());
        this.fileService.writeToFile("Part with ID:" + part.getId() + " and name '" + part.getName() +
                "' was successfully updated by user '" + user.getUsername() + "'");

        this.partDao.save(part);
    }

    //operation to delete part by id
    public void deletePart(Long partId, User user) throws IOException {
        this.validatePartId(partId);
        String partName = this.partDao.findById(partId).get().getName();
        this.partDao.deleteById(partId);
        this.logService.logDeletedPart(partId, partName);

        Part part = partDao.findById(partId).get();
        this.fileService.writeToFile("Part with ID:" + part.getId() + " and name '" + part.getName() +
                "' was successfully deleted by user '" + user.getUsername() + "'");
    }

    //operation to sell a part
    public Part sellPart(SaleDto saleDto, Long partId, User user) throws MessagingException, IOException {
        this.validatePartId(partId);
        Part part = partDao.findById(partId).get();
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

        String mailSubject = "New sale for part " + part.getName();
        String emailBody = "Dear Carsitty Managers,\n\nThere has been a successful sale of part ID " + partId + ", with name " +
                "'" + part.getName() + "' and there are " + part.getQuantity() + " items left of it.\n\n" +
                "This is an automated message by Carsitty Part Manager.";
        this.mailService.sendEmail(getManagersEmail(), mailSubject, emailBody);
        this.logService.logSoldPart(partId, part.getName());
        this.fileService.writeToFile("Part with ID:" + part.getId() + " and name '" + part.getName() +
                "' was successfully sold by user '" + user.getUsername() + "'");
        this.checkPartAvailability(part);

        return part;
    }

    //checks for the id
    private void validatePartId(Long id) throws IOException {
        if (this.partDao.count() == 0) {
            this.fileService.writeToFile("Parts not found", true);
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Parts not found");
        }

        if (id == null) {
            this.fileService.writeToFile("ID cannot be empty", true);
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "ID cannot be empty");
        }

        if (!this.partDao.existsById(id)) {
            this.fileService.writeToFile(String.format("Part with ID '%d' not found", id), true);
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, String.format("Part with ID '%d' not found", id));
        }
    }

    private void validatePartData(NewPartDto newPartDto) throws IOException {
        //check for particular car existence
        if (!this.carDao.existsById(newPartDto.getCarId())) {
            this.fileService.writeToFile(String.format("The car with ID '%d' does not exist", newPartDto.getCarId()), true);
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, String.format("The car with ID '%d' does not exist", newPartDto.getCarId()));
        }

        //check for particular category existence
        if (!this.categoryDao.existsById(newPartDto.getCategoryId())) {
            this.fileService.writeToFile(String.format("The category with ID '%d' does not exist", newPartDto.getCategoryId()), true);
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

    private void checkPartAvailability(Part part) throws MessagingException {
        Long partQuantity = part.getQuantity();

        if (partQuantity < partLimit) {
            String mailSubject = "Urgent Replenishment Request for Part ID " + part.getId() + " - '" + part.getName() + "'";
            String emailBody = "Dear Carsitty Managers,\n\n" +
                    "We hope this message finds you well. We would like to bring an important matter to your attention regarding Part with ID '" + part.getId() + "', known as '" + part.getName() + "'. " +
                    "We have observed a concerning decrease in the available quantity of this part, currently standing at '" + part.getQuantity() + "' items, just below our part limit of '" + partLimit + "'.\n\n" +
                    "To ensure uninterrupted operations and customer satisfaction, we kindly request an immediate replenishment of the stock for Part ID " + part.getId() + " - '" + part.getName() + "'. " +
                    "Your prompt action in this matter would be greatly appreciated.\n\n" +
                    "Please note that this message has been generated automatically by the Carsitty Part Manager system to bring this issue to your attention.\n\n" +
                    "Thank you for your attention to this matter.\n\n" +
                    "Best regards,\n" +
                    "Carsitty";
            this.mailService.sendEmail(getManagersEmail(), mailSubject, emailBody);
        }
    }

    public PartDto getPartById(Long partId) throws IOException {
        this.validatePartId(partId);
        Part part = this.partDao.findById(partId).get();
        return this.modelMapper.map(part, PartDto.class);
    }

    public String getPartNameById(Long partId) throws IOException {
        return this.getPartById(partId).getName();
    }

    private List getManagersEmail() {
        List<User> managers = userDao.getUsersByRole(RoleEnum.Manager);
        List<String> managerEmails = managers.stream()
                .map(User::getEmail)
                .collect(Collectors.toList());

        return managerEmails;
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