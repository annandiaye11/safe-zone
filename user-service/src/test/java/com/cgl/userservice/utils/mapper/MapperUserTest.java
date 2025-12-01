package com.cgl.userservice.utils.mapper;
import com.cgl.userservice.data.entities.User;
import com.cgl.userservice.data.enums.Role;
import com.cgl.userservice.web.dto.UserAllResponse;
import com.cgl.userservice.web.dto.UserDto;
import com.cgl.userservice.web.dto.UserOneResponse;
import org.junit.jupiter.api.Test;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.fail;

class MapperUserTest {
    @Test
    void testConstructor_ThrowsException() throws Exception {
        assertThat(MapperUser.class.getDeclaredConstructors()).hasSize(1);
        assertThat(MapperUser.class.getDeclaredConstructors()[0].canAccess(null)).isFalse();
        var constructor = MapperUser.class.getDeclaredConstructor();
        constructor.setAccessible(true);
        try {
            constructor.newInstance();
            fail("Constructor should throw UnsupportedOperationException");
        } catch (Exception e) {
            assertThat(e.getCause()).isInstanceOf(UnsupportedOperationException.class);
            assertThat(e.getCause().getMessage()).isEqualTo("This is a utility class and cannot be instantiated");
        }
    }
    @Test
    void testToEntity_Success() {
        UserDto userDto = new UserDto();
        userDto.setName("John Doe");
        userDto.setEmail("john@example.com");
        userDto.setPassword("password123");
        userDto.setRole("CLIENT");
        userDto.setAvatar("avatar.jpg");
        User user = MapperUser.toEntity(userDto);
        assertThat(user).isNotNull();
        assertThat(user.getName()).isEqualTo("John Doe");
        assertThat(user.getEmail()).isEqualTo("john@example.com");
        assertThat(user.getPassword()).isEqualTo("password123");
        assertThat(user.getRole()).isEqualTo(Role.CLIENT);
        assertThat(user.getAvatar()).isEqualTo("avatar.jpg");
    }
    @Test
    void testToDto_Success() {
        User user = new User();
        user.setId("user-123");
        user.setName("John Doe");
        user.setEmail("john@example.com");
        user.setAvatar("avatar.jpg");
        user.setRole(Role.CLIENT);
        UserOneResponse response = MapperUser.toDto(user);
        assertThat(response).isNotNull();
        assertThat(response.getId()).isEqualTo("user-123");
        assertThat(response.getName()).isEqualTo("John Doe");
        assertThat(response.getEmail()).isEqualTo("john@example.com");
        assertThat(response.getAvatar()).isEqualTo("avatar.jpg");
        assertThat(response.getRole()).isEqualTo("CLIENT");
    }
    @Test
    void testToDtoSecond_Success() {
        User user = new User();
        user.setId("user-123");
        user.setName("John Doe");
        user.setEmail("john@example.com");
        user.setRole(Role.CLIENT);
        UserAllResponse response = MapperUser.toDtoSecond(user);
        assertThat(response).isNotNull();
        assertThat(response.getId()).isEqualTo("user-123");
        assertThat(response.getName()).isEqualTo("John Doe");
        assertThat(response.getEmail()).isEqualTo("john@example.com");
        assertThat(response.getRole()).isEqualTo("CLIENT");
    }
}
