package cn.itcast.core.controller.admin;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
/**
 * 订单列表
 * 订单查看
 */
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;

import cn.itcast.core.bean.order.Detail;
import cn.itcast.core.bean.order.Order;
import cn.itcast.core.bean.user.Addr;
import cn.itcast.core.query.order.DetailQuery;
import cn.itcast.core.query.order.OrderQuery;
import cn.itcast.core.query.user.AddrQuery;
import cn.itcast.core.service.order.DetailService;
import cn.itcast.core.service.order.OrderService;
import cn.itcast.core.service.user.AddrService;

@Controller
public class OrderController {
	@Autowired
	private OrderService orderService;
	@Autowired
	private DetailService detailService;
	@Autowired
	private AddrService addrService;
	
	//订单列表
	@RequestMapping(value = "/order/list.do")
	/**
	 * @param isPaiy 支付状态
	 * @param state 订单状态
	 * @return
	 */
	public String list(Integer isPaiy,Integer state,ModelMap model) {
		OrderQuery orderQuery = new OrderQuery();
		//支付状态
		if(null !=isPaiy) {
			orderQuery.setIsPaiy(isPaiy);
		}
		//订单状态
		if(null !=state) {
			orderQuery.setIsPaiy(state);
		}
		List<Order> orders = orderService.getOrderList(orderQuery);
		model.addAttribute("orders", orders);
		return "order/list";
		
	}
	
	/**
	 * 订单查看
	 * @param id  订单id
	 * @return
	 */
	@RequestMapping(value = "/order/view.do")
	public String view(Integer id,ModelMap model) {
		//查询订单主表
		Order order = orderService.getOrderByKey(id);
		//查询订单详情表
		DetailQuery detailQuery = new DetailQuery();
		detailQuery.setOrderId(id);
		List<Detail> details = detailService.getDetailList(detailQuery);
		
		//收货地址
		AddrQuery addrQuery = new  AddrQuery();
		addrQuery.setBuyerId(order.getBuyerId());
		addrQuery.setIsDef(1);
		List<Addr> addrs = addrService.getAddrList(addrQuery );
		model.addAttribute("order", order);
		model.addAttribute("details", details);
		model.addAttribute("addr", addrs.get(0));
		return "order/view";
	}
}
