package cn.itcast.common.encode;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import org.apache.commons.codec.binary.Hex;
import org.aspectj.bridge.Message;

/**
 * Md5加密实现
 * @author 许刚
 *
 */
public class Md5pwdImp implements Md5Pwd{
	//加密
	public String encode(String password) {
		String algorithm = "MD5";
		MessageDigest instance = null;
		//加盐
		try {
			
			instance = MessageDigest.getInstance(algorithm);
			
		} catch (NoSuchAlgorithmException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		//md5加密
		byte[] digest = instance.digest(password.getBytes());
		//16进制加密
		char[] encodeHex = Hex.encodeHex(digest);
		return new String(encodeHex);
	}
}
