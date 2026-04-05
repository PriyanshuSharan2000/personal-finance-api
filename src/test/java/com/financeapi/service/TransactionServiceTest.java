package com.financeapi.service;

import com.financeapi.dto.request.TransactionRequest;
import com.financeapi.dto.response.TransactionResponse;
import com.financeapi.exception.ResourceNotFoundException;
import com.financeapi.model.Transaction;
import com.financeapi.model.User;
import com.financeapi.repository.TransactionRepository;
import com.financeapi.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class TransactionServiceTest {

    @Mock private TransactionRepository transactionRepository;
    @Mock private UserRepository userRepository;
    @Mock private SecurityContext securityContext;
    @Mock private Authentication authentication;

    @InjectMocks
    private TransactionService transactionService;

    private User testUser;

    @BeforeEach
    void setUp() {
        testUser = User.builder()
                .id(1L)
                .name("Priyanshu")
                .email("test@example.com")
                .password("encoded")
                .role(User.Role.USER)
                .build();

        when(securityContext.getAuthentication()).thenReturn(authentication);
        when(authentication.getName()).thenReturn(testUser.getEmail());
        SecurityContextHolder.setContext(securityContext);
        when(userRepository.findByEmail(testUser.getEmail())).thenReturn(Optional.of(testUser));
    }

    @Test
    @DisplayName("Should create transaction successfully")
    void createTransaction_Success() {
        TransactionRequest request = new TransactionRequest();
        request.setType(Transaction.TransactionType.EXPENSE);
        request.setAmount(new BigDecimal("500.00"));
        request.setCategory(Transaction.Category.FOOD);
        request.setDescription("Lunch");
        request.setTxnDate(LocalDate.now());

        Transaction saved = Transaction.builder()
                .id(1L)
                .user(testUser)
                .type(request.getType())
                .amount(request.getAmount())
                .category(request.getCategory())
                .description(request.getDescription())
                .txnDate(request.getTxnDate())
                .build();

        when(transactionRepository.save(any(Transaction.class))).thenReturn(saved);

        TransactionResponse response = transactionService.create(request);

        assertThat(response).isNotNull();
        assertThat(response.getId()).isEqualTo(1L);
        assertThat(response.getAmount()).isEqualByComparingTo("500.00");
        assertThat(response.getCategory()).isEqualTo(Transaction.Category.FOOD);

        verify(transactionRepository, times(1)).save(any(Transaction.class));
    }

    @Test
    @DisplayName("Should throw ResourceNotFoundException for invalid transaction ID")
    void getById_NotFound_ThrowsException() {
        when(transactionRepository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> transactionService.getById(99L))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("99");
    }

    @Test
    @DisplayName("Should throw ResourceNotFoundException when deleting transaction of another user")
    void delete_OtherUserTransaction_ThrowsException() {
        User otherUser = User.builder().id(2L).email("other@example.com").build();
        Transaction otherTransaction = Transaction.builder()
                .id(10L)
                .user(otherUser)
                .build();

        when(transactionRepository.findById(10L)).thenReturn(Optional.of(otherTransaction));

        assertThatThrownBy(() -> transactionService.delete(10L))
                .isInstanceOf(ResourceNotFoundException.class);
    }

    @Test
    @DisplayName("Should delete transaction successfully")
    void delete_Success() {
        Transaction transaction = Transaction.builder()
                .id(1L)
                .user(testUser)
                .build();

        when(transactionRepository.findById(1L)).thenReturn(Optional.of(transaction));
        doNothing().when(transactionRepository).delete(transaction);

        assertThatCode(() -> transactionService.delete(1L)).doesNotThrowAnyException();
        verify(transactionRepository, times(1)).delete(transaction);
    }
}
