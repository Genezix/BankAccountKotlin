package carbon.kaiser.bankaccount.operation

import java.util.*

interface OperationRepository {
    fun add(operation: Operation)

    fun findAll(): List<Operation>

    fun getLast(): Optional<Operation>
}