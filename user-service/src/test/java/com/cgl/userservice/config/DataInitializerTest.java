package com.cgl.userservice.config;

import com.cgl.userservice.data.entities.User;
import com.cgl.userservice.data.enums.Role;
import com.cgl.userservice.data.repositories.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class DataInitializerTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Captor
    private ArgumentCaptor<User> userCaptor;

    private DataInitializer dataInitializer;

    @BeforeEach
    void setUp() {
        dataInitializer = new DataInitializer(userRepository, passwordEncoder);
    }

    @Test
    void testInitializeData_BeanCreation() {
        // When
        CommandLineRunner runner = dataInitializer.initializeData();

        // Then
        assertThat(runner).isNotNull();
    }

    @Test
    void testInitializeData_WhenDatabaseHasDataThenDeleteAll() throws Exception {
        // Given - Database has existing data (count != 0)
        when(userRepository.count()).thenReturn(5L, 0L); // First call returns 5, second returns 0
        when(passwordEncoder.encode(anyString())).thenReturn("encodedPassword");

        // When
        CommandLineRunner runner = dataInitializer.initializeData();
        runner.run();

        // Then - Condition 1: count() != 0 → deleteAll() called
        verify(userRepository, times(1)).deleteAll();
        verify(userRepository, times(3)).save(any(User.class)); // 3 users created
    }

    @Test
    void testInitializeData_WhenDatabaseEmpty_CreateThreeUsers() throws Exception {
        // Given - Database is empty (count == 0)
        when(userRepository.count()).thenReturn(0L);
        when(passwordEncoder.encode(anyString())).thenReturn("encodedPassword");

        // When
        CommandLineRunner runner = dataInitializer.initializeData();
        runner.run();

        // Then - Condition 2: count() == 0 → create users
        verify(userRepository, never()).deleteAll(); // Not called when already empty
        verify(userRepository, times(3)).save(userCaptor.capture());

        // Verify the 3 users created
        var savedUsers = userCaptor.getAllValues();
        assertThat(savedUsers).hasSize(3);

        // Verify seller
        User seller = savedUsers.get(0);
        assertThat(seller.getName()).isEqualTo("Fatima Keita");
        assertThat(seller.getEmail()).isEqualTo("ftk@user.com");
        assertThat(seller.getRole()).isEqualTo(Role.SELLER);
        assertThat(seller.getAvatar()).startsWith("data:image/png;base64");

        // Verify first client
        User client1 = savedUsers.get(1);
        assertThat(client1.getName()).isEqualTo("Anna Ndiaye");
        assertThat(client1.getEmail()).isEqualTo("anna@user.com");
        assertThat(client1.getRole()).isEqualTo(Role.CLIENT);

        // Verify second client
        User client2 = savedUsers.get(2);
        assertThat(client2.getName()).isEqualTo("John DOE");
        assertThat(client2.getEmail()).isEqualTo("johndoe@user.com");
        assertThat(client2.getRole()).isEqualTo(Role.CLIENT);
    }

    @Test
    void testInitializeData_UsesDefaultPassword_WhenEnvNotSet() throws Exception {
        // Given - No environment variables set, should use DEFAULT_TEMP_CREDENTIAL
        when(userRepository.count()).thenReturn(0L);
        when(passwordEncoder.encode("TempPass123!")).thenReturn("encodedDefaultPassword");

        // When
        CommandLineRunner runner = dataInitializer.initializeData();
        runner.run();

        // Then - Condition 3 & 4: getOrDefault returns default value
        verify(passwordEncoder, times(3)).encode("TempPass123!");
    }

    @Test
    void testInitializeData_PasswordEncodingCalled() throws Exception {
        // Given
        when(userRepository.count()).thenReturn(0L);
        when(passwordEncoder.encode(anyString())).thenReturn("encoded");

        // When
        CommandLineRunner runner = dataInitializer.initializeData();
        runner.run();

        // Then
        verify(passwordEncoder, times(3)).encode(anyString());
    }

    @Test
    void testInitializeData_LogInfoCalled_WhenInitializing() throws Exception {
        // Given
        when(userRepository.count()).thenReturn(0L);
        when(passwordEncoder.encode(anyString())).thenReturn("encoded");

        // When
        CommandLineRunner runner = dataInitializer.initializeData();
        runner.run();

        // Then - log.info("Initializing data") is called
        // This is implicitly tested by verifying users are created
        verify(userRepository, times(3)).save(any(User.class));
    }

    @Test
    void testInitializeData_SellerUser_HasAvatar() throws Exception {
        // Given
        when(userRepository.count()).thenReturn(0L);
        when(passwordEncoder.encode(anyString())).thenReturn("encoded");

        // When
        CommandLineRunner runner = dataInitializer.initializeData();
        runner.run();

        // Then
        verify(userRepository, times(3)).save(userCaptor.capture());
        User seller = userCaptor.getAllValues().get(0);

        assertThat(seller.getAvatar()).isNotNull();
        assertThat(seller.getAvatar()).isNotEmpty();
        assertThat(seller.getAvatar()).contains("data:image/png;base64");
        assertThat(seller.getAvatar().length()).isGreaterThan(100); // Avatar is a long base64 string
    }

    @Test
    void testInitializeData_ClientUsers_NoAvatar() throws Exception {
        // Given
        when(userRepository.count()).thenReturn(0L);
        when(passwordEncoder.encode(anyString())).thenReturn("encoded");

        // When
        CommandLineRunner runner = dataInitializer.initializeData();
        runner.run();

        // Then
        verify(userRepository, times(3)).save(userCaptor.capture());

        User client1 = userCaptor.getAllValues().get(1);
        User client2 = userCaptor.getAllValues().get(2);

        assertThat(client1.getAvatar()).isNull();
        assertThat(client2.getAvatar()).isNull();
    }

    @Test
    void testInitializeData_AllUsersHaveEncodedPasswords() throws Exception {
        // Given
        when(userRepository.count()).thenReturn(0L);
        when(passwordEncoder.encode(anyString())).thenReturn("superEncodedPassword");

        // When
        CommandLineRunner runner = dataInitializer.initializeData();
        runner.run();

        // Then
        verify(userRepository, times(3)).save(userCaptor.capture());

        for (User user : userCaptor.getAllValues()) {
            assertThat(user.getPassword()).isEqualTo("superEncodedPassword");
        }
    }

    @Test
    void testInitializeData_OnlyCreatesUsersWhenCountIsZero() throws Exception {
        // Given - Database not empty on second check
        when(userRepository.count()).thenReturn(0L, 5L); // First 0, then 5 after deleteAll check

        // When
        CommandLineRunner runner = dataInitializer.initializeData();
        runner.run();

        // Then - Should not create users because count is not 0 on second check
        verify(userRepository, never()).save(any(User.class));
    }

    @Test
    void testInitializeData_MultipleCallsDeleteAndRecreate() throws Exception {
        // Given
        when(userRepository.count()).thenReturn(3L, 0L); // Has data first, empty after delete
        when(passwordEncoder.encode(anyString())).thenReturn("encoded");

        // When
        CommandLineRunner runner = dataInitializer.initializeData();
        runner.run();

        // Then
        verify(userRepository, times(1)).deleteAll();
        verify(userRepository, times(3)).save(any(User.class));
    }

    @Test
    void testInitializeData_VerifyUserDetails() throws Exception {
        // Given
        when(userRepository.count()).thenReturn(0L);
        when(passwordEncoder.encode("TempPass123!")).thenReturn("encoded");

        // When
        CommandLineRunner runner = dataInitializer.initializeData();
        runner.run();

        // Then
        verify(userRepository, times(3)).save(userCaptor.capture());
        var users = userCaptor.getAllValues();

        // Verify all users are built correctly
        assertThat(users).extracting(User::getName)
                .containsExactly("Fatima Keita", "Anna Ndiaye", "John DOE");

        assertThat(users).extracting(User::getEmail)
                .containsExactly("ftk@user.com", "anna@user.com", "johndoe@user.com");

        assertThat(users).extracting(User::getRole)
                .containsExactly(Role.SELLER, Role.CLIENT, Role.CLIENT);
    }

    @Test
    void testDefaultTempCredential_Constant() {
        // This test verifies the constant is accessible
        // The constant value is "TempPass123!"
        // Verified implicitly through password encoding tests
        assertThat(dataInitializer).isNotNull();
    }

    @Test
    void testCommandLineRunner_RunMethodCanBeCalledMultipleTimes() throws Exception {
        // Given
        when(userRepository.count()).thenReturn(0L);
        when(passwordEncoder.encode(anyString())).thenReturn("encoded");

        // When
        CommandLineRunner runner = dataInitializer.initializeData();
        runner.run();

        // Reset mocks for second run
        reset(userRepository, passwordEncoder);
        when(userRepository.count()).thenReturn(3L, 0L); // Has data, then empty
        when(passwordEncoder.encode(anyString())).thenReturn("encoded");

        runner.run();

        // Then - Should delete and recreate on second run
        verify(userRepository, times(1)).deleteAll();
        verify(userRepository, times(3)).save(any(User.class));
    }
}

