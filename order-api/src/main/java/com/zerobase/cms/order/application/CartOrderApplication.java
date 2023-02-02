package com.zerobase.cms.order.application;

import static com.zerobase.cms.order.exception.ErrorCode.ORDER_FAIL_CHECK_CART;
import static com.zerobase.cms.order.exception.ErrorCode.ORDER_FAIL_NO_MONEY;

import com.zerobase.cms.order.client.MailgunClient;
import com.zerobase.cms.order.client.UserClient;
import com.zerobase.cms.order.client.mailgun.SendMailForm;
import com.zerobase.cms.order.client.user.ChangeBalanceForm;
import com.zerobase.cms.order.client.user.CustomerDto;
import com.zerobase.cms.order.domain.model.ProductItem;
import com.zerobase.cms.order.domain.redis.Cart;
import com.zerobase.cms.order.exception.CustomException;
import com.zerobase.cms.order.service.ProductItemService;
import java.util.stream.IntStream;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class CartOrderApplication {

	private final CartApplication cartApplication;
	private final UserClient userClient;
	private final ProductItemService productItemService;

	private final MailgunClient mailgunClient;

	@Transactional
	public String order(String token, String email, Cart cart) {
		// 1. 물건들이 전부 주문 가능한 상태인지 확인
		// 2. 가격 변동이 있었는지에 대해 확인
		Cart orderCart = cartApplication.refreshCart(cart);

		if (orderCart.getMessages().size() > 0) {
			throw new CustomException(ORDER_FAIL_CHECK_CART);
		}

		// 3. 고객의 돈이 충분한지 & 결제
		CustomerDto customerDto = userClient.getCustomerInfo(token).getBody();

		int totalPrice = getTotalPrice(cart);
		if (customerDto.getBalance() < totalPrice) {
			throw new CustomException(ORDER_FAIL_NO_MONEY);
		}

		// TODO 롤백 계획에 대해서 생각 해야 함.
		userClient.changeBalance(token, ChangeBalanceForm.builder()
				.from("USER")
				.message("Order")
				.money(-totalPrice).build());

		// 4. 상품의 재고 관리
		StringBuilder sb = new StringBuilder();
		for (Cart.Product product: orderCart.getProducts()) {
			sb.append("[" + product.getName() + "]\n");
			for (Cart.ProductItem cartItem: product.getItems()) {
				sb.append("ㄴ>  " + cartItem.getName() + "\t\t\t"
					+ cartItem.getPrice() + "\t" + cartItem.getCount()
					+ "\t\t" + cartItem.getPrice() * cartItem.getCount() + "\n");
				ProductItem productItem = productItemService.getProductItem(cartItem.getId());
				productItem.setCount(productItem.getCount() - cartItem.getCount());
			}
			sb.append("------------------------------------------------------------------------------------------\n");
		}
		sb.append("주문 금액 : " + totalPrice);

		SendMailForm sendMailForm = SendMailForm.builder()
			.from("tester@mytest.com")
			.to(email)
			.subject("Order Detail!")
			.text(sb.toString())
			.build();
		mailgunClient.sendEmail(sendMailForm);

		return "주문 내역을 메일로 발송하였습니다.";

	}
	private Integer getTotalPrice(Cart cart) {

		return cart.getProducts().stream()
			.flatMapToInt(product -> product.getItems().stream()
				.flatMapToInt(productItem -> IntStream.of(
				productItem.getPrice() * productItem.getCount())))
			.sum();

	}

}
