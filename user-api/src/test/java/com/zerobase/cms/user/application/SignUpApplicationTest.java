package com.zerobase.cms.user.application;

import static com.zerobase.cms.user.exception.ErrorCode.ALREADY_REGISTER_USER;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.willDoNothing;

import com.zerobase.cms.user.client.MailgunClient;
import com.zerobase.cms.user.domain.SignUpForm;
import com.zerobase.cms.user.domain.model.Customer;
import com.zerobase.cms.user.domain.model.Seller;
import com.zerobase.cms.user.exception.CustomException;
import com.zerobase.cms.user.service.customer.SignUpCustomerService;
import com.zerobase.cms.user.service.seller.SignUpSellerService;
import java.time.LocalDate;
import java.time.LocalDateTime;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class SignUpApplicationTest {

	@Mock
	private MailgunClient mailgunClient;

	@Mock
	private SignUpCustomerService signUpCustomerService;

	@Mock
	private SignUpSellerService signUpSellerService;

	@InjectMocks
	private SignUpApplication application;

	@Test
	void customerSignUp() {
		// given
		SignUpForm form = SignUpForm.builder()
			.email("abc@test.com")
			.password("abc").birth(LocalDate.now())
			.phone("01000000000")
			.build();
		Customer c = Customer.builder()
			.id(1L).email("abc@test.com").password("abc")
			.verify(true).build();

		given(signUpCustomerService.isEmailExist(anyString()))
			.willReturn(false);
		given(signUpCustomerService.signUp(any()))
			.willReturn(c);
		willDoNothing()
			.given(mailgunClient).sendEmail(any());
		given(signUpCustomerService.changeCustomerValidateEmail(anyLong(), anyString()))
			.willReturn(LocalDateTime.now());

		// when
		String res = application.customerSignUp(form);

		// then
		assertEquals("회원 가입에 성공하였습니다.", res);
	}

	@Test
	void customerSignUp_alreadyRegister() {
		// given
		SignUpForm form = SignUpForm.builder()
			.email("abc@test.com")
			.password("abc").birth(LocalDate.now())
			.phone("01000000000")
			.build();

		given(signUpCustomerService.isEmailExist(anyString()))
			.willReturn(true);

		// when
		CustomException e = assertThrows(CustomException.class,
			() -> application.customerSignUp(form));

		// then
		assertEquals(ALREADY_REGISTER_USER, e.getErrorCode());
	}

	@Test
	void sellerSignUp() {
		// given
		SignUpForm form = SignUpForm.builder()
			.email("abc@test.com")
			.password("abc").birth(LocalDate.now())
			.phone("01000000000")
			.build();
		Seller s = Seller.builder()
			.id(1L).email("abc@test.com").password("abc")
			.verify(true).build();

		given(signUpSellerService.isEmailExist(anyString()))
			.willReturn(false);
		given(signUpSellerService.signUp(any()))
			.willReturn(s);
		willDoNothing()
			.given(mailgunClient).sendEmail(any());
		given(signUpSellerService.changeSellerValidateEmail(anyLong(), anyString()))
			.willReturn(LocalDateTime.now());

		// when
		String res = application.sellerSignUp(form);

		// then
		assertEquals("회원 가입에 성공하였습니다.", res);

	}

	@Test
	void sellerSignUp_alreadyRegister() {
		// given
		SignUpForm form = SignUpForm.builder()
			.email("abc@test.com")
			.password("abc").birth(LocalDate.now())
			.phone("01000000000")
			.build();

		given(signUpSellerService.isEmailExist(anyString()))
			.willReturn(true);

		// when
		CustomException e = assertThrows(CustomException.class,
			() -> application.sellerSignUp(form));

		// then
		assertEquals(ALREADY_REGISTER_USER, e.getErrorCode());
	}
}