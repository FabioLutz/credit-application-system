package me.dio.credit.application.system.service

import io.mockk.every
import io.mockk.impl.annotations.InjectMockKs
import io.mockk.impl.annotations.MockK
import io.mockk.junit5.MockKExtension
import io.mockk.verify
import me.dio.credit.application.system.entity.Credit
import me.dio.credit.application.system.entity.Customer
import me.dio.credit.application.system.repository.CreditRepository
import me.dio.credit.application.system.service.impl.CreditService
import me.dio.credit.application.system.service.impl.CustomerService
import org.assertj.core.api.Assertions
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.springframework.test.context.ActiveProfiles
import java.math.BigDecimal
import java.time.LocalDate
import java.util.UUID

@ActiveProfiles("test")
@ExtendWith(MockKExtension::class)
class CreditServiceTest {
    @MockK
    lateinit var creditRepository: CreditRepository

    @MockK
    lateinit var customerService: CustomerService

    @InjectMockKs
    lateinit var creditService: CreditService

    @Test
    fun should_create_credit() {
        val custumerId: Long = 1L
        val fakeCredit: Credit = buildCredit()

        every { customerService.findById(custumerId) } returns fakeCredit.customer!!
        every { creditRepository.save(fakeCredit) } returns fakeCredit

        val actual: Credit = creditService.save(fakeCredit)

        Assertions.assertThat(actual).isNotNull
        Assertions.assertThat(actual).isSameAs(fakeCredit)
        verify(exactly = 1) { customerService.findById(custumerId) }
        verify(exactly = 1) { creditRepository.save(fakeCredit) }
    }

    @Test
    fun should_return_credit_list_for_a_customer() {
        val customerId: Long = 1L
        val creditList: List<Credit> = listOf(buildCredit(), buildCredit())
        every { creditRepository.findAllByCustomerId(customerId) } returns creditList

        val actual: List<Credit> = creditService.findAllByCustomer(customerId)

        verify(exactly = 1) { creditRepository.findAllByCustomerId(customerId) }
        Assertions.assertThat(actual).isNotNull
        Assertions.assertThat(actual).isNotEmpty
        Assertions.assertThat(actual).isSameAs(creditList)
    }

    @Test
    fun should_return_credit_by_credit_code() {
        val customerId: Long = 1L
        val creditCode: UUID = UUID.randomUUID()
        val credit: Credit = buildCredit(customer = Customer(id = customerId))
        every { creditRepository.findByCreditCode(creditCode) } returns credit

        val actual: Credit = creditService.findByCreditCode(customerId, creditCode)

        Assertions.assertThat(actual).isNotNull
        Assertions.assertThat(actual).isSameAs(credit)
    }

    private fun buildCredit(
        creditValue: BigDecimal = BigDecimal.valueOf(400.0),
        dayFirstInstallment: LocalDate = LocalDate.now().plusMonths(2L),
        numberOfInstallment: Int = 20,
        customer: Customer = CustomerServiceTest.buildCustomer(),
    ): Credit = Credit(
        creditValue = creditValue,
        dayFirstInstallment = dayFirstInstallment,
        numberOfInstallment = numberOfInstallment,
        customer = customer,
    )
}