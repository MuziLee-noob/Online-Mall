package com.pinyougou.cart.controller;

import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.alibaba.dubbo.config.annotation.Reference;
import com.alibaba.fastjson.JSON;
import com.pinyougou.cart.service.CartService;
import com.pinyougou.pojogroup.Cart;

import entity.Result;
import util.CookieUtil;

@RestController
@RequestMapping("/cart")
public class CartController {

	@Reference(timeout = 5000)
	private CartService cartService;

	@Autowired
	private HttpServletRequest request;

	@Autowired
	private HttpServletResponse response;

	@RequestMapping("/addGoodsToCartList")
	@CrossOrigin(origins = "http://localhost:9105", allowCredentials = "true")
	public Result addGoodsToCartList(Long itemId, Integer num) {

		response.setHeader("Access-Control-Allow-Origin", "http://localhost:9105");
		response.setHeader("Access-Control-Allow-Credentials", "true");

		String name = SecurityContextHolder.getContext().getAuthentication().getName();
		System.out.println(name);
		try {
			// 1.从cookie中提取购物车
			List<Cart> cartList = findCartList();
			// 2.调用服务方法操作购物车
			cartList = cartService.addGoodsToCartList(cartList, itemId, num);
			if (name.equals("anonymousUser")) {
				// 3.将新购物车存入cookie

				String cartList_string = JSON.toJSONString(cartList);
				CookieUtil.setCookie(request, response, "cartList", cartList_string, 3600 * 24, "UTF-8");
				System.out.println("向cookie中保存购物车");
			} else {
				cartService.saveCartListToRedis(name, cartList);
			}

			return new Result(true, "存入购物车成功");
		} catch (Exception e) {
			e.printStackTrace();
			return new Result(false, "存入购物车失败");
		}

	}

	/**
	 * 从cookie中提取购物车
	 * 
	 * @return
	 */
	@RequestMapping("/findCartList")
	public List<Cart> findCartList() {

		String username = SecurityContextHolder.getContext().getAuthentication().getName();
		System.out.println(username);
		String cartListString = CookieUtil.getCookieValue(request, "cartList", "UTF-8");
		if (cartListString == null || cartListString.equals("") || cartListString.equals("null")) {
			cartListString = "[]";
		}
		List<Cart> cartList_cookie = JSON.parseArray(cartListString, Cart.class);
		if (username.equals("anonymousUser")) {
			System.out.println("从cooklie中提取购物车");

			return cartList_cookie;
		} else {
			List<Cart> cartList_redis = cartService.findCartListFromRedis(username);
			// 如果本地购物车有数据
			if (cartList_cookie.size() > 0) {
				System.out.println("执行合并购物车");
				List<Cart> cartList = cartService.mergeCartList(cartList_cookie, cartList_redis);// 得到合并后的购物车
				cartService.saveCartListToRedis(username, cartList);// 将合并后的购物车存入redis
				CookieUtil.deleteCookie(request, response, "cartList");
				return cartList;
			}
			return cartList_redis;
		}
	}

}
