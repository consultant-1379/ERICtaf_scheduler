package com.ericsson.cifwk.taf.scheduler.infrastructure.mapping;

import com.ericsson.cifwk.taf.scheduler.api.dto.UserInfo;
import com.ericsson.cifwk.taf.scheduler.model.User;
import com.google.common.collect.Lists;
import org.junit.Before;
import org.junit.Test;

import java.util.List;
import java.util.Set;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;


public class UserMapperTest {

    UserMapper userMapper;

    @Before
    public void setUp() {
        userMapper = new UserMapper();
    }

    @Test
    public void testMap() {

        User entity = mockUser(1);

        UserInfo dto = userMapper.map(entity);

        assertEquals(dto.getUserId(), entity.getExternalId());
        assertEquals(dto.getName(), entity.getName());
        assertEquals(dto.getEmail(), entity.getEmail());
    }

    @Test
    public void testMap_MultipleUsers() {

        User firstEntity = mockUser(1);
        User secondEntity = mockUser(2);

        List<User> users = Lists.newArrayList();
        users.add(firstEntity);
        users.add(secondEntity);

        Set<UserInfo> dto = userMapper.map(users);
        assertEquals(dto.size(), 2);
    }

    public static User mockUser(int userNumber) {
        User entity = mock(User.class);
        when(entity.getExternalId()).thenReturn("user" + userNumber);
        when(entity.getName()).thenReturn("UserName");
        when(entity.getEmail()).thenReturn("user" + userNumber + "@ericsson.com");
        return entity;
    }

}
