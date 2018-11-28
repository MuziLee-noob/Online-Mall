package com.pinyougou.search.service;

import java.util.List;
import java.util.Map;

public interface ItemSearchService {

	/**
	 * 搜索方法
	 * 
	 * @param searchMap
	 * @return
	 */
	public Map<String, Object> search(Map searchMap);

	/**
	 * 将商品SKU列表导入到索引库
	 * 
	 * @param list
	 */
	public void importList(List list);

	public void deleteByGoodsIds(List goodsIds);
}
