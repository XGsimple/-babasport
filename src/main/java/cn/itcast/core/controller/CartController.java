package cn.itcast.core.controller;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.model;

import java.io.IOException;
import java.io.Serializable;
import java.io.StringWriter;
import java.util.List;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.codehaus.jackson.JsonGenerationException;
import org.codehaus.jackson.JsonParseException;
import org.codehaus.jackson.map.JsonMappingException;
import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.map.annotate.JsonSerialize.Inclusion;
import org.springframework.beans.factory.annotation.Autowired;
/**
 * 购物车
 * 
 */
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;

import cn.itcast.common.web.session.SessionProvider;
import cn.itcast.core.bean.BuyCart;
import cn.itcast.core.bean.BuyItem;
import cn.itcast.core.bean.product.Sku;
import cn.itcast.core.bean.user.Addr;
import cn.itcast.core.bean.user.Buyer;
import cn.itcast.core.query.user.AddrQuery;
import cn.itcast.core.service.product.SkuService;
import cn.itcast.core.service.user.AddrService;
import cn.itcast.core.web.Constants;

@Controller
public class CartController {
	@Autowired
	private SkuService skuService;
	@Autowired
	private AddrService addrService;
	@Autowired
	private SessionProvider sessionProvider;
	//购买按钮
	@RequestMapping(value = "/shopping/buyCart.shtml")
	public String buyCart(Integer skuId,Integer amount,Integer buyLimit,Integer productId,HttpServletRequest request,HttpServletResponse response,ModelMap model) {
			
			// springmvc  对象与json互转
			ObjectMapper om = new ObjectMapper();
			om.setSerializationInclusion(Inclusion.NON_NULL);
			//声明
			BuyCart buyCart = null;
			//*****判断cookie是否保存有购物车，如果有，就从cookie总恢复，不再新建  ******
			Cookie[] cookies = request.getCookies();
			if(null != cookies&&cookies.length>0) {
				for(Cookie c:cookies) {
					if(Constants.BUYCART_COOKIE.equals(c.getName())) {
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
			//如果cookie中没有购物车，实例化一个
			if(null == buyCart) {
				buyCart = new BuyCart();
			}
			//必定存在一个buyCart对象，判断skuId，添加新购物项
			if(null != skuId) {
			Sku sku = new Sku();
			sku.setId(skuId);
			if(null!=buyLimit) {
				sku.setSkuUpperLimit(buyLimit);
			}
			//创建购物项
			BuyItem buyItem = new BuyItem();
			buyItem.setSku(sku);
			//数量
			buyItem.setAmount(amount);
			//添加新购物项,并判断是否重复，重复则只增加数量
			buyCart.addItem(buyItem);
			//添加最新一款的商品id,更新后的购物车
			if(null != productId) {
				buyCart.setProductId(productId);
			}
	
			//流
			StringWriter writer = new StringWriter();
			//将更新后的购物车写json  写的过程，json是字符串流
			try {
				om.writeValue(writer, buyCart);
			} catch (Exception e) {
				e.printStackTrace();
			} 
			//新购物车保存在cookie，对象转为json，保留skuid，具体参数等要用时再通过skuid查找数据库
			Cookie cookie = new Cookie(Constants.BUYCART_COOKIE, writer.toString());
			//关闭浏览器，也要有cookie
			//默认为-1  关闭浏览器就消失
			//消灭 0 cookie立刻失效   
			//保存在硬盘 一天 单位秒
			cookie.setMaxAge(60*60*24);
			//路径 默认为/shoppign 能访问
			//设置为可在同一应用服务器内共享方法
			cookie.setPath("/");
			//发送前台
			response.addCookie(cookie);
		}
			//读取购物车中的所有购物项，准备传递展示
			List<BuyItem> items = buyCart.getItems();
			for(BuyItem item:items) {
				//查找数据库
				Sku skuByKey = skuService.getSkuByKey(item.getSku().getId());
				item.setSku(skuByKey);
			}
			//传递到购物车页面
			model.addAttribute("buyCart", buyCart);
		return "product/cart";
	}
	//清空购物车
	@RequestMapping(value = "/shopping/clearCart.shtml")
	public String clearCart(HttpServletResponse response) {
		//清空购物车
		Cookie cookie = new Cookie(Constants.BUYCART_COOKIE, null);
		cookie.setMaxAge(0);
		cookie.setPath("/");
		response.addCookie(cookie);
		return "redirect:" + "/shopping/buyCart.shtml";
	}
	//单删除一项
	@RequestMapping(value = "/shopping/delItem.shtml")
	public String delItem(HttpServletRequest request,HttpServletResponse response,Integer skuId) {
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
		if (null != buyCart) {
			Sku sku = new Sku();
			sku.setId(skuId);
			// 创建购物项
			BuyItem buyItem = new BuyItem();
			buyItem.setSku(sku);
			buyCart.delItem(buyItem);
			// 流
			StringWriter writer = new StringWriter();
			// 将更新后的购物车写json 写的过程，json是字符串流
			try {
				om.writeValue(writer, buyCart);
			} catch (Exception e) {
				e.printStackTrace();
			}
			// 新购物车保存在cookie，对象转为json，保留skuid，具体参数等要用时再通过skuid查找数据库
			Cookie cookie = new Cookie(Constants.BUYCART_COOKIE, writer.toString());
			// 关闭浏览器，也要有cookie
			// 默认为-1 关闭浏览器就消失
			// 消灭 0 cookie立刻失效
			// 保存在硬盘 一天 单位秒
			cookie.setMaxAge(60 * 60 * 24);
			// 路径 默认为/shoppign 能访问
			// 设置为可在同一应用服务器内共享方法
			cookie.setPath("/");
			// 发送前台
			response.addCookie(cookie);
		}
		
		return "redirect:" + "/shopping/buyCart.shtml";
	}
	
	//结算
	@RequestMapping(value = "/buyer/trueBuy.shtml")
	public String trueBuy(HttpServletRequest request,HttpServletResponse response,ModelMap model) {
		//从cookie 中取回购物车
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
		//判断购物车是否存在、是否有商品
		if(null != buyCart) {
			//判断购物车商品是否有库存
			List<BuyItem> items = buyCart.getItems();
			if(null!=items&&items.size()>0) {
				//购物车商品项数目
				Integer iBefore = items.size();
				//清除失效商品
				for(BuyItem item:items) {
					Sku sku = skuService.getSkuByKey(item.getSku().getId());
					//判断库存是否足够
					if(sku.getStockInventory()<item.getAmount()) {
						//清理购物车，删除此商品
						buyCart.delItem(item);
					}
				}
				//清理后商品项数目
				Integer iAfter = items.size();
				//判断清理前后
				if (iBefore>iAfter) {
					//修改cookie 中购物车数据
					//购物车写回cookie
					// 流  
					StringWriter writer = new StringWriter();
					// 将更新后的购物车写json 写的过程，json是字符串流
					try {
						om.writeValue(writer, buyCart);
					} catch (Exception e) {
						e.printStackTrace();
					}
					// 新购物车保存在cookie，对象转为json，保留skuid，具体参数等要用时再通过skuid查找数据库
					Cookie cookie = new Cookie(Constants.BUYCART_COOKIE, writer.toString());
					// 关闭浏览器，也要有cookie
					// 默认为-1 关闭浏览器就消失
					// 消灭 0 cookie立刻失效
					// 保存在硬盘 一天 单位秒
					cookie.setMaxAge(60 * 60 * 24);
					// 路径 默认为/shoppign 能访问
					// 设置为可在同一应用服务器内共享方法
					cookie.setPath("/");
					// 发送前台
					response.addCookie(cookie);
					return "redirect:/shopping/buyCart.shtml";
				}else {
					//收货地址加载
					Buyer buyer = (Buyer)sessionProvider.getAttribute(request, Constants.BUYER_SESSION);
					AddrQuery addrQuery = new AddrQuery();
					addrQuery.setBuyerId(buyer.getUsername());
					//默认地址为1
					addrQuery.setIsDef(1);
					List<Addr> addrs = addrService.getAddrList(addrQuery);
					model.addAttribute("addr", addrs.get(0));
					
					//读取购物车中的所有购物项，准备传递展示
					List<BuyItem> its = buyCart.getItems();
					for(BuyItem item:its) {
						//查找数据库
						Sku skuByKey = skuService.getSkuByKey(item.getSku().getId());
						item.setSku(skuByKey);
					}
					//传递到购物车页面
					model.addAttribute("buyCart", buyCart);
					//正常
					return "product/productOrder";
				}
				
			}
		}else {
			//购物车为空
			return "redirect:/shopping/buyCart.shtml";
		}
		//正常 回到订单确认页面
		return "product/productOrder";
	}

}
