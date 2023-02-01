package com.zerobase.cms.order.application;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import com.zerobase.cms.order.domain.model.Product;
import com.zerobase.cms.order.domain.product.AddProductCartForm;
import com.zerobase.cms.order.domain.product.AddProductForm;
import com.zerobase.cms.order.domain.product.AddProductItemForm;
import com.zerobase.cms.order.domain.redis.Cart;
import com.zerobase.cms.order.domain.repository.ProductRepository;
import com.zerobase.cms.order.service.ProductService;
import java.util.ArrayList;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
class CartApplicationTest {

	@Autowired
	private CartApplication cartApplication;

	@Autowired
	private ProductService productService;

	@Autowired
	private ProductRepository productRepository;

	@Test
	void addTest_modify() {
		Long customerId = 100L;

		cartApplication.clearCart(customerId);

		Product p = addProduct();

		Product result = productRepository.findWithProductItemById(p.getId()).get();

		assertNotNull(result);

		Cart cart = cartApplication.addCart(customerId, makeAddForm(result));
		assertEquals(cart.getMessages().size(), 0);

		cart = cartApplication.getCart(customerId);
		assertEquals(cart.getMessages().size(), 1);
		assertEquals(cart.getMessages().get(0),
			p.getName() + " 상품의 변동 사항 : "
				+ p.getProductItems().get(0).getName()
				+ " 가격이 변동되었습니다., ");
	}

	AddProductCartForm makeAddForm(Product p) {
		AddProductCartForm.ProductItem productItem =
			AddProductCartForm.ProductItem.builder()
				.id(p.getProductItems().get(0).getId())
				.name(p.getProductItems().get(0).getName())
				.count(5)
				.price(20000)
				.build();

		return AddProductCartForm.builder()
				.id(p.getId())
				.sellerId(p.getSellerId())
				.name(p.getName())
				.description(p.getDescription())
				.items(List.of(productItem)).build();
	}

	Product addProduct() {
		Long sellerId = 1L;

		AddProductForm form = makeProductForm("nike 에어포스", "신발입니다.", 3);

		return productService.addProduct(sellerId, form);
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
			.count(10)
			.build();
	}
}