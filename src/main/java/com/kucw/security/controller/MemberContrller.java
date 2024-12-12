package com.kucw.security.controller;

import com.kucw.security.dao.MemberDao;
import com.kucw.security.model.Member;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class MemberContrller {

    @Autowired
    private MemberDao memberDao;

    @PostMapping("/register")
    public String register(@RequestBody Member member) {
        //省略參數檢查，如 email 是否有被註冊

        Integer memberId = memberDao.createMember(member);
        return "註冊成功";
    }
}
