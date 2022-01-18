package carbon.kaiser.bankaccount

import carbon.kaiser.bankaccount.display.StatementPrinter
import carbon.kaiser.bankaccount.operation.Operation
import carbon.kaiser.bankaccount.operation.OperationRepository
import carbon.kaiser.bankaccount.operation.OperationResponse
import io.mockk.*
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.Arguments
import org.junit.jupiter.params.provider.MethodSource
import java.math.BigDecimal
import java.time.Clock
import java.time.Instant
import java.time.ZoneId
import java.util.*
import kotlin.test.assertEquals
import kotlin.test.assertIs
import kotlin.test.assertTrue

class AccountTest {
    private lateinit var account: Account
    private lateinit var operationRepository: OperationRepository
    private lateinit var clock: Clock
    private var clockMillis: Long = 0

    @BeforeEach
    fun onSetUp() {
        clock = Clock.fixed(Instant.ofEpochSecond(561294300), ZoneId.systemDefault())
        clockMillis = clock.millis()
        operationRepository = mockk()
        // GIVEN
        account = Account(operationRepository, clock)
    }

    @AfterEach
    fun after() {
        clearAllMocks()
    }

    @ParameterizedTest
    @MethodSource("validAmounts")
    fun `GIVEN Account WHEN deposit positive amount of money THEN return operation success`(
        amount: BigDecimal
    ) {
        every { operationRepository.getLast() } returns Optional.empty()
        every { operationRepository.add(any()) } returns Unit

        // WHEN
        val response = account.deposit(amount)

        // THEN
        assertIs<OperationResponse.Success>(response)
        verifySequence {
            operationRepository.getLast()
            operationRepository.add(any())
        }
    }

    @ParameterizedTest
    @MethodSource("notValidAmounts")
    fun `GIVEN Account WHEN deposit negative amount of money THEN return operation failed`(
        amount: BigDecimal
    ) {
        // WHEN
        val response = account.deposit(amount)

        // THEN
        assertIs<OperationResponse.Failed>(response)
        verify { operationRepository wasNot Called }
    }

    @ParameterizedTest
    @MethodSource("validAmounts")
    fun `GIVEN Account with money WHEN withdrawal all money THEN return operation success with balance == ZERO`(
        amount: BigDecimal
    ) {
        val operation = mockk<Operation> {
            every { newBalance } returns amount
        }
        every { operationRepository.getLast() } returns Optional.of(operation)
        every { operationRepository.add(any()) } returns Unit

        // WHEN
        val response = account.withdrawal(amount)

        // THEN
        assertIs<OperationResponse.Success>(response)
        assertTrue { response.balance.compareTo(BigDecimal.ZERO) == 0 }
        verifySequence {
            operationRepository.getLast()
            operationRepository.add(any())
        }
    }

    @Test
    fun `GIVEN Account with money WHEN withdrawal all money THEN return operation success with balance == ZERO`() {
        every { operationRepository.getLast() } returns Optional.empty()

        // WHEN
        val response = account.withdrawal(BigDecimal("10.0"))

        // THEN
        assertIs<OperationResponse.Failed>(response)
        verifySequence {
            operationRepository.getLast()
        }
    }

    @ParameterizedTest
    @MethodSource("notValidAmounts")
    fun `GIVEN Account WHEN withdrawal negative amount of money THEN return operation failed`(
        amount: BigDecimal
    ) {
        // WHEN
        val response = account.withdrawal(amount)

        // THEN
        assertIs<OperationResponse.Failed>(response)
    }

    @ParameterizedTest
    @MethodSource("balances")
    fun `GIVEN Account with operation WHEN get balance THEN return operation success with balance`(
        balance: BigDecimal
    ) {
        val operation = mockk<Operation> {
            every { newBalance } returns balance
        }
        every { operationRepository.getLast() } returns Optional.of(operation)

        // WHEN
        val response = account.getBalance()

        // THEN
        assertIs<OperationResponse.Success>(response)
        assertEquals(balance, response.balance)
        verifySequence {
            operationRepository.getLast()
        }
    }

    @Test
    fun `GIVEN Account without operation WHEN get balance THEN return operation success with balance == ZERO`() {
        every { operationRepository.getLast() } returns Optional.empty()

        // WHEN
        val response = account.getBalance()

        // THEN
        assertIs<OperationResponse.Success>(response)
        assertEquals(BigDecimal.ZERO, response.balance)
        verifySequence {
            operationRepository.getLast()
        }
    }

    @Test
    fun `GIVEN Account two operations WHEN print statement THEN call printer with two operations`() {
        val operations: List<Operation> = listOf(mockk(), mockk())
        val printer: StatementPrinter = mockk()
        every { operationRepository.findAll() } returns operations
        every { printer.printStatement(any()) } returns Unit

        // WHEN
        account.printStatement(printer)

        // THEN
        verifySequence {
            operationRepository.findAll()
            printer.printStatement(operations)
        }
    }

    companion object {
        @JvmStatic
        fun validAmounts() = listOf(
            "10.0",
            "500.0",
            "1000.0",
            "7893123120.0",
        ).map { Arguments.of(BigDecimal(it)) }

        @JvmStatic
        fun notValidAmounts() = listOf(
            "-10.0",
            "-500.0",
            "-1000.0",
            "-7893123120.0",
        ).map { Arguments.of(BigDecimal(it)) }

        @JvmStatic
        fun balances() = listOf(
            "1.0",
            "123.123",
            "0.0000000012",
            "123456789.0",
        ).map { Arguments.of(BigDecimal(it)) }
    }
}