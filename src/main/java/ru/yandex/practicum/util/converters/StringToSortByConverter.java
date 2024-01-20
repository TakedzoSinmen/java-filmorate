package ru.yandex.practicum.util.converters;

import org.springframework.core.convert.converter.Converter;
import ru.yandex.practicum.model.enums.SortBy;

public class StringToSortByConverter implements Converter<String, SortBy> {
    @Override
    public SortBy convert(String source) {
        return SortBy.valueOf(source.toUpperCase());
    }
}
