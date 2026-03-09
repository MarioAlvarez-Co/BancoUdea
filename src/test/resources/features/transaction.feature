Feature: Transferencias entre Cuentas
  Como cliente del banco
  Quiero transferir dinero a otra cuenta
  Para realizar pagos y movimientos bancarios

  Background:
    Given el sistema está inicializado

  Scenario: Transferencia exitosa entre cuentas
    Given que la cuenta "123456" tiene saldo de 1000.0
    And que la cuenta "789012" tiene saldo de 500.0
    When se transfiere 200.0 de "123456" a "789012"
    Then la transferencia se completa exitosamente
    And la cuenta "123456" tiene saldo de 800.0
    And la cuenta "789012" tiene saldo de 700.0
    And se registra la transacción con remitente "123456"
    And se registra la transacción con destinatario "789012"
    And se registra la transacción con monto 200.0

  Scenario: Transferencia con cuenta remitente nula falla
    Given que se quiere realizar una transferencia
    When la cuenta remitente es null
    Then el sistema lanza IllegalArgumentException
    And el mensaje de error es "Sender Account Number or Receiver Account Number cannot be null"

  Scenario: Transferencia con cuenta destinataria nula falla
    Given que se quiere realizar una transferencia
    When la cuenta destinataria es null
    Then el sistema lanza IllegalArgumentException
    And el mensaje de error es "Sender Account Number or Receiver Account Number cannot be null"

  Scenario: Transferencia con cuenta remitente inexistente falla
    Given que la cuenta "999999" no existe
    When se intenta transferir de la cuenta "999999" a "789012"
    Then el sistema lanza IllegalArgumentException
    And el mensaje de error es "Sender Account Number not found"

  Scenario: Transferencia con cuenta destinataria inexistente falla
    Given que la cuenta "123456" existe con saldo 1000.0
    And la cuenta "888888" no existe
    When se intenta transferir de "123456" a "888888"
    Then el sistema lanza IllegalArgumentException
    And el mensaje de error es "Receiver Account Number not found"

  Scenario: Transferencia con saldo insuficiente falla
    Given que la cuenta "123456" tiene saldo de 100.0
    And la cuenta "789012" tiene saldo de 500.0
    When se intenta transferir 200.0 de "123456" a "789012"
    Then el sistema lanza IllegalArgumentException
    And el mensaje de error es "Sender Balance not enough"
    And el saldo de "123456" permanece en 100.0
    And el saldo de "789012" permanece en 500.0

  Scenario: Transferencia del monto exacto del saldo
    Given que la cuenta "123456" tiene saldo de 500.0
    And la cuenta "789012" tiene saldo de 1000.0
    When se transfiere exactamente 500.0 de "123456" a "789012"
    Then la transferencia se completa exitosamente
    And el saldo de "123456" es 0.0
    And el saldo de "789012" es 1500.0

  Scenario: Transferencia con monto cero
    Given que la cuenta "123456" tiene saldo de 1000.0
    And la cuenta "789012" tiene saldo de 500.0
    When se transfiere 0.0 de "123456" a "789012"
    Then la transferencia se completa exitosamente
    And el saldo de "123456" permanece en 1000.0
    And el saldo de "789012" permanece en 500.0

  Scenario: Consultar historial de transacciones de una cuenta
    Given que la cuenta "123456" tiene saldo de 1000.0
    And la cuenta "789012" tiene saldo de 500.0
    And se ha realizado una transferencia de 200.0 de "123456" a "789012"
    And se ha realizado una transferencia de 150.0 de "789012" a "123456"
    And se ha realizado una transferencia de 300.0 de "123456" a "789012"
    When se consulta el historial de la cuenta "123456"
    Then se retornan 3 transacciones
    And incluye transacciones enviadas
    And incluye transacciones recibidas

  Scenario: Consultar historial de cuenta sin transacciones
    Given que la cuenta "999999" tiene saldo de 1000.0
    And no tiene transacciones
    When se consulta su historial
    Then se retorna una lista vacía

  Scenario: Transferencia con decimales
    Given que la cuenta "123456" tiene saldo de 1000.0
    And la cuenta "789012" tiene saldo de 500.0
    When se transfiere 123.45 de "123456" a "789012"
    Then la transferencia se completa exitosamente
    And el saldo de "123456" es 876.55
    And el saldo de "789012" es 623.45
