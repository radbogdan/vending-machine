package com.mvp.vendingmachine.mapper;

import com.mvp.vendingmachine.api.model.UserDto;
import com.mvp.vendingmachine.storage.model.StoredUser;
import java.util.ArrayList;
import java.util.List;
import javax.annotation.processing.Generated;
import org.springframework.stereotype.Component;

@Generated(
    value = "org.mapstruct.ap.MappingProcessor",
    date = "2022-03-04T12:30:29+0200",
    comments = "version: 1.4.2.Final, compiler: javac, environment: Java 17.0.2 (Oracle Corporation)"
)
@Component
public class UserMapperImpl implements UserMapper {

    @Override
    public List<UserDto> asUserList(List<StoredUser> src) {
        if ( src == null ) {
            return null;
        }

        List<UserDto> list = new ArrayList<UserDto>( src.size() );
        for ( StoredUser storedUser : src ) {
            list.add( asUserDto( storedUser ) );
        }

        return list;
    }

    @Override
    public UserDto asUserDto(StoredUser src) {
        if ( src == null ) {
            return null;
        }

        UserDto userDto = new UserDto();

        userDto.setUsername( src.getUsername() );
        userDto.setPassword( src.getPassword() );
        userDto.setDeposit( src.getDeposit() );
        userDto.setRole( src.getRole() );

        return userDto;
    }

    @Override
    public StoredUser asStoredUser(UserDto userDto) {
        if ( userDto == null ) {
            return null;
        }

        StoredUser storedUser = new StoredUser();

        storedUser.setUsername( userDto.getUsername() );
        storedUser.setPassword( userDto.getPassword() );
        storedUser.setDeposit( userDto.getDeposit() );
        storedUser.setRole( userDto.getRole() );

        return storedUser;
    }
}
