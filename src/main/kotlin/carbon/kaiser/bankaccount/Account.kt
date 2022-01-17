package carbon.kaiser.bankaccount

import java.math.BigDecimal

class Account {
    fun deposit(amount: BigDecimal): OperationResponse {
        return if (amount >= BigDecimal.ZERO) {
            // TODO add deposit operation in account
            OperationResponse.Success(amount)
        } else {
            OperationResponse.Failed("Deposit amount money must be positive : deposit = $amount")
        }
    }

    fun withdrawal(amount: BigDecimal): OperationResponse {
        return if (amount >= BigDecimal.ZERO) {
            // TODO add withdrawal operation in account
            OperationResponse.Success(amount)
        } else {
            OperationResponse.Failed("Withdrawal amount money must be positive : deposit = $amount")
        }
    }
}
