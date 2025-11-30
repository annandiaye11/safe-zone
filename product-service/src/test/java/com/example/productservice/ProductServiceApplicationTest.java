package com.example.productservice;

import org.junit.jupiter.api.Test;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.data.mongodb.config.EnableMongoAuditing;

import static org.assertj.core.api.Assertions.assertThat;

class ProductServiceApplicationTest {

    @Test
    void testMainMethod() {
        // Then - Verify main method exists
        assertThat(ProductServiceApplication.class).isNotNull();
        assertThat(ProductServiceApplication.class.getDeclaredMethods())
                .anyMatch(method -> method.getName().equals("main"));
    }

    @Test
    void testApplicationHasSpringBootApplicationAnnotation() {
        // Then - Verify @SpringBootApplication annotation is present
        assertThat(ProductServiceApplication.class.isAnnotationPresent(SpringBootApplication.class)).isTrue();
    }

    @Test
    void testApplicationHasEnableMongoAuditingAnnotation() {
        // Then - Verify @EnableMongoAuditing annotation is present
        assertThat(ProductServiceApplication.class.isAnnotationPresent(EnableMongoAuditing.class)).isTrue();
    }

    @Test
    void testApplicationHasEnableDiscoveryClientAnnotation() {
        // Then - Verify @EnableDiscoveryClient annotation is present
        assertThat(ProductServiceApplication.class.isAnnotationPresent(EnableDiscoveryClient.class)).isTrue();
    }

    @Test
    void testApplicationClassExists() {
        // Then - Verify the main application class exists
        assertThat(ProductServiceApplication.class).isNotNull();
        assertThat(ProductServiceApplication.class.getSimpleName()).isEqualTo("ProductServiceApplication");
    }

    @Test
    void testApplicationPackage() {
        // Then - Verify the package
        assertThat(ProductServiceApplication.class.getPackage().getName()).isEqualTo("com.example.productservice");
    }

    @Test
    void testMainMethodSignature() throws NoSuchMethodException {
        // When
        var mainMethod = ProductServiceApplication.class.getDeclaredMethod("main", String[].class);

        // Then
        assertThat(mainMethod).isNotNull();
        assertThat(mainMethod.getReturnType()).isEqualTo(void.class);
        assertThat(mainMethod.getParameterCount()).isEqualTo(1);
    }
}

