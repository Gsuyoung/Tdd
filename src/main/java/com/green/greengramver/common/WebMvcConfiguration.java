package com.green.greengramver.common;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.method.HandlerTypePredicate;
import org.springframework.web.servlet.config.annotation.PathMatchConfigurer;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

//싱글톤 : 객체화를 한번만 하여 동일한객체를 여러번 생성하지않고 DI를 할때마다 주소값을 주도록 하는것.(SHALLOW COPY)
//빈등록을 하는 경우만

//@Bean : 무조건 return타입
@Configuration //빈등록 : webMvcConfiguration이 객체화가됨 (자동으로 @Bean이 붙어서 @Configuration을 사용할 필요는 없음)
//@Component
public class WebMvcConfiguration implements WebMvcConfigurer {
    private final String uploadPath;

    public WebMvcConfiguration(@Value("${file.directory}") String uploadPath) {
        this.uploadPath = uploadPath;
    }

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        registry.addResourceHandler("/pic/**").addResourceLocations("file:" + uploadPath + "/");
    }

    @Override
    public void configurePathMatch(PathMatchConfigurer configurer) {
        //RestController애노테이션이 붙은 모든 URL에 "/api" prefix를 설정
        configurer.addPathPrefix("api", HandlerTypePredicate.forAnnotation(RestController.class));
    }
}