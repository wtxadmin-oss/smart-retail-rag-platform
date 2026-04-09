package com.smartcoffee.controller.config;

import com.smartcoffee.config.AuthInterceptor;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import org.springframework.context.annotation.Bean;

@Configuration
public class WebCorsConfig implements WebMvcConfigurer {

  @Bean
  // 注册认证拦截器 Bean，供 Spring 容器统一管理。
  public AuthInterceptor authInterceptor() {
      return new AuthInterceptor();
  }

  @Override
  // 配置拦截器生效路径，放行登录、注册和静态资源访问。
  public void addInterceptors(InterceptorRegistry registry) {
      registry.addInterceptor(authInterceptor())
              .addPathPatterns("/api/admin/**", "/api/user/**", "/api/cart/**", "/api/orders/**", "/api/auth/change-password");
  }

  @Override
  // 将本地 uploads 目录映射为可直接访问的静态资源路径。
  public void addResourceHandlers(ResourceHandlerRegistry registry) {
      String uploadPath = new java.io.File(System.getProperty("user.dir"), "uploads").getAbsolutePath();
      registry.addResourceHandler("/uploads/**")
              .addResourceLocations("file:" + uploadPath + "/");
  }

  @Override
  // 统一配置跨域规则，允许前端在开发环境下访问后端接口。
  public void addCorsMappings(CorsRegistry registry) {
    registry.addMapping("/api/**")
      .allowedOriginPatterns("*")
      .allowedMethods("*")
      .allowedHeaders("*")
      .allowCredentials(false)
      .maxAge(3600);
  }
}
