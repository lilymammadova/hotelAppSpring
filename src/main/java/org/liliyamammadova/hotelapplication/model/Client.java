package org.liliyamammadova.hotelapplication.model;

import jakarta.persistence.*;
import org.liliyamammadova.jpastarter.entity.BaseEntity;

import java.util.Objects;

/**
 * Further could be added more fields such as passport, surname, contacts etc.
 */
@Entity
@Table(name = "clients")
public class Client extends BaseEntity {
    @Column(name = "name", nullable = false, length = 45)
    private String name;

    public Client(String name) {
        this.name = name;
    }

    public Client(int id, String name) {
        this.setId(id);
        this.name = name;
    }

    public Client() {
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Client client)) return false;
        return Objects.equals(getId(), client.getId()) && Objects.equals(getName(), client.getName());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getId(), getName());
    }

    @Override
    public String toString() {
        return "Client{" +
                "id=" + getId() +
                ", name='" + name + '\'' +
                '}';
    }
}
