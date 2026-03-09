package com.udea.bancoudea.service;

import com.udea.bancoudea.DTO.CustomerDTO;
import com.udea.bancoudea.entity.Customer;
import com.udea.bancoudea.mapper.CustomerMapper;
import com.udea.bancoudea.repository.CustomerRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("Unit Tests para CustomerService")
class CustomerServiceTest {

    @Mock
    private CustomerRepository customerRepository;

    @Mock
    private CustomerMapper customerMapper;

    @InjectMocks
    private CustomerService customerService;

    private Customer customer1;
    private Customer customer2;
    private CustomerDTO customerDTO1;
    private CustomerDTO customerDTO2;

    @BeforeEach
    void setUp() {
        customer1 = new Customer(1L, "123456", "Juan", "Pérez", 1000.0);
        customer2 = new Customer(2L, "789012", "María", "Gómez", 2500.0);

        customerDTO1 = new CustomerDTO(1L, "Juan", "Pérez", "123456", 1000.0);
        customerDTO2 = new CustomerDTO(2L, "María", "Gómez", "789012", 2500.0);
    }

    @Test
    @DisplayName("Dado que existen 2 clientes cuando se obtienen todos los clientes entonces retorna lista con 2 clientes")
    void givenTwoCustomersExist_whenGetAllCustomers_thenReturnsListOfTwoCustomers() {
        // GIVEN
        List<Customer> customers = Arrays.asList(customer1, customer2);
        when(customerRepository.findAll()).thenReturn(customers);
        when(customerMapper.toDTO(customer1)).thenReturn(customerDTO1);
        when(customerMapper.toDTO(customer2)).thenReturn(customerDTO2);

        // WHEN
        List<CustomerDTO> result = customerService.getAllCustomer();

        // THEN
        assertThat(result).hasSize(2);
        assertThat(result).containsExactly(customerDTO1, customerDTO2);
        verify(customerRepository, times(1)).findAll();
        verify(customerMapper, times(1)).toDTO(customer1);
        verify(customerMapper, times(1)).toDTO(customer2);
    }

    @Test
    @DisplayName("Dado que no existen clientes cuando se obtienen todos los clientes entonces retorna lista vacía")
    void givenNoCustomersExist_whenGetAllCustomers_thenReturnsEmptyList() {
        // GIVEN
        when(customerRepository.findAll()).thenReturn(Arrays.asList());

        // WHEN
        List<CustomerDTO> result = customerService.getAllCustomer();

        // THEN
        assertThat(result).isEmpty();
        verify(customerRepository, times(1)).findAll();
        verify(customerMapper, never()).toDTO(any());
    }

    @Test
    @DisplayName("Dado que existe un cliente con ID 1 cuando se obtiene por ID entonces retorna el cliente correcto")
    void givenCustomerExistsWithId1_whenGetCustomerById_thenReturnsCorrectCustomer() {
        // GIVEN
        when(customerRepository.findById(1L)).thenReturn(Optional.of(customer1));
        when(customerMapper.toDTO(customer1)).thenReturn(customerDTO1);

        // WHEN
        CustomerDTO result = customerService.getCustomerById(1L);

        // THEN
        assertThat(result).isNotNull();
        assertThat(result.getId()).isEqualTo(1L);
        assertThat(result.getAccountNumber()).isEqualTo("123456");
        verify(customerRepository, times(1)).findById(1L);
        verify(customerMapper, times(1)).toDTO(customer1);
    }

    @Test
    @DisplayName("Dado que no existe un cliente con ID 999 cuando se obtiene por ID entonces lanza RuntimeException")
    void givenCustomerDoesNotExistWithId999_whenGetCustomerById_thenThrowsRuntimeException() {
        // GIVEN
        when(customerRepository.findById(999L)).thenReturn(Optional.empty());

        // WHEN - THEN
        assertThatThrownBy(() -> customerService.getCustomerById(999L))
                .isInstanceOf(RuntimeException.class)
                .hasMessage("Cliente no encontrado");

        verify(customerRepository, times(1)).findById(999L);
        verify(customerMapper, never()).toDTO(any());
    }

    @Test
    @DisplayName("Dado un CustomerDTO válido cuando se crea un cliente entonces retorna el cliente creado")
    void givenValidCustomerDTO_whenCreateCustomer_thenReturnsCreatedCustomer() {
        // GIVEN
        when(customerMapper.toEntity(customerDTO1)).thenReturn(customer1);
        when(customerRepository.save(customer1)).thenReturn(customer1);
        when(customerMapper.toDTO(customer1)).thenReturn(customerDTO1);

        // WHEN
        CustomerDTO result = customerService.createCustomer(customerDTO1);

        // THEN
        assertThat(result).isNotNull();
        assertThat(result.getAccountNumber()).isEqualTo("123456");
        verify(customerMapper, times(1)).toEntity(customerDTO1);
        verify(customerRepository, times(1)).save(customer1);
        verify(customerMapper, times(1)).toDTO(customer1);
    }

    @Test
    @DisplayName("Dado que existe un cliente con ID 1 cuando se actualiza firstName entonces retorna cliente actualizado")
    void givenCustomerExistsWithId1_whenUpdateCustomerFirstName_thenReturnsUpdatedCustomer() {
        // GIVEN
        CustomerDTO updateDTO = new CustomerDTO(null, "Carlos", null, null, null);
        Customer updatedCustomer = new Customer(1L, "123456", "Carlos", "Pérez", 1000.0);
        CustomerDTO updatedDTO = new CustomerDTO(1L, "Carlos", "Pérez", "123456", 1000.0);

        when(customerRepository.findById(1L)).thenReturn(Optional.of(customer1));
        when(customerRepository.save(any(Customer.class))).thenReturn(updatedCustomer);
        when(customerMapper.toDTO(updatedCustomer)).thenReturn(updatedDTO);

        // WHEN
        CustomerDTO result = customerService.updateCustomer(1L, updateDTO);

        // THEN
        assertThat(result).isNotNull();
        assertThat(result.getFirstName()).isEqualTo("Carlos");
        verify(customerRepository, times(1)).findById(1L);
        verify(customerRepository, times(1)).save(any(Customer.class));
        verify(customerMapper, times(1)).toDTO(updatedCustomer);
    }

    @Test
    @DisplayName("Dado que existe un cliente cuando se actualiza balance entonces retorna cliente con balance actualizado")
    void givenCustomerExists_whenUpdateCustomerBalance_thenReturnsUpdatedCustomerWithNewBalance() {
        // GIVEN
        CustomerDTO updateDTO = new CustomerDTO(null, null, null, null, 5000.0);
        Customer updatedCustomer = new Customer(1L, "123456", "Juan", "Pérez", 5000.0);
        CustomerDTO updatedDTO = new CustomerDTO(1L, "Juan", "Pérez", "123456", 5000.0);

        when(customerRepository.findById(1L)).thenReturn(Optional.of(customer1));
        when(customerRepository.save(any(Customer.class))).thenReturn(updatedCustomer);
        when(customerMapper.toDTO(updatedCustomer)).thenReturn(updatedDTO);

        // WHEN
        CustomerDTO result = customerService.updateCustomer(1L, updateDTO);

        // THEN
        assertThat(result).isNotNull();
        assertThat(result.getBalance()).isEqualTo(5000.0);
        verify(customerRepository, times(1)).save(any(Customer.class));
    }

    @Test
    @DisplayName("Dado que no existe un cliente con ID 999 cuando se actualiza entonces lanza RuntimeException")
    void givenCustomerDoesNotExistWithId999_whenUpdateCustomer_thenThrowsRuntimeException() {
        // GIVEN
        CustomerDTO updateDTO = new CustomerDTO(null, "Carlos", null, null, null);
        when(customerRepository.findById(999L)).thenReturn(Optional.empty());

        // WHEN - THEN
        assertThatThrownBy(() -> customerService.updateCustomer(999L, updateDTO))
                .isInstanceOf(RuntimeException.class)
                .hasMessage("Cliente no encontrado");

        verify(customerRepository, times(1)).findById(999L);
        verify(customerRepository, never()).save(any());
    }

    @Test
    @DisplayName("Dado que existe un cliente con ID 1 cuando se elimina entonces el cliente es eliminado")
    void givenCustomerExistsWithId1_whenDeleteCustomer_thenCustomerIsDeleted() {
        // GIVEN
        when(customerRepository.existsById(1L)).thenReturn(true);
        doNothing().when(customerRepository).deleteById(1L);

        // WHEN
        customerService.deleteCustomer(1L);

        // THEN
        verify(customerRepository, times(1)).existsById(1L);
        verify(customerRepository, times(1)).deleteById(1L);
    }

    @Test
    @DisplayName("Dado que no existe un cliente con ID 999 cuando se elimina entonces lanza RuntimeException")
    void givenCustomerDoesNotExistWithId999_whenDeleteCustomer_thenThrowsRuntimeException() {
        // GIVEN
        when(customerRepository.existsById(999L)).thenReturn(false);

        // WHEN - THEN
        assertThatThrownBy(() -> customerService.deleteCustomer(999L))
                .isInstanceOf(RuntimeException.class)
                .hasMessage("Cliente no encontrado");

        verify(customerRepository, times(1)).existsById(999L);
        verify(customerRepository, never()).deleteById(any());
    }

    @Test
    @DisplayName("Dado un CustomerDTO con todos los campos nulos cuando se actualiza entonces solo actualiza campos no nulos")
    void givenCustomerDTOWithAllNullFields_whenUpdateCustomer_thenOnlyUpdatesNonNullFields() {
        // GIVEN
        CustomerDTO updateDTO = new CustomerDTO(null, null, null, null, null);
        when(customerRepository.findById(1L)).thenReturn(Optional.of(customer1));
        when(customerRepository.save(customer1)).thenReturn(customer1);
        when(customerMapper.toDTO(customer1)).thenReturn(customerDTO1);

        // WHEN
        CustomerDTO result = customerService.updateCustomer(1L, updateDTO);

        // THEN
        assertThat(result).isNotNull();
        verify(customerRepository, times(1)).findById(1L);
        verify(customerRepository, times(1)).save(customer1);
        assertThat(customer1.getFirstName()).isEqualTo("Juan"); // Sin cambios
        assertThat(customer1.getLastName()).isEqualTo("Pérez"); // Sin cambios
    }
}