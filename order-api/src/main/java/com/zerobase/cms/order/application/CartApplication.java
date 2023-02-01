package com.zerobase.cms.order.application;

import static com.zerobase.cms.order.exception.ErrorCode.ITEM_COUNT_NOT_ENOUGH;
import static com.zerobase.cms.order.exception.ErrorCode.NOT_FOUND_PRODUCT;

import com.zerobase.cms.order.domain.model.Product;
import com.zerobase.cms.order.domain.model.ProductItem;
import com.zerobase.cms.order.domain.product.AddProductCartForm;
import com.zerobase.cms.order.domain.redis.Cart;
import com.zerobase.cms.order.exception.CustomException;
import com.zerobase.cms.order.service.CartService;
import com.zerobase.cms.order.service.ProductSearchService;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CartApplication {
	private final CartService cartService;
	private final ProductSearchService productSearchService;

	public Cart addCart(Long customerId, AddProductCartForm form) {
		Product product = productSearchService.getByProductId(form.getId());
		if (product == null) {
			throw new CustomException(NOT_FOUND_PRODUCT);
		}
		Cart cart = cartService.getCart(customerId);

		if (!addAble(cart, product, form)) {
			throw new CustomException(ITEM_COUNT_NOT_ENOUGH);
		}

		return cartService.addCart(customerId, form);
	}

	public Cart getCart(Long customerId) {
		Cart cart = refreshCart(cartService.getCart(customerId));
		Cart returnCart = new Cart();
		returnCart.setCustomerId(customerId);
		returnCart.setProducts(cart.getProducts());
		returnCart.setMessages(cart.getMessages());
		// 메세지를 보고 난 다음에는, 이미 본 메세지는 스팸이 되기 때문에 제거.
		cart.setMessages(new ArrayList<>());
		// 메세지 없는 것
		cartService.putCart(customerId, cart);
		return returnCart;
	}

	public Cart updateCart(Long customerId, Cart cart) {
		cartService.putCart(customerId, cart);
		return getCart(customerId);
	}

	public void clearCart(Long customerId) {
		cartService.putCart(customerId, null);
	}

	private Cart refreshCart(Cart cart) {
		// 1. 상품이나 상품의 아이템의 정보, 가격, 수량이 변경되었는지 체크하고
		// 그에 맞는 알람 제공해 준다.
		// 2. 상품 수량, 가격을 우리가 임의로 변경한다.
		Map<Long, Product> productMap = productSearchService
			.getListByProductIds(cart.getProducts().stream()
				.map(Cart.Product::getId).collect(Collectors.toList()))
			.stream()
			.collect(Collectors.toMap(Product::getId, product -> product));

		for (int i = 0; i < cart.getProducts().size(); i++) {

			Cart.Product cartProduct = cart.getProducts().get(i);

			Product p = productMap.get(cartProduct.getId());
			if (p == null) {
				cart.getProducts().remove(cartProduct);
				i--;
				cart.addMessage(cartProduct.getName() + "상품이 삭제되었습니다.");
				continue;
			}

			Map<Long, ProductItem> productItemMap = p.getProductItems().stream()
				.collect(Collectors.toMap(ProductItem::getId, productItem -> productItem));

			List<String> tmpMessages = new ArrayList<>();
			for (int j = 0; j < cartProduct.getItems().size(); j++) {
				Cart.ProductItem cartProductItem = cartProduct.getItems().get(j);

				ProductItem pi = productItemMap.get(cartProduct.getId());
				if (pi == null) {
					cartProduct.getItems().remove(cartProductItem);
					j--;
					tmpMessages.add(cartProductItem.getName() + " 옵션이 삭제되었습니다.");
					continue;
				}

				boolean isPriceChanged = false, isCountNotEnough = false;
				if (!cartProductItem.getPrice().equals(pi.getPrice())) {
					isPriceChanged = true;
					cartProductItem.setPrice(pi.getPrice());
				}
				if (cartProductItem.getCount() > pi.getCount()) {
					isCountNotEnough = true;
					cartProductItem.setCount(pi.getCount());
				}
				
				if (isPriceChanged && isCountNotEnough) {
					tmpMessages.add(cartProductItem.getName() + " 가격 변동, 수량이 부족하여 구매 가능한 최대치로 변경되었습니다.");
				} else if (isPriceChanged) {
					tmpMessages.add(cartProductItem.getName() + " 가격이 변동되었습니다.");
				} else if (isCountNotEnough) {
					tmpMessages.add(cartProductItem.getName() + " 수량이 부족하여 구매 가능한 최대치로 변경되었습니다.");
				}
			}

			if (cartProduct.getItems().size() == 0) {
				cart.getProducts().remove(cartProduct);
				i--;
				cart.addMessage(cartProduct.getName() + " 상품의 옵션이 모두 없어져 구매가 불가능합니다.");

			} else if (tmpMessages.size() > 0) {
				StringBuilder builder = new StringBuilder();
				builder.append(cartProduct.getName() + " 상품의 변동 사항 : ");
				for (String message: tmpMessages) {
					builder.append(message);
					builder.append(", ");
				}
				cart.addMessage(builder.toString());
			}
		}
		cartService.putCart(cart.getCustomerId(), cart);
		return cart;
	}

	private boolean addAble(Cart cart, Product product, AddProductCartForm form) {
		Cart.Product cartProduct = cart.getProducts()
			.stream().filter(p -> p.getId().equals(form.getId()))
			.findFirst().orElse(Cart.Product.builder().id(product.getId())
				.items(Collections.emptyList()).build());

		Map<Long, Integer> cartItemCountMap = cartProduct.getItems().stream()
			.collect(Collectors.toMap(Cart.ProductItem::getId, Cart.ProductItem::getCount));
		Map<Long, Integer> currentItemCountMap = product.getProductItems().stream()
			.collect(Collectors.toMap(ProductItem::getId, ProductItem::getCount));

		return form.getItems().stream().noneMatch(
			formItem -> {
				Integer cartCount = cartItemCountMap.get(formItem.getId());
				if (cartCount == null) {
					cartCount = 0;
				}
				Integer currentCount = currentItemCountMap.get(formItem.getId());
				return formItem.getCount() + cartCount > currentCount;
			});
	}
}
