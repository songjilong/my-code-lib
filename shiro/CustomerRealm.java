package com.sjl.shiro.realms;

import com.sjl.entity.Permis;
import com.sjl.entity.User;
import com.sjl.service.UserService;
import org.apache.shiro.authc.AuthenticationException;
import org.apache.shiro.authc.AuthenticationInfo;
import org.apache.shiro.authc.AuthenticationToken;
import org.apache.shiro.authc.SimpleAuthenticationInfo;
import org.apache.shiro.authz.AuthorizationInfo;
import org.apache.shiro.authz.SimpleAuthorizationInfo;
import org.apache.shiro.realm.AuthorizingRealm;
import org.apache.shiro.subject.PrincipalCollection;
import org.apache.shiro.util.ByteSource;
import org.apache.shiro.util.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.ObjectUtils;

import java.util.List;

/**
 * @author songjilong
 * @date 2020/7/1 20:33
 */
public class CustomerRealm extends AuthorizingRealm {

    @Autowired
    private UserService userService;

    @Override
    protected AuthorizationInfo doGetAuthorizationInfo(PrincipalCollection principals) {
        String primaryPrincipal = (String) principals.getPrimaryPrincipal();
        User user = userService.findRolesByUsername(primaryPrincipal);
        if (user != null && !CollectionUtils.isEmpty(user.getRoles())) {
            SimpleAuthorizationInfo simpleAuthorizationInfo = new SimpleAuthorizationInfo();
            System.out.println("具有的角色：" + user.getRoles());
            user.getRoles().forEach(role -> {
                //添加角色
                simpleAuthorizationInfo.addRole(role.getName());
                List<Permis> permisList = userService.findPermisByRoleId(role.getId());
                System.out.println("具有的权限：" + permisList);
                //添加权限
                if (!CollectionUtils.isEmpty(permisList)) {
                    permisList.forEach(permis -> {
                        simpleAuthorizationInfo.addStringPermission(permis.getName());
                    });
                }
            });
            return simpleAuthorizationInfo;
        }
        return null;
    }

    @Override
    protected AuthenticationInfo doGetAuthenticationInfo(AuthenticationToken token) throws AuthenticationException {
        String username = (String) token.getPrincipal();
        User user = userService.findByUsername(username);
        if (!ObjectUtils.isEmpty(user)) {
            return new SimpleAuthenticationInfo(username, user.getPassword(), ByteSource.Util.bytes(user.getSalt()), this.getName());
        }
        return null;
    }
}
