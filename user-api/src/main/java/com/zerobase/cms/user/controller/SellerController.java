package com.zerobase.cms.user.controller;

import com.zerobase.cms.domain.config.JwtAuthenticationProvider;
import com.zerobase.cms.user.service.seller.SellerService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/seller")
@RequiredArgsConstructor
public class SellerController {

	private final JwtAuthenticationProvider provider;
	private final SellerService sellerService;

}
