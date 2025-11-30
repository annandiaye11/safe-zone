package com.example.eurekaserver;

import org.junit.jupiter.api.Test;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.netflix.eureka.server.EnableEurekaServer;

import static org.assertj.core.api.Assertions.assertThat;

class EurekaServerApplicationTest {

    @Test
    void testMainMethod() {
        // Then - Verify main method exists
        assertThat(EurekaServerApplication.class).isNotNull();
        assertThat(EurekaServerApplication.class.getDeclaredMethods())
                .anyMatch(method -> method.getName().equals("main"));
    }

    @Test
    void testApplicationHasSpringBootApplicationAnnotation() {
        // Then - Verify @SpringBootApplication annotation is present
        assertThat(EurekaServerApplication.class.isAnnotationPresent(SpringBootApplication.class)).isTrue();
    }

    @Test
    void testApplicationHasEnableEurekaServerAnnotation() {
        // Then - Verify @EnableEurekaServer annotation is present
        assertThat(EurekaServerApplication.class.isAnnotationPresent(EnableEurekaServer.class)).isTrue();
    }

    @Test
    void testApplicationClassExists() {
        // Then - Verify the main application class exists
        assertThat(EurekaServerApplication.class).isNotNull();
        assertThat(EurekaServerApplication.class.getSimpleName()).isEqualTo("EurekaServerApplication");
    }

    @Test
    void testApplicationPackage() {
        // Then - Verify the package
        assertThat(EurekaServerApplication.class.getPackage().getName()).isEqualTo("com.example.eurekaserver");
    }

    @Test
    void testMainMethodSignature() throws NoSuchMethodException {
        // When
        var mainMethod = EurekaServerApplication.class.getDeclaredMethod("main", String[].class);

        // Then
        assertThat(mainMethod).isNotNull();
        assertThat(mainMethod.getReturnType()).isEqualTo(void.class);
        assertThat(mainMethod.getParameterCount()).isEqualTo(1);
    }
}

