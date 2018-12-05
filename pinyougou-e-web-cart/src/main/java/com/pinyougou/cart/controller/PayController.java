package com.pinyougou.cart.controller;

import java.util.HashMap;
import java.util.Map;

import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.alibaba.dubbo.config.annotation.Reference;
import com.pinyougou.order.service.OrderService;
import com.pinyougou.pay.service.WeiChatPayService;
import com.pinyougou.pojo.TbPayLog;

import entity.Result;

@RestController
@RequestMapping("/pay")
public class PayController {

	@Reference
	private WeiChatPayService weiChatPayService;

	@Reference
	private OrderService orderService;

	@RequestMapping("/createNative")
	public Map<String, String> createNative() {
		// 获取当前用户
		String userId = SecurityContextHolder.getContext().getAuthentication().getName();
		// 到 redis 查询支付日志
		TbPayLog payLog = orderService.searchPayLogFromRedis(userId);
		// 判断支付日志存在
		if (payLog != null) {
			return weiChatPayService.createNative(payLog.getOutTradeNo(), payLog.getTotalFee() + "");
		} else {
			return new HashMap<>();
		}
	}

	@RequestMapping("/queryPayStatus")
	public Result queryPayStatus(String out_trade_no) {
		Result result = null;
		int x = 0;
		while (true) {
			Map<String, String> map = weiChatPayService.queryPayStatus(out_trade_no);
			if (map == null) {
				result = new Result(false, "支付失败");
				break;
			}

			if ("SUCCESS".equals(map.get("trade_state"))) {
				result = new Result(true, "支付成功");
				orderService.updateOrderStatus(out_trade_no, map.get("transaction_id"));
				break;
			}
			try {
				Thread.sleep(3000);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			x++;
			if (x > 100) {
				result = new Result(false, "二维码超时");
				break;
			}
		}
		return result;
	}
}
