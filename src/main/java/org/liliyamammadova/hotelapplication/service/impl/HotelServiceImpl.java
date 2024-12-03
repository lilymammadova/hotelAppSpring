package org.liliyamammadova.hotelapplication.service.impl;

import org.liliyamammadova.hotelapplication.configuration.Properties;
import org.liliyamammadova.hotelapplication.exception.ReservationException;
import org.liliyamammadova.hotelapplication.model.Apartment;
import org.liliyamammadova.hotelapplication.model.Client;
import org.liliyamammadova.hotelapplication.model.ReservationStatus;
import org.liliyamammadova.hotelapplication.service.ApartmentService;
import org.liliyamammadova.hotelapplication.service.ClientService;
import org.liliyamammadova.hotelapplication.service.HotelService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class HotelServiceImpl implements HotelService {
    private final ApartmentService apartmentService;
    private final ClientService clientService;
    private final Properties properties;

    @Autowired
    public HotelServiceImpl(ApartmentService apartmentService, ClientService clientService, Properties properties) {
        this.apartmentService = apartmentService;
        this.properties = properties;
        this.clientService = clientService;
    }

    @Override
    public int register(double price) {
        Apartment apartment = new Apartment();
        apartment.setPrice(price);
        apartment.setReservationStatus(ReservationStatus.AVAILABLE);
        apartment.setClient(null);
        return apartmentService.save(apartment).getId();
    }

    @Override
    public boolean reserve(Client client) throws ReservationException {
        if (!properties.isStatusChangeAvailability()) {
            throw new ReservationException("Changing status of apartment not allowed.");
        }
        Client savedClient = clientService.save(client);

        Apartment availableApartment = apartmentService.getFirstByReservationStatus(ReservationStatus.AVAILABLE);
        if (availableApartment != null) {
            availableApartment.setReservationStatus(ReservationStatus.RESERVED);
            availableApartment.setClient(savedClient);
            apartmentService.save(availableApartment);
            return true;
        }
        return false;
    }

    @Override
    public boolean release(int id) throws ReservationException {
        if (!properties.isStatusChangeAvailability()) {
            throw new ReservationException("Changing status of apartment not allowed.");
        }
        Apartment apartment = apartmentService.getById(id).orElse(null);
        if (apartment != null && apartment.getReservationStatus() == ReservationStatus.RESERVED) {
            Client client = apartment.getClient();
            apartment.setReservationStatus(ReservationStatus.AVAILABLE);
            apartment.setClient(null);
            apartmentService.save(apartment);
            if (client != null) {
                clientService.delete(client.getId());
            }
            return true;
        }
        return false;
    }

    @Override
    public List<Apartment> getPaginatedAndSortedApartments(int page, int size, String sortBy) {
        return apartmentService
                .getAllPaginatedAndSorted(PageRequest.of(page, size, Sort.by(sortBy)))
                .getContent();
    }
}
