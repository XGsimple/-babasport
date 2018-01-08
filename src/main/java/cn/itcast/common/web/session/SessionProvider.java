package cn.itcast.common.web.session;

import java.io.Serializable;

import javax.servlet.http.HttpServletRequest;

import org.springframework.http.HttpRequest;

/**
 * 
 *session 公用类
 * @author 许刚
 *
 */
public interface SessionProvider {
	//往session里设置值
	//name constants buyer_sesson
	//value 用户对象
	public void setAttribute(HttpServletRequest request,String name,Serializable value);
	//从session中取值
	public Serializable getAttribute(HttpServletRequest request,String name);
	//退出登录
	public void logout(HttpServletRequest request);
	//获取sessionId
	public String getSessionID(HttpServletRequest request);
}
