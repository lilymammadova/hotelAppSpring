package org.liliyamammadova.hotelapplication.repository;

import org.liliyamammadova.hotelapplication.model.Apartment;
import org.liliyamammadova.hotelapplication.model.ReservationStatus;
import org.liliyamammadova.jpastarter.repository.BaseRepository;

public interface ApartmentRepository extends BaseRepository<Apartment> {
    Apartment findFirstByReservationStatus(ReservationStatus reservationStatus);
}
