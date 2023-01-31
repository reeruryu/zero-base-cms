package com.zerobase.cms.order.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.BDDMockito.given;

import com.zerobase.cms.order.domain.model.Product;
import com.zerobase.cms.order.domain.model.ProductItem;
import com.zerobase.cms.order.domain.repository.ProductRepository;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class ProductSearchServiceTest {

	@InjectMocks
	private ProductSearchService productSearchService;

	@Mock
	private ProductRepository productRepository;

	@Test
	void searchByName() {
		// given
		Long sellerId = 1L;
		List<Product> list = Arrays.asList(
			Product.builder()
				.id(1L).sellerId(sellerId)
				.name("nike").description("신발!!").build(),
			Product.builder()
				.id(2L).sellerId(sellerId)
				.name("adidas").description("신발!!").build()
		);

		given(productRepository.searchByName(anyString()))
			.willReturn(list);

		// when
		List<Product> productList = productSearchService.searchByName("name");

		// then
		assertEquals(2, productList.size());
		assertEquals("nike", productList.get(0).getName());
		assertEquals("adidas", productList.get(1).getName());
	}

	@Test
	void getByProductId() {
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
					.id(2L).sellerId(sellerId).name("black").price(12000).count(345).build()
			))
			.build();

		given(productRepository.findWithProductItemById(anyLong()))
			.willReturn(Optional.of(product));

		// when
		Product p = productSearchService.getByProductId(1L);

		// then
		assertEquals(p.getId(), 1L);
		assertEquals(p.getSellerId(), 1L);
		assertEquals(p.getName(), "nike");
		assertEquals(p.getDescription(), "신발!!");
		assertEquals(p.getProductItems().size(), 2);
		assertEquals(p.getProductItems().get(0).getName(), "white");
		assertEquals(p.getProductItems().get(1).getName(), "black");

	}
}