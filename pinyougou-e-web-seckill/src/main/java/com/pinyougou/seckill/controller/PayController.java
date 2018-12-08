package com.pinyougou.seckill.controller;

import java.util.HashMap;
import java.util.Map;

import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.alibaba.dubbo.config.annotation.Reference;
import com.pinyougou.pay.service.WeiChatPayService;
import com.pinyougou.pojo.TbSeckillOrder;
import com.pinyougou.seckill.service.SeckillOrderService;

import entity.Result;

@RestController
@RequestMapping("/pay")
public class PayController {

	@Reference
	private WeiChatPayService weiChatPayService;

	@Reference
	private SeckillOrderService seckillOrderService;

	@RequestMapping("/createNative")
	public Map<String, String> createNative() {
		// 获取当前用户
		String userId = SecurityContextHolder.getContext().getAuthentication().getName();
		// 到 redis 查询秒杀订单
		TbSeckillOrder seckillOrder = seckillOrderService.searchOrderFromRedisByUserId(userId);
		// 判断秒杀订单存在
		if (seckillOrder != null) {
			long fen = (long) (seckillOrder.getMoney().doubleValue() * 100);// 金额（分）

			return weiChatPayService.createNative(seckillOrder.getId() + "", +fen + "");
		} else {
			return new HashMap<>();
		}
	}

	@RequestMapping("/queryPayStatus")
	public Result queryPayStatus(String out_trade_no) {

		String userId = SecurityContextHolder.getContext().getAuthentication().getName();
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
				// 保存订单
				seckillOrderService.saveOrderFromRedis2DB(userId, Long.valueOf(out_trade_no),
						map.get("transaction_id"));
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

				Map<String, String> payResult = weiChatPayService.closePay(out_trade_no);

				if (payResult != null && "FAIL".equals(payResult.get("return_code"))) {
					if ("ORDERPAID".equals(payResult.get("err_code"))) {
						result = new Result(true, "支付成功");
						seckillOrderService.saveOrderFromRedis2DB(userId, Long.valueOf(out_trade_no),
								map.get("transaction_id"));
					}
				}
				if (!result.isSuccess()) {
					seckillOrderService.deleteOrderFromRedis(userId, Long.valueOf(out_trade_no));
				}
				break;
			}
		}
		return result;
	}
}
