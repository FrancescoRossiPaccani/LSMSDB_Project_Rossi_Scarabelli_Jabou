package it.unipi.dii.lsmsdb.lsmsdb_project_jabou_rossi_paccani_scarabelli.controller;

import it.unipi.dii.lsmsdb.lsmsdb_project_jabou_rossi_paccani_scarabelli.model.Car;
import it.unipi.dii.lsmsdb.lsmsdb_project_jabou_rossi_paccani_scarabelli.service.CarService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/cars")
public class CarController {

    @Autowired
    private CarService carService;

    @GetMapping
    public List<Car> getAllCars() {
        return carService.getAllCars();
    }

    @GetMapping("/{id}")
    public Car getCarById(@PathVariable String id) {
        return carService.getCarById(id).orElse(null);
    }

    @GetMapping("/owner/{ownerId}")
    public List<Car> getCarsByOwner(@PathVariable String ownerId) {
        return carService.getCarsByOwner(ownerId);
    }
}