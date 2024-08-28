package com.ericsson.cifwk.taf.scheduler.presentation.controllers;


import com.ericsson.cifwk.taf.scheduler.application.users.UserService;
import com.ericsson.cifwk.taf.scheduler.api.dto.UserInfo;
import com.ericsson.cifwk.taf.scheduler.api.dto.UserValidationResult;
import com.ericsson.cifwk.taf.scheduler.infrastructure.mapping.UserMapper;
import com.ericsson.cifwk.taf.scheduler.model.User;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.util.Collections;
import java.util.List;
import java.util.Set;

@RestController
@RequestMapping("/api/users")
public class UserController {

    private static final String DEFAULT_LIMIT = "10";

    @Autowired
    UserService userService;
    @Autowired
    UserMapper userMapper;

    @RequestMapping(method = RequestMethod.GET)
    public Set<UserInfo> getUsers(@RequestParam(value = "name", required = false) String name,
                                   @RequestParam(value = "limit", required = false, defaultValue = DEFAULT_LIMIT) Integer limit) {
        if (!StringUtils.isBlank(name)) {
            List<User> users = userService.findByNameOrEmailStartingWith(name, limit);
            return userMapper.map(users);
        } else {
            return Collections.emptySet();
        }
    }

    @RequestMapping(value = "/validate", method = RequestMethod.POST)
    public ResponseEntity<UserValidationResult> validateReviewers(@RequestBody List<String> names) throws IOException {
        UserValidationResult result = userService.findInvalidReviewers(names);
        return new ResponseEntity<>(result, result.isValid() ? HttpStatus.OK : HttpStatus.BAD_REQUEST);
    }
}
