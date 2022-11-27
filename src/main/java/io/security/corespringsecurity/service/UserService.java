package io.security.corespringsecurity.service;

import io.security.corespringsecurity.domain.User;
import io.security.corespringsecurity.domain.dto.AccountDto;

import java.util.List;

public interface UserService {

    void createUser(User account);

    void modifyUser(AccountDto accountDto);

    List<User> getUsers();

    AccountDto getUser(Long id);

    void deleteUser(Long idx);

    void order();
}
