package com.zerobase.cms.order.client;

import com.zerobase.cms.order.client.mailgun.SendMailForm;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.cloud.openfeign.SpringQueryMap;
import org.springframework.web.bind.annotation.PostMapping;

@FeignClient(name = "mailgun", url = "https://api.mailgun.net/v3/")
public interface MailgunClient {

	@PostMapping("sandbox87c784b78bae450c85b15d1f772f1b64.mailgun.org/messages")
	void sendEmail(@SpringQueryMap SendMailForm form);

}
