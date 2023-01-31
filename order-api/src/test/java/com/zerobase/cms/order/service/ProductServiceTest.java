package com.zerobase.cms.order.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import com.zerobase.cms.order.domain.model.Product;
import com.zerobase.cms.order.domain.model.ProductItem;
import com.zerobase.cms.order.domain.product.UpdateProductForm;
import com.zerobase.cms.order.domain.product.UpdateProductItemForm;
import com.zerobase.cms.order.domain.repository.ProductRepository;
import java.util.Arrays;
import java.util.Optional;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class ProductServiceTest {

	@InjectMocks
	private ProductService productService;

	@Mock
	private ProductRepository productRepository;

	@Test
	void updateProduct() {
		// given
		Long sellerId = 1L;
		UpdateProductForm form = UpdateProductForm.builder()
			.id(1L)
			.name("NIKE")
			.description("신발~~")
			.items(Arrays.asList(
				UpdateProductItemForm.builder()
					.id(1L).name("white").count(234).price(10000).build(),
				UpdateProductItemForm.builder()
					.id(2L).name("black").count(345).price(12000).build()))
			.build();
		Product product = Product.builder()
			.id(1L)
			.sellerId(sellerId)
			.name("nike")
			.description("신발!!")
			.productItems(Arrays.asList(
				ProductItem.builder()
					.id(1L).sellerId(sellerId).name("white").price(10000).count(234).build(),
				ProductItem.builder()
					.id(2L).sellerId(sellerId).name("검정색").price(12000).count(345).build()
			))
			.build();

		given(productRepository.findBySellerIdAndId(anyLong(), anyLong()))
			.willReturn(Optional.of(product));

		// when
		Product p = productService.updateProduct(sellerId, form);

		// then
		assertEquals(p.getName(), form.getName());
		assertEquals(p.getDescription(), form.getDescription());
		assertEquals(p.getProductItems().get(1).getName(), form.getItems().get(1).getName());

	}

	@Test
	void deleteProduct() {
		// given
		Long sellerId = 1L;
		Product product = Product.builder()
			.id(1L)
			.sellerId(sellerId)
			.name("nike")
			.description("신발!!")
			.productItems(Arrays.asList(
				ProductItem.builder()
					.id(1L).sellerId(sellerId).name("white").price(10000).count(234).build(),
				ProductItem.builder()
					.id(2L).sellerId(sellerId).name("검정색").price(12000).count(345).build()
			))
			.build();

		given(productRepository.findBySellerIdAndId(anyLong(), anyLong()))
			.willReturn(Optional.of(product));
		ArgumentCaptor<Product> captor = ArgumentCaptor.forClass(Product.class);

		// when
		productService.deleteProduct(1L, 1L);

		// then
		verify(productRepository, times(1)).delete(captor.capture());
		assertEquals(2, captor.getValue().getProductItems().size());

	}

}