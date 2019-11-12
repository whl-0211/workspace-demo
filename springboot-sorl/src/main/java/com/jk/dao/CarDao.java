package com.jk.dao;

import com.jk.model.Car;

public interface CarDao {
    void delete(Car car);

    void addCar(Car car);

    void getById(Integer carId);
}
