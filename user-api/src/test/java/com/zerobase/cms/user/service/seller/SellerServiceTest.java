package com.zerobase.cms.user.service.seller;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.BDDMockito.given;

import com.zerobase.cms.user.domain.model.Seller;
import com.zerobase.cms.user.domain.repository.SellerRepository;
import java.util.Optional;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class SellerServiceTest {

	@Mock
	private SellerRepository sellerRepository;

	@InjectMocks
	private SellerService service;

	@Test
	void findByIdAndEmail() {
		// given
		Seller s = Seller.builder()
			.id(1L).email("abc@test.com").build();

		given(sellerRepository.findByIdAndEmail(anyLong(), anyString()))
			.willReturn(Optional.of(s));

		// when
		Optional<Seller> os = service.findByIdAndEmail(1L, "abc@test.com");

		// then
		assertTrue(os.isPresent());
	}

	@Test
	void findValidSeller() {
		// given
		Seller s = Seller.builder()
			.id(1L).email("abc@test.com").password("abc")
			.verify(true).build();

		given(sellerRepository.findByEmailAndPasswordAndVerifyIsTrue(anyString(), anyString()))
			.willReturn(Optional.of(s));

		// when
		Optional<Seller> os = service.findValidSeller("abc@test.com", "abc");

		// then
		assertTrue(os.isPresent());
	}


}