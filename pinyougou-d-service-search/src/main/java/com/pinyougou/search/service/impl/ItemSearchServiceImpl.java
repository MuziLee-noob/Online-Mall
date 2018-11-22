package com.pinyougou.search.service.impl;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.solr.core.SolrTemplate;
import org.springframework.data.solr.core.query.Criteria;
import org.springframework.data.solr.core.query.HighlightOptions;
import org.springframework.data.solr.core.query.HighlightQuery;
import org.springframework.data.solr.core.query.SimpleHighlightQuery;
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

}
