package org.liliyamammadova.hotelapplication.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.liliyamammadova.hotelapplication.configuration.Properties;
import org.liliyamammadova.hotelapplication.exception.ReservationException;
import org.liliyamammadova.hotelapplication.model.Apartment;
import org.liliyamammadova.hotelapplication.model.Client;
import org.liliyamammadova.hotelapplication.model.ReservationStatus;
import org.liliyamammadova.hotelapplication.repository.ApartmentRepository;
import org.liliyamammadova.hotelapplication.repository.ClientRepository;
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
public class ApartmentServiceImplTest {
    @Mock
    private ApartmentRepository apartmentRepository;
    @Mock
    private ClientRepository clientRepository;
    @Mock
    private Properties properties;
    private ApartmentServiceImpl apartmentService;

    @BeforeEach
    void setUp() {
        apartmentService = new ApartmentServiceImpl(apartmentRepository, clientRepository, properties);
    }

    @Test
    void givenValidPrice_whenRegister_thenApartmentShouldBeeAddedToDatabase() {
        double price = 100.0;
        Integer apartmentId = 1;
        Apartment apartment = new Apartment();
        apartment.setId(apartmentId);
        apartment.setPrice(price);
        when(apartmentRepository.save(any(Apartment.class))).thenReturn(apartment);

        int result = apartmentService.register(price);

        assertEquals(apartmentId, result);
        verify(apartmentRepository, times(1)).save(any(Apartment.class));
    }

    @Test
    void givenClientName_whenReserve_ThenClientShouldBeAddedToDatabase() throws ReservationException {
        Client client = new Client("Test client");
        Apartment availableApartment = new Apartment();
        availableApartment.setReservationStatus(ReservationStatus.AVAILABLE);

        when(clientRepository.save(any(Client.class))).thenReturn(client);
        when(properties.isStatusChangeAvailability()).thenReturn(true);
        when(apartmentRepository.findFirstByReservationStatus(ReservationStatus.AVAILABLE))
                .thenReturn(availableApartment);
        when(apartmentRepository.save(any(Apartment.class))).thenReturn(availableApartment);

        boolean result = apartmentService.reserve(client);

        assertTrue(result);
        assertEquals(ReservationStatus.RESERVED, availableApartment.getReservationStatus());

        verify(clientRepository).save(client);
        verify(apartmentRepository).findFirstByReservationStatus(ReservationStatus.AVAILABLE);
        verify(apartmentRepository).save(availableApartment);
    }

    @Test
    void givenNoAvailableApartments_whenReserve_ThenFailToReserve() throws ReservationException {
        Client client = new Client("Test client");
        Apartment availableApartment = new Apartment();
        availableApartment.setReservationStatus(ReservationStatus.AVAILABLE);

        when(clientRepository.save(any(Client.class))).thenReturn(client);
        when(properties.isStatusChangeAvailability()).thenReturn(true);
        when(apartmentRepository.findFirstByReservationStatus(ReservationStatus.AVAILABLE))
                .thenReturn(null);

        boolean result = apartmentService.reserve(client);

        assertFalse(result);

        verify(clientRepository).save(client);
        verify(apartmentRepository).findFirstByReservationStatus(ReservationStatus.AVAILABLE);
        verify(apartmentRepository, never()).save(any(Apartment.class));
    }

    @Test
    void givenNoAbilityToChangeStatus_whenReserve_ThenExceptionShouldBeThrown() {
        Client client = new Client("Test client");

        when(properties.isStatusChangeAvailability()).thenReturn(false);

        assertThrows(ReservationException.class, () -> apartmentService.reserve(client));
    }

    @Test
    void givenReservedApartment_whenRelease_ThenClientShouldBeDeletedFromDatabase() throws ReservationException {
        Apartment reservedApartment = new Apartment();
        int apartmentId = 1;
        Client client = new Client("Test client");

        reservedApartment.setId(apartmentId);
        reservedApartment.setClient(client);
        reservedApartment.setReservationStatus(ReservationStatus.RESERVED);

        when(apartmentRepository.findById(apartmentId)).thenReturn(Optional.of(reservedApartment));
        when(apartmentRepository.save(any(Apartment.class))).thenReturn(reservedApartment);
        doNothing().when(clientRepository).delete(client);
        when(properties.isStatusChangeAvailability()).thenReturn(true);

        boolean result = apartmentService.release(apartmentId);

        assertTrue(result);
        assertNull(reservedApartment.getClient());
        assertEquals(ReservationStatus.AVAILABLE, reservedApartment.getReservationStatus());

        verify(apartmentRepository).findById(apartmentId);
        verify(apartmentRepository).save(reservedApartment);
        verify(clientRepository).delete(client);
    }

    @Test
    void givenInvalidId_whenRelease_thenFailToRelease() throws ReservationException {
        Apartment reservedApartment = new Apartment();
        int apartmentId = 1;
        Client client = new Client("Test client");

        reservedApartment.setId(apartmentId);
        reservedApartment.setClient(client);
        reservedApartment.setReservationStatus(ReservationStatus.RESERVED);

        when(apartmentRepository.findById(apartmentId)).thenReturn(Optional.empty());

        when(properties.isStatusChangeAvailability()).thenReturn(true);

        boolean result = apartmentService.release(apartmentId);

        assertFalse(result);

        verify(apartmentRepository).findById(apartmentId);

    }

    @Test
    void givenNoAbilityToChangeStatus_whenRelease_ThenExceptionShouldBeThrown() {
        int apartmentId = 1;

        when(properties.isStatusChangeAvailability()).thenReturn(false);

        assertThrows(ReservationException.class, () -> apartmentService.release(apartmentId));
    }

    @Test
    void givenSortingParameters_whenSortingApartments_thenPaginatedAndSortedListShouldBeReturned() {
        int page = 0;
        int size = 2;
        String sortBy = "price";

        List<Apartment> apartments = List.of(new Apartment(), new Apartment());
        Page<Apartment> paginatedApartments = new PageImpl<>(apartments);

        when(apartmentRepository.findAll(PageRequest.of(page, size, Sort.by(sortBy)))).thenReturn(paginatedApartments);
        List<Apartment> result = apartmentService.getPaginatedAndSortedApartments(page, size, sortBy);

        assertEquals(apartments.size(), result.size());
        verify(apartmentRepository).findAll(PageRequest.of(page, size, Sort.by(sortBy)));
    }

}

