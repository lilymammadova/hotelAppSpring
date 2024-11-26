package org.liliyamammadova.hotelapplication.service;

import org.liliyamammadova.hotelapplication.configuration.Properties;
import org.liliyamammadova.hotelapplication.exception.ReservationException;
import org.liliyamammadova.hotelapplication.model.Apartment;
import org.liliyamammadova.hotelapplication.model.Client;
import org.liliyamammadova.hotelapplication.model.ReservationStatus;
import org.liliyamammadova.hotelapplication.repository.ApartmentRepository;
import org.liliyamammadova.hotelapplication.repository.ClientRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ApartmentServiceImpl implements ApartmentService {
    private final ApartmentRepository apartmentRepository;
    private final ClientRepository clientRepository;
    private final Properties properties;

    @Autowired
    public ApartmentServiceImpl(ApartmentRepository apartmentRepository, ClientRepository clientRepository, Properties properties) {
        this.apartmentRepository = apartmentRepository;
        this.clientRepository = clientRepository;
        this.properties = properties;
    }

    @Override
    public int register(double price) {
        Apartment apartment = new Apartment();
        apartment.setPrice(price);
        apartment.setReservationStatus(ReservationStatus.AVAILABLE);
        apartment.setClient(null);
        return apartmentRepository.save(apartment).getId();
    }

    @Override
    public boolean reserve(Client client) throws ReservationException {
        if (!properties.isStatusChangeAvailability()) {
            throw new ReservationException("Changing status of apartment not allowed.");
        }
        Client savedClient = clientRepository.save(client);

        Apartment availableApartment = apartmentRepository.findFirstByReservationStatus(ReservationStatus.AVAILABLE);
        if (availableApartment != null) {
            availableApartment.setReservationStatus(ReservationStatus.RESERVED);
            availableApartment.setClient(savedClient);
            apartmentRepository.save(availableApartment);
            return true;
        }
        return false;
    }

    @Override
    public boolean release(int id) throws ReservationException {
        if (!properties.isStatusChangeAvailability()) {
            throw new ReservationException("Changing status of apartment not allowed.");
        }
        Apartment apartment = apartmentRepository.findById(id).orElse(null);
        if (apartment != null && apartment.getReservationStatus() == ReservationStatus.RESERVED) {
            Client client = apartment.getClient();
            apartment.setReservationStatus(ReservationStatus.AVAILABLE);
            apartment.setClient(null);
            apartmentRepository.save(apartment);
            if (client != null) {
                clientRepository.delete(client);
            }
            return true;
        }
        return false;
    }

    @Override
    public List<Apartment> getPaginatedAndSortedApartments(int page, int size, String sortBy) {
        return apartmentRepository
                .findAll(PageRequest.of(page, size, Sort.by(sortBy)))
                .getContent();
    }
}
