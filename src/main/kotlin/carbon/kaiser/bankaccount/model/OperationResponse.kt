package carbon.kaiser.bankaccount.model

import java.math.BigDecimal

sealed class OperationResponse {
    data class Success(val newBalance: BigDecimal) : OperationResponse()
    sealed class Error(val reason: String) : OperationResponse() {
        data class NegativeAmountError(val amount: BigDecimal) : Error(reason = "Amount should be positive : $amount")
        data class NotEnoughMoneyError(val amount: BigDecimal, val balance: BigDecimal) :
            Error(reason = "Tried to withdraw $amount but the account contains only $balance")
    }
}