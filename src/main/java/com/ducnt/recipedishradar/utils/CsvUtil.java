package com.ducnt.recipedishradar.utils;

import com.ducnt.recipedishradar.exception.CustomException;
import com.ducnt.recipedishradar.exception.ErrorResponse;
import com.fasterxml.jackson.databind.MappingIterator;
import com.fasterxml.jackson.databind.ObjectReader;
import com.fasterxml.jackson.dataformat.csv.CsvMapper;
import com.fasterxml.jackson.dataformat.csv.CsvParser;
import com.fasterxml.jackson.dataformat.csv.CsvSchema;
import lombok.experimental.UtilityClass;
import org.springframework.core.io.ClassPathResource;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;

@UtilityClass
public class CsvUtil {
    private static final CsvMapper csvMapper = new CsvMapper();

    public static <T> List<T> readCsv(Class<T> clazz, InputStream stream, CsvSchema csvSchema)
            throws IOException {
        ObjectReader objectReader = csvMapper.readerFor(clazz).with(csvSchema)
                .withFeatures(CsvParser.Feature.INSERT_NULLS_FOR_MISSING_COLUMNS);
        return objectReader.<T>readValues(stream).readAll();
    }

    public static <T> List<T> readCsvFileInLocal(String fileName, Class<T> clazz, InputStream stream) throws IOException {
        if(fileName.isEmpty()) {
            throw new CustomException(ErrorResponse.IO_FILE_INVALID);
        }
        CsvSchema csvSchema = CsvSchema.emptySchema().withHeader().withColumnReordering(true);
        File file = new ClassPathResource(fileName).getFile();
        MappingIterator<T> readValues =
                csvMapper.reader(clazz).with(csvSchema).readValues(file);
        return readValues.readAll();
    }
}
