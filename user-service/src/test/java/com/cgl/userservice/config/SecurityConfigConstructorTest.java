package com.cgl.userservice.config;

import com.cgl.userservice.utils.JwtControl;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith(MockitoExtension.class)
class SecurityConfigConstructorTest {

    @Mock
    private JwtControl jwtFilter;

    @Test
    void testSecurityConfigConstructor() {
        // When - Create instance using Lombok-generated constructor
        SecurityConfig securityConfig = new SecurityConfig(jwtFilter);

        // Then - Verify the instance is created
        assertThat(securityConfig).isNotNull();
    }

    @Test
    void testSecurityConfigConstructorWithRealFilter() {
        // Given
        JwtControl realFilter = new JwtControl(null, null);

        // When
        SecurityConfig securityConfig = new SecurityConfig(realFilter);

        // Then
        assertThat(securityConfig).isNotNull();
    }

    @Test
    void testSecurityConfigIsNotAbstract() {
        // Then - Verify SecurityConfig can be instantiated
        assertThat(SecurityConfig.class).isNotNull();
        assertThat(java.lang.reflect.Modifier.isAbstract(SecurityConfig.class.getModifiers())).isFalse();
    }
}

