package org.example.commonjpa.service.impl;

import lombok.RequiredArgsConstructor;
import org.example.commonjpa.entity.AppUser;
import org.example.commonjpa.entity.enums.State;
import org.example.commonjpa.repository.AppUserRepository;
import org.example.commonjpa.service.UserService;
import org.springframework.stereotype.Service;

import static org.example.commonjpa.entity.enums.State.WAITING_FOR_ITEM_NAME_EDIT;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final AppUserRepository appUserRepository;
    @Override
    public AppUser changeState(Long userId, State state) {
        var user = appUserRepository.findByUserId(userId);
        user.setState(state);
        return appUserRepository.save(user);
    }
}
