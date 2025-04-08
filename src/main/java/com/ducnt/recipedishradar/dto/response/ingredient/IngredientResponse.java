package com.ducnt.recipedishradar.dto.response.ingredient;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.*;
import lombok.experimental.FieldDefaults;

@Data
@FieldDefaults(level = AccessLevel.PRIVATE)
@AllArgsConstructor
@NoArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
@Builder
public class IngredientResponse {
    String name;
    String synonymName;
    long aliasId;
    String category;
}
