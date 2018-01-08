package cn.itcast.core.controller.admin;
/**
 * 后台商品管理
 * 商品列表
 * 商品添加
 * 商品上架
 * @author 许刚
 *
 */

import static org.hamcrest.CoreMatchers.instanceOf;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;

import cn.itcast.common.page.Pagination;
import cn.itcast.core.bean.product.Brand;
import cn.itcast.core.bean.product.Color;
import cn.itcast.core.bean.product.Feature;
import cn.itcast.core.bean.product.Img;
import cn.itcast.core.bean.product.Product;
import cn.itcast.core.bean.product.Sku;
import cn.itcast.core.bean.product.Type;
import cn.itcast.core.query.product.BrandQuery;
import cn.itcast.core.query.product.ColorQuery;
import cn.itcast.core.query.product.FeatureQuery;
import cn.itcast.core.query.product.ProductQuery;
import cn.itcast.core.query.product.TypeQuery;
import cn.itcast.core.service.product.BrandService;
import cn.itcast.core.service.product.ColorService;
import cn.itcast.core.service.product.FeatureService;
import cn.itcast.core.service.product.ProductService;
import cn.itcast.core.service.product.SkuService;
import cn.itcast.core.service.product.TypeService;
import cn.itcast.core.service.staticpage.StaticPageService;
import freemarker.core.ReturnInstruction.Return;

@Controller 
public class ProductController {
	@Autowired
	private BrandService brandService;
	@Autowired
	private ProductService productService;
	@Autowired
	private TypeService typeService;
	@Autowired
	private FeatureService featureService;
	@Autowired
	private ColorService colorService;
	//商品列表
	@RequestMapping(value = "/product/list.do")
	public String list(Integer pageNo,String name,Integer brandId,Integer isShow,ModelMap model) {
		//品牌条件对象
		BrandQuery brandQuery = new BrandQuery();
		brandQuery.setFields("id,name");
		brandQuery.setIsDisplay(1);
		//加载品牌
		List<Brand> brands = brandService.getBrandList(brandQuery );
		model.addAttribute("brands",brands);
		//分页参数
		StringBuilder paramas = new StringBuilder();
		
		
		
		
		ProductQuery productQuery = new ProductQuery(); 
		//判断条件是否为null
		if(StringUtils.isNotBlank(name)) {
			productQuery.setName(name);
			productQuery.setNameLike(true);
			paramas.append("&name=").append(name);
			
			//回显查询条件
			model.addAttribute("name",name);
		}
		//判断品牌ID
		if(null != brandId) {
			productQuery.setBrandId(brandId);
			paramas.append("&").append("brandId=").append(brandId);
			//回显查询条件
			model.addAttribute("brandId",brandId);
		}
		//判断上下架状态
		if(null != isShow) {
			productQuery.setIsShow(isShow);
			paramas.append("&").append("isShow=").append(isShow);
			model.addAttribute("isShow",isShow);
		}else {
			//默认状态
			productQuery.setIsShow(0);
			paramas.append("&").append("isShow=").append(0);
			model.addAttribute("isShow",0);
		}
		//设置页号
		productQuery.setPageNo(Pagination.cpn(pageNo));
		productQuery.setPageSize(5);
		//按照id倒排
		productQuery.orderbyId(false);
		//加载带有分页的商品
		Pagination pagination = productService.getProductListWithPage(productQuery);
		//分页页面展示
		String url = "/product/list.do";
		pagination.pageView(url, paramas.toString());
		model.addAttribute("pagination",pagination);
		return "/product/list";
	}
	//去添加页面
	@RequestMapping(value = "/product/toAdd.do")
	public String toAdd(ModelMap model) {
		//加载商品类型
		TypeQuery typeQuery = new TypeQuery();
		typeQuery.setFields("id,name");
		typeQuery.setIsDisplay(1);
		//typeQuery.setParentId(0);
		List<Type> types = typeService.getTypeList(typeQuery);
		model.addAttribute("types",types);
		//加载商品品牌
		BrandQuery brandQuery = new BrandQuery();
		brandQuery.setFields("id,name");
		brandQuery.setIsDisplay(1);
		//加载品牌
		List<Brand> brands = brandService.getBrandList(brandQuery );
		model.addAttribute("brands",brands);
		
		//加载商品属性
		FeatureQuery featureQuery = new FeatureQuery();
		List<Feature> features = featureService.getFeatureList(featureQuery);
		model.addAttribute("features",features);
		
		//加载颜色大全
		ColorQuery colorQuery = new ColorQuery();
		List<Color> colors = colorService.getColorList(colorQuery);
		model.addAttribute("colors",colors);
		return "product/add";
		
	}
	
	//商品添加
	@RequestMapping(value = "/product/add.do")
	public String add(Product product,Img img) {
		//1 商品表 图片表  最小单元表
		product.setImg(img);
		productService.addProduct(product);
		return "redirect:/product/list.do";
	}
	//上架
	@RequestMapping(value = "/product/isShow.do")
	public String isShow(Integer[] ids,Integer pageNo,String name,Integer brandId,Integer isShow,ModelMap model) {
		Product product = new Product();
		product.setIsShow(1);
		
		//上架
		if(null != ids&&ids.length>0) {
			for(Integer id:ids) {
				product.setId(id);
				//修改上架状态
				productService.updateProductByKey(product);
				
				
				//静态化
				Map<String, Object> root = new HashMap<>();
				//设置值
				//商品加载
				Product p = productService.getProductByKey(id);
				root.put("product", p);
				
				List<Sku> skus = skuService.getStock(id);
				root.put("skus", skus);
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
				root.put("colors", colors);
				staticPageService.productIndex(root, id);
			}
		}
		//判断
		if(null != pageNo) {
			model.addAttribute("pageNo", pageNo);
		}
		if(StringUtils.isNotBlank(name)) {
			model.addAttribute("name", name);
		}
		if(null != brandId) {
			model.addAttribute("brandId", brandId);
		}
		if(null != isShow) {
			model.addAttribute("isShow", isShow);
		}
		return "redirect:/product/list.do";
	}
	
	@Autowired
	private StaticPageService staticPageService;
	@Autowired
	private SkuService skuService;
	
	
	 
}
