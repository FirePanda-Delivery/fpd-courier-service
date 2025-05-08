package ru.diplom.fpd.courier.dto;

import lombok.Data;

@Data
public class CourierReq {

    private String firstName;

    private String lastName;

    private String phone;

    private String email;

    private String city;

}
