package com.pinyougou.search.service.impl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.solr.core.SolrTemplate;
import org.springframework.data.solr.core.query.Criteria;
import org.springframework.data.solr.core.query.GroupOptions;
import org.springframework.data.solr.core.query.HighlightOptions;
import org.springframework.data.solr.core.query.HighlightQuery;
import org.springframework.data.solr.core.query.Query;
import org.springframework.data.solr.core.query.SimpleHighlightQuery;
import org.springframework.data.solr.core.query.SimpleQuery;
import org.springframework.data.solr.core.query.result.GroupEntry;
import org.springframework.data.solr.core.query.result.GroupPage;
import org.springframework.data.solr.core.query.result.GroupResult;
import org.springframework.data.solr.core.query.result.HighlightEntry;
import org.springframework.data.solr.core.query.result.HighlightEntry.Highlight;
import org.springframework.data.solr.core.query.result.HighlightPage;

import com.alibaba.dubbo.config.annotation.Service;
import com.pinyougou.pojo.TbItem;
import com.pinyougou.search.service.ItemSearchService;

@Service(timeout = 5000)
public class ItemSearchServiceImpl implements ItemSearchService {

	@Autowired
	private SolrTemplate solrTemplate;

	@Override
	public Map<String, Object> search(Map searchMap) {

		Map<String, Object> map = new HashMap<>();

		/*
		 * Query query = new SimpleQuery(); Criteria criteria = new
		 * Criteria("item_keywords").is(searchMap.get("keywords"));
		 * query.addCriteria(criteria);
		 * 
		 * ScoredPage<TbItem> page = solrTemplate.queryForPage(query, TbItem.class);
		 * map.put("rows", page.getContent());
		 */
		// 1.查询列表
		map.putAll(searchList(searchMap));
		// 2.分组查询商品分类列表
		List<String> categoryList = searchCategoryList(searchMap);
		map.put("categoryList", categoryList);
		// 3.查询品牌和规格列表
		if (categoryList.size() > 0) {
			map.putAll(searchBrandAndSpecList(categoryList.get(0)));
		}
		return map;
	}

	// 查询列表方法
	private Map<String, Object> searchList(Map searchMap) {

		Map<String, Object> map = new HashMap<>();

		// 高亮显示关键词
		HighlightQuery query = new SimpleHighlightQuery();

		HighlightOptions options = new HighlightOptions().addField("item_title");// 高亮域（字段）
		options.setSimplePrefix("<em style='color:red'>");// 前缀
		options.setSimplePostfix("</em>");// 后缀

		query.setHighlightOptions(options);// 为查询对象设置高亮选项
		Criteria criteria = new Criteria("item_keywords").is(searchMap.get("keywords"));
		query.addCriteria(criteria);

		HighlightPage<TbItem> page = solrTemplate.queryForHighlightPage(query, TbItem.class);// 返回一个高亮页对象
		// 高亮入口集合
		List<HighlightEntry<TbItem>> entryList = page.getHighlighted();

		for (HighlightEntry<TbItem> entry : entryList) {
			// 获取高亮列表
			List<Highlight> highlights = entry.getHighlights();

			/*
			 * for (Highlight highlight : highlights) { List<String> sns =
			 * highlight.getSnipplets();//每个域有可能存在多个值 System.out.println(sns); }
			 */

			if (highlights.size() > 0 && highlights.get(0).getSnipplets().size() > 0) {
				TbItem item = entry.getEntity();
				item.setTitle(highlights.get(0).getSnipplets().get(0));
			}

		}
		map.put("rows", page.getContent());
		return map;
	}

	private List<String> searchCategoryList(Map searchMap) {
		List<String> list = new ArrayList<>();

		Query query = new SimpleQuery();
		// 关键词查询
		Criteria criteria = new Criteria("item_keywords").is(searchMap.get("keywords"));// where 条件
		query.addCriteria(criteria);
		// 设置分组选项
		GroupOptions groupOptions = new GroupOptions().addGroupByField("item_category");// group by
		query.setGroupOptions(groupOptions);
		// 获取分组页
		GroupPage<TbItem> page = solrTemplate.queryForGroupPage(query, TbItem.class);
		// 获取分组结果对象
		GroupResult<TbItem> result = page.getGroupResult("item_category");
		// 获取分组入口对象
		Page<GroupEntry<TbItem>> entries = result.getGroupEntries();
		// 获取分组入口集合
		List<GroupEntry<TbItem>> entriesList = entries.getContent();

		for (GroupEntry<TbItem> entry : entriesList) {
			list.add(entry.getGroupValue());
		}

		return list;
	}

	@Autowired
	private RedisTemplate redisTemplate;

	/**
	 * 查询品牌和规格列表
	 * 
	 * @param categoryName 商品分类名称
	 * @return
	 */
	private Map searchBrandAndSpecList(String categoryName) {
		Map map = new HashMap<>();

		// 1.根据商品分类名称得到模板id
		Long templateId = (Long) redisTemplate.boundHashOps("itemCat").get(categoryName);
		if (templateId != null) {
			// 2.根据模板id获取品牌列表
			List brandList = (List) redisTemplate.boundHashOps("brandList").get(templateId);
			map.put("brandList", brandList);
			// 3.根据模板id获取规格列表
			List specList = (List) redisTemplate.boundHashOps("specList").get(templateId);
			map.put("specList", specList);
		}

		return map;
	}

}
