package carbon.kaiser.bankaccount.display

import carbon.kaiser.bankaccount.model.Operation

fun interface StatementPrinter {
    fun printStatement(operations: List<Operation>)
}