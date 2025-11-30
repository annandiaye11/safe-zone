package com.example.apigateway;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.cloud.gateway.route.RouteLocator;
import org.springframework.context.ApplicationContext;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
class
ApiGatewayApplicationTests {

    @Autowired
    private ApplicationContext applicationContext;

    @Autowired(required = false)
    private RouteLocator routeLocator;

    @Test
    void contextLoads() {
        // Verify that the application context loads successfully
        assertThat(applicationContext).isNotNull();
    }

    @Test
    void testRouteLocatorBeanExists() {
        // Verify that RouteLocator bean is available
        assertThat(routeLocator).isNotNull();
    }

    @Test
    void testApplicationContextContainsRequiredBeans() {
        // Verify essential beans are loaded
        String[] beanNames = applicationContext.getBeanDefinitionNames();
        assertThat(beanNames).hasSizeGreaterThan(0);
    }
}
