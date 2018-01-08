package cn.itcast.core.service.staticpage;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.util.Map;

import javax.servlet.ServletConfig;
import javax.servlet.ServletContext;

import org.omg.CORBA.FREE_MEM;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.context.ServletConfigAware;
import org.springframework.web.context.ServletContextAware;
import org.springframework.web.servlet.view.freemarker.FreeMarkerConfigurer;

import freemarker.template.Configuration;
import freemarker.template.Template;

/**
 * 生成静态页实现类
 * @author 许刚
 *
 */

public class StaticPageServiceImp implements StaticPageService,ServletContextAware{
	private Configuration conf;
	public void setFreeMarkerConfigurer(FreeMarkerConfigurer freeMarkerConfigurer) {
		this.conf = freeMarkerConfigurer.getConfiguration();
	}

	//静态化方法
	@Override
	public void productIndex(Map<String, Object> root,Integer id) {
		//设置模版目录,已在freemarker.xml中设置好了
		//
		//输出流 从内存中写到磁盘上
		Writer out =null;
		try {
			//读模版进来，读到内存中 用utf-8
			Template template = conf.getTemplate("productDetail.html");
			//获取生成的静态的html保存路径
			String path = getPath("/html/product/" + id +".html");//id 为freemarker生成的静态页id
			File file = new File(path);
			if(!file.getParentFile().exists()) {
				file.getParentFile().mkdirs();
			}
			//输出流
			out = new OutputStreamWriter(new FileOutputStream(file), "UTF-8");
			//处理模版
			template.process(root, out);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}finally {
			if(out!= null) {
				try {
					out.close();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}
	}
	//获取路径
	public String getPath(String name) {
		return servletContext.getRealPath(name);
	}
	private ServletContext servletContext;

	@Override
	public void setServletContext(ServletContext servletContext) {
		this.servletContext = servletContext;
	}

}
