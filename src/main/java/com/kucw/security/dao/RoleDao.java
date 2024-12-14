package com.kucw.security.dao;

import com.kucw.security.model.Role;

public interface RoleDao {

    //為新註冊帳號新增預設 role
    Role getRoleByName(String roleName);
}
