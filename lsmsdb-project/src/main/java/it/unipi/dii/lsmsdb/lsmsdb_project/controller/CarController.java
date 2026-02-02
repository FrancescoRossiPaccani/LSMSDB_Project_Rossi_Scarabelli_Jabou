package it.unipi.dii.lsmsdb.lsmsdb_project.controller;

import it.unipi.dii.lsmsdb.lsmsdb_project.model.Car;
import it.unipi.dii.lsmsdb.lsmsdb_project.service.CarService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/cars")
public class CarController {

    @Autowired private CarService carService;

    @GetMapping("/owner/{ownerId}")
    public List<Car> getCarsByOwner(@PathVariable String ownerId) {
        return carService.getCarsByOwner(ownerId);
    }

    @PostMapping
    public Car addCar(@RequestBody Car car) {
        return carService.saveCar(car);
    }
}