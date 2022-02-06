package com.works.glycemic.config;

import com.works.glycemic.services.UserService;
import lombok.AllArgsConstructor;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;

@Configuration
@AllArgsConstructor
public class SecurityConfig extends WebSecurityConfigurerAdapter {

    final UserService userService;

    // sql -> jpa query -> user control
    @Override
    protected void configure(AuthenticationManagerBuilder auth) throws Exception {
        auth.userDetailsService(userService).passwordEncoder(userService.encoder());
    }

    // hangi yöntemle giriş yapılacak, rollere göre hangi servis kullanılcak?
    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http
                .httpBasic()
                .and()
                .authorizeHttpRequests()
                .antMatchers("/foods/save","/foods/userFoodList","/foods/foodDelete","/foods/foodUpdate").hasAnyRole("user","admin")
                .antMatchers("/foods/list").hasAnyRole("user","global","admin")
                .antMatchers("/register/**").permitAll()
                .and()
                .csrf().disable()
                .logout().logoutUrl("/logout").invalidateHttpSession(true);


   /*     http
                .httpBasic()
                .and()
                .authorizeRequests()
                .antMatchers("/customer/**").hasRole("USER")
                .antMatchers("/news/**").hasRole("ADMIN")
                .antMatchers("/admin/**").permitAll()
                .and()
                .csrf().disable()
                .formLogin().disable()
                .logout().logoutUrl("/admin/logout").invalidateHttpSession(true) ;
        http.headers().frameOptions().disable(); // h2-console for using
*/
    }


}