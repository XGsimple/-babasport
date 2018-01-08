package cn.itcast.core.controller;
import java.util.List;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.map.annotate.JsonSerialize.Inclusion;
import org.springframework.beans.factory.annotation.Autowired;
/**
 * 提交订单 前台
 */
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import cn.itcast.common.web.session.SessionProvider;
import cn.itcast.core.bean.BuyCart;
import cn.itcast.core.bean.BuyItem;
import cn.itcast.core.bean.order.Order;
import cn.itcast.core.bean.product.Sku;
import cn.itcast.core.bean.user.Buyer;
import cn.itcast.core.service.order.OrderService;
import cn.itcast.core.service.product.SkuService;
import cn.itcast.core.web.Constants;

@Controller
public class FrontOrderController {
	@Autowired
	private OrderService orderService;
	@Autowired
	private SessionProvider sessionProvider;
	@Autowired
	private SkuService skuService;
	//提交订单
	@RequestMapping(value ="/buyer/confirmOrder.shtml")
	public String confirmOrder(Order order,HttpServletRequest request,HttpServletResponse response) {
		//1:接受前台传来的4个参数
	
		// 从cookie 中取回购物车
		// springmvc 对象与json互转
		ObjectMapper om = new ObjectMapper();
		om.setSerializationInclusion(Inclusion.NON_NULL);
		// 声明
		BuyCart buyCart = null;
		// *****判断cookie是否保存有购物车，如果有，就从cookie总恢复，不再新建 ******
		Cookie[] cookies = request.getCookies();
		if (null != cookies && cookies.length > 0) {
			for (Cookie c : cookies) {
				if (Constants.BUYCART_COOKIE.equals(c.getName())) {
					String value = c.getValue();
					try {
						buyCart = om.readValue(value, BuyCart.class);
					} catch (Exception e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					break;
				}
			}
		}
		
		//读取购物车中的所有购物项,根据skuId 查找SKU
		List<BuyItem> its = buyCart.getItems();
		for(BuyItem item:its) {
			//查找数据库
			Sku skuByKey = skuService.getSkuByKey(item.getSku().getId());
			item.setSku(skuByKey);
		}
		
		Buyer buyer = (Buyer)sessionProvider.getAttribute(request, Constants.BUYER_SESSION);
		//用户id
		order.setBuyerId(buyer.getUsername());
		//***********************保存订单  订单详情 两张表******************************************
		orderService.addOrder(order,buyCart);
		//清空购物车
		Cookie cookie = new Cookie(Constants.BUYER_SESSION, null);
		cookie.setMaxAge(0);
		// 路径 默认为/shoppign 能访问
		// 设置为可在同一应用服务器内共享方法
		cookie.setPath("/");
		// 发送前台
		response.addCookie(cookie);
		
		return "product/confirmOrder";
	}
}
