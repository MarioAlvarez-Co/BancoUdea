package com.udea.bancoudea.mapper;

import com.udea.bancoudea.DTO.CustomerDTO;
import com.udea.bancoudea.entity.Customer;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("Unit Tests para CustomerMapper")
class CustomerMapperTest {

    private CustomerMapper customerMapper;

    @BeforeEach
    void setUp() {
        customerMapper = CustomerMapper.INSTANCE;
    }

    @Test
    @DisplayName("Dado una entidad Customer válida cuando se convierte a DTO entonces retorna CustomerDTO con todos los campos")
    void givenCustomerEntity_whenToDto_thenReturnsCorrectCustomerDTO() {
        // GIVEN
        Customer customer = new Customer(1L, "123456", "Juan", "Pérez", 1000.0);

        // WHEN
        CustomerDTO customerDTO = customerMapper.toDTO(customer);

        // THEN
        assertThat(customerDTO).isNotNull();
        assertThat(customerDTO.getId()).isEqualTo(1L);
        assertThat(customerDTO.getAccountNumber()).isEqualTo("123456");
        assertThat(customerDTO.getFirstName()).isEqualTo("Juan");
        assertThat(customerDTO.getLastName()).isEqualTo("Pérez");
        assertThat(customerDTO.getBalance()).isEqualTo(1000.0);
    }

    @Test
    @DisplayName("Dado un CustomerDTO válido cuando se convierte a entidad entonces retorna Customer con todos los campos")
    void givenCustomerDTO_whenToEntity_thenReturnsCorrectCustomerEntity() {
        // GIVEN
        CustomerDTO customerDTO = new CustomerDTO(1L, "Juan", "Pérez", "123456", 1000.0);

        // WHEN
        Customer customer = customerMapper.toEntity(customerDTO);

        // THEN
        assertThat(customer).isNotNull();
        assertThat(customer.getId()).isEqualTo(1L);
        assertThat(customer.getAccountNumber()).isEqualTo("123456");
        assertThat(customer.getFirstName()).isEqualTo("Juan");
        assertThat(customer.getLastName()).isEqualTo("Pérez");
        assertThat(customer.getBalance()).isEqualTo(1000.0);
    }

    @Test
    @DisplayName("Dado una entidad Customer con saldo cero cuando se convierte a DTO entonces retorna DTO con saldo cero")
    void givenCustomerEntityWithZeroBalance_whenToDto_thenReturnsDTOWithZeroBalance() {
        // GIVEN
        Customer customer = new Customer(1L, "123456", "Maria", "Gómez", 0.0);

        // WHEN
        CustomerDTO customerDTO = customerMapper.toDTO(customer);

        // THEN
        assertThat(customerDTO).isNotNull();
        assertThat(customerDTO.getBalance()).isEqualTo(0.0);
    }

    @Test
    @DisplayName("Dado una entidad Customer nula cuando se convierte a DTO entonces retorna null")
    void givenNullCustomer_whenToDto_thenReturnsNull() {
        // GIVEN
        Customer customer = null;

        // WHEN
        CustomerDTO customerDTO = customerMapper.toDTO(customer);

        // THEN
        assertThat(customerDTO).isNull();
    }

    @Test
    @DisplayName("Dado un CustomerDTO nulo cuando se convierte a entidad entonces retorna null")
    void givenNullCustomerDTO_whenToEntity_thenReturnsNull() {
        // GIVEN
        CustomerDTO customerDTO = null;

        // WHEN
        Customer customer = customerMapper.toEntity(customerDTO);

        // THEN
        assertThat(customer).isNull();
    }
}