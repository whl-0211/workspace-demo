package com.jk.service;

import com.jk.dao.CarDao;
import com.jk.model.Car;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class CarServiceImpl implements CarService{

    @Autowired
    private CarDao carDao;

    @Override
    public void delete(Car car) {
        carDao.delete(car);
    }

    @Override
    public Integer addCar(Car car) {
        carDao.addCar(car);
        return car.getCarId();
    }

    @Override
    public void getById(Integer carId) {
        carDao.getById(carId);
    }
}
