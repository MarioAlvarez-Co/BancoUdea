package com.udea.bancoudea.cucumber;

import com.udea.bancoudea.DTO.CustomerDTO;
import com.udea.bancoudea.entity.Customer;
import com.udea.bancoudea.repository.CustomerRepository;
import com.udea.bancoudea.service.CustomerService;
import io.cucumber.java.en.And;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;
import java.util.Map;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@SpringBootTest
public class CustomerStepDefinitions {

    @Autowired
    private CustomerService customerService;

    @Autowired
    private CustomerRepository customerRepository;

    private CustomerDTO createdCustomer;
    private CustomerDTO retrievedCustomer;
    private List<CustomerDTO> customerList;
    private Throwable thrownException;

    @Given("el sistema está inicializado")
    public void elSistemaEstaInicializado() {
        customerRepository.deleteAll();
    }

    @Given("que no existe un cliente con cuenta {string}")
    public void queNoExisteUnClienteConCuenta(String accountNumber) {
        customerRepository.findByAccountNumber(accountNumber).ifPresent(customer -> {
            customerRepository.delete(customer);
        });
    }

    @When("se crea un cliente con:")
    public void seCreaUnClienteCon(io.cucumber.datatable.DataTable dataTable) {
        Map<String, String> customerData = dataTable.asMaps().get(0);

        CustomerDTO customerDTO = new CustomerDTO();
        customerDTO.setAccountNumber(customerData.get("accountNumber"));
        customerDTO.setFirstName(customerData.get("firstName"));
        customerDTO.setLastName(customerData.get("lastName"));
        customerDTO.setBalance(Double.parseDouble(customerData.get("balance")));

        createdCustomer = customerService.createCustomer(customerDTO);
    }

    @Then("el cliente es creado exitosamente")
    public void elClienteEsCreadoExitosamente() {
        assertThat(createdCustomer).isNotNull();
        assertThat(createdCustomer.getId()).isNotNull();
    }

    @And("el cliente tiene ID asignado")
    public void elClienteTieneIDAsignado() {
        assertThat(createdCustomer.getId()).isNotNull();
    }

    @And("el número de cuenta es {string}")
    public void elNumeroDeCuentaEs(String accountNumber) {
        assertThat(createdCustomer.getAccountNumber()).isEqualTo(accountNumber);
    }

    @And("el nombre del cliente es {string}")
    public void elNombreDelClienteEs(String firstName) {
        assertThat(createdCustomer.getFirstName()).isEqualTo(firstName);
    }

    @And("el apellido del cliente es {string}")
    public void elApellidoDelClienteEs(String lastName) {
        assertThat(createdCustomer.getLastName()).isEqualTo(lastName);
    }

    @And("el saldo del cliente es {double}")
    public void elSaldoDelClienteEs(Double balance) {
        assertThat(createdCustomer.getBalance()).isEqualTo(balance);
    }

    @Given("que existen los siguientes clientes:")
    public void queExistenLosSiguientesClientes(io.cucumber.datatable.DataTable dataTable) {
        List<Map<String, String>> customers = dataTable.asMaps();
        for (Map<String, String> customerData : customers) {
            CustomerDTO customerDTO = new CustomerDTO();
            customerDTO.setAccountNumber(customerData.get("accountNumber"));
            customerDTO.setFirstName(customerData.get("firstName"));
            customerDTO.setLastName(customerData.get("lastName"));
            customerDTO.setBalance(Double.parseDouble(customerData.get("balance")));
            customerService.createCustomer(customerDTO);
        }
    }

    @When("se obtiene la lista de todos los clientes")
    public void seObtieneLaListaDeTodosLosClientes() {
        customerList = customerService.getAllCustomer();
    }

    @Then("se retornan {int} clientes")
    public void seRetornanClientes(int count) {
        assertThat(customerList).hasSize(count);
    }

    @And("cada cliente tiene su información completa")
    public void cadaClienteTieneSuInformacionCompleta() {
        for (CustomerDTO customer : customerList) {
            assertThat(customer.getId()).isNotNull();
            assertThat(customer.getAccountNumber()).isNotNull();
            assertThat(customer.getFirstName()).isNotNull();
            assertThat(customer.getLastName()).isNotNull();
            assertThat(customer.getBalance()).isNotNull();
        }
    }

    @Given("que existe un cliente con:")
    public void queExisteUnClienteCon(io.cucumber.datatable.DataTable dataTable) {
        Map<String, String> customerData = dataTable.asMaps().get(0);

        CustomerDTO customerDTO = new CustomerDTO();
        customerDTO.setAccountNumber(customerData.get("accountNumber"));
        customerDTO.setFirstName(customerData.get("firstName"));
        customerDTO.setLastName(customerData.get("lastName"));
        customerDTO.setBalance(Double.parseDouble(customerData.get("balance")));

        createdCustomer = customerService.createCustomer(customerDTO);
    }

    @And("el cliente tiene ID {long}")
    public void elClienteTieneID(Long id) {
        createdCustomer.setId(id);
    }

    @When("se consulta el cliente con ID {long}")
    public void seConsultaElClienteConID(Long id) {
        try {
            retrievedCustomer = customerService.getCustomerById(id);
        } catch (Exception e) {
            thrownException = e;
        }
    }

    @Then("se retorna la información del cliente")
    public void seRetornaLaInformacionDelCliente() {
        assertThat(retrievedCustomer).isNotNull();
    }

    @Given("que el cliente con ID {long} no existe")
    public void queElClienteConIDNoExiste(Long id) {
        customerRepository.findById(id).ifPresent(customer -> {
            customerRepository.delete(customer);
        });
    }

    @Then("el sistema lanza RuntimeException")
    public void elSistemaLanzaRuntimeException() {
        assertThat(thrownException).isNotNull();
        assertThat(thrownException).isInstanceOf(RuntimeException.class);
    }

    @And("el mensaje es {string}")
    public void elMensajeEs(String message) {
        assertThat(thrownException.getMessage()).isEqualTo(message);
    }

    @When("se actualiza el cliente con ID {long} con:")
    public void seActualizaElClienteConIDCon(Long id, io.cucumber.datatable.DataTable dataTable) {
        Map<String, String> updateData = dataTable.asMaps().get(0);

        CustomerDTO customerDTO = new CustomerDTO();
        if (updateData.get("firstName") != null && !updateData.get("firstName").equals("null")) {
            customerDTO.setFirstName(updateData.get("firstName"));
        }
        if (updateData.get("lastName") != null && !updateData.get("lastName").equals("null")) {
            customerDTO.setLastName(updateData.get("lastName"));
        }

        retrievedCustomer = customerService.updateCustomer(id, customerDTO);
    }

    @And("el cliente ahora tiene firstName {string}")
    public void elClienteAhoraTieneFirstName(String firstName) {
        assertThat(retrievedCustomer.getFirstName()).isEqualTo(firstName);
    }

    @And("el apellido permanece como {string}")
    public void elApellidoPermaneceComo(String lastName) {
        assertThat(retrievedCustomer.getLastName()).isEqualTo(lastName);
    }

    @And("el saldo permanece como {double}")
    public void elSaldoPermaneceComo(Double balance) {
        assertThat(retrievedCustomer.getBalance()).isEqualTo(balance);
    }

    @When("se elimina el cliente con ID {long}")
    public void seEliminaElClienteConID(Long id) {
        try {
            customerService.deleteCustomer(id);
        } catch (Exception e) {
            thrownException = e;
        }
    }

    @Then("el cliente ya no existe en el sistema")
    public void elClienteYaNoExisteEnElSistema() {
        Optional<Customer> customer = customerRepository.findById(createdCustomer.getId());
        assertThat(customer).isEmpty();
    }
}
