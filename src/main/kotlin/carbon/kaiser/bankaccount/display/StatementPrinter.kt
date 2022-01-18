package carbon.kaiser.bankaccount.display

import carbon.kaiser.bankaccount.operation.Operation

interface StatementPrinter {
    fun printStatement(operations: List<Operation>)
}