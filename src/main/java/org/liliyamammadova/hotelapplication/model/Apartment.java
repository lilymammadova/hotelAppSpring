package org.liliyamammadova.hotelapplication.model;

import jakarta.persistence.*;

import java.io.Serializable;
import java.util.Objects;

@Entity
@Table(name = "apartments")
public class Apartment implements Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;
    @Column(name = "price", nullable = false)
    private double price;
    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    private ReservationStatus reservationStatus;
    @ManyToOne
    @JoinColumn(name = "client_id")
    private Client client;

    public Apartment() {
    }

    public Apartment(int id, double price, ReservationStatus reservationStatus, Client client) {
        this.id = id;
        this.price = price;
        this.reservationStatus = reservationStatus;
        this.client = client;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    public ReservationStatus getReservationStatus() {
        return reservationStatus;
    }

    public void setReservationStatus(ReservationStatus reservationStatus) {
        this.reservationStatus = reservationStatus;
    }

    public Client getClient() {
        return client;
    }

    public void setClient(Client client) {
        this.client = client;
    }

    @Override
    public String toString() {
        return "Apartment{" +
                "id=" + id +
                ", price=" + price +
                ", reservationStatus='" + reservationStatus + '\'' +
                ", clientName='" + client + '\'' +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Apartment apartment)) return false;
        return getId() == apartment.getId() && Double.compare(apartment.getPrice(), getPrice()) == 0 && getReservationStatus() == apartment.getReservationStatus() && Objects.equals(getClient(), apartment.getClient());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getId(), getPrice(), getReservationStatus(), getClient());
    }
}
