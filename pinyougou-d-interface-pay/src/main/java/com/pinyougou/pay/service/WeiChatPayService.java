package com.pinyougou.pay.service;

import java.util.Map;

public interface WeiChatPayService {

	/**
	 * 生成二维码
	 * 
	 * @param out_trade_no
	 * @param total_fee
	 * @return
	 */
	public Map<String, String> createNative(String out_trade_no, String total_fee);

	public Map<String, String> queryPayStatus(String out_trade_no);

	public Map<String, String> closePay(String out_trade_no);
}
