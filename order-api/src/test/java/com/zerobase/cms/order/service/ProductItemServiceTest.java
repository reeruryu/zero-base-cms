package com.zerobase.cms.order.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.BDDMockito.given;

import com.zerobase.cms.order.domain.model.ProductItem;
import com.zerobase.cms.order.domain.product.UpdateProductItemForm;
import com.zerobase.cms.order.domain.repository.ProductItemRepository;
import com.zerobase.cms.order.domain.repository.ProductRepository;
import java.util.Optional;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class ProductItemServiceTest {

	@Mock
	private ProductRepository productRepository;

	@Mock
	private ProductItemRepository productItemRepository;

	@InjectMocks
	private ProductItemService productItemService;

	@Test
	void updateProductItem() {
		// given
		Long sellerId = 1L;

		ProductItem productItem = ProductItem.builder()
			.id(1L)
			.sellerId(sellerId)
			.name("white")
			.price(10000)
			.count(234)
			.build();

		UpdateProductItemForm form = UpdateProductItemForm.builder()
			.id(1L)
			.name("red")
			.price(13000)
			.count(135)
			.build();

		given(productItemRepository.findById(anyLong()))
			.willReturn(Optional.of(productItem));

		// when
		ProductItem pi = productItemService.updateProductItem(sellerId, form);

		// then
		assertEquals(pi.getName(), form.getName());
		assertEquals(pi.getPrice(), form.getPrice());
		assertEquals(pi.getCount(), form.getCount());
	}
}