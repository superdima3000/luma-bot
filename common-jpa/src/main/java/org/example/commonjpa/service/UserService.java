package org.example.commonjpa.service;

import org.example.commonjpa.entity.AppUser;
import org.example.commonjpa.entity.enums.State;

public interface UserService {
    AppUser changeState(Long userId, State state);
}
