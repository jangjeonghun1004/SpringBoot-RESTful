package com.example.demo.config;

import org.springframework.context.MessageSource;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.support.ReloadableResourceBundleMessageSource;

@Configuration
public class InternationalizationConfig {
    @Bean
    public MessageSource messageSource() {
        ReloadableResourceBundleMessageSource messageSource = new ReloadableResourceBundleMessageSource();
        // classpath 하위의 messages*.properties 파일을 읽어옵니다.
        messageSource.setBasename("classpath:messages");
        messageSource.setDefaultEncoding("UTF-8");
        // 기본 메시지를 찾지 못할 경우 예외 대신 코드 자체를 반환하도록 설정 가능
        messageSource.setUseCodeAsDefaultMessage(true);
        return messageSource;
    }
}
