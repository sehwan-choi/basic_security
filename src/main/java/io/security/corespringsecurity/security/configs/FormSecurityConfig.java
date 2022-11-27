package io.security.corespringsecurity.security.configs;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.security.corespringsecurity.repository.AccessIpRepository;
import io.security.corespringsecurity.security.common.FormAuthenticationDetailsSource;
import io.security.corespringsecurity.security.filter.PermitAllFilter;
import io.security.corespringsecurity.security.handler.CustomAccessDeniedHandler;
import io.security.corespringsecurity.security.handler.CustomAuthenticationEntryPoint;
import io.security.corespringsecurity.security.handler.CustomAuthenticationFailureHandler;
import io.security.corespringsecurity.security.handler.CustomAuthenticationSuccessHandler;
import io.security.corespringsecurity.security.provider.CustomAuthenticationProvider;
import io.security.corespringsecurity.security.voter.IpAddressVoter;
import lombok.Setter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.autoconfigure.security.servlet.PathRequest;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.security.access.AccessDecisionManager;
import org.springframework.security.access.AccessDecisionVoter;
import org.springframework.security.access.hierarchicalroles.RoleHierarchyImpl;
import org.springframework.security.access.vote.AffirmativeBased;
import org.springframework.security.access.vote.RoleHierarchyVoter;
import org.springframework.security.access.vote.RoleVoter;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.core.session.SessionRegistry;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.access.intercept.FilterInvocationSecurityMetadataSource;
import org.springframework.security.web.access.intercept.FilterSecurityInterceptor;

import java.util.ArrayList;
import java.util.List;

@Order(1)
@Configuration
@EnableWebSecurity
public class FormSecurityConfig extends WebSecurityConfigurerAdapter {

    @Setter(onMethod_ = @Autowired, onParam_ = @Qualifier("customUserDetailsService"))
    private UserDetailsService userDetailsService;

    @Setter(onMethod_ = @Autowired)
    private ObjectMapper objectMapper;

    @Setter(onMethod_ = @Autowired)
    private PasswordEncoder passwordEncoder;

    @Setter(onMethod_ = @Autowired)
    private SessionRegistry sessionRegistry;

    @Setter(onMethod_ = @Autowired, onParam_ = @Qualifier("urlFilterInvocationSecurityMetadataSource"))
    private FilterInvocationSecurityMetadataSource metadataSource;

    @Setter(onMethod_ = @Autowired)
    private AccessIpRepository accessIpRepository;

    private final String[] permitAllResources = {"/", "/users", "/login*", "/denied*", "/user/login/**"};

    @Override
    protected void configure(AuthenticationManagerBuilder auth) throws Exception {

//        String password = passwordEncoder().encode("1111");
//
//        auth.inMemoryAuthentication().withUser("user").password(password).roles("USER");
//        auth.inMemoryAuthentication().withUser("manager").password(password).roles("MANAGER", "USER");
//        auth.inMemoryAuthentication().withUser("admin").password(password).roles("ADMIN", "USER", "MANAGER");
//        CustomUserDetailsService customUserDetailsService = new CustomUserDetailsService(userRepository);
//        auth.userDetailsService(customUserDetailsService);
        auth.authenticationProvider(new CustomAuthenticationProvider(userDetailsService, passwordEncoder));
    }

    @Override
    public AuthenticationManager authenticationManagerBean() throws Exception {
        return super.authenticationManagerBean();
    }

    @Override
    protected void configure(HttpSecurity http) throws Exception {

        http
                .authorizeRequests()
//                .antMatchers(permitAllResources).permitAll()
//                .antMatchers("/mypage").hasRole("USER")
//                .antMatchers("/messages").hasRole("MANAGER")
//                .antMatchers("/config").hasRole("ADMIN")
                .anyRequest().authenticated();

        /**
         *  Spring Security에서 AccessDeniedHandler인터페이스와 AuthenticationEntryPoint인터페이스가 존재한다.
         *  AccessDeniedHandler는 서버에 요청을 할 때 액세스가 가능한지 권한을 체크후 액세스 할 수 없는 요청을 했을시 동작된다.
         *   -> 로그인은 성공했지만 해당 리소스에 접근 권한이 없는경우
         *
         *  AuthenticationEntryPoint는 인증이 되지않은 유저가 요청을 했을때 동작된다.
         *   -> 로그인도 하지 않았으면서 해당 리소스에 접근 하려는 경우
         */

        http.exceptionHandling()
                .accessDeniedHandler(new CustomAccessDeniedHandler(objectMapper))
                .authenticationEntryPoint(new CustomAuthenticationEntryPoint(objectMapper));

        http
                .formLogin()
                .loginPage("/login")
                .loginProcessingUrl("/login_proc")
                .defaultSuccessUrl("/")
                .authenticationDetailsSource(new FormAuthenticationDetailsSource())
                .successHandler(new CustomAuthenticationSuccessHandler())
                .failureHandler(new CustomAuthenticationFailureHandler())
                .permitAll()
                ;

        http
                .sessionManagement()
                .maximumSessions(1)
                .expiredUrl("/")
                .maxSessionsPreventsLogin(true)
                .sessionRegistry(sessionRegistry)
                ;

        http
                .addFilterBefore(permitAllFilterSecurityInterceptor(), FilterSecurityInterceptor.class);
//                .addFilterBefore(customFilterSecurityInterceptor(), FilterSecurityInterceptor.class);

        http
                .csrf().disable();

        http
                .logout()
                .logoutSuccessUrl("/")
                .logoutUrl("/logout")
        ;
    }

    @Override
    public void configure(WebSecurity web) throws Exception {
        web.ignoring()
                .requestMatchers(PathRequest.toStaticResources().atCommonLocations());
    }

    @Bean
    public FilterSecurityInterceptor permitAllFilterSecurityInterceptor() throws Exception {
        PermitAllFilter permitAllFilter = new PermitAllFilter(permitAllResources);

        permitAllFilter.setSecurityMetadataSource(metadataSource);
        permitAllFilter.setAccessDecisionManager(affirmativeBased());
        permitAllFilter.setAuthenticationManager(authenticationManagerBean());
        return permitAllFilter;
    }

    @Bean
    public FilterSecurityInterceptor customFilterSecurityInterceptor() throws Exception {
        FilterSecurityInterceptor filter = new FilterSecurityInterceptor();

        filter.setSecurityMetadataSource(metadataSource);
        filter.setAccessDecisionManager(affirmativeBased());
        filter.setAuthenticationManager(authenticationManagerBean());
        return filter;
    }

    @Bean
    public FilterSecurityInterceptor hierarchyFilterSecurityInterceptor() throws Exception {
        FilterSecurityInterceptor filter = new FilterSecurityInterceptor();

        filter.setSecurityMetadataSource(metadataSource);
        filter.setAccessDecisionManager(affirmativeBased());
        filter.setAuthenticationManager(authenticationManagerBean());
        return filter;
    }

    private AccessDecisionManager affirmativeBased() {
        return new AffirmativeBased(getAccessDecisionVoters());
    }

    private List<AccessDecisionVoter<?>> getAccessDecisionVoters() {
        List<AccessDecisionVoter<? extends Object>> accessDecisionVoters = new ArrayList<>();

        accessDecisionVoters.add(new IpAddressVoter(accessIpRepository));
        accessDecisionVoters.add(new RoleVoter());
        accessDecisionVoters.add(hierarchyVoter());
        return accessDecisionVoters;
    }

    @Bean
    public AccessDecisionVoter<?> hierarchyVoter() {
        return new RoleHierarchyVoter(roleHierarchy());
    }

    @Bean
    public RoleHierarchyImpl roleHierarchy() {
        return new RoleHierarchyImpl();
    }

}
