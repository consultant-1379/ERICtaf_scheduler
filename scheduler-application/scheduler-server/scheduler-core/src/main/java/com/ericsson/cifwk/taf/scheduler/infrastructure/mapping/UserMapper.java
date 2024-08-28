package com.ericsson.cifwk.taf.scheduler.infrastructure.mapping;

import com.ericsson.cifwk.taf.scheduler.api.dto.UserInfo;
import com.ericsson.cifwk.taf.scheduler.model.User;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class UserMapper {

    public User map(UserInfo dto) {
        return new User(dto.getUserId(), dto.getName(), dto.getEmail());
    }

    public UserInfo map(User entity) {
        return new UserInfo(entity.getExternalId(), entity.getName(), entity.getEmail());
    }

    public Set<UserInfo> map(List<User> users) {
        return users.stream()
                .map(this::map)
                .collect(Collectors.toCollection(HashSet::new));
    }
}
