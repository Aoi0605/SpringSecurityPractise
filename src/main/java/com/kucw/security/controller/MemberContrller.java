package com.kucw.security.controller;

import com.kucw.security.dao.MemberDao;
import com.kucw.security.dao.RoleDao;
import com.kucw.security.model.Member;
import com.kucw.security.model.Role;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.Collection;

@RestController
public class MemberContrller {

    @Autowired
    private MemberDao memberDao;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private RoleDao roleDao;

    @PostMapping("/register")
    public String register(@RequestBody Member member) {
        //省略參數檢查，如 email 是否有被註冊

        //hash密碼加密
        String hashedPassword = passwordEncoder.encode(member.getPassword());
        member.setPassword(hashedPassword);

        //資料庫中插入 member 數據
        Integer memberId = memberDao.createMember(member);

        //設定注射預設的 Role
        //因為測試環境不同，因此須從資料庫將 ROLE_NORMAL_MEMBER 取出，以確認所取得 role 符合預期
        Role normalRole = roleDao.getRoleByName("ROLE_NORMAL_MEMBER");
        memberDao.addRoleForMemberId(memberId, normalRole);


        return "註冊成功";
    }

    // "/longin"API 已經由 Security 取走了，這邊為了演示改成 "/userLogin"
    @PostMapping("/userLogin")
    public String login(Authentication authentication) {
        //帳號密碼由 SpringSecurity 處理，執行到此代表成功，因此不需要檢查密碼是否正確，Security 已經幫我們檢查完了

        //取得使用者帳號
        String username = authentication.getName();
        //取得使用者權限
        Collection<? extends GrantedAuthority> authorities = authentication.getAuthorities();

        return "登入成功!帳號：" + username + "的權限為：" + authorities;
    }
}
