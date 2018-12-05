package com.pinyougou.pay.service.impl;

import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Value;

import com.alibaba.dubbo.config.annotation.Service;
import com.github.wxpay.sdk.WXPayUtil;
import com.pinyougou.pay.service.WeiChatPayService;

import util.HttpClient;

@Service
public class WeiChatPayServiceImpl implements WeiChatPayService {

	@Value("${appid}")
	private String appid;

	@Value("${partner}")
	private String partner;

	@Value("${partnerkey}")
	private String partnerkey;

	@Override
	public Map<String, String> createNative(String out_trade_no, String total_fee) {
		// 1.参数绑定
		Map<String, String> param = new HashMap<>();
		param.put("appid", appid);// 公众账号ID
		param.put("mch_id", partner);// 商户号
		param.put("nonce_str", WXPayUtil.generateNonceStr());// 随机字符串
		param.put("body", "品优购");// 商品描述
		param.put("out_trade_no", out_trade_no);// 商户订单号
		param.put("total_fee", total_fee);// 标价金额
		param.put("spbill_create_ip", "127.0.0.1");// 终端IP
		param.put("notify_url", "http://www.itcast.cn");// 通知地址
		param.put("trade_type", "NATIVE");// 交易类型

		// 定义返回的map
		Map<String, String> returnMap = new HashMap<>();
		try {
			String paramXml = WXPayUtil.generateSignedXml(param, partnerkey);
			System.out.println("请求的参数" + paramXml);

			// 2.发送请求
			HttpClient httpClient = new HttpClient("https://api.mch.weixin.qq.com/pay/unifiedorder");
			httpClient.setHttps(true);
			httpClient.setXmlParam(paramXml);
			httpClient.post();

			// 3.获取结果
			String xmlResult = httpClient.getContent();
			Map<String, String> resultMap = WXPayUtil.xmlToMap(xmlResult);
			System.out.println(resultMap);
			if (resultMap.get("return_code").equalsIgnoreCase("success")
			/* && resultMap.get("result_code)").equalsIgnoreCase("success") */) {
				returnMap.put("code_url", resultMap.get("code_url"));
				returnMap.put("out_trade_no", out_trade_no);
				returnMap.put("total_fee", total_fee);
			} else {
				returnMap.put("return_msg", resultMap.get("return_msg"));
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return returnMap;
	}

	/**
	 * 获取支付状态
	 */
	@Override
	public Map<String, String> queryPayStatus(String out_trade_no) {
		Map<String, String> param = new HashMap<>();
		param.put("appid", appid);// 公众账号ID
		param.put("mch_id", partner);// 商户号
		param.put("out_trade_no", out_trade_no);// 商户订单号
		param.put("nonce_str", WXPayUtil.generateNonceStr());// 随机字符串
		System.out.println("发送的参数" + param);
		try {
			String paramXml = WXPayUtil.generateSignedXml(param, partnerkey);
			HttpClient httpClient = new HttpClient("https://api.mch.weixin.qq.com/pay/orderquery");
			httpClient.setHttps(true);
			httpClient.setXmlParam(paramXml);
			httpClient.post();

			String resultXml = httpClient.getContent();
			Map<String, String> resultMap = WXPayUtil.xmlToMap(resultXml);
			System.out.println(resultMap);
			return resultMap;
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}

	}
}
