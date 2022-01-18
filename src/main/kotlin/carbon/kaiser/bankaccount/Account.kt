package carbon.kaiser.bankaccount

import carbon.kaiser.bankaccount.display.StatementPrinter
import carbon.kaiser.bankaccount.operation.Operation
import carbon.kaiser.bankaccount.operation.OperationRepository
import carbon.kaiser.bankaccount.operation.OperationResponse
import java.math.BigDecimal
import java.time.Clock

class Account(private val operationRepository: OperationRepository, private val clock: Clock = Clock.systemUTC()) {
    fun deposit(amount: BigDecimal): OperationResponse {
        if (amount <= BigDecimal.ZERO) {
            return OperationResponse.Failed("Deposit negative amount : $amount")
        }

        val newBalance = when (val balanceResponse = getBalance()) {
            is OperationResponse.Failed -> return balanceResponse
            is OperationResponse.Success -> balanceResponse.balance.add(amount)
        }

        operationRepository.add(Operation.Deposit(amount, newBalance, clock.millis()))

        return OperationResponse.Success(amount)
    }

    fun withdrawal(amount: BigDecimal): OperationResponse {
        if (amount <= BigDecimal.ZERO) {
            return OperationResponse.Failed("Withdrawal negative amount : $amount")
        }

        val newBalance = when (val balanceResponse = getBalance()) {
            is OperationResponse.Failed -> return balanceResponse
            is OperationResponse.Success -> balanceResponse.balance.subtract(amount)
        }

        if (newBalance < BigDecimal.ZERO) {
            return OperationResponse.Failed("Withdrawal negative balance : $newBalance")
        }

        operationRepository.add(Operation.Withdrawal(amount, newBalance, clock.millis()))

        return OperationResponse.Success(newBalance)
    }

    fun getBalance(): OperationResponse {
        val lastOperation = operationRepository.getLast()
        val balance = if (lastOperation.isEmpty) BigDecimal.ZERO else lastOperation.get().newBalance
        return OperationResponse.Success(balance)
    }

    fun printStatement(printer: StatementPrinter) {
        val operations = operationRepository.findAll()
        printer.printStatement(operations)
    }
}
