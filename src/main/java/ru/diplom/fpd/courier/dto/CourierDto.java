package ru.diplom.fpd.courier.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import java.io.Serializable;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class CourierDto implements Serializable {

    private long id;
    private String firstName;
    private String lastName;
    private String phone;
    private String email;
    private float rating;
    private String city;
}