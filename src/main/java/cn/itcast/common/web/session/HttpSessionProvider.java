package cn.itcast.common.web.session;

import java.io.Serializable;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

/**
 * 
 * 本地session
 * @author 许刚
 *
 */
public class HttpSessionProvider implements SessionProvider {

	@Override
	public void setAttribute(HttpServletRequest request, String name, Serializable value) {
		HttpSession session = request.getSession();
		session.setAttribute(name, value);
	}

	@Override
	public Serializable getAttribute(HttpServletRequest request, String name) {
		HttpSession session = request.getSession(false);//不新创建个session，寻找存在的
		if(session!= null) {
			return (Serializable) session.getAttribute(name);
		}
		return null;
	}

	@Override
	public void logout(HttpServletRequest request) {
		HttpSession session = request.getSession(false);//不新创建个session，寻找存在的
		if(session!= null) {
			session.invalidate();//session失效
		}
		//Cookie JSESSION
	}

	@Override
	public String getSessionID(HttpServletRequest request) {
		
		//request.getRequestedSessionId();//获取url上的作为参数的JSESSIONID
		return request.getSession().getId();
	}
	
}
