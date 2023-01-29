package com.zerobase.cms.user.service.seller;

import static com.zerobase.cms.user.exception.ErrorCode.ALREADY_VERIFY;
import static com.zerobase.cms.user.exception.ErrorCode.EXPIRE_CODE;
import static com.zerobase.cms.user.exception.ErrorCode.NOT_FOUND_USER;
import static com.zerobase.cms.user.exception.ErrorCode.WRONG_VERIFICATION;

import com.zerobase.cms.user.domain.SignUpForm;
import com.zerobase.cms.user.domain.model.Seller;
import com.zerobase.cms.user.domain.repository.SellerRepository;
import com.zerobase.cms.user.exception.CustomException;
import java.time.LocalDateTime;
import java.util.Locale;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class SignUpSellerService {

	private final SellerRepository sellerRepository;

	public Seller signUp(SignUpForm form) {
		return sellerRepository.save(Seller.from(form));
	}

	public boolean isEmailExist(String email) {
		return sellerRepository.findByEmail(email.toLowerCase(Locale.ROOT))
			.isPresent();
	}

	@Transactional
	public void verifyEmail(String email, String code) {
		Seller seller = sellerRepository.findByEmail(email)
			.orElseThrow(() -> new CustomException(NOT_FOUND_USER));

		if (seller.isVerify()) {
			throw new CustomException(ALREADY_VERIFY);
		} else if (!seller.getVerificationCode().equals(code)) {
			throw new CustomException(WRONG_VERIFICATION);
		} else if (seller.getVerifyExpiredAt().isBefore(LocalDateTime.now())) {
			throw new CustomException(EXPIRE_CODE);
		}
		seller.setVerify(true);
	}

	@Transactional
	public LocalDateTime changeSellerValidateEmail(Long sellerId, String verificationCode) {
		Seller s = sellerRepository.findById(sellerId)
			.orElseThrow(() -> new CustomException(NOT_FOUND_USER));

		s.setVerificationCode(verificationCode);
		s.setVerifyExpiredAt(LocalDateTime.now().plusDays(1));
		return s.getVerifyExpiredAt();
	}
}
