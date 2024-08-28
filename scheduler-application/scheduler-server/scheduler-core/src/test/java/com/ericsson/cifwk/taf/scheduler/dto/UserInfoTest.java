package com.ericsson.cifwk.taf.scheduler.api.dto;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameters;
import org.mockito.MockitoAnnotations;

import java.util.Arrays;
import static org.junit.Assert.assertEquals;

/**
 * @author Niall Kelly (niall.kelly@ericsson.com)
 *         16/11/2015
 */

@RunWith(Parameterized.class)
public class UserInfoTest {

    private UserInfo existingReviewer;
    private UserInfo newReviewer;
    private boolean expected;

    @Before
    public void setUp() throws Exception {
        MockitoAnnotations.initMocks(this);
    }

    public UserInfoTest(UserInfo existingReviewer, UserInfo newReviewer,
                                            boolean expected) {
        this.existingReviewer = existingReviewer;
        this.newReviewer = newReviewer;
        this.expected = expected;
    }

    @Parameters
    public static Iterable<Object[]> testData() {
        return Arrays.asList(new Object[][]{
                {generateExistingUser(1), generateNewUserWithUserIdOnly(1), true},
                {generateExistingUser(1), generateNewUserWithUserIdOnly(2), false},
                {generateExistingUser(1), generateNewUserWithEmailOnly(1), true},
                {generateExistingUser(1), generateNewUserWithEmailOnly(2), false},
                {generateExistingUser(1), null, false},
                {generateNewUserWithUserIdOnly(1), generateExistingUser(1), true},
                {generateNewUserWithUserIdOnly(2), generateExistingUser(1), false},
                {generateNewUserWithEmailOnly(1), generateExistingUser(1), true},
                {generateNewUserWithEmailOnly(2), generateExistingUser(1), false},
        });
    }

    @Test
    public void verifyEqualsMethod() throws Exception {
        boolean equals = existingReviewer.equals(newReviewer);
        assertEquals(equals, expected);
    }

    @Test
    public void verifyHashCode() throws Exception {
        int hashCode = existingReviewer.hashCode();
        assertEquals(hashCode, 0);
    }

    private static UserInfo generateExistingUser(int index) {
        UserInfo userInfo = new UserInfo();
        userInfo.setName("userName" + index);
        userInfo.setEmail("userEmail" + index);
        userInfo.setUserId("userId" + index);
        return userInfo;
    }

    private static UserInfo generateNewUserWithUserIdOnly(int index) {
        UserInfo userInfo = new UserInfo();
        userInfo.setUserId("userId" + index);
        return userInfo;
    }

    private static UserInfo generateNewUserWithEmailOnly(int index) {
        UserInfo userInfo = new UserInfo();
        userInfo.setEmail("userEmail" + index);
        return userInfo;
    }
}
