Feature: Gestión de Clientes
  Como banco
  Quiero gestionar los clientes del sistema
  Para mantener la información de cuentas bancarias

  Background:
    Given el sistema está inicializado

  Scenario: Crear un nuevo cliente exitosamente
    Given que no existe un cliente con cuenta "123456"
    When se crea un cliente con:
      | accountNumber | firstName | lastName | balance |
      | 123456        | Juan      | Pérez    | 1000.0  |
    Then el cliente es creado exitosamente
    And el cliente tiene ID asignado
    And el número de cuenta es "123456"
    And el nombre del cliente es "Juan"
    And el apellido del cliente es "Pérez"
    And el saldo del cliente es 1000.0

  Scenario: Consultar todos los clientes
    Given que existen los siguientes clientes:
      | accountNumber | firstName | lastName | balance |
      | 123456        | Juan      | Pérez    | 1000.0  |
      | 789012        | María     | Gómez    | 2500.0  |
      | 456789        | Carlos    | López    | 500.0   |
    When se obtiene la lista de todos los clientes
    Then se retornan 3 clientes
    And cada cliente tiene su información completa

  Scenario: Consultar cliente por ID existente
    Given que existe un cliente con:
      | accountNumber | firstName | lastName | balance |
      | 123456        | Juan      | Pérez    | 1000.0  |
    And el cliente tiene ID 1
    When se consulta el cliente con ID 1
    Then se retorna la información del cliente
    And el número de cuenta es "123456"
    And el nombre del cliente es "Juan"

  Scenario: Consultar cliente por ID inexistente
    Given que el cliente con ID 999 no existe
    When se consulta el cliente con ID 999
    Then el sistema lanza RuntimeException
    And el mensaje es "Cliente no encontrado"

  Scenario: Actualizar información de un cliente existente
    Given que existe un cliente con:
      | accountNumber | firstName | lastName | balance |
      | 123456        | Juan      | Pérez    | 1000.0  |
    And el cliente tiene ID 1
    When se actualiza el cliente con ID 1 con:
      | firstName | lastName |
      | Carlos    | null     |
    Then el cliente ahora tiene firstName "Carlos"
    And el apellido permanece como "Pérez"
    And el saldo permanece como 1000.0

  Scenario: Eliminar un cliente existente
    Given que existe un cliente con:
      | accountNumber | firstName | lastName | balance |
      | 123456        | Juan      | Pérez    | 1000.0  |
    And el cliente tiene ID 1
    When se elimina el cliente con ID 1
    Then el cliente ya no existe en el sistema

  Scenario: Eliminar un cliente inexistente falla
    Given que el cliente con ID 999 no existe
    When se elimina el cliente con ID 999
    Then el sistema lanza RuntimeException
    And el mensaje es "Cliente no encontrado"
