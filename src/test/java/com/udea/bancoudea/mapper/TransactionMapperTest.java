package com.udea.bancoudea.mapper;

import com.udea.bancoudea.DTO.TransactionDTO;
import com.udea.bancoudea.entity.Transaction;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("Unit Tests para TransactionMapper")
class TransactionMapperTest {

    private TransactionMapper transactionMapper;

    @BeforeEach
    void setUp() {
        transactionMapper = TransactionMapper.INSTANCE;
    }

    @Test
    @DisplayName("Dado una entidad Transaction válida cuando se convierte a DTO entonces retorna TransactionDTO con todos los campos")
    void givenTransactionEntity_whenToDto_thenReturnsCorrectTransactionDTO() {
        // GIVEN
        LocalDateTime timestamp = LocalDateTime.of(2025, 3, 9, 15, 30);
        Transaction transaction = new Transaction(1L, "123456", "789012", 200.0, timestamp);

        // WHEN
        TransactionDTO transactionDTO = transactionMapper.toDTO(transaction);

        // THEN
        assertThat(transactionDTO).isNotNull();
        assertThat(transactionDTO.getId()).isEqualTo(1L);
        assertThat(transactionDTO.getSenderAccountNumber()).isEqualTo("123456");
        assertThat(transactionDTO.getReceiverAccountNumber()).isEqualTo("789012");
        assertThat(transactionDTO.getAmount()).isEqualTo(200.0);
        assertThat(transactionDTO.getTimestamp()).isEqualTo(timestamp);
    }

    @Test
    @DisplayName("Dado una Transaction con monto cero cuando se convierte a DTO entonces retorna DTO con monto cero")
    void givenTransactionWithZeroAmount_whenToDto_thenReturnsDTOWithZeroAmount() {
        // GIVEN
        LocalDateTime timestamp = LocalDateTime.now();
        Transaction transaction = new Transaction(1L, "123456", "789012", 0.0, timestamp);

        // WHEN
        TransactionDTO transactionDTO = transactionMapper.toDTO(transaction);

        // THEN
        assertThat(transactionDTO).isNotNull();
        assertThat(transactionDTO.getAmount()).isEqualTo(0.0);
    }

    @Test
    @DisplayName("Dado una Transaction con monto grande cuando se convierte a DTO entonces presiona el valor correctamente")
    void givenTransactionWithLargeAmount_whenToDto_thenPreservesAmount() {
        // GIVEN
        LocalDateTime timestamp = LocalDateTime.now();
        Transaction transaction = new Transaction(1L, "123456", "789012", 9999999.99, timestamp);

        // WHEN
        TransactionDTO transactionDTO = transactionMapper.toDTO(transaction);

        // THEN
        assertThat(transactionDTO).isNotNull();
        assertThat(transactionDTO.getAmount()).isEqualTo(9999999.99);
    }

    @Test
    @DisplayName("Dado una entidad Transaction nula cuando se convierte a DTO entonces retorna null")
    void givenNullTransaction_whenToDto_thenReturnsNull() {
        // GIVEN
        Transaction transaction = null;

        // WHEN
        TransactionDTO transactionDTO = transactionMapper.toDTO(transaction);

        // THEN
        assertThat(transactionDTO).isNull();
    }
}