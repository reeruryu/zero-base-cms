package com.zerobase.cms.user.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import com.zerobase.cms.user.domain.SignUpForm;
import com.zerobase.cms.user.domain.model.Customer;
import java.time.LocalDate;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
class SignUpCustomerServiceTest {

	@Autowired
	private SignUpCustomerService service;

	@Test
	void signUp() {
		SignUpForm form = SignUpForm.builder()
			.name("name")
			.birth(LocalDate.now())
			.email("abc@gmail.com")
			.password("1")
			.phone("01000000000")
			.build();
		Customer c = service.signUp(form);

		assertNotNull(c.getId());
		assertEquals("name", c.getName());
		assertNotNull(c.getBirth());
		assertEquals("abc@gmail.com", c.getEmail());
		assertEquals("1", c.getPassword());
		assertEquals("01000000000", c.getPhone());
		assertNotNull(c.getCreatedAt());
		assertNotNull(c.getModifiedAt());
	}

}