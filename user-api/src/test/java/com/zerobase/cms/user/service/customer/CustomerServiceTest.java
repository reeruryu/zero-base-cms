package com.zerobase.cms.user.service.customer;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.BDDMockito.given;

import com.zerobase.cms.user.domain.model.Customer;
import com.zerobase.cms.user.domain.repository.CustomerRepository;
import java.util.Optional;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class CustomerServiceTest {

	@Mock
	private CustomerRepository customerRepository;

	@InjectMocks
	private CustomerService service;

	@Test
	void findByIdAndEmail() {
		// given
		Customer c = Customer.builder()
			.id(1L).email("abc@test.com").build();

		given(customerRepository.findById(anyLong()))
			.willReturn(Optional.of(c));

		// when
		Optional<Customer> oc = service.findByIdAndEmail(1L, "abc@test.com");

		// then
		assertTrue(oc.isPresent());
	}

	@Test
	void findValidCustomer() {
		// given
		Customer c = Customer.builder()
			.id(1L).email("abc@test.com").password("abc")
			.verify(true).build();

		given(customerRepository.findByEmail(anyString()))
			.willReturn(Optional.of(c));

		// when
		Optional<Customer> oc = service.findValidCustomer("abc@test.com", "abc");

		// then
		assertTrue(oc.isPresent());
	}
}