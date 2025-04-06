package com.ducnt.recipedishradar.models;

import com.ducnt.recipedishradar.enums.AccountRole;
import com.ducnt.recipedishradar.enums.AccountStatus;
import lombok.*;
import lombok.experimental.FieldDefaults;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDate;

@EqualsAndHashCode(callSuper = true)
@Data
@Document
@AllArgsConstructor
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
@Builder
public class Account extends BaseModel{
    String fullName;
    String email;
    String password;
    String phone;
    String address;
    LocalDate dob;
    String image;
    AccountStatus status;
    AccountRole role;
}
