package carbon.kaiser.bankaccount.operation

import java.math.BigDecimal

sealed class OperationResponse {
    data class Success(val balance: BigDecimal) : OperationResponse()
    data class Failed(val reason: String) : OperationResponse()
}