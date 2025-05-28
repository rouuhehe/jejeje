package com.dbp.legalcheck.event;

import com.dbp.legalcheck.domain.user.User;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public class WelcomeEvent {
    private final User user;
}