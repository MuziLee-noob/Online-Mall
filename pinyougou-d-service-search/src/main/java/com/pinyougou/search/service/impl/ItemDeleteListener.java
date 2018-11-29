package com.pinyougou.search.service.impl;

import java.util.Arrays;

import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageListener;
import javax.jms.ObjectMessage;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.pinyougou.search.service.ItemSearchService;

@Component
public class ItemDeleteListener implements MessageListener {

	@Autowired
	private ItemSearchService itemSearchService;

	@Override
	public void onMessage(Message message) {
		ObjectMessage objectMessage = (ObjectMessage) message;
		System.out.println("监听到删除消息");
		try {
			Long[] goodsIds = (Long[]) objectMessage.getObject();
			itemSearchService.deleteByGoodsIds(Arrays.asList(goodsIds));
			System.out.println("执行删除动作");
		} catch (JMSException e) {
			e.printStackTrace();
		}
	}

}
