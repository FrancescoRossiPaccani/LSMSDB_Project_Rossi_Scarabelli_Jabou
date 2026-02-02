package it.unipi.dii.lsmsdb.lsmsdb_project.service;

import it.unipi.dii.lsmsdb.lsmsdb_project.model.Car;
import it.unipi.dii.lsmsdb.lsmsdb_project.repository.CarRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
public class CarService {
    @Autowired private CarRepository carRepository;

    public List<Car> getCarsByOwner(String ownerId) {
        return carRepository.findByOwnerId(ownerId);
    }

    public Car saveCar(Car car) {
        return carRepository.save(car);
    }
}