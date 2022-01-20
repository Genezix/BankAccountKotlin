package carbon.kaiser.bankaccount

import carbon.kaiser.bankaccount.display.StatementPrinter
import carbon.kaiser.bankaccount.model.Operation
import carbon.kaiser.bankaccount.model.OperationRepository
import carbon.kaiser.bankaccount.model.OperationResponse
import carbon.kaiser.bankaccount.model.OperationResponse.Error.NegativeAmountError
import carbon.kaiser.bankaccount.model.OperationResponse.Error.NotEnoughMoneyError
import java.math.BigDecimal
import java.time.Clock

class Account(private val operationRepository: OperationRepository, private val clock: Clock = Clock.systemUTC()) {
    fun deposit(amount: BigDecimal): OperationResponse {
        if (amount <= BigDecimal.ZERO) {
            return NegativeAmountError(amount)
        }

        val newBalance = getBalance().add(amount)

        operationRepository.add(Operation.Deposit(amount, newBalance, clock.millis()))

        return OperationResponse.Success(newBalance)
    }

    fun withdraw(amount: BigDecimal): OperationResponse {
        if (amount <= BigDecimal.ZERO) {
            return NegativeAmountError(amount)
        }

        val currentBalance = getBalance()
        val newBalance = currentBalance.subtract(amount)

        if (newBalance < BigDecimal.ZERO) {
            return NotEnoughMoneyError(amount, currentBalance)
        }

        operationRepository.add(Operation.Withdrawal(amount, newBalance, clock.millis()))

        return OperationResponse.Success(newBalance)
    }

    fun getBalance(): BigDecimal = operationRepository.getLast()?.newBalance ?: BigDecimal.ZERO

    fun printStatement(printer: StatementPrinter) {
        val operations = operationRepository.findAll()
        printer.printStatement(operations)
    }
}
