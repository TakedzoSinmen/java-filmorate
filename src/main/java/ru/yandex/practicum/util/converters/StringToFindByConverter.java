package ru.yandex.practicum.util.converters;

import org.springframework.core.convert.converter.Converter;
import ru.yandex.practicum.model.enums.FindBy;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class StringToFindByConverter implements Converter<String, List<FindBy>> {

    @Override
    public List<FindBy> convert(String source) {
        return Arrays.stream(source.split(","))
                .map(String::trim)
                .map(String::toUpperCase)
                .map(FindBy::valueOf)
                .collect(Collectors.toList());
    }
}
