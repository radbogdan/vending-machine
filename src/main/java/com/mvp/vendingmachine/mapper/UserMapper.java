package com.mvp.vendingmachine.mapper;

import com.mvp.vendingmachine.api.model.UserDto;
import com.mvp.vendingmachine.storage.model.StoredUser;
import org.mapstruct.Mapper;

import java.util.List;

@Mapper(componentModel = "spring")
public interface UserMapper {

    List<UserDto> asUserList(final List<StoredUser> src);

    UserDto asUserDto(final StoredUser src);

    StoredUser asStoredUser(final UserDto userDto);
}
