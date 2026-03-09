package com.udea.bancoudea.service;

import com.udea.bancoudea.DTO.TransactionDTO;
import com.udea.bancoudea.entity.Customer;
import com.udea.bancoudea.entity.Transaction;
import com.udea.bancoudea.repository.CustomerRepository;
import com.udea.bancoudea.repository.TransactionRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("Unit Tests para TransactionService")
class TransactionServiceTest {

    @Mock
    private TransactionRepository transactionRepository;

    @Mock
    private CustomerRepository customerRepository;

    @InjectMocks
    private TransactionService transactionService;

    private Customer sender;
    private Customer receiver;
    private Transaction transaction;
    private TransactionDTO transactionDTO;

    @BeforeEach
    void setUp() {
        sender = new Customer(1L, "123456", "Juan", "Pérez", 1000.0);
        receiver = new Customer(2L, "789012", "María", "Gómez", 500.0);

        LocalDateTime timestamp = LocalDateTime.of(2025, 3, 9, 15, 30);
        transaction = new Transaction(1L, "123456", "789012", 200.0, timestamp);

        transactionDTO = new TransactionDTO(null, "123456", "789012", 200.0, null);
    }

    @Test
    @DisplayName("Dado cuentas válidas con saldo suficiente cuando se transfiere dinero entonces actualiza saldos y crea transacción")
    void givenValidAccountsWithSufficientBalance_whenTransferMoney_thenUpdatesBalancesAndCreatesTransaction() {
        // GIVEN
        when(customerRepository.findByAccountNumber("123456")).thenReturn(Optional.of(sender));
        when(customerRepository.findByAccountNumber("789012")).thenReturn(Optional.of(receiver));
        when(customerRepository.save(sender)).thenReturn(sender);
        when(customerRepository.save(receiver)).thenReturn(receiver);
        when(transactionRepository.save(any(Transaction.class))).thenReturn(transaction);

        // WHEN
        TransactionDTO result = transactionService.transferMoney(transactionDTO);

        // THEN
        assertThat(result).isNotNull();
        assertThat(result.getSenderAccountNumber()).isEqualTo("123456");
        assertThat(result.getReceiverAccountNumber()).isEqualTo("789012");
        assertThat(result.getAmount()).isEqualTo(200.0);
        assertThat(result.getTimestamp()).isNotNull();

        // Verificar que los saldos se actualizaron
        assertThat(sender.getBalance()).isEqualTo(800.0);
        assertThat(receiver.getBalance()).isEqualTo(700.0);

        verify(customerRepository, times(1)).findByAccountNumber("123456");
        verify(customerRepository, times(1)).findByAccountNumber("789012");
        verify(customerRepository, times(1)).save(sender);
        verify(customerRepository, times(1)).save(receiver);
        verify(transactionRepository, times(1)).save(any(Transaction.class));
    }

    @Test
    @DisplayName("Dado cuenta remitente nula cuando se transfiere dinero entonces lanza IllegalArgumentException")
    void givenNullSenderAccount_whenTransferMoney_thenThrowsIllegalArgumentException() {
        // GIVEN
        transactionDTO.setSenderAccountNumber(null);

        // WHEN - THEN
        assertThatThrownBy(() -> transactionService.transferMoney(transactionDTO))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Sender Account Number or Receiver Account Number cannot be null");

        verify(customerRepository, never()).findByAccountNumber(any());
        verify(customerRepository, never()).save(any());
        verify(transactionRepository, never()).save(any());
    }

    @Test
    @DisplayName("Dado cuenta destinataria nula cuando se transfiere dinero entonces lanza IllegalArgumentException")
    void givenNullReceiverAccount_whenTransferMoney_thenThrowsIllegalArgumentException() {
        // GIVEN
        transactionDTO.setReceiverAccountNumber(null);

        // WHEN - THEN
        assertThatThrownBy(() -> transactionService.transferMoney(transactionDTO))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Sender Account Number or Receiver Account Number cannot be null");

        verify(customerRepository, never()).findByAccountNumber(any());
        verify(customerRepository, never()).save(any());
        verify(transactionRepository, never()).save(any());
    }

    @Test
    @DisplayName("Dado cuenta remitente inexistente cuando se transfiere dinero entonces lanza IllegalArgumentException")
    void givenNonExistentSenderAccount_whenTransferMoney_thenThrowsIllegalArgumentException() {
        // GIVEN
        when(customerRepository.findByAccountNumber("999999")).thenReturn(Optional.empty());

        transactionDTO.setSenderAccountNumber("999999");

        // WHEN - THEN
        assertThatThrownBy(() -> transactionService.transferMoney(transactionDTO))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Sender Account Number not found");

        verify(customerRepository, times(1)).findByAccountNumber("999999");
        verify(customerRepository, never()).save(any());
        verify(transactionRepository, never()).save(any());
    }

    @Test
    @DisplayName("Dado cuenta destinataria inexistente cuando se transfiere dinero entonces lanza IllegalArgumentException")
    void givenNonExistentReceiverAccount_whenTransferMoney_thenThrowsIllegalArgumentException() {
        // GIVEN
        when(customerRepository.findByAccountNumber("123456")).thenReturn(Optional.of(sender));
        when(customerRepository.findByAccountNumber("888888")).thenReturn(Optional.empty());

        transactionDTO.setReceiverAccountNumber("888888");

        // WHEN - THEN
        assertThatThrownBy(() -> transactionService.transferMoney(transactionDTO))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Receiver Account Number not found");

        verify(customerRepository, times(1)).findByAccountNumber("123456");
        verify(customerRepository, times(1)).findByAccountNumber("888888");
        verify(customerRepository, never()).save(any());
        verify(transactionRepository, never()).save(any());
    }

    @Test
    @DisplayName("Dado saldo insuficiente en cuenta remitente cuando se transfiere dinero entonces lanza IllegalArgumentException")
    void givenInsufficientBalance_whenTransferMoney_thenThrowsIllegalArgumentException() {
        // GIVEN
        Customer poorSender = new Customer(1L, "123456", "Juan", "Pérez", 100.0);
        when(customerRepository.findByAccountNumber("123456")).thenReturn(Optional.of(poorSender));
        when(customerRepository.findByAccountNumber("789012")).thenReturn(Optional.of(receiver));

        transactionDTO.setAmount(200.0);

        // WHEN - THEN
        assertThatThrownBy(() -> transactionService.transferMoney(transactionDTO))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Sender Balance not enough");

        // Verificar que los saldos no cambiaron
        assertThat(poorSender.getBalance()).isEqualTo(100.0);
        assertThat(receiver.getBalance()).isEqualTo(500.0);

        verify(customerRepository, times(1)).findByAccountNumber("123456");
        verify(customerRepository, times(1)).findByAccountNumber("789012");
        verify(customerRepository, never()).save(any());
        verify(transactionRepository, never()).save(any());
    }

    @Test
    @DisplayName("Dado saldo exacto para transferencia cuando se transfiere dinero entonces completa exitosamente dejando saldo en cero")
    void givenExactBalance_whenTransferMoney_thenCompletesSuccessfullyWithZeroBalance() {
        // GIVEN
        Customer exactSender = new Customer(1L, "123456", "Juan", "Pérez", 200.0);
        when(customerRepository.findByAccountNumber("123456")).thenReturn(Optional.of(exactSender));
        when(customerRepository.findByAccountNumber("789012")).thenReturn(Optional.of(receiver));
        when(customerRepository.save(exactSender)).thenReturn(exactSender);
        when(customerRepository.save(receiver)).thenReturn(receiver);
        when(transactionRepository.save(any(Transaction.class))).thenReturn(transaction);

        transactionDTO.setAmount(200.0);

        // WHEN
        TransactionDTO result = transactionService.transferMoney(transactionDTO);

        // THEN
        assertThat(result).isNotNull();
        assertThat(exactSender.getBalance()).isEqualTo(0.0);
        assertThat(receiver.getBalance()).isEqualTo(700.0);

        verify(customerRepository, times(1)).save(exactSender);
        verify(customerRepository, times(1)).save(receiver);
        verify(transactionRepository, times(1)).save(any(Transaction.class));
    }

    @Test
    @DisplayName("Dado transferencia con monto cero cuando se transfiere dinero entonces completa sin cambiar saldos")
    void givenZeroAmount_whenTransferMoney_thenCompletesWithoutChangingBalances() {
        // GIVEN
        when(customerRepository.findByAccountNumber("123456")).thenReturn(Optional.of(sender));
        when(customerRepository.findByAccountNumber("789012")).thenReturn(Optional.of(receiver));
        when(customerRepository.save(sender)).thenReturn(sender);
        when(customerRepository.save(receiver)).thenReturn(receiver);

        LocalDateTime timestamp = LocalDateTime.now();
        Transaction zeroTransaction = new Transaction(1L, "123456", "789012", 0.0, timestamp);
        when(transactionRepository.save(any(Transaction.class))).thenReturn(zeroTransaction);

        transactionDTO.setAmount(0.0);

        // WHEN
        TransactionDTO result = transactionService.transferMoney(transactionDTO);

        // THEN
        assertThat(result).isNotNull();
        assertThat(sender.getBalance()).isEqualTo(1000.0);
        assertThat(receiver.getBalance()).isEqualTo(500.0);

        verify(customerRepository, times(1)).save(sender);
        verify(customerRepository, times(1)).save(receiver);
        verify(transactionRepository, times(1)).save(any(Transaction.class));
    }

    @Test
    @DisplayName("Dado cuenta con transacciones cuando se obtienen transacciones entonces retorna todas las transacciones de la cuenta")
    void givenAccountWithTransactions_whenGetTransactionsForAccount_thenReturnsAllTransactions() {
        // GIVEN
        Transaction transaction1 = new Transaction(1L, "123456", "789012", 200.0, LocalDateTime.now());
        Transaction transaction2 = new Transaction(2L, "999999", "123456", 150.0, LocalDateTime.now());
        List<Transaction> transactions = Arrays.asList(transaction1, transaction2);

        when(transactionRepository.findBySenderAccountNumberOrReceiverAccountNumber("123456", "123456"))
                .thenReturn(transactions);

        // WHEN
        List<TransactionDTO> result = transactionService.getTransactionsForAccount("123456");

        // THEN
        assertThat(result).hasSize(2);
        assertThat(result.get(0).getSenderAccountNumber()).isEqualTo("123456");
        assertThat(result.get(1).getReceiverAccountNumber()).isEqualTo("123456");

        verify(transactionRepository, times(1))
                .findBySenderAccountNumberOrReceiverAccountNumber("123456", "123456");
    }

    @Test
    @DisplayName("Dado cuenta sin transacciones cuando se obtienen transacciones entonces retorna lista vacía")
    void givenAccountWithoutTransactions_whenGetTransactionsForAccount_thenReturnsEmptyList() {
        // GIVEN
        when(transactionRepository.findBySenderAccountNumberOrReceiverAccountNumber("999999", "999999"))
                .thenReturn(Arrays.asList());

        // WHEN
        List<TransactionDTO> result = transactionService.getTransactionsForAccount("999999");

        // THEN
        assertThat(result).isEmpty();

        verify(transactionRepository, times(1))
                .findBySenderAccountNumberOrReceiverAccountNumber("999999", "999999");
    }

    @Test
    @DisplayName("Dado transferencia grande cuando se transfiere dinero entonces actualiza saldos correctamente")
    void givenLargeAmountTransfer_whenTransferMoney_thenUpdatesBalancesCorrectly() {
        // GIVEN
        Customer richSender = new Customer(1L, "123456", "Juan", "Pérez", 1000000.0);
        when(customerRepository.findByAccountNumber("123456")).thenReturn(Optional.of(richSender));
        when(customerRepository.findByAccountNumber("789012")).thenReturn(Optional.of(receiver));
        when(customerRepository.save(richSender)).thenReturn(richSender);
        when(customerRepository.save(receiver)).thenReturn(receiver);

        LocalDateTime timestamp = LocalDateTime.now();
        Transaction largeTransaction = new Transaction(1L, "123456", "789012", 500000.0, timestamp);
        when(transactionRepository.save(any(Transaction.class))).thenReturn(largeTransaction);

        transactionDTO.setAmount(500000.0);

        // WHEN
        TransactionDTO result = transactionService.transferMoney(transactionDTO);

        // THEN
        assertThat(result).isNotNull();
        assertThat(richSender.getBalance()).isEqualTo(500000.0);
        assertThat(receiver.getBalance()).isEqualTo(500500.0);

        verify(customerRepository, times(1)).save(richSender);
        verify(customerRepository, times(1)).save(receiver);
    }

    @Test
    @DisplayName("Dado transferencia con monto decimal cuando se transfiere dinero entonces presiciona los decimales correctamente")
    void givenDecimalAmountTransfer_whenTransferMoney_thenPreservesDecimalPrecision() {
        // GIVEN
        when(customerRepository.findByAccountNumber("123456")).thenReturn(Optional.of(sender));
        when(customerRepository.findByAccountNumber("789012")).thenReturn(Optional.of(receiver));
        when(customerRepository.save(sender)).thenReturn(sender);
        when(customerRepository.save(receiver)).thenReturn(receiver);

        LocalDateTime timestamp = LocalDateTime.now();
        Transaction decimalTransaction = new Transaction(1L, "123456", "789012", 123.45, timestamp);
        when(transactionRepository.save(any(Transaction.class))).thenReturn(decimalTransaction);

        transactionDTO.setAmount(123.45);

        // WHEN
        TransactionDTO result = transactionService.transferMoney(transactionDTO);

        // THEN
        assertThat(result).isNotNull();
        assertThat(result.getAmount()).isEqualTo(123.45);
        assertThat(sender.getBalance()).isEqualTo(876.55);
        assertThat(receiver.getBalance()).isEqualTo(623.45);

        verify(customerRepository, times(1)).save(sender);
        verify(customerRepository, times(1)).save(receiver);
    }
}