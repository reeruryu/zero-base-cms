package com.zerobase.cms.user.application;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.BDDMockito.given;

import com.zerobase.cms.domain.config.JwtAuthenticationProvider;
import com.zerobase.cms.user.domain.SignInForm;
import com.zerobase.cms.user.domain.model.Customer;
import com.zerobase.cms.user.domain.model.Seller;
import com.zerobase.cms.user.service.customer.CustomerService;
import com.zerobase.cms.user.service.seller.SellerService;
import java.util.Optional;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class SignInApplicationTest {

	@Mock
	private CustomerService customerService;

	@Mock
	private SellerService sellerService;

	@Mock
	private JwtAuthenticationProvider provider;

	@InjectMocks
	private SignInApplication application;

	@Test
	void customerLoginToken() {
		// given
		SignInForm form = new SignInForm("abc@test.com", "abc");
		Customer c = Customer.builder()
			.id(1L).email("abc@test.com").password("abc")
			.verify(true).build();

		given(customerService.findValidCustomer(anyString(), anyString()))
			.willReturn(Optional.of(c));
		given(provider.createToken(anyString(), anyLong(), any()))
			.willReturn("abc1234jdkajflg");

		// when
		String token = application.customerLoginToken(form);

		// then
		assertNotNull(token);
	}

	@Test
	void sellerLoginToken() {
		// given
		SignInForm form = new SignInForm("abc@test.com", "abc");
		Seller s = Seller.builder()
			.id(1L).email("abc@test.com").password("abc")
			.verify(true).build();

		given(sellerService.findValidSeller(anyString(), anyString()))
			.willReturn(Optional.of(s));
		given(provider.createToken(anyString(), anyLong(), any()))
			.willReturn("abc1234jdkajflg");

		// when
		String token = application.sellerLoginToken(form);

		// then
		assertNotNull(token);
	}
}