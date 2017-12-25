package cn.edu.jxnu.blog.commons;

import org.apache.shiro.crypto.hash.Md5Hash;


/**
 * @Description MD5加密工具类
 */
public class MD5Util {
    /**
     * @Description 使用Shiro中的md5加密
     * @param str
     * @param salt
     * @return
     */
    public static String md5(String str,String salt){
        //Md5Hash是Shiro中的一个方法
        return new Md5Hash(str, salt).toString();
    }
    public static void main(String[] args) {
    	
    	
    	String password = MD5Util.md5("liguobin", "admin");
    	System.out.println("加密后的密码"+password);
	}
}
