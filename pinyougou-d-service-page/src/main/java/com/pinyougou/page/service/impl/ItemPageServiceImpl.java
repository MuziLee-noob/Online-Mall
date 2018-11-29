package com.pinyougou.page.service.impl;

import java.io.File;
import java.io.FileWriter;
import java.io.Writer;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.view.freemarker.FreeMarkerConfigurer;

import com.pinyougou.mapper.TbGoodsDescMapper;
import com.pinyougou.mapper.TbGoodsMapper;
import com.pinyougou.mapper.TbItemCatMapper;
import com.pinyougou.mapper.TbItemMapper;
import com.pinyougou.page.service.ItemPageService;
import com.pinyougou.pojo.TbGoods;
import com.pinyougou.pojo.TbGoodsDesc;
import com.pinyougou.pojo.TbItem;
import com.pinyougou.pojo.TbItemExample;
import com.pinyougou.pojo.TbItemExample.Criteria;

import freemarker.template.Configuration;
import freemarker.template.Template;

@Service
public class ItemPageServiceImpl implements ItemPageService {

	@Autowired
	private FreeMarkerConfigurer freeMarkerConfigurer;

	@Value("${pagedir}")
	private String pagedir;

	@Autowired
	private TbGoodsMapper goodsMapper;

	@Autowired
	private TbGoodsDescMapper goodsDescMapper;

	@Autowired
	private TbItemCatMapper itemCatMapper;

	@Autowired
	private TbItemMapper itemMapper;

	@Override
	public boolean genItemHtml(Long goodsId) {

		Configuration configuration = freeMarkerConfigurer.getConfiguration();

		try {
			Template template = configuration.getTemplate("item.ftl");

			Map<String, Object> dataModel = new HashMap<>();
			TbGoods goods = goodsMapper.selectByPrimaryKey(goodsId);
			dataModel.put("goods", goods);

			TbGoodsDesc goodsDesc = goodsDescMapper.selectByPrimaryKey(goodsId);
			dataModel.put("goodsDesc", goodsDesc);

			String itemcat1 = itemCatMapper.selectByPrimaryKey(goods.getCategory1Id()).getName();
			String itemcat2 = itemCatMapper.selectByPrimaryKey(goods.getCategory2Id()).getName();
			String itemcat3 = itemCatMapper.selectByPrimaryKey(goods.getCategory3Id()).getName();
			dataModel.put("itemCat1", itemcat1);
			dataModel.put("itemCat2", itemcat2);
			dataModel.put("itemCat3", itemcat3);

			// 4.读取SKU列表数据
			TbItemExample example = new TbItemExample();
			Criteria criteria = example.createCriteria();
			criteria.andGoodsIdEqualTo(goodsId);
			criteria.andStatusEqualTo("1");// 状态有效
			List<TbItem> itemList = itemMapper.selectByExample(example);
			example.setOrderByClause("is_default desc");// 按照是否默认字段进行排序，返回第一条结果为默认SKU
			dataModel.put("itemList", itemList);

			Writer writer = new FileWriter(pagedir + goodsId + ".html");
			template.process(dataModel, writer);
			writer.close();

			return true;
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}

	}

	@Override
	public boolean deleteItemHtml(Long[] ids) {
		try {
			for (Long goodsId : ids) {
				new File(pagedir + goodsId + ".html").delete();
			}
			return true;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return false;
	}

}
