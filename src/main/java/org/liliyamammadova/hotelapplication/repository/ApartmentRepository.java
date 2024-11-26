package org.liliyamammadova.hotelapplication.repository;

import org.liliyamammadova.hotelapplication.model.Apartment;
import org.liliyamammadova.hotelapplication.model.ReservationStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ApartmentRepository extends JpaRepository<Apartment,Integer> {
    Apartment findFirstByReservationStatus(ReservationStatus reservationStatus);
}
