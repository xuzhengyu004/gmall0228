package com.atguigu.gmall.passport;

import com.atguigu.gmall.passport.util.JwtUtil;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.HashMap;
import java.util.Map;

@RunWith(SpringRunner.class)
@SpringBootTest
public class GmallPassportWebApplicationTests {

	@Test
	public void contextLoads() {
	}

	@Test
	public void test01(){
	/*自定义*/
		String key = "atguigu";
		/*ip是自动获取的*/
		String ip="192.168.176.130";
		Map map = new HashMap();
		map.put("userId","1001");
		map.put("nickName","marry");
		/*生成token*/
		String token = JwtUtil.encode(key, map, ip);
		/*token：eyJhbGciOiJIUzI1NiJ9.eyJuaWNrTmFtZSI6Im1hcnJ5IiwidXNlcklkIjoiMTAwMSJ9.U4WM52Wu7W1v8JFGGyUL3X2iCFTXuYz1JodxPxHVYNU*/
		System.out.println("token："+token);
		Map<String, Object> decode = JwtUtil.decode(token, key, "192.168.176.130");
		/*decode:{nickName=marry, userId=1001}*/
		System.out.println("decode:"+decode);
	}
}
