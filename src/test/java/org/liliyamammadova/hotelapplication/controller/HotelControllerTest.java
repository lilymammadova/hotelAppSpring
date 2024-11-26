package org.liliyamammadova.hotelapplication.controller;

import org.junit.jupiter.api.Test;
import org.liliyamammadova.hotelapplication.model.Apartment;
import org.liliyamammadova.hotelapplication.model.Client;
import org.liliyamammadova.hotelapplication.model.ReservationStatus;
import org.liliyamammadova.hotelapplication.service.ApartmentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(HotelController.class)
public class HotelControllerTest {
    @Autowired
    private MockMvc mockMvc;
    @MockitoBean
    private ApartmentService apartmentService;

    @Test
    void givenValidId_whenRegister_thenReturnApartmentId() throws Exception {
        int apartmentId = 1;
        double price = 500.0;

        when(apartmentService.register(price)).thenReturn(apartmentId);

        mockMvc.perform(post("/api/apartments/register")
                        .param("price", String.valueOf(price)))
                .andExpect(status().isOk())
                .andExpect(content().string(String.valueOf(apartmentId)));

        verify(apartmentService).register(price);
    }

    @Test
    void givenClientName_whenReserve_thenReturnSuccessReservation() throws Exception {
        Client client = new Client("Test Client");

        when(apartmentService.reserve(any(Client.class))).thenReturn(true);

        mockMvc.perform(post("/api/apartments/reserve")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"name\": \"Test Client\"}"))
                .andExpect(status().isOk())
                .andExpect(content().string("Apartment reserved successfully"));

        verify(apartmentService).reserve(client);
    }

    @Test
    void givenReservedApartment_whenRelease_thenReturnSuccessRelease() throws Exception {
        int apartmentId = 1;

        when(apartmentService.release(apartmentId)).thenReturn(true);

        mockMvc.perform(post("/api/apartments/release/{id}", apartmentId))
                .andExpect(status().isOk())
                .andExpect(content().string("Apartment successfully released"));

        verify(apartmentService).release(apartmentId);
    }

    @Test
    void givenSortingParameters_whenGetApartments_thenReturnSortedApartments() throws Exception {
        Apartment apartment1 = new Apartment(1, 300.0, ReservationStatus.AVAILABLE, null);
        Apartment apartment2 = new Apartment(2, 400.0, ReservationStatus.AVAILABLE, null);
        List<Apartment> apartments = List.of(apartment1, apartment2);

        when(apartmentService.getPaginatedAndSortedApartments(0, 10, "price")).thenReturn(apartments);

        mockMvc.perform(get("/api/apartments/getPaginated")
                        .param("page", "0")
                        .param("size", "10")
                        .param("sortBy", "price"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(2))
                .andExpect(jsonPath("$[0].id").value(apartment1.getId()))
                .andExpect(jsonPath("$[0].price").value(apartment1.getPrice()))
                .andExpect(jsonPath("$[0].reservationStatus").value(apartment1.getReservationStatus().name()))
                .andExpect(jsonPath("$[1].id").value(apartment2.getId()))
                .andExpect(jsonPath("$[1].price").value(apartment2.getPrice()))
                .andExpect(jsonPath("$[1].reservationStatus").value(apartment2.getReservationStatus().name()));

        verify(apartmentService).getPaginatedAndSortedApartments(0, 10, "price");
    }
}

