package com.dcp.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.LocaleResolver;
import org.springframework.web.servlet.i18n.AcceptHeaderLocaleResolver;

import java.util.List;
import java.util.Locale;

@Configuration
public class LocaleConfig {
    public static final List<Locale> SUPPORTED_LOCALES = List.of(
            Locale.ENGLISH, new Locale("fa", "IR")
    );

    @Bean
    public LocaleResolver sessionLocaleResolver() {
        var resolver = new AcceptHeaderLocaleResolver();
        resolver.setSupportedLocales(SUPPORTED_LOCALES);
        return resolver;
    }
}
