package com.ericsson.cifwk.taf.scheduler.application.users;

import com.ericsson.cifwk.taf.scheduler.api.dto.UserInfo;
import com.ericsson.cifwk.taf.scheduler.api.dto.UserValidationResult;
import com.ericsson.cifwk.taf.scheduler.application.repository.UserRepository;
import com.ericsson.cifwk.taf.scheduler.infrastructure.mapping.UserMapper;
import com.ericsson.cifwk.taf.scheduler.model.User;
import com.google.common.collect.Lists;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import java.util.List;
import java.util.Optional;

import static junit.framework.TestCase.assertTrue;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.Matchers.empty;
import static org.junit.Assert.assertThat;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * @author Vladimirs Iljins (vladimirs.iljins@ericsson.com)
 *         28/10/2015
 */
@RunWith(MockitoJUnitRunner.class)
public class UserServiceTest {

    private static final String USER_ID = "userId";
    private static final String USER_NAME = "Name";
    private static final String USER_EMAIL = "tafuser@ericsson.com";

    private static final User userEntity = new User(USER_ID, USER_NAME, USER_EMAIL);
    private static final UserInfo userInfo = new UserInfo(USER_ID, USER_NAME, USER_EMAIL);

    @InjectMocks
    UserService userService;
    @Mock
    private UserRepository userRepository;
    @Mock
    UserMapper userMapper;
    @Mock
    LdapUserService ldapUserService;

    @Test
    public void findByNameOrEmailStartingWith_shouldReturnEmptyListForBlankInput() throws Exception {
        List<User> users = userService.findByNameOrEmailStartingWith(" ", 10);
        assertThat(users, empty());
    }

    @Test
    public void findOrCreateUser_shouldCreateUserWhenNotFound() throws Exception {
        when(userRepository.findByExternalIdOrEmail(USER_ID)).thenReturn(null);
        when(userRepository.save(any(User.class))).thenReturn(userEntity);
        when(userMapper.map(any(UserInfo.class))).thenReturn(userEntity);
        when(ldapUserService.findUserBySignumOrEmail(USER_ID)).thenReturn(Optional.of(userInfo));

        User user = userService.findOrCreateUser(userInfo);

        assertThat(user, notNullValue());
        verify(userRepository).save(any(User.class));
    }

    @Test
    public void onUserLoggedIn_shouldCreateUserWhenNotFound() throws Exception {
        when(userRepository.findByExternalIdOrEmail(USER_ID)).thenReturn(null);
        when(userRepository.save(any(User.class))).thenReturn(userEntity);
        when(userMapper.map(any(UserInfo.class))).thenReturn(userEntity);

        userService.onUserLoggedIn(userInfo);

        verify(userRepository).save(any(User.class));
    }

    @Test
    public void findInvalidReviewers_FromLDAP() throws Exception {
        when(userRepository.findByExternalIdOrEmail(USER_ID)).thenReturn(null);
        when(ldapUserService.findUserBySignumOrEmail(USER_ID)).thenReturn(Optional.of(userInfo));

        UserValidationResult invalidUsers = userService.findInvalidReviewers(getUsers());
        assertTrue(invalidUsers.getInvalidUsers().isEmpty());
    }

    @Test
    public void findInvalidReviewers_FromRepository() throws Exception {
        when(userRepository.findByExternalIdOrEmail(USER_ID)).thenReturn(userEntity);

        UserValidationResult invalidUsers = userService.findInvalidReviewers(getUsers());
        assertTrue(invalidUsers.getInvalidUsers().isEmpty());
    }

    private static List<String> getUsers() {
        List<String> users = Lists.newArrayList();
        users.add(USER_ID);
        return users;
    }
}
