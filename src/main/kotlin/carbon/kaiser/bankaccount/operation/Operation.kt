package carbon.kaiser.bankaccount.operation

import java.math.BigDecimal

sealed class Operation(val newBalance: BigDecimal, val time: Long) {
    data class Deposit(val amount: BigDecimal, val balance: BigDecimal, val operationTime: Long) :
        Operation(balance, operationTime)

    data class Withdrawal(val amount: BigDecimal, val balance: BigDecimal, val operationTime: Long) :
        Operation(balance, operationTime)
}