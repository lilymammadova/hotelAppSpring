package org.liliyamammadova.hotelapplication.service;

import org.liliyamammadova.hotelapplication.exception.ReservationException;
import org.liliyamammadova.hotelapplication.model.Apartment;
import org.liliyamammadova.hotelapplication.model.Client;

import java.util.List;

public interface HotelService {
    int register(double price);

    boolean reserve(Client client) throws ReservationException;

    boolean release(int id) throws ReservationException;

    List<Apartment> getPaginatedAndSortedApartments(int page, int size, String sortBy);
}
