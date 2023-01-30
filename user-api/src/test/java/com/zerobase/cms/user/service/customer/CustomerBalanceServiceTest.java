package com.zerobase.cms.user.service.customer;

import static com.zerobase.cms.user.exception.ErrorCode.NOT_ENOUGH_BALANCE;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import com.zerobase.cms.user.domain.customer.ChangeBalanceForm;
import com.zerobase.cms.user.domain.model.Customer;
import com.zerobase.cms.user.domain.model.CustomerBalanceHistory;
import com.zerobase.cms.user.domain.repository.CustomerBalanceHistoryRepository;
import com.zerobase.cms.user.domain.repository.CustomerRepository;
import com.zerobase.cms.user.exception.CustomException;
import java.util.Optional;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class CustomerBalanceServiceTest {

	@Mock
	private CustomerRepository customerRepository;

	@Mock
	private CustomerBalanceHistoryRepository customerBalanceHistoryRepository;

	@InjectMocks
	private CustomerBalanceService service;

	@Test
	void changeBalance() {
		// given
		Customer c = Customer.builder()
			.id(1L).email("abc@test.com").password("abc")
			.verify(true).build();
		CustomerBalanceHistory cb = CustomerBalanceHistory.builder()
			.id(1L).customer(c)
			.changeMoney(2000).currentMoney(3000).build();

		given(customerBalanceHistoryRepository.findFirstByCustomer_IdOrderByIdDesc(anyLong()))
			.willReturn(Optional.of(cb));
		given(customerRepository.findById(anyLong()))
			.willReturn(Optional.of(c));
		ArgumentCaptor<CustomerBalanceHistory> captor = ArgumentCaptor.forClass(CustomerBalanceHistory.class);

		// when
		CustomerBalanceHistory cbh = service.changeBalance(
			1L, new ChangeBalanceForm("admin", "point", 500));

		// then
		verify(customerBalanceHistoryRepository, times(1)).save(captor.capture());
		assertEquals(500, captor.getValue().getChangeMoney());
		assertEquals(3500, captor.getValue().getCurrentMoney());

	}

	@Test
	void changeBalance_notEnoughBalance() {
		// given
		Customer c = Customer.builder()
			.id(1L).email("abc@test.com").password("abc")
			.verify(true).build();
		CustomerBalanceHistory cb = CustomerBalanceHistory.builder()
			.id(1L).customer(c)
			.changeMoney(2000).currentMoney(3000).build();

		given(customerBalanceHistoryRepository.findFirstByCustomer_IdOrderByIdDesc(anyLong()))
			.willReturn(Optional.of(cb));
		given(customerRepository.findById(anyLong()))
			.willReturn(Optional.of(c));

		// when
		CustomException e = assertThrows(CustomException.class, () ->
			service.changeBalance(1L, new ChangeBalanceForm("abc", "use", -3500)));

		// then
		assertEquals(NOT_ENOUGH_BALANCE, e.getErrorCode());

	}
}