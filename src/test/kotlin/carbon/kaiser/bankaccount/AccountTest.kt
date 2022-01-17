package carbon.kaiser.bankaccount

import org.junit.Test
import java.math.BigDecimal

class AccountTest {
    @Test
    fun `GIVEN Account WHEN deposit positive amount of money THEN return operation success`() {
        // GIVEN
        val account = Account()

        // WHEN
        val response = account.deposit(BigDecimal(10))

        // THEN
        assert(response is OperationResponse.Success)
    }

    @Test
    fun `GIVEN Account WHEN deposit negative amount of money THEN return operation failed`() {
        // GIVEN
        val account = Account()

        // WHEN
        val response = account.deposit(BigDecimal(-10))

        // THEN
        assert(response is OperationResponse.Failed)
    }

    @Test
    fun `GIVEN Account WHEN withdrawal positive amount of money THEN return operation success`() {
        // GIVEN
        val account = Account()

        // WHEN
        val response = account.withdrawal(BigDecimal(10))

        // THEN
        assert(response is OperationResponse.Success)
    }

    @Test
    fun `GIVEN Account WHEN withdrawal negative amount of money THEN return operation failed`() {
        // GIVEN
        val account = Account()

        // WHEN
        val response = account.withdrawal(BigDecimal(-10))

        // THEN
        assert(response is OperationResponse.Failed)
    }
}