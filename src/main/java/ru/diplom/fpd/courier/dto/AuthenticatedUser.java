package ru.diplom.fpd.courier.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import java.io.Serializable;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@AllArgsConstructor
@Getter
@JsonIgnoreProperties(ignoreUnknown = true)
@Builder
public class AuthenticatedUser implements Serializable {
    private final String id;
    private final String userName;
    private final String firstName;
    private final String lastName;
    private final String phone;
    private final String email;
}