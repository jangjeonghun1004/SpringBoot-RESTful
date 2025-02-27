package com.example.demo.provider;

import org.springframework.stereotype.Component;

import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

/**
 * TokenBlacklistProvider는 블랙리스트에 등록된 토큰들을 관리하는 컴포넌트입니다.
 * 이 클래스는 동시성을 고려하여 ConcurrentHashMap 기반의 key set을 사용합니다.
 */
@Component
public class TokenBlacklistProvider {

    // 동시성 보장이 되는 Set을 사용하여 토큰을 관리합니다.
    private final Set<String> blacklistedTokens = ConcurrentHashMap.newKeySet();

    /**
     * 주어진 토큰을 블랙리스트에 추가합니다.
     *
     * @param token 블랙리스트에 추가할 토큰
     */
    public void addToBlacklist(String token) {
        blacklistedTokens.add(token);
    }

    /**
     * 주어진 토큰이 블랙리스트에 등록되어 있는지 확인합니다.
     *
     * @param token 확인할 토큰
     * @return 토큰이 블랙리스트에 있다면 true, 그렇지 않으면 false
     */
    public boolean isBlacklisted(String token) {
        return blacklistedTokens.contains(token);
    }

    /**
     * 주어진 토큰을 블랙리스트에서 제거합니다.
     * (필요에 따라 블랙리스트에서 토큰을 삭제할 수 있는 기능)
     *
     * @param token 블랙리스트에서 제거할 토큰
     * @return 토큰이 존재하여 제거되었다면 true, 그렇지 않으면 false
     */
    public boolean removeFromBlacklist(String token) {
        return blacklistedTokens.remove(token);
    }
}
