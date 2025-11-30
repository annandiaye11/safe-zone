package com.example.eurekaserver;

import com.netflix.eureka.EurekaServerContext;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.context.ApplicationContext;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
class EurekaServerApplicationTests {

    @Autowired
    private ApplicationContext applicationContext;

    @Autowired(required = false)
    private EurekaServerContext eurekaServerContext;

    @Test
    void contextLoads() {
        // Verify that the application context loads successfully
        assertThat(applicationContext).isNotNull();
    }

    @Test
    void testEurekaServerContextExists() {
        // Verify that Eureka Server context is available
        assertThat(eurekaServerContext).isNotNull();
    }

    @Test
    void testApplicationContextContainsRequiredBeans() {
        // Verify essential beans are loaded
        String[] beanNames = applicationContext.getBeanDefinitionNames();
        assertThat(beanNames).hasSizeGreaterThan(10);
    }
}
