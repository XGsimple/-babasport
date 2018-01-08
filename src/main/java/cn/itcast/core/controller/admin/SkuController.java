package cn.itcast.core.controller.admin;
import java.util.List;

import javax.servlet.http.HttpServletResponse;

import org.codehaus.jackson.map.util.JSONPObject;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
/**
 * 库存管理
 * 修改库存
 */
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;

import cn.itcast.common.web.ResponseUtils;
import cn.itcast.core.bean.product.Sku;
import cn.itcast.core.query.product.SkuQuery;
import cn.itcast.core.service.product.SkuService;

@Controller
public class SkuController {
	@Autowired
	private SkuService skuService;
	//跳转到库存管理页面
	@RequestMapping(value = "/sku/list.do")
	public String list(Integer productId,String pno,ModelMap model) {
		//商品编号回显
		model.addAttribute("pno",pno);
		//最小销售单元 通过商品id
		SkuQuery skuQuery = new SkuQuery();
		skuQuery.setProductId(productId);
		List<Sku> skus = skuService.getSkuList(skuQuery);
		model.addAttribute("skus", skus);
		return "sku/list";
	}
	//保存修改
	@RequestMapping(value = "/sku/add.do")
	public void add(Sku sku,HttpServletResponse response ) {
		//修改
		skuService.updateSkuByKey(sku);
		//回写
		JSONObject jO = new JSONObject();
		jO.put("message", "保存成功");
		ResponseUtils.renderJson(response, jO.toString());
	}
	
}
