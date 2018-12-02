package com.pinyougou.cart.service;

import java.util.List;

import com.pinyougou.pojogroup.Cart;

/**
 * 购物车服务接口
 * 
 * @author hbbxl
 *
 */
public interface CartService {

	/**
	 * 添加商品到购物车列表
	 * 
	 * @param list
	 * @param itemId
	 * @param num
	 * @return
	 */
	public List<Cart> addGoodsToCartList(List<Cart> list, Long itemId, Integer num);

	/**
	 * 使用username获取购物车列表，从redis中
	 * 
	 * @param username
	 * @return
	 */
	public List<Cart> findCartListFromRedis(String username);

	/**
	 * 将购物车列表存入redis中，使用username作为key
	 * 
	 * @param username
	 * @param cartList
	 */
	public void saveCartListToRedis(String username, List<Cart> cartList);

	/**
	 * 合并购物车
	 * 
	 * @param cartList1
	 * @param cartList2
	 * @return
	 */
	public List<Cart> mergeCartList(List<Cart> cartList1, List<Cart> cartList2);
}
