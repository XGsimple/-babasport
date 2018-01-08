package cn.itcast;
/**
 * 购物车对象转成json
 * @author 许刚
 *
 */

import java.io.IOException;
import java.io.StringWriter;

import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.map.annotate.JsonSerialize.Inclusion;
import org.junit.Test;

import cn.itcast.core.bean.user.Buyer;

public class TestCookie {
	@Test
	public void testCookie() throws Exception {
		Buyer buyer = new Buyer();
		buyer.setUsername("fbb2014");
		
		// springmvc
		ObjectMapper om = new ObjectMapper();
		om.setSerializationInclusion(Inclusion.NON_NULL);
		//流
		StringWriter writer = new StringWriter();
		//对象写json  写的过程，json是字符串流
		om.writeValue(writer, buyer);
		System.out.println(writer.toString());
		//json转对象
		Buyer b = om.readValue(writer.toString(), Buyer.class);
		System.out.println(b.toString());

	}
}
