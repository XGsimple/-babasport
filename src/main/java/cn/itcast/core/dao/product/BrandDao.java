package cn.itcast.core.dao.product;

import java.util.List;

import cn.itcast.core.bean.product.Brand;
import cn.itcast.core.query.product.BrandQuery;

/**
 * 品牌
 * @author lx
 *
 */
public interface BrandDao {
	//List集合
	public List<Brand> getBrandListWithPage(Brand brand);
	
	//查询集合
	public List<Brand> getBrandList(BrandQuery brandQuery);
	//查询总记录数
	public int getBrandCount(Brand brand);
	//添加品牌
	public void addBrand(Brand brand);
	//删除
	public void deleteBrandByKey(Integer id);
	
	//批量删除
	public void deleteBrandByKeys(Integer[] ids);
	//修改
	public void updateBrandByKey(Brand brand);
	//通过id查询品牌
	public Brand getBrandById(Integer id);

}
