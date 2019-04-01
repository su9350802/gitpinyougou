package cn.itcast.core.service;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import java.util.HashSet;
import java.util.Set;

/**
 * @ClassName UserDetailServiceImpl
 * @Description 自定义认证类
 * @Author Ygkw
 * @Date 20:07 2019/3/31
 * @Version 2.1
 **/
public class UserDetailServiceImpl implements UserDetailsService {

    /**
     * @author 举个栗子
     * @Description 只需要对用户进行授权(认证：交给cas去完成)
     * @Date 20:12 2019/3/31
      * @param username
     * @return org.springframework.security.core.userdetails.UserDetails
     **/
    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        Set<GrantedAuthority> authorities = new HashSet<>();
        // 添加访问权限
        SimpleGrantedAuthority grantedAuthority = new SimpleGrantedAuthority("ROLE_USER");
        authorities.add(grantedAuthority);
        User user = new User(username,"",authorities);
        return user;
    }
}
