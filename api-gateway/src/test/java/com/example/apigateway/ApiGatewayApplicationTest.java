package com.example.apigateway;

import org.junit.jupiter.api.Test;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;

import static org.assertj.core.api.Assertions.assertThat;

class ApiGatewayApplicationTest {

    @Test
    void testMainMethod() {
        // Then - Verify main method exists
        assertThat(ApiGatewayApplication.class).isNotNull();
        assertThat(ApiGatewayApplication.class.getDeclaredMethods())
                .anyMatch(method -> method.getName().equals("main"));
    }

    @Test
    void testApplicationHasSpringBootApplicationAnnotation() {
        // Then - Verify @SpringBootApplication annotation is present
        assertThat(ApiGatewayApplication.class.isAnnotationPresent(SpringBootApplication.class)).isTrue();
    }

    @Test
    void testApplicationHasEnableDiscoveryClientAnnotation() {
        // Then - Verify @EnableDiscoveryClient annotation is present
        assertThat(ApiGatewayApplication.class.isAnnotationPresent(EnableDiscoveryClient.class)).isTrue();
    }

    @Test
    void testApplicationClassExists() {
        // Then - Verify the main application class exists
        assertThat(ApiGatewayApplication.class).isNotNull();
        assertThat(ApiGatewayApplication.class.getSimpleName()).isEqualTo("ApiGatewayApplication");
    }

    @Test
    void testApplicationPackage() {
        // Then - Verify the package
        assertThat(ApiGatewayApplication.class.getPackage().getName()).isEqualTo("com.example.apigateway");
    }

    @Test
    void testMainMethodSignature() throws NoSuchMethodException {
        // When
        var mainMethod = ApiGatewayApplication.class.getDeclaredMethod("main", String[].class);

        // Then
        assertThat(mainMethod).isNotNull();
        assertThat(mainMethod.getReturnType()).isEqualTo(void.class);
        assertThat(mainMethod.getParameterCount()).isEqualTo(1);
    }
}

