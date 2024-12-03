package org.liliyamammadova.hotelapplication.service.impl;

import org.liliyamammadova.hotelapplication.model.Apartment;
import org.liliyamammadova.hotelapplication.model.ReservationStatus;
import org.liliyamammadova.hotelapplication.repository.ApartmentRepository;
import org.liliyamammadova.hotelapplication.service.ApartmentService;
import org.liliyamammadova.jpastarter.repository.BaseRepository;
import org.liliyamammadova.jpastarter.service.BaseServiceImpl;
import org.springframework.stereotype.Service;

@Service
public class ApartmentServiceImpl extends BaseServiceImpl<Apartment> implements ApartmentService {
    private final ApartmentRepository apartmentRepository;
    public ApartmentServiceImpl(BaseRepository<Apartment> baseRepository, ApartmentRepository apartmentRepository) {
        super(baseRepository);
        this.apartmentRepository = apartmentRepository;
    }

    @Override
    public Apartment getFirstByReservationStatus(ReservationStatus reservationStatus) {
        return apartmentRepository.findFirstByReservationStatus(reservationStatus);
    }
}
