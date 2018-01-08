package cn.itcast;

import java.util.List;

import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import cn.itcast.common.junit.SpringJunitTest;
import cn.itcast.core.bean.product.Brand;
import cn.itcast.core.query.product.BrandQuery;
import cn.itcast.core.service.product.BrandService;

/**
 * 测试
 * @author lx
 *
 */

public class TestBrand extends SpringJunitTest{

	@Autowired
	private BrandService brandService;
	@Test
	public void testGet() throws Exception {
		BrandQuery brandQuery = new BrandQuery();
		//brandQuery.setFields("id");  //设置要查询的列
	/*	brandQuery.setNameLike(true);
		brandQuery.setName("皮");*/
		/*brandQuery.orderById(true);
		brandQuery.setPageNo(2);
		brandQuery.setPageSize(3);*/
		List<Brand> brands = brandService.getBrandList(brandQuery);
		for(Brand brand:brands) {
			System.out.println(brand);
		}
	}
}
