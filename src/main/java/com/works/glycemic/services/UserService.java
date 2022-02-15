package com.works.glycemic.services;

import com.works.glycemic.models.Role;
import com.works.glycemic.models.User;
import com.works.glycemic.repositories.RoleRepository;
import com.works.glycemic.repositories.UserRepository;
import lombok.AllArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.authentication.logout.SimpleUrlLogoutSuccessHandler;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
@AllArgsConstructor
@Transactional
public class UserService extends SimpleUrlLogoutSuccessHandler implements UserDetailsService {

    final RoleRepository rRepo;
    final UserRepository uRepo;

    // User Login Method
    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        UserDetails userDetails = null;
        Optional<User> oUser = uRepo.findByEmailEqualsIgnoreCase(email);
        if ( oUser.isPresent() ) {
            User u = oUser.get();
            userDetails = new org.springframework.security.core.userdetails.User(
                    u.getEmail(),
                    u.getPassword(),
                    u.isEnabled(),
                    u.isTokenExpired(),
                    true,
                    true,
                    getAuthorities( u.getRoles() )
            );
            return userDetails;
        }
        throw new UsernameNotFoundException("User name not found!");
    }


    public User register( User us )  {

        Optional<User> uOpt = uRepo.findByEmailEqualsIgnoreCase(us.getEmail());
        if ( uOpt.isPresent() ) {
            return null;
        }
        us.setPassword( encoder().encode( us.getPassword() ) );
        return uRepo.save(us);

    }


    public PasswordEncoder encoder() {
        return new BCryptPasswordEncoder();
    }


    // User Roles
    private List<GrantedAuthority> getAuthorities (List<Role> roles) {
        List<GrantedAuthority> authorities = new ArrayList<>();
        for (Role role : roles) {
            authorities.add(new SimpleGrantedAuthority( role.getName() ));
        }
        return authorities;
    }

    // Logout Listener -> Method
    @Override
    public void onLogoutSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException, ServletException {
        super.onLogoutSuccess(request, response, authentication);
    }

    //User Register Service
    public User userRegisterService(User user){

        //User  Control
        Optional<User> oUser = uRepo.findByEmailEqualsIgnoreCase(user.getEmail());
        if(oUser.isPresent()){
            return null;
        }
        else {
            Optional<Role> oRole = rRepo.findById(2L);
            if(oRole.isPresent()){
                //Register
                List<Role> roles = new ArrayList<>();
                Role r = oRole.get();
                roles.add(r);
                user.setRoles(roles);

                //email send -> enabled false
               return register(user);
            }
        }
        return  null;
    }


    //Admin Register Service
    public User adminRegisterService(User user){

        //User  Control
        Optional<User> oUser = uRepo.findByEmailEqualsIgnoreCase(user.getEmail());
        if(oUser.isPresent()){
            return null;
        }
        else {
                //Register
                user.setRoles(rRepo.findAll());

                //email send -> enabled false
                return register(user);

        }
    }

    // login with security
    public User login( String email ) {
        Optional<User> oUser = uRepo.findByEmailEqualsIgnoreCase( email );
        if (oUser.isPresent() ) {
            User u = oUser.get();
            //u.setPassword(null);
            return u;
        }
        return null;
    }

}