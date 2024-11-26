package org.liliyamammadova.hotelapplication.controller;

import org.liliyamammadova.hotelapplication.exception.ReservationException;
import org.liliyamammadova.hotelapplication.model.Apartment;
import org.liliyamammadova.hotelapplication.model.Client;
import org.liliyamammadova.hotelapplication.service.ApartmentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/apartments")
public class HotelController {
    private final ApartmentService apartmentService;

    @Autowired
    public HotelController(ApartmentService apartmentService) {
        this.apartmentService = apartmentService;
    }

    @PostMapping("/register")
    public ResponseEntity<Integer> registerApartment(@RequestParam double price) {
        int apartmentId = apartmentService.register(price);
        return ResponseEntity.ok(apartmentId);
    }

    @PostMapping("/reserve")
    public ResponseEntity<String> reserveApartment(@RequestBody Client client) throws ReservationException {
        boolean reserved = apartmentService.reserve(client);
        if (reserved) {
            return ResponseEntity.ok("Apartment reserved successfully");
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("No available apartments found");
        }
    }

    @PostMapping("/release/{id}")
    public ResponseEntity<String> releaseApartment(@PathVariable int id) throws ReservationException {
        boolean released = apartmentService.release(id);
        if (released) {
            return ResponseEntity.ok("Apartment successfully released");
        } else return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Apartment not found with such id");
    }

    @GetMapping("/getPaginated")
    public ResponseEntity<List<Apartment>> getApartments(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "price") String sortBy) {

        List<Apartment> sortedApartments = apartmentService.getPaginatedAndSortedApartments(page, size, sortBy);

        return ResponseEntity.ok(sortedApartments);
    }
}
