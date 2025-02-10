package com.example.demo.provider;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.stereotype.Component;

import jakarta.annotation.PostConstruct;

@Component
public class MessageProvider {

    private static MessageSource messageSource;

    // MessageSource 빈을 주입받음 (비정적 필드)
    @Autowired
    private MessageSource autowiredMessageSource;

    // PostConstruct를 통해 정적 변수에 할당
    @PostConstruct
    public void init() {
        MessageProvider.messageSource = this.autowiredMessageSource;
    }

    /**
     * 주어진 메시지 코드와 인자에 대해 현재 요청의 Locale에 맞는 메시지를 반환합니다.
     *
     * @param code 메시지 코드 (예: "user.email.exists")
     * @param args 메시지 포맷에 사용할 인자 (필요하지 않은 경우 null)
     * @return 해당 로케일에 맞는 메시지 문자열
     */
    public static String getMessage(String code, Object[] args) {
        return messageSource.getMessage(code, args, LocaleContextHolder.getLocale());
    }

    // 인자 없이 메시지 코드를 조회할 수 있도록 오버로드된 메서드
    public static String getMessage(String code) {
        return getMessage(code, null);
    }
}
