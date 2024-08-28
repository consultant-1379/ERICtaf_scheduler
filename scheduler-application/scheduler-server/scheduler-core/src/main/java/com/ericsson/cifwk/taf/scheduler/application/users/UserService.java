package com.ericsson.cifwk.taf.scheduler.application.users;

import com.ericsson.cifwk.taf.scheduler.api.dto.UserInfo;
import com.ericsson.cifwk.taf.scheduler.api.dto.UserValidationResult;
import com.ericsson.cifwk.taf.scheduler.application.repository.UserRepository;
import com.ericsson.cifwk.taf.scheduler.infrastructure.mapping.UserMapper;
import com.ericsson.cifwk.taf.scheduler.model.User;
import com.google.common.eventbus.EventBus;
import com.google.common.eventbus.Subscribe;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

@Service
public class UserService {

    private static final int MAX_RESULT_SIZE = 100;
    private static final int DEFAULT_RESULT_SIZE = 10;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    UserMapper userMapper;

    @Autowired
    EventBus eventBus;

    @Autowired
    LdapUserService ldapUserService;

    public Optional<User> findByExternalIdOrEmail(String externalId) {
        if (!StringUtils.isBlank(externalId)) {
            return Optional.ofNullable(userRepository.findByExternalIdOrEmail(externalId));
        }
        return Optional.empty();
    }

    public List<User> findByNameOrEmailStartingWith(String name, int limit) {
        if (!StringUtils.isBlank(name)) {
            int validLimit = limit;
            if (limit > MAX_RESULT_SIZE) {
                validLimit = MAX_RESULT_SIZE;
            } else if (limit < 1) {
                validLimit = DEFAULT_RESULT_SIZE;
            }
            return userRepository.findByNameOrEmailStartingWith(name.trim().toLowerCase(),
                    new PageRequest(0, validLimit));
        }
        return Collections.emptyList();
    }

    public UserValidationResult findInvalidReviewers(List<String> externalIds) {
        UserValidationResult invalidReviewers = new UserValidationResult();

        for (String externalId : externalIds) {
            User user = userRepository.findByExternalIdOrEmail(externalId);
            if (user == null) {
                Optional<UserInfo> isValidUser = ldapUserService.findUserBySignumOrEmail(externalId);
                if (!isValidUser.isPresent()) {
                    invalidReviewers.addInvalidUser(externalId);
                }
            }
        }
        return invalidReviewers;
    }

    public User findOrCreateUser(UserInfo userInfo) {
        String externalId = StringUtils.defaultIfEmpty(userInfo.getUserId(), userInfo.getEmail());
        User user = userRepository.findByExternalIdOrEmail(externalId);
        if (user == null) {
            Optional<UserInfo> maybeUser = ldapUserService.findUserBySignumOrEmail(externalId);
            if (maybeUser.isPresent()) {
                user = userMapper.map(maybeUser.get());
                userRepository.save(user);
            }
        }
        return user;
    }

    @SuppressWarnings("unused")
    @Subscribe
    public void onUserLoggedIn(UserInfo userInfo) {
        User user = userRepository.findByExternalIdOrEmail(userInfo.getUserId());
        if (user == null) {
            User newUser = userMapper.map(userInfo);
            userRepository.save(newUser);
        }
    }

    @PostConstruct
    public void init() {
        eventBus.register(this);
    }

    @PreDestroy
    public void onDestroy() {
        eventBus.unregister(this);
    }
}
