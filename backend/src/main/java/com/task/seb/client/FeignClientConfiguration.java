package com.task.seb.client;

import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.type.TypeFactory;
import com.fasterxml.jackson.dataformat.xml.XmlMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import feign.Feign;
import feign.Logger.Level;
import feign.Util;
import feign.codec.Decoder;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.openfeign.support.SpringMvcContract;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

import static com.fasterxml.jackson.databind.DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES;
import static com.fasterxml.jackson.databind.DeserializationFeature.READ_UNKNOWN_ENUM_VALUES_USING_DEFAULT_VALUE;
import static com.fasterxml.jackson.databind.SerializationFeature.WRITE_DATES_AS_TIMESTAMPS;
import static feign.Logger.Level.BASIC;
import static feign.Util.UTF_8;

@Configuration
public class FeignClientConfiguration {
    @Bean
    @Profile("dev")
    Level feignLoggerLevel() {
        return BASIC;
    }

    @Bean
    public XmlMapper xmlMapper() {
        var xmlMapper = new XmlMapper();
        xmlMapper.registerModule(new JavaTimeModule());
        xmlMapper.configure(WRITE_DATES_AS_TIMESTAMPS, false);
        xmlMapper.configure(FAIL_ON_UNKNOWN_PROPERTIES, false);
        xmlMapper.configure(READ_UNKNOWN_ENUM_VALUES_USING_DEFAULT_VALUE, true);
        return xmlMapper;
    }

    @Bean
    public Decoder feignDecoder(XmlMapper xmlMapper) {
        return (response, type) -> {
            String bodyStr = Util.toString(response.body().asReader(UTF_8));
            JavaType javaType = TypeFactory.defaultInstance().constructType(type);
            return xmlMapper.readValue(bodyStr, javaType);
        };
    }

    @Bean
    public RatesApiClient ratesApiClient(@Value("${rates-api.base-url}") String baseUrl) {
        return Feign.builder()
            .decoder(feignDecoder(xmlMapper()))
            .contract(new SpringMvcContract())
            .target(RatesApiClient.class, baseUrl);
    }
}