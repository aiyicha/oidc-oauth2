package com.auth2.server.service;

import com.auth2.server.entity.SysUser;
import com.auth2.server.repository.SysUserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * @author baizh@enlink.cn
 * @date 2020/3/2
 */
@Service
public class UserService {

    @Autowired
    private SysUserRepository sysUserRepository;

    public SysUser getByUsername(String username) {
        return sysUserRepository.findByUsername(username);
    }
}
