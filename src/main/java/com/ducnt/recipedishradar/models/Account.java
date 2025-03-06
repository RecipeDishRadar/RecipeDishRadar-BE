package com.ducnt.recipedishradar.models;

import com.ducnt.recipedishradar.enums.AccountStatus;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.time.LocalDate;

@EqualsAndHashCode(callSuper = true)
@Data
@AllArgsConstructor
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class Account extends BaseModel{
    String fullName;
    String email;
    String password;
    String phone;
    String address;
    LocalDate dob;
    String image;
    AccountStatus status;
}
