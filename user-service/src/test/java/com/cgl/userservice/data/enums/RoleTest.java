package com.cgl.userservice.data.enums;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import static org.assertj.core.api.Assertions.assertThat;

class RoleTest {

    // ==================== ENUM VALUES TESTS ====================

    @Test
    void testRoleValues() {
        // When
        Role[] roles = Role.values();

        // Then
        assertThat(roles).hasSize(2).containsExactly(Role.CLIENT, Role.SELLER);
    }

    @Test
    void testRoleValueOf() {
        // When & Then
        assertThat(Role.valueOf("CLIENT")).isEqualTo(Role.CLIENT);
        assertThat(Role.valueOf("SELLER")).isEqualTo(Role.SELLER);
    }

    // ==================== getRole() TESTS ====================

    @ParameterizedTest
    @ValueSource(strings = {"client", "CLIENT", "ClIeNt", "cLiEnT"})
    void testGetRole_ClientVariations_ReturnsClient(String input) {
        // When
        Role result = Role.getRole(input);

        // Then
        assertThat(result).isEqualTo(Role.CLIENT);
    }

    @ParameterizedTest
    @ValueSource(strings = {"seller", "SELLER", "SeLlEr", "sElLeR"})
    void testGetRole_SellerVariations_ReturnsSeller(String input) {
        // When
        Role result = Role.getRole(input);

        // Then
        assertThat(result).isEqualTo(Role.SELLER);
    }

    @ParameterizedTest
    @ValueSource(strings = {"admin", "", "ADMIN", "USER", "MANAGER", "unknown", "123", "client123"})
    void testGetRole_InvalidRoles_ReturnsNull(String invalidRole) {
        // When
        Role result = Role.getRole(invalidRole);

        // Then
        assertThat(result).isNull();
    }

    // ==================== EDGE CASES TESTS ====================

    @Test
    void testGetRole_ClientWithSpaces_ReturnsNull() {
        // Given - toLowerCase doesn't remove spaces
        String input = " client ";

        // When
        Role result = Role.getRole(input);

        // Then
        assertThat(result).isNull(); // equalsIgnoreCase fails with spaces
    }

    @Test
    void testGetRole_SellerWithSpaces_ReturnsNull() {
        // Given
        String input = " seller ";

        // When
        Role result = Role.getRole(input);

        // Then
        assertThat(result).isNull();
    }

    // ==================== ENUM PROPERTIES TESTS ====================

    @Test
    void testRoleEnumName() {
        // Then
        assertThat(Role.CLIENT.name()).isEqualTo("CLIENT");
        assertThat(Role.SELLER.name()).isEqualTo("SELLER");
    }

    @Test
    void testRoleEnumToString() {
        // Then
        assertThat(Role.CLIENT).hasToString("CLIENT");
        assertThat(Role.SELLER).hasToString("SELLER");
    }

    @Test
    void testRoleEnumOrdinal() {
        // Then
        assertThat(Role.CLIENT.ordinal()).isZero();
        assertThat(Role.SELLER.ordinal()).isEqualTo(1);
    }

    @Test
    void testRoleEnumEquality() {
        // When & Then - Test that CLIENT and SELLER are different
        assertThat(Role.CLIENT).isNotEqualTo(Role.SELLER);
        assertThat(Role.SELLER).isNotEqualTo(Role.CLIENT);
    }

}

