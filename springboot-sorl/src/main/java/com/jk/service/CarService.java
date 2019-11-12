package com.jk.service;

import com.jk.model.Car;

public interface CarService {
    void delete(Car car);

    Integer addCar(Car car);


    void getById(Integer carId);
}
