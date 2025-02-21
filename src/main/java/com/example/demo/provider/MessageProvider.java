package com.example.demo.provider;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.stereotype.Component;

/**
 * 메시지 프로바이더는 현재 요청의 Locale에 맞는 메시지를 MessageSource에서 조회하여 반환합니다.
 * 이 클래스는 전역에서 정적 메서드를 통해 손쉽게 사용될 수 있습니다.
 */
@Component
public final class MessageProvider {

    private static MessageSource messageSource;

    /**
     * 생성자 주입을 통해 MessageSource를 할당합니다.
     *
     * @param messageSource Spring에서 제공하는 MessageSource 빈
     */
    @Autowired
    public MessageProvider(MessageSource messageSource) {
        MessageProvider.messageSource = messageSource;
    }

    /**
     * 주어진 메시지 코드와 가변 인자를 사용하여 현재 요청의 Locale에 맞는 메시지를 반환합니다.
     *
     * @param code 메시지 코드 (예: "user.email.exists")
     * @param args 메시지 포맷에 사용할 인자 (없을 경우 생략 가능)
     * @return 해당 로케일에 맞는 메시지 문자열
     */
    public static String getMessage(String code, Object... args) {
        return messageSource.getMessage(code, args, LocaleContextHolder.getLocale());
    }
}
