package carbon.kaiser.bankaccount.model

interface OperationRepository {
    fun add(operation: Operation)

    fun findAll(): List<Operation>

    fun getLast(): Operation?
}