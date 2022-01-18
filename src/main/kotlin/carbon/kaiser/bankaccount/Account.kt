package carbon.kaiser.bankaccount

import carbon.kaiser.bankaccount.display.StatementPrinter
import carbon.kaiser.bankaccount.operation.Operation
import carbon.kaiser.bankaccount.operation.OperationRepository
import carbon.kaiser.bankaccount.operation.OperationResponse
import carbon.kaiser.bankaccount.operation.OperationResponse.Error.NegativeAmountError
import carbon.kaiser.bankaccount.operation.OperationResponse.Error.NotEnoughMoneyError
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

    fun withdrawal(amount: BigDecimal): OperationResponse {
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

    fun getBalance(): BigDecimal = with(operationRepository.getLast()) {
        if (isEmpty) BigDecimal.ZERO else get().newBalance
    }

    fun printStatement(printer: StatementPrinter) {
        val operations = operationRepository.findAll()
        printer.printStatement(operations)
    }
}
