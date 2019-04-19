package com.zzd.spring.gateway.filter;

import org.apache.commons.lang.StringUtils;
import org.springframework.cloud.gateway.filter.GatewayFilter;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.core.Ordered;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

/**
 * 单个拦截，全局拦截的话用GlobalFilter.
 * 自定义GatewayFilter又有两种实现方式，
 * 一种是直接 实现GatewayFilter接口【本例】
 * 另一种是 继承AbstractGatewayFilterFactory类
 * Created by Administrator on 2019/4/4 0004.
 */
@Component
public class AuthorizeGatewayFilter implements GatewayFilter,Ordered {

    private static final String AUTH_PLATFORM="platform";

    /**
     * 首先从header中获取token，但是如果header中没有token，那么就从参数中获取【总有人会瞎鸡儿搞】，
     * 优先header，如果没传就返回401，
     * @param exchange
     * @param chain
     * @return
     */
    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        ServerHttpRequest request=exchange.getRequest();
        HttpHeaders headers=request.getHeaders();
        String token=headers.getFirst(AUTH_PLATFORM);
        if(StringUtils.isBlank(token)){
            token=request.getQueryParams().getFirst(AUTH_PLATFORM);
        }
        ServerHttpResponse response=exchange.getResponse();
        if(StringUtils.isBlank(token)){
            response.setStatusCode(HttpStatus.FORBIDDEN);
            return response.setComplete();
        }
        return chain.filter(exchange);
    }

    @Override
    public int getOrder() {
        return 10;
    }
}
