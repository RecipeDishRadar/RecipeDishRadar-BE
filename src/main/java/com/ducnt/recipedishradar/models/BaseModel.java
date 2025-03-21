package com.ducnt.recipedishradar.models;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;

import java.time.LocalDate;

@Data
@AllArgsConstructor
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class BaseModel {
    @Id
    ObjectId id = new ObjectId();
    LocalDate createdDate = LocalDate.now();
    LocalDate modifiedDate = LocalDate.now();
}
