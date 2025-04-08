package com.ducnt.recipedishradar.controller;

import com.ducnt.recipedishradar.dto.response.ApiResponse;
import com.ducnt.recipedishradar.models.Ingredient;
import com.ducnt.recipedishradar.services.implementations.IngredientService;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.InputStream;

@RestController
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@RequestMapping("${server.base.url}")
public class IngredientController {
    IngredientService ingredientService;

    @PostMapping("/ingredient")
    public ApiResponse uploadSingleCsvIngredientFile(@RequestParam("file") MultipartFile requestFile) {
        var result = ingredientService.uploadSingleFileCsv(requestFile);
        if(result) {
            return ApiResponse
                    .builder()
                    .message("Ingredient added successfully")
                    .build();
        } else {
            return ApiResponse
                    .builder()
                    .code(HttpStatus.BAD_REQUEST.value())
                    .message("Ingredient added successfully")
                    .build();
        }
    }
}
