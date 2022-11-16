package com.atguigu.srb.gateway.filter;


import com.atguigu.srb.common.util.JwtUtils;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.http.HttpHeaders;
import org.springframework.http.server.RequestPath;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.net.URI;
import java.util.List;

@Component
public class AuthFilter implements GlobalFilter {
    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        //过滤请求 在请求到达微服务之前对请求做处理
        ServerHttpResponse response = exchange.getResponse();
        ServerHttpRequest request = exchange.getRequest();
        //路径
        RequestPath path = request.getPath();
        //uri=host+path
        URI uri = request.getURI();

        //这里可以做很多事情 白名单 黑名单 有哪些请求不需要鉴权就可以访问

        //鉴权 简易版

        Long userId=null;
        //如果请求是直接访问没有经过前端的axios则会出现 没有token的情况
        HttpHeaders headers = request.getHeaders();
        if (headers.size()>0&&headers!=null){
            List<String> tokens = headers.get("token");
            if (tokens!=null&&tokens.size()>0){
                String token = tokens.get(0);
                if (!StringUtils.isEmpty(token)){
                    boolean b = JwtUtils.checkToken(token);
                    if (b){
                        userId=JwtUtils.getUserId(token);
                        //将解析出来的userId 放在请求头中 带到后面
                        request.mutate().header("userId",userId.toString());
                        exchange.mutate().request(request);
                    }
                }
            }
        }
        return chain.filter(exchange);
    }
}
