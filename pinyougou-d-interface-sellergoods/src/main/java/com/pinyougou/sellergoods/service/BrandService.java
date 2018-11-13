package com.pinyougou.sellergoods.service;

import java.util.List;
import java.util.Map;

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

	/**
	 * 增加品牌
	 * 
	 * @param brand
	 */
	void add(TbBrand brand);

	/**
	 * 根据id查询
	 * 
	 * @param id
	 * @return
	 */
	TbBrand findOne(Long id);

	/**
	 * 修改
	 * 
	 * @param brand
	 */
	void update(TbBrand brand);

	/**
	 * 删除
	 * 
	 * @param id
	 */
	void delete(Long[] ids);

	/**
	 * 品牌分页
	 * 
	 * @param brand
	 * @param pageNum  当前页面
	 * @param pageSize 每页记录数
	 * @return
	 */
	PageResult findPage(TbBrand brand, int pageNum, int pageSize);

	/**
	 * 返回下拉列表
	 */
	List<Map> selectOptionList();
}
