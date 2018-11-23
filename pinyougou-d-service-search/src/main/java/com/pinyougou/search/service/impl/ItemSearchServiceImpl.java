package com.pinyougou.search.service.impl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.solr.core.SolrTemplate;
import org.springframework.data.solr.core.query.Criteria;
import org.springframework.data.solr.core.query.FilterQuery;
import org.springframework.data.solr.core.query.GroupOptions;
import org.springframework.data.solr.core.query.HighlightOptions;
import org.springframework.data.solr.core.query.HighlightQuery;
import org.springframework.data.solr.core.query.Query;
import org.springframework.data.solr.core.query.SimpleFilterQuery;
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
		String categoryName = (String) searchMap.get("category");
		if (!"".equals(categoryName)) {
			map.putAll(searchBrandAndSpecList(categoryName));
		} else {
			if (categoryList.size() > 0) {
				map.putAll(searchBrandAndSpecList(categoryList.get(0)));
			}
		}

		return map;
	}

	// 查询列表方法
	private Map<String, Object> searchList(Map searchMap) {

		Map<String, Object> map = new HashMap<>();

		// 高亮选项初始化
		HighlightQuery query = new SimpleHighlightQuery();

		HighlightOptions options = new HighlightOptions().addField("item_title");// 高亮域（字段）
		options.setSimplePrefix("<em style='color:red'>");// 前缀
		options.setSimplePostfix("</em>");// 后缀
		query.setHighlightOptions(options);// 为查询对象设置高亮选项

		// ************************ 添加搜索条件 **********************
		// 1.1关键词查询
		Criteria criteria = new Criteria("item_keywords").is(searchMap.get("keywords"));
		query.addCriteria(criteria);

		// 1.2按照商品分类进行筛选
		if (!"".equals(searchMap.get("category"))) { // 如果用户选择了分类筛选
			FilterQuery filterQuery = new SimpleFilterQuery();
			Criteria filterCriteria = new Criteria("item_category").is(searchMap.get("category"));
			filterQuery.addCriteria(filterCriteria);
			query.addFilterQuery(filterQuery);
		}

		// 1.3按照品牌分类进行筛选
		if (!"".equals(searchMap.get("brand"))) { // 如果用户选择了品牌筛选
			FilterQuery filterQuery = new SimpleFilterQuery();
			Criteria filterCriteria = new Criteria("item_brand").is(searchMap.get("brand"));
			filterQuery.addCriteria(filterCriteria);
			query.addFilterQuery(filterQuery);
		}

		// 1.4按照规格分类进行筛选
		if (searchMap.get("spec") != null) { // 如果用户选择了规格筛选
			Map<String, String> specMap = (Map<String, String>) searchMap.get("spec");
			Set<String> keySet = specMap.keySet();
			for (String key : keySet) {
				String value = specMap.get(key);
				FilterQuery filterQuery = new SimpleFilterQuery();
				Criteria filterCriteria = new Criteria("item_spec_" + key).is(value);
				filterQuery.addCriteria(filterCriteria);
				query.addFilterQuery(filterQuery);
			}

		}

		// *************** 获取高亮结果集 ****************
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
