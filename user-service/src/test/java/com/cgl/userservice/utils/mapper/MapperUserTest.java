package com.cgl.userservice.utils.mapper;

import com.cgl.userservice.data.entities.User;
import com.cgl.userservice.data.enums.Role;
import com.cgl.userservice.web.dto.UserAllResponse;
import com.cgl.userservice.web.dto.UserDto;
import com.cgl.userservice.web.dto.UserOneResponse;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class MapperUserTest {

    @Test
    void testToEntity_ConvertsUserDtoToUser_WithClientRole() {
        // Given
        UserDto userDto = new UserDto();
        userDto.setName("John Doe");
        userDto.setEmail("john@example.com");
        userDto.setPassword("password123");
        userDto.setRole("CLIENT");
        userDto.setAvatar("avatar-url");

        // When
        User user = MapperUser.toEntity(userDto);

        // Then
        assertThat(user).isNotNull();
        assertThat(user.getName()).isEqualTo("John Doe");
        assertThat(user.getEmail()).isEqualTo("john@example.com");
        assertThat(user.getPassword()).isEqualTo("password123");
        assertThat(user.getRole()).isEqualTo(Role.CLIENT);
        assertThat(user.getAvatar()).isEqualTo("avatar-url");
        assertThat(user.getId()).isNull(); // Not set by mapper
    }

    @Test
    void testToEntity_ConvertsUserDtoToUser_WithSellerRole() {
        // Given
        UserDto userDto = new UserDto();
        userDto.setName("Jane Seller");
        userDto.setEmail("jane@example.com");
        userDto.setPassword("sellerPass");
        userDto.setRole("SELLER");
        userDto.setAvatar("seller-avatar");

        // When
        User user = MapperUser.toEntity(userDto);

        // Then
        assertThat(user).isNotNull();
        assertThat(user.getName()).isEqualTo("Jane Seller");
        assertThat(user.getEmail()).isEqualTo("jane@example.com");
        assertThat(user.getPassword()).isEqualTo("sellerPass");
        assertThat(user.getRole()).isEqualTo(Role.SELLER);
        assertThat(user.getAvatar()).isEqualTo("seller-avatar");
    }

    @Test
    void testToEntity_WithNullAvatar() {
        // Given
        UserDto userDto = new UserDto();
        userDto.setName("User Without Avatar");
        userDto.setEmail("noavatar@example.com");
        userDto.setPassword("pass");
        userDto.setRole("CLIENT");
        userDto.setAvatar(null);

        // When
        User user = MapperUser.toEntity(userDto);

        // Then
        assertThat(user.getAvatar()).isNull();
        assertThat(user.getName()).isEqualTo("User Without Avatar");
    }

    @Test
    void testToEntity_WithEmptyAvatar() {
        // Given
        UserDto userDto = new UserDto();
        userDto.setName("User");
        userDto.setEmail("user@example.com");
        userDto.setPassword("pass");
        userDto.setRole("CLIENT");
        userDto.setAvatar("");

        // When
        User user = MapperUser.toEntity(userDto);

        // Then
        assertThat(user.getAvatar()).isEmpty();
    }

    @Test
    void testToEntity_CreatesNewUserInstance() {
        // Given
        UserDto userDto = new UserDto();
        userDto.setName("Test");
        userDto.setEmail("test@example.com");
        userDto.setPassword("pass");
        userDto.setRole("CLIENT");

        // When
        User user1 = MapperUser.toEntity(userDto);
        User user2 = MapperUser.toEntity(userDto);

        // Then - Each call creates a new instance
        assertThat(user1).isNotSameAs(user2);
        assertThat(user1.getEmail()).isEqualTo(user2.getEmail());
    }

    // ==================== toDto() TESTS ====================

    @Test
    void testToDto_ConvertsUserToUserOneResponse() {
        // Given
        User user = User.builder()
                .id("user-123")
                .name("Alice Client")
                .email("alice@example.com")
                .password("encoded-password")
                .role(Role.CLIENT)
                .avatar("alice-avatar-url")
                .build();

        // When
        UserOneResponse userOneResponse = MapperUser.toDto(user);

        // Then
        assertThat(userOneResponse).isNotNull();
        assertThat(userOneResponse.getId()).isEqualTo("user-123");
        assertThat(userOneResponse.getName()).isEqualTo("Alice Client");
        assertThat(userOneResponse.getEmail()).isEqualTo("alice@example.com");
        assertThat(userOneResponse.getAvatar()).isEqualTo("alice-avatar-url");
        assertThat(userOneResponse.getRole()).isEqualTo("CLIENT");
    }

    @Test
    void testToDto_WithSellerRole() {
        // Given
        User user = User.builder()
                .id("seller-456")
                .name("Bob Seller")
                .email("bob@example.com")
                .role(Role.SELLER)
                .avatar("bob-avatar")
                .build();

        // When
        UserOneResponse userOneResponse = MapperUser.toDto(user);

        // Then
        assertThat(userOneResponse.getRole()).isEqualTo("SELLER");
        assertThat(userOneResponse.getId()).isEqualTo("seller-456");
    }

    @Test
    void testToDto_WithNullAvatar() {
        // Given
        User user = User.builder()
                .id("user-789")
                .name("User")
                .email("user@example.com")
                .role(Role.CLIENT)
                .avatar(null)
                .build();

        // When
        UserOneResponse userOneResponse = MapperUser.toDto(user);

        // Then
        assertThat(userOneResponse.getAvatar()).isNull();
    }

    @Test
    void testToDto_RoleConvertedToString() {
        // Given
        User user = User.builder()
                .id("user-001")
                .name("Test User")
                .email("test@example.com")
                .role(Role.CLIENT)
                .build();

        // When
        UserOneResponse userOneResponse = MapperUser.toDto(user);

        // Then - Role is converted to String using String.valueOf()
        assertThat(userOneResponse.getRole()).isInstanceOf(String.class);
        assertThat(userOneResponse.getRole()).isEqualTo("CLIENT");
    }

    @Test
    void testToDto_CreatesNewUserOneResponseInstance() {
        // Given
        User user = User.builder()
                .id("user-123")
                .name("Test")
                .email("test@example.com")
                .role(Role.CLIENT)
                .build();

        // When
        UserOneResponse response1 = MapperUser.toDto(user);
        UserOneResponse response2 = MapperUser.toDto(user);

        // Then - Each call creates a new instance
        assertThat(response1).isNotSameAs(response2);
        assertThat(response1.getId()).isEqualTo(response2.getId());
    }

    @Test
    void testToDtoSecond_ConvertsUserToUserAllResponse() {
        // Given
        User user = User.builder()
                .id("user-999")
                .name("Charlie Client")
                .email("charlie@example.com")
                .role(Role.CLIENT)
                .build();

        // When
        UserAllResponse userAllResponse = MapperUser.toDtoSecond(user);

        // Then
        assertThat(userAllResponse).isNotNull();
        assertThat(userAllResponse.getId()).isEqualTo("user-999");
        assertThat(userAllResponse.getName()).isEqualTo("Charlie Client");
        assertThat(userAllResponse.getEmail()).isEqualTo("charlie@example.com");
        assertThat(userAllResponse.getRole()).isEqualTo("CLIENT");
    }

    @Test
    void testToDtoSecond_WithSellerRole() {
        // Given
        User user = User.builder()
                .id("seller-888")
                .name("Diana Seller")
                .email("diana@example.com")
                .role(Role.SELLER)
                .build();

        // When
        UserAllResponse userAllResponse = MapperUser.toDtoSecond(user);

        // Then
        assertThat(userAllResponse.getRole()).isEqualTo("SELLER");
        assertThat(userAllResponse.getId()).isEqualTo("seller-888");
    }

    @Test
    void testToDtoSecond_RoleConvertedUsingName() {
        // Given
        User user = User.builder()
                .id("user-002")
                .name("Test")
                .email("test@example.com")
                .role(Role.CLIENT)
                .build();

        // When
        UserAllResponse userAllResponse = MapperUser.toDtoSecond(user);

        // Then - Role is converted using .name() method
        assertThat(userAllResponse.getRole()).isInstanceOf(String.class);
        assertThat(userAllResponse.getRole()).isEqualTo("CLIENT");
    }

    @Test
    void testToDtoSecond_NoAvatarField() {
        // Given
        User user = User.builder()
                .id("user-003")
                .name("User With Avatar")
                .email("user@example.com")
                .role(Role.CLIENT)
                .avatar("some-avatar-url")
                .build();

        // When
        UserAllResponse userAllResponse = MapperUser.toDtoSecond(user);

        // Then - UserAllResponse doesn't have avatar field (not mapped)
        assertThat(userAllResponse.getId()).isNotNull();
        assertThat(userAllResponse.getName()).isNotNull();
        assertThat(userAllResponse.getEmail()).isNotNull();
        assertThat(userAllResponse.getRole()).isNotNull();
    }

    @Test
    void testToDtoSecond_CreatesNewUserAllResponseInstance() {
        // Given
        User user = User.builder()
                .id("user-123")
                .name("Test")
                .email("test@example.com")
                .role(Role.CLIENT)
                .build();

        // When
        UserAllResponse response1 = MapperUser.toDtoSecond(user);
        UserAllResponse response2 = MapperUser.toDtoSecond(user);

        // Then - Each call creates a new instance
        assertThat(response1).isNotSameAs(response2);
        assertThat(response1.getId()).isEqualTo(response2.getId());
    }

    // ==================== INTEGRATION TESTS ====================

    @Test
    void testRoundTrip_EntityToDtoPreservesData() {
        // Given - Create a User entity
        User originalUser = User.builder()
                .id("round-trip-id")
                .name("Round Trip User")
                .email("roundtrip@example.com")
                .role(Role.CLIENT)
                .avatar("avatar-url")
                .build();

        // When - Convert to DTO
        UserOneResponse userOneResponse = MapperUser.toDto(originalUser);

        // Then - All data is preserved in the DTO
        assertThat(userOneResponse.getId()).isEqualTo(originalUser.getId());
        assertThat(userOneResponse.getName()).isEqualTo(originalUser.getName());
        assertThat(userOneResponse.getEmail()).isEqualTo(originalUser.getEmail());
        assertThat(userOneResponse.getAvatar()).isEqualTo(originalUser.getAvatar());
        assertThat(userOneResponse.getRole()).isEqualTo(originalUser.getRole().name());
    }

    @Test
    void testCompareToDto_AndToDtoSecond() {
        // Given
        User user = User.builder()
                .id("compare-id")
                .name("Compare User")
                .email("compare@example.com")
                .role(Role.SELLER)
                .avatar("compare-avatar")
                .build();

        // When
        UserOneResponse userOneResponse = MapperUser.toDto(user);
        UserAllResponse userAllResponse = MapperUser.toDtoSecond(user);

        // Then - Both should have same basic fields
        assertThat(userOneResponse.getId()).isEqualTo(userAllResponse.getId());
        assertThat(userOneResponse.getName()).isEqualTo(userAllResponse.getName());
        assertThat(userOneResponse.getEmail()).isEqualTo(userAllResponse.getEmail());
        assertThat(userOneResponse.getRole()).isEqualTo(userAllResponse.getRole());

        // UserOneResponse has avatar, UserAllResponse doesn't
        assertThat(userOneResponse.getAvatar()).isEqualTo("compare-avatar");
    }

    @Test
    void testToEntity_TypicalRegistrationScenario() {
        // Given - Typical user registration DTO
        UserDto userDto = new UserDto();
        userDto.setName("New User");
        userDto.setEmail("newuser@example.com");
        userDto.setPassword("plainPassword123"); // Will be encoded later
        userDto.setRole("CLIENT");
        userDto.setAvatar(null);

        // When
        User user = MapperUser.toEntity(userDto);

        // Then - Ready to be saved to database (id will be generated by DB)
        assertThat(user).isNotNull();
        assertThat(user.getId()).isNull(); // Will be generated by database
        assertThat(user.getEmail()).isEqualTo("newuser@example.com");
        assertThat(user.getRole()).isEqualTo(Role.CLIENT);
    }

    @Test
    void testToDto_TypicalApiResponseScenario() {
        // Given - User entity retrieved from database
        User user = User.builder()
                .id("db-generated-id")
                .name("API User")
                .email("apiuser@example.com")
                .password("$2a$10$encodedPassword") // BCrypt encoded
                .role(Role.CLIENT)
                .avatar("https://example.com/avatars/user.jpg")
                .build();

        // When - Convert to DTO for API response
        UserOneResponse userOneResponse = MapperUser.toDto(user);

        // Then - DTO ready to be sent in API response (password not included)
        assertThat(userOneResponse).isNotNull();
        assertThat(userOneResponse.getId()).isNotNull();
        assertThat(userOneResponse.getEmail()).isNotNull();
        assertThat(userOneResponse.getAvatar()).startsWith("https://");
        // Password is not included in UserOneResponse
    }

    @Test
    void testToDtoSecond_TypicalListUsersScenario() {
        // Given - User entity for listing users (no avatar needed)
        User user = User.builder()
                .id("list-user-id")
                .name("List User")
                .email("listuser@example.com")
                .role(Role.SELLER)
                .avatar("some-avatar") // Will not be included in response
                .build();

        // When - Convert to UserAllResponse for list endpoint
        UserAllResponse userAllResponse = MapperUser.toDtoSecond(user);

        // Then - DTO ready for user list (minimal data)
        assertThat(userAllResponse).isNotNull();
        assertThat(userAllResponse.getId()).isNotNull();
        assertThat(userAllResponse.getName()).isNotNull();
        assertThat(userAllResponse.getEmail()).isNotNull();
        assertThat(userAllResponse.getRole()).isNotNull();
    }
}

