package com.auth2.server.service;

import com.alibaba.fastjson.JSON;
import com.auth2.server.domain.MyUser;
import com.auth2.server.entity.SysPermission;
import com.auth2.server.entity.SysUser;
import com.auth2.server.openid.connect.repository.impl.JpaAuthoritiesRepository;
import lombok.extern.slf4j.Slf4j;
import org.mitre.openid.connect.model.Authorities;
import org.mitre.openid.connect.model.UserInfo;
import org.mitre.openid.connect.repository.UserInfoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author baizh@enlink.cn
 * @date 2020/3/2
 */
@Slf4j
@Service("ownUserDetailsService")
public class OwnUserDetailsService implements UserDetailsService {

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private UserInfoRepository userInfoRepository;

    @Autowired
    private UserService userService;

    @Autowired
    private JpaAuthoritiesRepository jpaAuthoritiesRepository;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        UserInfo userInfo = userInfoRepository.getByUsername(username);
        if (null == userInfo) {
            log.warn("用户{}不存在", username);
            throw new UsernameNotFoundException(username);
        }
        List<Authorities> authorities = jpaAuthoritiesRepository.getByUserId(userInfo.getId());


//        List<SysPermission> permissionList = permissionService.findByUserId(sysUser.getId());
//        List<SimpleGrantedAuthority> authorityList = new ArrayList<>();
//        if (!CollectionUtils.isEmpty(permissionList)) {
//            for (SysPermission sysPermission : permissionList) {
//                authorityList.add(new SimpleGrantedAuthority(sysPermission.getCode()));
//            }
//        }

        List<SimpleGrantedAuthority> authorityList =
                authorities.stream().map(o -> new SimpleGrantedAuthority(o.getAuthority())).collect(Collectors.toList());

        MyUser myUser = new MyUser(userInfo.getPreferredUsername(), passwordEncoder.encode(userInfo.getPassword()), authorityList);

        log.info("登录成功！用户: {}", JSON.toJSONString(myUser));

        return myUser;
    }
}
