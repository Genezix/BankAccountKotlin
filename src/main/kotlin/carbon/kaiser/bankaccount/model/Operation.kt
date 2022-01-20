package carbon.kaiser.bankaccount.model

import java.math.BigDecimal

sealed class Operation(val amount: BigDecimal, val newBalance: BigDecimal, val time: Long) {
    data class Deposit(val depositAmount: BigDecimal, val balance: BigDecimal, val operationTime: Long) :
        Operation(depositAmount, balance, operationTime)

    data class Withdrawal(val withdrawalAmount: BigDecimal, val balance: BigDecimal, val operationTime: Long) :
        Operation(withdrawalAmount, balance, operationTime)
}