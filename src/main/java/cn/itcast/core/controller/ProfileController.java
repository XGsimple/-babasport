package cn.itcast.core.controller;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang3.StringUtils;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
/**
 * 跳转登录页面
 * 登录
 * 个人中心
 * 收货地址
 */
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import com.octo.captcha.service.image.ImageCaptchaService;

import cn.itcast.common.encode.Md5Pwd;
import cn.itcast.common.web.ResponseUtils;
import cn.itcast.common.web.session.SessionProvider;
import cn.itcast.core.bean.country.City;
import cn.itcast.core.bean.country.Province;
import cn.itcast.core.bean.country.Town;
import cn.itcast.core.bean.user.Buyer;
import cn.itcast.core.query.country.CityQuery;
import cn.itcast.core.query.country.TownQuery;
import cn.itcast.core.service.country.CityService;
import cn.itcast.core.service.country.ProvinceService;
import cn.itcast.core.service.country.TownService;
import cn.itcast.core.service.user.BuyerService;
import cn.itcast.core.web.Constants;

@Controller
public class ProfileController {
	@Autowired
	private SessionProvider sessionProvider;
	@Autowired
	private Md5Pwd md5Pwd;
	@Autowired
	private BuyerService buyerService;
	@Autowired
	private ImageCaptchaService imageCaptchaService;
	
	@RequestMapping(value = "/shopping/login.shtml",method=RequestMethod.GET)
	public String login() {
		
		return "buyer/login";
	}
	
	//登陆后提交，登录表格的action="/shopping/login.shtml" method="post"  ，returnUrl 由<input type="hidden"传递
	@RequestMapping(value = "/shopping/login.shtml", method = RequestMethod.POST)
	public String login(Buyer buyer, String captcha, String returnUrl, HttpServletRequest request, ModelMap model) {
		if (null != buyer) {
			// 验证 验证码是否正确，
			// 参数1：JESESSIONID
			// 参数2：验证码
			if (imageCaptchaService.validateResponseForID(sessionProvider.getSessionID(request), captcha)) {
				// 判断用户名是否为null 或 空串
				if (null != buyer.getUsername() && StringUtils.isNotBlank(buyer.getUsername())) {
					//
					if (null != buyer.getPassword() && StringUtils.isNotBlank(buyer.getPassword())) {
						Buyer b = buyerService.getBuyerByKey(buyer.getUsername());
						if (null != b) {
							if (md5Pwd.encode(buyer.getPassword()).equals(b.getPassword())) {
								// 设置到本地Session中
								sessionProvider.setAttribute(request, Constants.BUYER_SESSION, b);
								// request.getS
								// 验证成功跳转哪里?
								String url = "/buyer/index.shtml";
								if (null != returnUrl && StringUtils.isNotBlank(returnUrl)) {
									url = returnUrl;
								}
								return "redirect:" + url;
							} else {
								model.addAttribute("error", "密码不正确!");
							}
						} else {
							model.addAttribute("error", "用户名不存在!");
						}
					} else {
						model.addAttribute("error", "密码不能为空!");
					}

				} else {
					model.addAttribute("error", "用户名不能为空!");
				}
			} else {
				model.addAttribute("error", "验证码不正确");
			}

		}
		return "buyer/login";
	}
	
	//个人中心
	@RequestMapping(value="/buyer/index.shtml")
	public String index() {
		return "buyer/index";
	}
	//个人资料
	@RequestMapping(value="/buyer/profile.shtml")
	public String profile(HttpServletRequest request,ModelMap model) {
		//加载用户,不用走数据库
		Buyer buyer = (Buyer) sessionProvider.getAttribute(request, Constants.BUYER_SESSION);
		Buyer b = buyerService.getBuyerByKey(buyer.getUsername());
		model.addAttribute("buyer", b);
		//省
		List<Province> provinces= provinceService.getProvinceList(null);
		model.addAttribute("provinces", provinces);
		//市
		CityQuery cityQuery = new CityQuery();
		cityQuery.setProvince(b.getProvince());
		List<City> citys = cityService.getCityList(cityQuery);
		model.addAttribute("citys", citys);
		//县
		TownQuery townQuery = new TownQuery();
		townQuery.setCity(b.getCity());
		List<Town> towns = townService.getTownList(townQuery);
		model.addAttribute("towns", towns);
		
		return "buyer/profile";
	}
	//加载城市
	@RequestMapping(value="/buyer/city.shtml")
	public void city(String code,HttpServletResponse response) {
		CityQuery cityQuery = new CityQuery();
		cityQuery.setProvince(code);
		List<City> citys = cityService.getCityList(cityQuery);
		JSONObject jObject = new JSONObject();
		jObject.put("citys", citys);
		ResponseUtils.renderJson(response, jObject.toString());
	}
	
	//加载县
	@RequestMapping(value="/buyer/town.shtml")
	public void town(String code,HttpServletResponse response) {
		TownQuery townQuery = new TownQuery();
		townQuery.setCity(code);
		List<Town> towns = townService.getTownList(townQuery);
		JSONObject jObject = new JSONObject();
		jObject.put("towns", towns);
		ResponseUtils.renderJson(response, jObject.toString());
	}
	
	//收货地址
	@RequestMapping(value="/buyer/deliver_address.shtml")
	public String address() {
		return "buyer/deliver_address";
	}
	@Autowired
	private ProvinceService provinceService;
	@Autowired
	private CityService cityService;
	@Autowired
	private TownService townService;
	

}
