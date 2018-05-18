package com.lei;

import java.util.Set;

import javax.websocket.Endpoint;
import javax.websocket.server.ServerApplicationConfig;
import javax.websocket.server.ServerEndpointConfig;

//配置websocket
public class WebscoketConf implements ServerApplicationConfig {

	 @Override
	 public Set<Class<?>> getAnnotatedEndpointClasses(Set<Class<?>> set) {
	 // 使用注解则要使用这个方法进行配置，可以在这里下断点看set里面有什么内容
	     return set;
	 }

	@Override
	public Set<ServerEndpointConfig> getEndpointConfigs(Set<Class<? extends Endpoint>> arg0) {
		// TODO Auto-generated method stub
		return null;
	}
}
