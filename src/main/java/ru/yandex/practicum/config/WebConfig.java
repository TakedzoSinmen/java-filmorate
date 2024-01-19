package ru.yandex.practicum.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.format.FormatterRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import ru.yandex.practicum.util.converters.StringToFindByConverter;
import ru.yandex.practicum.util.converters.StringToSortByConverter;

@Configuration
public class WebConfig implements WebMvcConfigurer {
    @Override
    public void addFormatters(FormatterRegistry registry) {
        registry.addConverter(new StringToSortByConverter());
        registry.addConverter(new StringToFindByConverter());
    }
}
