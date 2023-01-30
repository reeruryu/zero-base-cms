package com.zerobase.cms.user.service.customer;

import static com.zerobase.cms.user.exception.ErrorCode.ALREADY_VERIFY;
import static com.zerobase.cms.user.exception.ErrorCode.EXPIRE_CODE;
import static com.zerobase.cms.user.exception.ErrorCode.NOT_FOUND_USER;
import static com.zerobase.cms.user.exception.ErrorCode.WRONG_VERIFICATION;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import com.zerobase.cms.user.domain.SignUpForm;
import com.zerobase.cms.user.domain.model.Customer;
import com.zerobase.cms.user.domain.repository.CustomerRepository;
import com.zerobase.cms.user.exception.CustomException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Optional;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class SignUpCustomerServiceTest {

	@Mock
	private CustomerRepository customerRepository;

	@InjectMocks
	private SignUpCustomerService service;

	@Test
	void signUp() {
		// given
		SignUpForm form = SignUpForm.builder()
			.name("name")
			.birth(LocalDate.now())
			.email("h2ju1004@gmail.com")
			.password("1")
			.phone("01000000000")
			.build();
		ArgumentCaptor<Customer> captor = ArgumentCaptor.forClass(Customer.class);

		// when
		Customer c = service.signUp(form);

		// then
		verify(customerRepository, times(1)).save(captor.capture());
		assertEquals("name", captor.getValue().getName());
		assertEquals("h2ju1004@gmail.com", captor.getValue().getEmail());
		assertEquals("1", captor.getValue().getPassword());
	}

	@Test
	void isEmailExist() {
		// given
		given(customerRepository.findByEmail(anyString()))
			.willReturn(Optional.of(Customer.builder()
				.id(1L).build()));

		// when
		boolean result = service.isEmailExist("h2ju1004@gmail.com");

		// then
		assertTrue(result);
	}

	@Test
	void verifyEmail() {
		// given
		String email = "h2ju1004@gmail.com";
		String code = "1234ABC";
		Customer c = Customer.builder()
			.id(1L).verify(false)
			.verificationCode(code)
			.verifyExpiredAt(LocalDateTime.now().plusDays(1)).build();

		given(customerRepository.findByEmail(anyString()))
			.willReturn(Optional.of(c));

		// when
		service.verifyEmail(email, code);

		// then
		assertTrue(c.isVerify());
	}

	@Test
	void verifyEmail_notFoundUser() {
		// given
		String email = "h2ju1004@gmail.com";
		String code = "1234ABC";

		given(customerRepository.findByEmail(anyString()))
			.willReturn(Optional.empty());

		// when
		CustomException e = assertThrows(CustomException.class,
			() -> service.verifyEmail(email, code));

		// then
		assertEquals(NOT_FOUND_USER, e.getErrorCode());
	}

	@Test
	void verifyEmail_alreadyVerify() {
		// given
		String email = "h2ju1004@gmail.com";
		String code = "1234ABC";
		Customer c = Customer.builder()
			.id(1L).verify(true).build();

		given(customerRepository.findByEmail(anyString()))
			.willReturn(Optional.of(c));

		// when
		CustomException e = assertThrows(CustomException.class,
			() -> service.verifyEmail(email, code));

		// then
		assertEquals(ALREADY_VERIFY, e.getErrorCode());
	}

	@Test
	void verifyEmail_wrongVerification() {
		// given
		String email = "h2ju1004@gmail.com";
		String code = "1234ABC";
		String code2 = "5678DEF";
		Customer c = Customer.builder()
			.id(1L).verify(false)
			.verificationCode(code).build();

		given(customerRepository.findByEmail(anyString()))
			.willReturn(Optional.of(c));

		// when
		CustomException e = assertThrows(CustomException.class,
			() -> service.verifyEmail(email, code2));

		// then
		assertEquals(WRONG_VERIFICATION, e.getErrorCode());
	}

	@Test
	void verifyEmail_expireCode() {
		// given
		String email = "h2ju1004@gmail.com";
		String code = "1234ABC";
		Customer c = Customer.builder()
			.id(1L).verify(false)
			.verificationCode(code)
			.verifyExpiredAt(LocalDateTime.now().minusDays(1)).build();

		given(customerRepository.findByEmail(anyString()))
			.willReturn(Optional.of(c));

		// when
		CustomException e = assertThrows(CustomException.class,
			() -> service.verifyEmail(email, code));

		// then
		assertEquals(EXPIRE_CODE, e.getErrorCode());
	}

	@Test
	void changeCustomerValidateEmail() {
		// given
		String code = "1234ABC";
		Customer c = Customer.builder()
			.id(1L).verify(false).build();

		given(customerRepository.findById(anyLong()))
			.willReturn(Optional.of(c));

		// when
		LocalDateTime ldt = service.changeCustomerValidateEmail(100L, code);

		// then
		assertNotNull(ldt);
	}

	@Test
	void changeCustomerValidateEmail_notFoundUser() {
		// given
		String code = "1234ABC";

		given(customerRepository.findById(anyLong()))
			.willReturn(Optional.empty());

		// when
		CustomException e = assertThrows(CustomException.class,
			() -> service.changeCustomerValidateEmail(100L, code));

		// then
		assertEquals(NOT_FOUND_USER, e.getErrorCode());
	}
}