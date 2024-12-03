package org.liliyamammadova.hotelapplication.service;

import org.liliyamammadova.hotelapplication.model.Apartment;
import org.liliyamammadova.hotelapplication.model.ReservationStatus;
import org.liliyamammadova.jpastarter.service.BaseService;

public interface ApartmentService extends BaseService<Apartment> {
    Apartment getFirstByReservationStatus(ReservationStatus reservationStatus);
}
