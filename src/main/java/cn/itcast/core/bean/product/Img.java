package cn.itcast.core.bean.product;

import java.io.Serializable;

import org.aspectj.apache.bcel.classfile.Constant;

import cn.itcast.core.web.Constants;

/**
 * 图片
 * @author lixu
 * @Date [2014-3-28 下午04:38:53]
 */
public class Img implements Serializable {
	/**
	 * 序列化ID
	 */
	private static final long serialVersionUID = 1L;

	private Integer id;
	private Integer productId;
	private String url;
	private String allUrl;
	private Integer isDef;
	

	//获取全url
	public String getAllUrl() {
		return   "http://localhost:8088/image-web/" + url;
		//return Constants.IMAGE_URL + url;
	}
	public void setAllUrl(String allUrl) {
		this.allUrl = allUrl;
	}
	public Integer getId() {
		return id;
	}
	public void setId(Integer id) {
		this.id = id;
	}
	public Integer getProductId() {
		return productId;
	}
	public void setProductId(Integer productId) {
		this.productId = productId;
	}
	public String getUrl() {
		return url;
	}
	public void setUrl(String url) {
		this.url = url;
	}
	public Integer getIsDef() {
		return isDef;
	}
	public void setIsDef(Integer isDef) {
		this.isDef = isDef;
	}
	public String toString() {
		return "Img [id=" + id + ",productId=" + productId + ",url=" + url + ",isDef=" + isDef + "]";
	}
}
