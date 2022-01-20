package carbon.kaiser.bankaccount.integration

import carbon.kaiser.bankaccount.Account
import carbon.kaiser.bankaccount.display.StatementPrinter
import carbon.kaiser.bankaccount.model.Operation
import carbon.kaiser.bankaccount.model.Operation.Deposit
import carbon.kaiser.bankaccount.model.Operation.Withdrawal
import carbon.kaiser.bankaccount.model.OperationRepository
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import java.io.ByteArrayOutputStream
import java.io.PrintStream
import java.math.BigDecimal
import java.time.*
import java.time.format.DateTimeFormatter
import java.util.*
import kotlin.test.assertEquals

class BankAccountTest {
    private lateinit var account: Account
    private val clock = Clock.offset(Clock.fixed(Instant.ofEpochMilli(0L), ZoneId.systemDefault()), Duration.ZERO)

    @BeforeEach
    fun onSetUp() {
        account = Account(OperationRepositoryTest(), clock)
    }

    @Test
    fun `Apply operations on OperationRepository implementation and verify balance`() {
        val initialAmount = BigDecimal(100)
        // deposits
        account.deposit(initialAmount)
        account.deposit(BigDecimal(2000))
        account.deposit(BigDecimal(30000))

        // withdrawal
        account.withdraw(BigDecimal(2000))
        account.withdraw(BigDecimal(30000))

        // balance
        assertEquals(initialAmount, account.getBalance())
    }

    @Test
    fun `Apply operations on OperationRepository implementation and verify that all operations are printed`() {
        val originalOut = System.out
        val outContent = ByteArrayOutputStream()
        System.setOut(PrintStream(outContent))

        val expectedStatement = StringBuilder()
            .append("Date             | Amount  | Balance " + System.lineSeparator())
            .append("01-01-1970 00:00 | +100.05 | 100.05  " + System.lineSeparator())
            .append("01-01-1970 00:00 | +2000   | 2100.05 " + System.lineSeparator())
            .append("01-01-1970 00:00 | +30000  | 32100.05" + System.lineSeparator())
            .append("01-01-1970 00:00 | -1.02   | 32099.03" + System.lineSeparator())
            .append("01-01-1970 00:00 | -10     | 32089.03" + System.lineSeparator())
            .append("01-01-1970 00:00 | -100    | 31989.03" + System.lineSeparator())
            .toString()

        // deposits
        account.deposit(BigDecimal("100.05"))
        account.deposit(BigDecimal("2000"))
        account.deposit(BigDecimal("30000"))
        account.withdraw(BigDecimal("1.02"))
        account.withdraw(BigDecimal("10"))
        account.withdraw(BigDecimal("100"))

        // print
        account.printStatement(StatementPrinterTest())

        // Assert
        Assertions.assertEquals(expectedStatement, outContent.toString())
        System.setOut(originalOut)
    }
}

private class StatementPrinterTest : StatementPrinter {
    private val datePattern = "dd-MM-yyyy HH:mm"
    private val formatter = DateTimeFormatter.ofPattern(datePattern)
    private val separator = " | "


    override fun printStatement(operations: List<Operation>) {
        val maxAmountLength = operations.maxOf { it.amount.toString().length } + 1
        val maxBalanceLength = operations.maxOf { it.newBalance.toString().length }

        println(header(maxAmountLength, maxBalanceLength))
        operations.forEach { operation ->
            println(operation.format(maxAmountLength, maxBalanceLength))
        }
    }

    private fun header(columnAmountSize: Int, columnBalanceSize: Int): String = StringJoiner(separator)
        .add(formatString("Date", datePattern.length))
        .add(formatString("Amount", columnAmountSize))
        .add(formatString("Balance", columnBalanceSize))
        .toString()

    fun Operation.format(columnAmountSize: Int, columnBalanceSize: Int): String {
        val sign = when (this) {
            is Deposit -> "+"
            is Withdrawal -> "-"
        }
        return StringJoiner(separator)
            .add(formatString(formatter.format(time.toLocalDateTime()), datePattern.length))
            .add(formatString("$sign$amount", columnAmountSize))
            .add(formatString(newBalance.toString(), columnBalanceSize))
            .toString()
    }

    private fun formatString(value: String, size: Int): String {
        return String.format("%-" + size + "s", value)
    }

    private fun Long.toLocalDateTime(): LocalDateTime = LocalDateTime.ofInstant(
        Instant.ofEpochSecond(this / 1000),
        ZoneOffset.UTC
    )
}

private class OperationRepositoryTest : OperationRepository {
    private val operations = LinkedList<Operation>()

    override fun add(operation: Operation) {
        operations.add(operation)
    }

    override fun findAll(): List<Operation> = operations.toList()

    override fun getLast(): Operation? = operations.lastOrNull()
}