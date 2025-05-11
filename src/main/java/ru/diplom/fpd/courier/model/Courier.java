package ru.diplom.fpd.courier.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.SequenceGenerator;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Table
public class Courier {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "SEQUENCE")
    @SequenceGenerator(name = "SEQUENCE", sequenceName = "courier_id_sequence", allocationSize = 1)
    private long id;

    @Column(nullable = false, unique = true)
    private String userId;

    @Column(name = "FIRST_NAME", nullable = false)
    private String firstName;

    @Column(name = "LAST_NAME", nullable = false)
    private String lastName;

    @Column(name = "PHONE", nullable = false, unique = true)
    private String phone;

    @Column(name = "EMAIL", unique = true)
    private String email;

    @Column(name = "rating")
    private float rating;

    @Column(name = "status")
    @Enumerated(EnumType.STRING)
    private CourierStatus status = CourierStatus.NOT_ACTIVE;

    @Column(name = "city", nullable = false)
    private String city;

    @Column
    @JsonIgnore
    private boolean isDeleted;

}
