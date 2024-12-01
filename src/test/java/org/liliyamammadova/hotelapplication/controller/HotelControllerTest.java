package org.liliyamammadova.hotelapplication.controller;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.liliyamammadova.hotelapplication.model.Apartment;
import org.liliyamammadova.hotelapplication.model.Client;
import org.liliyamammadova.hotelapplication.model.ReservationStatus;
import org.liliyamammadova.hotelapplication.repository.ApartmentRepository;
import org.liliyamammadova.hotelapplication.repository.ClientRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.testcontainers.containers.MySQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.shaded.org.checkerframework.checker.units.qual.A;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@Testcontainers
public class HotelControllerTest {
    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ClientRepository clientRepository;

    @Autowired
    private ApartmentRepository apartmentRepository;

    @Container
    private static final MySQLContainer<?> mysqlContainer =
            new MySQLContainer<>("mysql:8.0")
                    .withDatabaseName("hotelapp_test")
                    .withUsername("test")
                    .withPassword("test");

    @BeforeAll
    static void setUp() {
        mysqlContainer.start();
        System.setProperty("spring.datasource.url", mysqlContainer.getJdbcUrl());
        System.setProperty("spring.datasource.username", mysqlContainer.getUsername());
        System.setProperty("spring.datasource.password", mysqlContainer.getPassword());
    }

    @BeforeEach
    void setup() {
        apartmentRepository.deleteAll();
        clientRepository.deleteAll();
    }


    @Test
    void givenValidId_whenRegister_thenReturnApartmentId() throws Exception {
        int apartmentId = 3;
        double price = 100.0;

        mockMvc.perform(post("/api/apartments/register")
                        .param("price", String.valueOf(price)))
                .andExpect(status().isOk())
                .andExpect(content().string(String.valueOf(apartmentId)));

        assertTrue(apartmentRepository.findById(3).isPresent());
    }

    @Test
    void givenClientName_whenReserve_thenReturnSuccessReservation() throws Exception {
        createApartment();
        mockMvc.perform(post("/api/apartments/reserve")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"name\": \"Test Client\"}"))
                .andExpect(status().isOk())
                .andExpect(content().string("Apartment reserved successfully"));

        Apartment apartment = apartmentRepository.findById(1).orElse(new Apartment());
        assertEquals(ReservationStatus.RESERVED, apartment.getReservationStatus());
    }

    @Test
    void givenReservedApartment_whenRelease_thenReturnSuccessRelease() throws Exception {
        Apartment apartment = new Apartment();
        apartment.setPrice(100.0);
        int apartmentId = createApartmentWithClient(apartment).getId();

        mockMvc.perform(post("/api/apartments/release/{id}", apartmentId))
                .andExpect(status().isOk())
                .andExpect(content().string("Apartment successfully released"));

        Apartment result = apartmentRepository.findById(apartmentId).orElse(new Apartment());
        assertEquals(ReservationStatus.AVAILABLE, result.getReservationStatus());
    }

    @Test
    void givenSortingParameters_whenGetApartments_thenReturnSortedApartments() throws Exception {
        createApartment();
        mockMvc.perform(get("/api/apartments")
                        .param("page", "0")
                        .param("size", "10")
                        .param("sortBy", "price"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1))
                .andExpect(jsonPath("$[0].id").value(2))
                .andExpect(jsonPath("$[0].price").value(100.0))
                .andExpect(jsonPath("$[0].reservationStatus").value(ReservationStatus.AVAILABLE.name()));
    }

    private Apartment createApartment() {
        Apartment apartment = new Apartment();
        apartment.setPrice(100.0);
        apartment.setReservationStatus(ReservationStatus.AVAILABLE);
        apartment.setClient(null);
        return apartmentRepository.save(apartment);
    }

    private Apartment createApartmentWithClient(Apartment apartment) {
        Client client = new Client("Test Client");
        clientRepository.save(client);
        apartment.setReservationStatus(ReservationStatus.RESERVED);
        apartment.setClient(client);
        return apartmentRepository.save(apartment);
    }
}

