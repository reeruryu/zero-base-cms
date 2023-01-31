package com.zerobase.cms.order.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import com.zerobase.cms.order.domain.model.Product;
import com.zerobase.cms.order.domain.product.AddProductForm;
import com.zerobase.cms.order.domain.product.AddProductItemForm;
import com.zerobase.cms.order.domain.repository.ProductRepository;
import java.util.ArrayList;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
class ProductServiceBootTest {

	@Autowired
	private ProductService productService;

	@Autowired
	private ProductRepository productRepository;

	@Test
	void addProduct() {
		Long sellerId = 1L;

		AddProductForm form = makeProductForm("nike 에어포스", "신발입니다.", 3);

		Product p = productService.addProduct(sellerId, form);

		Product result = productRepository.findWithProductItemById(p.getId()).get();

		assertNotNull(result);
		assertEquals(result.getName(), "nike 에어포스");
		assertEquals(result.getDescription(), "신발입니다.");


		assertEquals(result.getProductItems().size(), 3);
		assertEquals(result.getProductItems().get(0).getName(), "nike 에어포스0");
		assertEquals(result.getProductItems().get(1).getName(), "nike 에어포스1");
		assertEquals(result.getProductItems().get(2).getName(), "nike 에어포스2");
		assertEquals(result.getProductItems().get(0).getPrice(), 10000);
		assertEquals(result.getProductItems().get(0).getCount(), 1);

	}

	private static AddProductForm makeProductForm(String name, String description, int itemCount) {
		List<AddProductItemForm> itemForms = new ArrayList<>();
		for (int i = 0; i < itemCount; i++) {
			itemForms.add(makeProductItemForm(null, name + i));
		}

		return AddProductForm.builder()
			.name(name)
			.description(description)
			.items(itemForms)
			.build();
	}

	private static AddProductItemForm makeProductItemForm(Long productId, String name) {
		return AddProductItemForm.builder()
			.productId(productId)
			.name(name)
			.price(10000)
			.count(1)
			.build();
	}

}