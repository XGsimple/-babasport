package cn.itcast.core.web;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

import cn.itcast.common.web.session.SessionProvider;
import cn.itcast.core.bean.user.Buyer;

/**
 * 处理上下文
 * 处理session
 * 全局
 * @author 许刚
 *
 */
public class SpringmvcInterceptor implements HandlerInterceptor{

	@Autowired
	private SessionProvider sessionProvider;
	//常量
	private static final String INTERCEPTOR_URL ="/buyer/";
	
	private Integer adminId;
	
	//方法前
	@Override
	public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler)
			throws Exception {
		
		
		if(null != adminId) {
			Buyer buyer = new Buyer();
			buyer.setUsername("fbb2014");
			sessionProvider.setAttribute(request, Constants.BUYER_SESSION, buyer);
			
		}else {
			Buyer buyer = (Buyer) sessionProvider.getAttribute(request, Constants.BUYER_SESSION);
			boolean isLogin = false;
			if (null != buyer) {
				isLogin = true;
			}
			request.setAttribute("isLogin", isLogin);
			//是否拦截
			String requestURI = request.getRequestURI();  
			if (requestURI.startsWith(INTERCEPTOR_URL)) {
				//必须登录
				if (null == buyer) {
					//用户不存在
					response.sendRedirect("/shopping/login.shtml?returnUrl="+request.getParameter("returnUrl"));
					return false;//拦截住，不再进行
				}
			}
		}
		
		return true;
		
	}
	
	//方法后
	@Override
	public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler,
			ModelAndView modelAndView) throws Exception {
		// TODO Auto-generated method stub
		
	}

	//页面渲染后
	@Override
	public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex)
			throws Exception {
		// TODO Auto-generated method stub
		
	}

	public void setAdminId(Integer adminId) {
		this.adminId = adminId;
	}

}
