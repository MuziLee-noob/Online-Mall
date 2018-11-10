package com.pinyougou.sellergoods.service;

import java.util.List;

import com.pinyougou.pojo.TbBrand;

import entity.PageResult;

/**
 * 品牌接口
 * 
 * @author hbbxl
 *
 */
public interface BrandService {

	public List<TbBrand> findAll();

	/**
	 * 分页查询
	 * 
	 * @param pageNum  当前页码
	 * @param pageSize 每页记录数
	 * @return
	 */
	PageResult findPage(int pageNum, int pageSize);
}
