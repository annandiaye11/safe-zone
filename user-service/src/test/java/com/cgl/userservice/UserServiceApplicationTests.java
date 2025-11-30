package com.cgl.userservice;

import com.cgl.userservice.data.entities.User;
import com.cgl.userservice.data.enums.Role;
import org.junit.jupiter.api.Test;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.data.mongodb.config.EnableMongoAuditing;
import org.springframework.security.core.GrantedAuthority;

import java.util.Collection;

import static org.assertj.core.api.Assertions.assertThat;

class UserServiceApplicationTests {

    // ==================== TESTS FOR UserServiceApplication CLASS ====================

    @Test
    void testMainMethod() {
        // Then - Verify main method exists
        assertThat(UserServiceApplication.class).isNotNull();
        assertThat(UserServiceApplication.class.getDeclaredMethods())
                .anyMatch(method -> method.getName().equals("main"));
    }

    @Test
    void testApplicationHasSpringBootApplicationAnnotation() {
        // Then - Verify @SpringBootApplication annotation is present
        assertThat(UserServiceApplication.class.isAnnotationPresent(SpringBootApplication.class)).isTrue();
    }

    @Test
    void testApplicationHasEnableMongoAuditingAnnotation() {
        // Then - Verify @EnableMongoAuditing annotation is present
        assertThat(UserServiceApplication.class.isAnnotationPresent(EnableMongoAuditing.class)).isTrue();
    }

    @Test
    void testApplicationHasEnableDiscoveryClientAnnotation() {
        // Then - Verify @EnableDiscoveryClient annotation is present
        assertThat(UserServiceApplication.class.isAnnotationPresent(EnableDiscoveryClient.class)).isTrue();
    }

    @Test
    void testApplicationClassExists() {
        // Then - Verify the main application class exists
        assertThat(UserServiceApplication.class).isNotNull();
        assertThat(UserServiceApplication.class.getSimpleName()).isEqualTo("UserServiceApplication");
    }

    @Test
    void testApplicationPackage() {
        // Then - Verify the package
        assertThat(UserServiceApplication.class.getPackage().getName()).isEqualTo("com.cgl.userservice");
    }

    // ==================== TESTS FOR User ENTITY ====================

    @Test
    void testUserEntityCreationWithClientRole() {
        // Given & When
        User user = User.builder()
                .id("user-1")
                .name("John Doe")
                .email("john.doe@example.com")
                .password("encrypted-password")
                .role(Role.CLIENT)
                .avatar("https://example.com/avatar.jpg")
                .build();

        // Then
        assertThat(user).isNotNull();
        assertThat(user.getId()).isEqualTo("user-1");
        assertThat(user.getName()).isEqualTo("John Doe");
        assertThat(user.getEmail()).isEqualTo("john.doe@example.com");
        assertThat(user.getRole()).isEqualTo(Role.CLIENT);
    }

    @Test
    void testUserEntityCreationWithSellerRole() {
        // Given & When
        User user = User.builder()
                .id("seller-1")
                .name("Jane Seller")
                .email("jane.seller@example.com")
                .password("seller-password")
                .role(Role.SELLER)
                .build();

        // Then
        assertThat(user).isNotNull();
        assertThat(user.getRole()).isEqualTo(Role.SELLER);
        assertThat(user.getEmail()).isEqualTo("jane.seller@example.com");
    }

    @Test
    void testUserDetailsImplementation() {
        // Given
        User user = User.builder()
                .id("user-2")
                .name("Test User")
                .email("test@example.com")
                .password("test-password")
                .role(Role.CLIENT)
                .build();

        // When
        Collection<? extends GrantedAuthority> authorities = user.getAuthorities();
        String username = user.getUsername();
        String password = user.getPassword();

        // Then
        assertThat(authorities).isNotNull();
        assertThat(username).isEqualTo("test@example.com");
        assertThat(password).isEqualTo("test-password");
    }

    @Test
    void testUserAccountStatus() {
        // Given
        User user = User.builder()
                .id("user-3")
                .email("active@example.com")
                .password("password")
                .role(Role.CLIENT)
                .build();

        // When & Then
        assertThat(user.isAccountNonExpired()).isTrue();
        assertThat(user.isAccountNonLocked()).isTrue();
        assertThat(user.isCredentialsNonExpired()).isTrue();
        assertThat(user.isEnabled()).isTrue();
    }

    @Test
    void testUserRoleEnum() {
        // When & Then
        assertThat(Role.CLIENT).isNotNull();
        assertThat(Role.SELLER).isNotNull();
        assertThat(Role.values()).hasSize(2);
        assertThat(Role.valueOf("CLIENT")).isEqualTo(Role.CLIENT);
        assertThat(Role.valueOf("SELLER")).isEqualTo(Role.SELLER);
    }

    @Test
    void testUserWithMinimalData() {
        // Given & When
        User user = User.builder()
                .email("minimal@example.com")
                .password("password123")
                .role(Role.CLIENT)
                .build();

        // Then
        assertThat(user).isNotNull();
        assertThat(user.getEmail()).isEqualTo("minimal@example.com");
        assertThat(user.getId()).isNull();
        assertThat(user.getName()).isNull();
        assertThat(user.getAvatar()).isNull();
    }

    @Test
    void testUserEmailUpdate() {
        // Given
        User user = User.builder()
                .id("user-4")
                .email("old@example.com")
                .password("password")
                .role(Role.CLIENT)
                .build();

        // When
        user.setEmail("new@example.com");

        // Then
        assertThat(user.getEmail()).isEqualTo("new@example.com");
        assertThat(user.getUsername()).isEqualTo("new@example.com");
    }

    @Test
    void testUserPasswordUpdate() {
        // Given
        User user = User.builder()
                .id("user-5")
                .email("user@example.com")
                .password("old-password")
                .role(Role.CLIENT)
                .build();

        // When
        user.setPassword("new-encrypted-password");

        // Then
        assertThat(user.getPassword()).isEqualTo("new-encrypted-password");
    }

    @Test
    void testUserRoleUpdate() {
        // Given
        User user = User.builder()
                .id("user-6")
                .email("upgrade@example.com")
                .password("password")
                .role(Role.CLIENT)
                .build();

        // When
        user.setRole(Role.SELLER);

        // Then
        assertThat(user.getRole()).isEqualTo(Role.SELLER);
    }

    @Test
    void testUserAvatarManagement() {
        // Given
        User user = User.builder()
                .id("user-7")
                .email("avatar@example.com")
                .password("password")
                .role(Role.CLIENT)
                .build();

        // When
        user.setAvatar("https://cdn.example.com/avatar/user-7.jpg");

        // Then
        assertThat(user.getAvatar()).isNotNull();
        assertThat(user.getAvatar()).contains("user-7.jpg");
    }

}
