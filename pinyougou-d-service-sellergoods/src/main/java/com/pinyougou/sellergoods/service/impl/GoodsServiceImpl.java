package com.pinyougou.sellergoods.service.impl;

import java.util.Date;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

import com.alibaba.dubbo.config.annotation.Service;
import com.alibaba.fastjson.JSON;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.pinyougou.mapper.TbBrandMapper;
import com.pinyougou.mapper.TbGoodsDescMapper;
import com.pinyougou.mapper.TbGoodsMapper;
import com.pinyougou.mapper.TbItemCatMapper;
import com.pinyougou.mapper.TbItemMapper;
import com.pinyougou.mapper.TbSellerMapper;
import com.pinyougou.pojo.TbBrand;
import com.pinyougou.pojo.TbGoods;
import com.pinyougou.pojo.TbGoodsDesc;
import com.pinyougou.pojo.TbGoodsExample;
import com.pinyougou.pojo.TbGoodsExample.Criteria;
import com.pinyougou.pojo.TbItem;
import com.pinyougou.pojo.TbItemCat;
import com.pinyougou.pojo.TbItemExample;
import com.pinyougou.pojo.TbSeller;
import com.pinyougou.pojogroup.Goods;
import com.pinyougou.sellergoods.service.GoodsService;

import entity.PageResult;

/**
 * 服务实现层
 * 
 * @author Administrator
 *
 */
@Service
@Transactional
public class GoodsServiceImpl implements GoodsService {

	@Autowired
	private TbGoodsMapper goodsMapper;

	@Autowired
	private TbGoodsDescMapper goodsDescMapper;

	@Autowired
	private TbItemMapper itemMapper;

	@Autowired
	private TbItemCatMapper itemCatMapper;

	@Autowired
	private TbBrandMapper brandMapper;

	@Autowired
	private TbSellerMapper sellerMapper;

	/**
	 * 查询全部
	 */
	@Override
	public List<TbGoods> findAll() {
		return goodsMapper.selectByExample(null);
	}

	/**
	 * 按分页查询
	 */
	@Override
	public PageResult findPage(int pageNum, int pageSize) {
		PageHelper.startPage(pageNum, pageSize);
		Page<TbGoods> page = (Page<TbGoods>) goodsMapper.selectByExample(null);
		return new PageResult(page.getTotal(), page.getResult());
	}

	/**
	 * 增加
	 */
	@Override
	public void add(Goods goods) {
		TbGoods tbGoods = goods.getGoods();
		tbGoods.setAuditStatus("0");// 设置状态未审核
		goodsMapper.insert(tbGoods);// 插入商品基本信息

		TbGoodsDesc goodsDesc = goods.getGoodsDesc();
		goodsDesc.setGoodsId(tbGoods.getId());// 将基本表id设置给扩展表
		goodsDescMapper.insert(goodsDesc);// 插入商品扩展表数据

		// 插入SKU列表数据
		saveItemList(tbGoods, goods, goodsDesc);
	}

	private void saveItemList(TbGoods tbGoods, Goods goods, TbGoodsDesc goodsDesc) {
		// 如果启用了规格
		if (tbGoods.getIsEnableSpec().equals("1")) {
			for (TbItem item : goods.getItemList()) {
				// 构建标题 SPU名称 + 规格选项值
				String title = tbGoods.getGoodsName();// SPU名称
				Map<String, Object> map = JSON.parseObject(item.getSpec());

				for (String key : map.keySet()) {
					title += " " + map.get(key);
				}
				item.setTitle(title);
				saveItem(item, tbGoods, goodsDesc);
			}
		} else {
			TbItem item = new TbItem();
			// 构建标题
			String title = tbGoods.getGoodsName();
			item.setTitle(title);
			item.setPrice(tbGoods.getPrice());
			item.setStatus("1");
			item.setIsDefault("1");
			item.setNum(99999);
			item.setSpec("{}");
			saveItem(item, tbGoods, goodsDesc);
		}
	}

	private void saveItem(TbItem item, TbGoods tbGoods, TbGoodsDesc goodsDesc) {
		item.setCategoryid(tbGoods.getCategory3Id());// 商品分类
		item.setCreateTime(new Date());// 创建日期
		item.setUpdateTime(new Date());// 更新日期

		item.setGoodsId(tbGoods.getId());// 商品id
		item.setSellerId(tbGoods.getSellerId());// 商家id

		// 分类名称
		TbItemCat itemCat = itemCatMapper.selectByPrimaryKey(tbGoods.getCategory3Id());
		item.setCategory(itemCat.getName());

		// 商品名称
		TbBrand brand = brandMapper.selectByPrimaryKey(tbGoods.getBrandId());
		item.setBrand(brand.getName());

		// 店铺名称
		TbSeller seller = sellerMapper.selectByPrimaryKey(tbGoods.getSellerId());
		item.setSeller(seller.getNickName());

		// 图片

		List<Map> imageList = JSON.parseArray(goodsDesc.getItemImages(), Map.class);
		if (imageList.size() > 0) {
			item.setImage((String) imageList.get(0).get("url"));
		} else {
			item.setImage("");
		}
		itemMapper.insert(item);

	}

	/**
	 * 修改
	 */
	@Override
	public void update(Goods goods) {
		// 更新基本表数据
		goodsMapper.updateByPrimaryKey(goods.getGoods());
		// 更新扩展表数据
		goodsDescMapper.updateByPrimaryKey(goods.getGoodsDesc());
		// 删除原有的SKU列表数据
		TbItemExample example = new TbItemExample();
		com.pinyougou.pojo.TbItemExample.Criteria criteria = example.createCriteria();
		criteria.andGoodsIdEqualTo(goods.getGoods().getId());
		itemMapper.deleteByExample(example);

		// 插入SKU列表数据
		saveItemList(goods.getGoods(), goods, goods.getGoodsDesc());
	}

	/**
	 * 根据ID获取实体
	 * 
	 * @param id
	 * @return
	 */
	@Override
	public Goods findOne(Long id) {
		Goods goods = new Goods();
		// 基本表
		TbGoods tbGoods = goodsMapper.selectByPrimaryKey(id);
		goods.setGoods(tbGoods);
		// 扩展表
		TbGoodsDesc goodsDesc = goodsDescMapper.selectByPrimaryKey(id);
		goods.setGoodsDesc(goodsDesc);

		// SKU列表
		TbItemExample example = new TbItemExample();
		com.pinyougou.pojo.TbItemExample.Criteria criteria = example.createCriteria();
		criteria.andGoodsIdEqualTo(id);
		List<TbItem> itemList = itemMapper.selectByExample(example);
		goods.setItemList(itemList);

		return goods;
	}

	/**
	 * 批量删除
	 */
	@Override
	public void delete(Long[] ids) {
		for (Long id : ids) {
			TbGoods tbGoods = goodsMapper.selectByPrimaryKey(id);
			tbGoods.setIsDelete("1");// 表示逻辑删除
			goodsMapper.updateByPrimaryKey(tbGoods);
		}
	}

	@Override
	public PageResult findPage(TbGoods goods, int pageNum, int pageSize) {
		PageHelper.startPage(pageNum, pageSize);

		TbGoodsExample example = new TbGoodsExample();
		Criteria criteria = example.createCriteria();
		criteria.andIsDeleteIsNull();// 指定逻辑条件为未删除
		criteria.andIsMarketableEqualTo("1");// 指定上架状态
		if (goods != null) {
			if (goods.getSellerId() != null && goods.getSellerId().length() > 0) {
//				criteria.andSellerIdLike("%" + goods.getSellerId() + "%");
				criteria.andSellerIdEqualTo(goods.getSellerId());
			}
			if (goods.getGoodsName() != null && goods.getGoodsName().length() > 0) {
				criteria.andGoodsNameLike("%" + goods.getGoodsName() + "%");
			}
			if (goods.getAuditStatus() != null && goods.getAuditStatus().length() > 0) {
				criteria.andAuditStatusLike("%" + goods.getAuditStatus() + "%");
			}
			if (goods.getIsMarketable() != null && goods.getIsMarketable().length() > 0) {
				criteria.andIsMarketableLike("%" + goods.getIsMarketable() + "%");
			}
			if (goods.getCaption() != null && goods.getCaption().length() > 0) {
				criteria.andCaptionLike("%" + goods.getCaption() + "%");
			}
			if (goods.getSmallPic() != null && goods.getSmallPic().length() > 0) {
				criteria.andSmallPicLike("%" + goods.getSmallPic() + "%");
			}
			if (goods.getIsEnableSpec() != null && goods.getIsEnableSpec().length() > 0) {
				criteria.andIsEnableSpecLike("%" + goods.getIsEnableSpec() + "%");
			}
			if (goods.getIsDelete() != null && goods.getIsDelete().length() > 0) {
				criteria.andIsDeleteLike("%" + goods.getIsDelete() + "%");
			}

		}

		Page<TbGoods> page = (Page<TbGoods>) goodsMapper.selectByExample(example);
		return new PageResult(page.getTotal(), page.getResult());
	}

	@Override
	public void updateStauts(Long[] ids, String status) {

		for (Long id : ids) {
			TbGoods tbGoods = goodsMapper.selectByPrimaryKey(id);
			tbGoods.setAuditStatus(status);
			goodsMapper.updateByPrimaryKey(tbGoods);
		}
	}

	@Override
	public void updateMarketable(Long id, String isMarketable) {
		TbGoods tbGoods = goodsMapper.selectByPrimaryKey(id);
		tbGoods.setIsMarketable(isMarketable);
		goodsMapper.updateByPrimaryKey(tbGoods);

	}

	@Override
	public List<TbItem> findItemListByGoodsIdListAndStatus(Long[] goodsIds, String status) {

		return itemMapper.findItemListByGoodsIdAndStatus(goodsIds, status);
	}
}
