package com.kalinov.carsitty.service;

import com.kalinov.carsitty.dao.CarDao;
import com.kalinov.carsitty.dao.PartDao;
import com.kalinov.carsitty.dto.DiscountDto;
import com.kalinov.carsitty.dto.PartDto;
import com.kalinov.carsitty.entity.Car;
import com.kalinov.carsitty.entity.CarBrand;
import com.kalinov.carsitty.entity.Part;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class PartDiscountSharingService {
    private final PartService partService;
    private final FacebookService facebookService;
    private final LogService logService;
    private final FileService fileService;
    private final PartDao partDao;
    private final CarDao carDao;

    @Value("${company.name}")
    private String companyName;

    @Autowired
    public PartDiscountSharingService(PartService partService, FacebookService facebookService, LogService logService,
                                      FileService fileService, PartDao partDao, CarDao carDao) {
        this.partService = partService;
        this.facebookService = facebookService;
        this.logService = logService;
        this.fileService = fileService;
        this.partDao = partDao;
        this.carDao = carDao;
    }

    public void sharePartDiscountToFacebook(Long partId, DiscountDto discountDto) throws IOException {
        Part part = partDao.getReferenceById(partId);

        this.facebookService.postTextToFacebook(this.getPartDiscountPostText(partId, discountDto));
        this.logService.logPartSharedToFacebook(partId, this.partService.getPartNameById(partId));
        fileService.writeToFile("Part with ID:" + part.getId() + " and name '" + part.getName() + "' was successfully shared in Facebook.");
    }

    private String getPartDiscountPostText(Long partId, DiscountDto discountDto) throws IOException {
        PartDto partDto = this.partService.getPartById(partId);
        String partName = partDto.getName();
        BigDecimal partPrice = partDto.getPrice();
        BigDecimal partDiscount = BigDecimal.valueOf(discountDto.getPartDiscount());
        BigDecimal partPriceAfterDiscount = partPrice.subtract(partDiscount.multiply(partPrice).divide(BigDecimal.valueOf(100)));

        List<Car> cars = partDto.getCarIds().stream()
                .map(carId -> this.carDao.findById(carId).get())
                .collect(Collectors.toList());

        StringBuilder stringBuilder = new StringBuilder();
        for (Car car : cars) {
            String carInfo = String.format("%s %s, ", car.getCarBrand().getBrand(), car.getModel());
            stringBuilder.append(carInfo);
        }

        String carInfoText = stringBuilder.toString();
        if (!carInfoText.isEmpty()) {
            carInfoText = carInfoText.substring(0, carInfoText.length() - 2);
        }

        return String.format("Check out the discounted price for %s used in %s at %s:\n\n" +
                        "Original Price: %.2f BGN\n" +
                        "Discount: %.0f%%\n" +
                        "Price After Discount: %.2f BGN\n\n" +
                        "Don't miss out on this great deal! Grab your %s now!",
                partName, carInfoText, this.companyName, partPrice, partDiscount, partPriceAfterDiscount, partName);
    }
}