package org.liliyamammadova.hotelapplication.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.liliyamammadova.hotelapplication.configuration.Properties;
import org.liliyamammadova.hotelapplication.exception.ReservationException;
import org.liliyamammadova.hotelapplication.model.Apartment;
import org.liliyamammadova.hotelapplication.model.Client;
import org.liliyamammadova.hotelapplication.model.ReservationStatus;
import org.liliyamammadova.hotelapplication.service.impl.HotelServiceImpl;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class HotelServiceImplTest {
    @Mock
    private ApartmentService apartmentService;
    @Mock
    private ClientService clientService;
    @Mock
    private Properties properties;
    private HotelServiceImpl hotelService;

    @BeforeEach
    void setUp() {
        hotelService = new HotelServiceImpl(apartmentService, clientService, properties);
    }

    @Test
    void givenValidPrice_whenRegister_thenApartmentShouldBeeAddedToDatabase() {
        double price = 100.0;
        Integer apartmentId = 1;
        Apartment apartment = new Apartment();
        apartment.setId(apartmentId);
        apartment.setPrice(price);
        when(apartmentService.save(any(Apartment.class))).thenReturn(apartment);

        int result = hotelService.register(price);

        assertEquals(apartmentId, result);
        verify(apartmentService, times(1)).save(any(Apartment.class));
    }

    @Test
    void givenClientName_whenReserve_ThenClientShouldBeAddedToDatabase() throws ReservationException {
        Client client = new Client("Test client");
        Apartment availableApartment = new Apartment();
        availableApartment.setReservationStatus(ReservationStatus.AVAILABLE);

        when(clientService.save(any(Client.class))).thenReturn(client);
        when(properties.isStatusChangeAvailability()).thenReturn(true);
        when(apartmentService.getFirstByReservationStatus(ReservationStatus.AVAILABLE))
                .thenReturn(availableApartment);
        when(apartmentService.save(any(Apartment.class))).thenReturn(availableApartment);

        boolean result = hotelService.reserve(client);

        assertTrue(result);
        assertEquals(ReservationStatus.RESERVED, availableApartment.getReservationStatus());

        verify(clientService).save(client);
        verify(apartmentService).getFirstByReservationStatus(ReservationStatus.AVAILABLE);
        verify(apartmentService).save(availableApartment);
    }

    @Test
    void givenNoAvailableApartments_whenReserve_ThenFailToReserve() throws ReservationException {
        Client client = new Client("Test client");
        Apartment availableApartment = new Apartment();
        availableApartment.setReservationStatus(ReservationStatus.AVAILABLE);

        when(clientService.save(any(Client.class))).thenReturn(client);
        when(properties.isStatusChangeAvailability()).thenReturn(true);
        when(apartmentService.getFirstByReservationStatus(ReservationStatus.AVAILABLE))
                .thenReturn(null);

        boolean result = hotelService.reserve(client);

        assertFalse(result);

        verify(clientService).save(client);
        verify(apartmentService).getFirstByReservationStatus(ReservationStatus.AVAILABLE);
        verify(apartmentService, never()).save(any(Apartment.class));
    }

    @Test
    void givenNoAbilityToChangeStatus_whenReserve_ThenExceptionShouldBeThrown() {
        Client client = new Client("Test client");

        when(properties.isStatusChangeAvailability()).thenReturn(false);

        assertThrows(ReservationException.class, () -> hotelService.reserve(client));
    }

    @Test
    void givenReservedApartment_whenRelease_ThenClientShouldBeDeletedFromDatabase() throws ReservationException {
        Apartment reservedApartment = new Apartment();
        int apartmentId = 1;
        Client client = new Client(1,"Test client");

        reservedApartment.setId(apartmentId);
        reservedApartment.setClient(client);
        reservedApartment.setReservationStatus(ReservationStatus.RESERVED);

        when(apartmentService.getById(apartmentId)).thenReturn(Optional.of(reservedApartment));
        when(apartmentService.save(any(Apartment.class))).thenReturn(reservedApartment);
        doNothing().when(clientService).delete(client.getId());
        when(properties.isStatusChangeAvailability()).thenReturn(true);

        boolean result = hotelService.release(apartmentId);

        assertTrue(result);
        assertNull(reservedApartment.getClient());
        assertEquals(ReservationStatus.AVAILABLE, reservedApartment.getReservationStatus());

        verify(apartmentService).getById(apartmentId);
        verify(apartmentService).save(reservedApartment);
        verify(clientService).delete(client.getId());
    }

    @Test
    void givenInvalidId_whenRelease_thenFailToRelease() throws ReservationException {
        Apartment reservedApartment = new Apartment();
        int apartmentId = 1;
        Client client = new Client("Test client");

        reservedApartment.setId(apartmentId);
        reservedApartment.setClient(client);
        reservedApartment.setReservationStatus(ReservationStatus.RESERVED);

        when(apartmentService.getById(apartmentId)).thenReturn(Optional.empty());

        when(properties.isStatusChangeAvailability()).thenReturn(true);

        boolean result = hotelService.release(apartmentId);

        assertFalse(result);

        verify(apartmentService).getById(apartmentId);

    }

    @Test
    void givenNoAbilityToChangeStatus_whenRelease_ThenExceptionShouldBeThrown() {
        int apartmentId = 1;

        when(properties.isStatusChangeAvailability()).thenReturn(false);

        assertThrows(ReservationException.class, () -> hotelService.release(apartmentId));
    }

    @Test
    void givenSortingParameters_whenSortingApartments_thenPaginatedAndSortedListShouldBeReturned() {
        int page = 0;
        int size = 2;
        String sortBy = "price";

        List<Apartment> apartments = List.of(new Apartment(), new Apartment());
        Page<Apartment> paginatedApartments = new PageImpl<>(apartments);

        when(apartmentService.getAllPaginatedAndSorted(PageRequest.of(page, size, Sort.by(sortBy)))).thenReturn(paginatedApartments);
        List<Apartment> result = hotelService.getPaginatedAndSortedApartments(page, size, sortBy);

        assertEquals(apartments.size(), result.size());
        verify(apartmentService).getAllPaginatedAndSorted(PageRequest.of(page, size, Sort.by(sortBy)));
    }

}

