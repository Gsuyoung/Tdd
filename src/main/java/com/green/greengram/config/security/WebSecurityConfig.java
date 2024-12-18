package com.green.greengram.config.security;
//Spring Security 세팅

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

@Configuration //메소드 빈등록이 있어야 의미가 있다. 안에 component가 들어가있다. / 메소드 빈등록이 싱글톤이 된다.
@RequiredArgsConstructor
public class WebSecurityConfig {
    //스프링 시큐리티 기능 비활성화(스프링 시큐리티가 관여하지 않았으면 하는 부분)
    /* @Bean
    public WebSecurityCustomizer webSecurityCustomizer() {
        return web -> web.ignoring() //web의 타입은 websecurity이고 websecurity에는 ignoring이라는 메소드가 존재한다.
                         .requestMatchers(new AntPathRequestMatcher("/static/**"));

    } */

    @Bean //@Bean이 붙어있으면 스프링이 메소드 호출을 하고 리턴한 객체의 주소값을 관리한다.(빈등록)
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        return http.sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS)) //시큐리티가 세션을 사용하지 않는다.
                .httpBasic(h -> h.disable()) //SSR(Server Side Rendering)이 아니다. 화면을 만들지 않을것이기 때문에 비활성화 시킨다. 시큐리티 로그인창 나타나지 않을 것이다.
                .formLogin(form -> form.disable()) //마찬가지로 SSR이 아니다. 폼로그인 기능 자체를 비활성화
                .csrf(csrf -> csrf.disable()) //보안관련 SSR이 아니면 보안이슈가 없기 때문에 기능을 끈다.
                .authorizeHttpRequests(res ->
                        res.requestMatchers("/api/feed", "/api/feed/**").authenticated() //로그인이 되어 있어야만 사용 가능
                                .requestMatchers(HttpMethod.GET,"/api/user").authenticated()
                                .requestMatchers(HttpMethod.PATCH,"/api/user/pic").authenticated()
                           .anyRequest().permitAll()) //나머지는 모두 허용. 아래에 위치해야한다.
                .build();
    }

    @Bean
    public BCryptPasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}
