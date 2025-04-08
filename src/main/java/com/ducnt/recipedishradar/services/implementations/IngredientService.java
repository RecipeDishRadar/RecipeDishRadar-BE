package com.ducnt.recipedishradar.services.implementations;

import com.ducnt.recipedishradar.dto.response.ingredient.IngredientResponse;
import com.ducnt.recipedishradar.exception.CustomException;
import com.ducnt.recipedishradar.exception.ErrorResponse;
import com.ducnt.recipedishradar.models.Ingredient;
import com.ducnt.recipedishradar.repositories.IngredientRepository;
import com.ducnt.recipedishradar.utils.CsvUtil;
import com.fasterxml.jackson.dataformat.csv.CsvSchema;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.Objects;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class IngredientService {
    IngredientRepository ingredientRepository;

    public boolean uploadSingleFileCsv(MultipartFile requestFile) {
        try {
            if(requestFile.isEmpty()) {
                throw new CustomException(ErrorResponse.IO_FILE_INVALID);
            }
            if(!Objects.requireNonNull(requestFile.getOriginalFilename()).endsWith(".csv")) {
                throw new CustomException(ErrorResponse.IO_FILE_INVALID);
            }
            CsvSchema csvSchema = CsvSchema
                    .builder()
                    .addColumn("name")
                    .addColumn("synonymName")
                    .addColumn("entityID")
                    .addColumn("category")
                    .build();
            List<Ingredient> data = CsvUtil.readCsv(Ingredient.class, requestFile.getInputStream(), csvSchema);
            ingredientRepository.saveAll(data);
            return true;
        } catch (IOException ex) {
            throw new CustomException(ErrorResponse.IO_FILE_INVALID);
        } catch (Exception e) {
            throw new CustomException(ErrorResponse.INTERNAL_SERVER);
        }
    }

}
