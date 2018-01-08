package cn.itcast.core.bean;

import java.util.ArrayList;
import java.util.List;

import org.codehaus.jackson.annotate.JsonIgnore;

/**
 * 购物车
 * @author 许刚
 *
 */



public class BuyCart {
	//购物项集合
	private List<BuyItem> items = new ArrayList<>();
	//继续购买 保留最新一款
	private Integer productId;
	public void setProductId(Integer productId) {
		this.productId = productId;
	}
	//添加方法
	@JsonIgnore
	public void addItem(BuyItem item) {
		//判断是否重复
		if(items.contains(item)) {
			for(BuyItem it:items) {
				if(it.equals(item)) {
					//判断是否超过购买限制，重写equals（）
					int result = it.getAmount() + item.getAmount();
					if(it.getSku().getSkuUpperLimit()>=result) {
						it.setAmount(result);
					}else {
						it.setAmount(it.getSku().getSkuUpperLimit());
					}
				}
			}
		}else {
			
			items.add(item);
		}
	}
	//删除一项
	public void delItem(BuyItem item) {
		items.remove(item);
	}
	
	
	//小计
	//商品数量
	//json忽略标签，因为ObjectMapper要求对象为标准javabean对象。setter、getter方法有声明，ObjectMapper不会转次标签下的
	@JsonIgnore
	public int getProductAmount() {
		int result = 0;
		for(BuyItem item:items) {
			result += item.getAmount();
		}
		return result;
		
	}
	//商品金额
	@JsonIgnore
	public Double getProductPrice() {
		Double result = 0.00;
		for(BuyItem item:items) {
			result += item.getSku().getSkuPrice()*item.getAmount();
		}
		return result;
	}
	//运费
	@JsonIgnore
	public Double getFee() {
		Double result = 0.00;
		if (getProductPrice()<=39) {
				result = 10.00;
			}
		return result;
	}
	//应付金额
	@JsonIgnore
	public Double getTotalPrice() {
		
		return getProductPrice() +getFee();
	}
	public List<BuyItem> getItems() {
		return items;
	}
	//小计
	
	
}
