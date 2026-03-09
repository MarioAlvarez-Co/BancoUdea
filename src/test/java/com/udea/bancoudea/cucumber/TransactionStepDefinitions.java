package com.udea.bancoudea.cucumber;

import com.udea.bancoudea.DTO.CustomerDTO;
import com.udea.bancoudea.DTO.TransactionDTO;
import com.udea.bancoudea.entity.Customer;
import com.udea.bancoudea.repository.CustomerRepository;
import com.udea.bancoudea.service.CustomerService;
import com.udea.bancoudea.service.TransactionService;
import io.cucumber.java.en.And;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
public class TransactionStepDefinitions {

    @Autowired
    private TransactionService transactionService;

    @Autowired
    private CustomerService customerService;

    @Autowired
    private CustomerRepository customerRepository;

    private TransactionDTO transactionResult;
    private List<TransactionDTO> transactionList;
    private Throwable thrownException;
    private Customer sender;
    private Customer receiver;

    @Given("que la cuenta {string} tiene saldo de {double}")
    public void queLaCuentaTieneSaldoDe(String accountNumber, double balance) {
        CustomerDTO customerDTO = new CustomerDTO();
        customerDTO.setAccountNumber(accountNumber);
        customerDTO.setFirstName("Cliente");
        customerDTO.setLastName("Test");
        customerDTO.setBalance(balance);

        CustomerDTO created = customerService.createCustomer(customerDTO);

        if (accountNumber.equals("123456")) {
            sender = customerRepository.findByAccountNumber(accountNumber).orElseThrow();
        } else if (accountNumber.equals("789012")) {
            receiver = customerRepository.findByAccountNumber(accountNumber).orElseThrow();
        }
    }

    @Given("que la cuenta {string} existe con saldo {double}")
    public void queLaCuentaExisteConSaldo(String accountNumber, double balance) {
        CustomerDTO customerDTO = new CustomerDTO();
        customerDTO.setAccountNumber(accountNumber);
        customerDTO.setFirstName("Cliente");
        customerDTO.setLastName("Test");
        customerDTO.setBalance(balance);
        customerService.createCustomer(customerDTO);

        if (accountNumber.equals("123456")) {
            sender = customerRepository.findByAccountNumber(accountNumber).orElseThrow();
        }
    }

    @Given("que la cuenta {string} no existe")
    public void queLaCuentaNoExiste(String accountNumber) {
        customerRepository.findByAccountNumber(accountNumber).ifPresent(customer -> {
            customerRepository.delete(customer);
        });
    }

    @Given("la cuenta {string} no existe")
    public void laCuentaNoExiste(String accountNumber) {
        customerRepository.findByAccountNumber(accountNumber).ifPresent(customer -> {
            customerRepository.delete(customer);
        });
    }

    @When("se transfiere {double} de {string} a {string}")
    public void seTransfiereDeA(double amount, String fromAccount, String toAccount) {
        try {
            TransactionDTO transactionDTO = new TransactionDTO();
            transactionDTO.setSenderAccountNumber(fromAccount);
            transactionDTO.setReceiverAccountNumber(toAccount);
            transactionDTO.setAmount(amount);

            transactionResult = transactionService.transferMoney(transactionDTO);
        } catch (Exception e) {
            thrownException = e;
        }
    }

    @When("se intenta transferir {double} de {string} a {string}")
    public void seIntentaTransferirDeA(double amount, String fromAccount, String toAccount) {
        try {
            TransactionDTO transactionDTO = new TransactionDTO();
            transactionDTO.setSenderAccountNumber(fromAccount);
            transactionDTO.setReceiverAccountNumber(toAccount);
            transactionDTO.setAmount(amount);

            transactionResult = transactionService.transferMoney(transactionDTO);
        } catch (Exception e) {
            thrownException = e;
        }
    }

    @When("se intenta transferir de la cuenta {string} a {string}")
    public void seIntentaTransferirDeLaCuentaA(String fromAccount, String toAccount) {
        try {
            TransactionDTO transactionDTO = new TransactionDTO();
            transactionDTO.setSenderAccountNumber(fromAccount);
            transactionDTO.setReceiverAccountNumber(toAccount);
            transactionDTO.setAmount(200.0);

            transactionResult = transactionService.transferMoney(transactionDTO);
        } catch (Exception e) {
            thrownException = e;
        }
    }

    @Then("la transferencia se completa exitosamente")
    public void laTransferenciaSeCompletaExitosamente() {
        assertThat(transactionResult).isNotNull();
        assertThat(transactionResult.getId()).isNotNull();
    }

    @And("la cuenta {string} tiene saldo de {double}")
    public void laCuentaTieneSaldoDe(String accountNumber, double expectedBalance) {
        Customer customer = customerRepository.findByAccountNumber(accountNumber).orElseThrow();
        assertThat(customer.getBalance()).isEqualTo(expectedBalance);
    }

    @And("se registra la transacción con remitente {string}")
    public void seRegistraLaTransaccionConRemitente(String accountNumber) {
        assertThat(transactionResult.getSenderAccountNumber()).isEqualTo(accountNumber);
    }

    @And("se registra la transacción con destinatario {string}")
    public void seRegistraLaTransaccionConDestinatario(String accountNumber) {
        assertThat(transactionResult.getReceiverAccountNumber()).isEqualTo(accountNumber);
    }

    @And("se registra la transacción con monto {double}")
    public void seRegistraLaTransaccionConMonto(double amount) {
        assertThat(transactionResult.getAmount()).isEqualTo(amount);
    }

    @Given("que se quiere realizar una transferencia")
    public void queSeQuiereRealizarUnaTransferencia() {
        CustomerDTO customerDTO1 = new CustomerDTO();
        customerDTO1.setAccountNumber("123456");
        customerDTO1.setFirstName("Juan");
        customerDTO1.setLastName("Pérez");
        customerDTO1.setBalance(1000.0);
        customerService.createCustomer(customerDTO1);

        CustomerDTO customerDTO2 = new CustomerDTO();
        customerDTO2.setAccountNumber("789012");
        customerDTO2.setFirstName("María");
        customerDTO2.setLastName("Gómez");
        customerDTO2.setBalance(500.0);
        customerService.createCustomer(customerDTO2);
    }

    @When("la cuenta remitente es null")
    public void laCuentaRemitenteEsNull() {
        try {
            TransactionDTO transactionDTO = new TransactionDTO();
            transactionDTO.setSenderAccountNumber(null);
            transactionDTO.setReceiverAccountNumber("789012");
            transactionDTO.setAmount(200.0);

            transactionResult = transactionService.transferMoney(transactionDTO);
        } catch (Exception e) {
            thrownException = e;
        }
    }

    @When("la cuenta destinataria es null")
    public void laCuentaDestinatariaEsNull() {
        try {
            TransactionDTO transactionDTO = new TransactionDTO();
            transactionDTO.setSenderAccountNumber("123456");
            transactionDTO.setReceiverAccountNumber(null);
            transactionDTO.setAmount(200.0);

            transactionResult = transactionService.transferMoney(transactionDTO);
        } catch (Exception e) {
            thrownException = e;
        }
    }

    @Then("el sistema lanza IllegalArgumentException")
    public void elSistemaLanzaIllegalArgumentException() {
        assertThat(thrownException).isNotNull();
        assertThat(thrownException).isInstanceOf(IllegalArgumentException.class);
    }

    @And("el mensaje de error es {string}")
    public void elMensajeDeErrorEs(String message) {
        assertThat(thrownException.getMessage()).isEqualTo(message);
    }

    @And("el saldo de {string} permanece en {double}")
    public void elSaldoDePermaneceEn(String accountNumber, double balance) {
        Customer customer = customerRepository.findByAccountNumber(accountNumber).orElseThrow();
        assertThat(customer.getBalance()).isEqualTo(balance);
    }

    @And("el saldo de {string} es {double}")
    public void elSaldoDeEs(String accountNumber, double balance) {
        Customer customer = customerRepository.findByAccountNumber(accountNumber).orElseThrow();
        assertThat(customer.getBalance()).isEqualTo(balance);
    }

    @And("se ha realizado una transferencia de {double} de {string} a {string}")
    public void seHaRealizadoUnaTransferenciaDeA(double amount, String from, String to) {
        TransactionDTO transactionDTO = new TransactionDTO();
        transactionDTO.setSenderAccountNumber(from);
        transactionDTO.setReceiverAccountNumber(to);
        transactionDTO.setAmount(amount);
        transactionService.transferMoney(transactionDTO);
    }

    @When("se consulta el historial de la cuenta {string}")
    public void seConsultaElHistorialDeLaCuenta(String accountNumber) {
        transactionList = transactionService.getTransactionsForAccount(accountNumber);
    }

    @Then("se retornan {int} transacciones")
    public void seRetornanTransacciones(int count) {
        assertThat(transactionList).hasSize(count);
    }

    @And("incluye transacciones enviadas")
    public void incluyeTransaccionesEnviadas() {
        assertThat(transactionList).anyMatch(t ->
            t.getSenderAccountNumber().equals("123456")
        );
    }

    @And("incluye transacciones recibidas")
    public void incluyeTransaccionesRecibidas() {
        assertThat(transactionList).anyMatch(t ->
            t.getReceiverAccountNumber().equals("123456")
        );
    }

    @And("no tiene transacciones")
    public void noTieneTransacciones() {
        // Ya está vacío por el setup
    }

    @Then("se retorna una lista vacía")
    public void seRetornaUnaListaVacia() {
        assertThat(transactionList).isEmpty();
    }
}
