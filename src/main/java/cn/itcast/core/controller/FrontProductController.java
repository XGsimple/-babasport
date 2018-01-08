package cn.itcast.core.controller;

import static org.hamcrest.CoreMatchers.nullValue;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import javax.jws.WebParam.Mode;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;

import cn.itcast.common.page.Pagination;
import cn.itcast.common.web.session.SessionProvider;
import cn.itcast.core.bean.product.Brand;
import cn.itcast.core.bean.product.Color;
import cn.itcast.core.bean.product.Feature;
import cn.itcast.core.bean.product.Product;
import cn.itcast.core.bean.product.Sku;
import cn.itcast.core.bean.product.Type;
import cn.itcast.core.query.product.BrandQuery;
import cn.itcast.core.query.product.FeatureQuery;
import cn.itcast.core.query.product.ProductQuery;
import cn.itcast.core.query.product.SkuQuery;
import cn.itcast.core.query.product.TypeQuery;
import cn.itcast.core.service.product.BrandService;
import cn.itcast.core.service.product.ColorService;
import cn.itcast.core.service.product.FeatureService;
import cn.itcast.core.service.product.ProductService;
import cn.itcast.core.service.product.SkuService;
import cn.itcast.core.service.product.TypeService;

/**
 * 前台商品列表
 */
@Controller
public class FrontProductController {
	@Autowired
	private BrandService brandService;
	@Autowired
	private ProductService productService;
	@Autowired
	private TypeService typeService;
	@Autowired
	private FeatureService featureService;
	@Autowired
	private SkuService skuService;
	@Autowired
	private ColorService colorService;
	@Autowired
	private SessionProvider sessionProvider;
	//商品列表页面
	@RequestMapping(value = "/product/display/list.shtml")
	public String list(Integer pageNo,Integer brandId,String brandName,Integer typeId,String typeName, ModelMap model) {
		
		//参数
		StringBuilder params = new StringBuilder();
		// 加载商品属性
		FeatureQuery featureQuery = new FeatureQuery();
		List<Feature> features = featureService.getFeatureList(featureQuery); 
		model.addAttribute("features", features);
		
		//设置页号
		ProductQuery productQuery= new ProductQuery(); 
		productQuery.setPageNo(Pagination.cpn(pageNo));
		//设置每页数
		productQuery.setPageSize(Product.FRONT_PAGE_SIZE);
		//上架
		productQuery.setIsShow(1);
		//设置按照id倒排
		productQuery.orderbyId(false);
		//条件 TODO
		//隐藏已选条件
		boolean flag = false;
		//条件map窗口
		Map<String, String> query = new LinkedHashMap<String,String >();
		//品牌id
		if(null!=brandId) {
			productQuery.setBrandId(brandId);
			flag = true;
			//显示在页面
			model.addAttribute("brandId", brandId);
			model.addAttribute("brandName", brandName);
			query.put("品牌", brandName);
			params.append("&").append("brandId=").append(brandId);
			params.append("&").append("brandName=").append(brandName);
		}else {
			// 加载商品品牌
			BrandQuery brandQuery = new BrandQuery();
			brandQuery.setFields("id,name");
			brandQuery.setIsDisplay(1);
			// 加载品牌
			List<Brand> brands = brandService.getBrandList(brandQuery);
			model.addAttribute("brands", brands);
		}
		//类型id
		if(null!=typeId) {
			productQuery.setTypeId(typeId);
			flag = true;
			model.addAttribute("typeId", typeId);
			model.addAttribute("typeName", typeName);
			query.put("类型", typeName);
			params.append("&").append("typeId=").append(typeId);
			params.append("&").append("typeName=").append(typeName);
		}else {
			// 加载商品类型
			TypeQuery typeQuery = new TypeQuery();
			typeQuery.setFields("id,name");
			typeQuery.setIsDisplay(1);
			// typeQuery.setParentId(0);
			List<Type> types = typeService.getTypeList(typeQuery);
			model.addAttribute("types", types);
		}
		
		model.addAttribute("flag",flag);
		//条件
		model.addAttribute("query",query);
		
		// 加载带有分页的商品
		Pagination pagination = productService.getProductListWithPage(productQuery);

		// 分页页面展示
		String url = "/product/display/list.shtml";
		pagination.pageView(url, params.toString());
		model.addAttribute("pagination", pagination);
		return "/product/product";
	}
	//跳转商品详情页面
	@RequestMapping(value = "/product/detail.shtml")
	public String detail(Integer id,ModelMap model) {
		//商品加载
		Product product = productService.getProductByKey(id);
		model.addAttribute("product", product);
		
		List<Sku> skus = skuService.getStock(id);
		model.addAttribute("skus", skus);
		//去颜色名重复
		List<Color> colors = new ArrayList<>();
		//遍历sku
		for(Sku sku:skus) {
			//判断集合是否已经有相同颜色对象
			Color color = sku.getColor();
			if(!colors.contains(color)) {
				colors.add(color);
			}
		}
		model.addAttribute("colors", colors);
		return "product/productDetail";
	}
	
}
