package com.kucw.security.dao.impl;

import com.kucw.security.dao.MemberDao;
import com.kucw.security.model.Member;
import com.kucw.security.model.Role;
import com.kucw.security.rowmapper.MemberRowMapper;
import com.kucw.security.rowmapper.RoleRowMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Repository;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Repository
public class MemberDaoImpl implements MemberDao {

    @Autowired
    private NamedParameterJdbcTemplate namedParameterJdbcTemplate;

    @Autowired
    private MemberRowMapper memberRowMapper;

    @Autowired
    private RoleRowMapper roleRowMapper;

    @Override
    public Member getMemberByEmail(String email) {
        String sql = "SELECT member_id, email, password, name, age FROM member WHERE email = :email";

        Map<String, Object> map = new HashMap<>();
        map.put("email", email);

        List<Member> memberList = namedParameterJdbcTemplate.query(sql, map, memberRowMapper);

        if (memberList.size() > 0) {
            return memberList.get(0);
        } else {
            return null;
        }
    }

    @Override
    public Integer createMember(Member member) {
        String sql = "INSERT INTO member(email, password, name, age) " +
                "VALUES (:email, :password, :name, :age)";

        Map<String, Object> params = new HashMap<>();
        params.put("email", member.getEmail());
        params.put("password", member.getPassword());
        params.put("name", member.getName());
        params.put("age", member.getAge());

        KeyHolder keyHolder = new GeneratedKeyHolder();

        namedParameterJdbcTemplate.update(sql, new MapSqlParameterSource(params), keyHolder);

        Integer memberId = keyHolder.getKey().intValue();

        return memberId;
    }

    @Override
    public List<Role> getRolesByMemberId(Integer memberId) {
        String sql = """
                SELECT role.role_id, role.role_name FROM role
                    JOIN member_has_role ON role.role_id = member_has_role.role_id
                    WHERE member_has_role.member_id = :memberId
                """;

        Map<String, Object> map = new HashMap<>();
        map.put("memberId", memberId);

        List<Role> roleList = namedParameterJdbcTemplate.query(sql, map, roleRowMapper);

        return roleList;
    }

    //為新註冊帳號新增預設 role
    @Override
    public void addRoleForMemberId(Integer memberId, Role role) {
        String sql = "INSERT INTO member_has_role(member_id, role_id)" +
                "VALUES (:memberId, :roleId)";

        Map<String, Object> map = new HashMap<>();
        map.put("memberId", memberId);
        map.put("roleId", role.getRoleId());

        namedParameterJdbcTemplate.update(sql, map);
    }

    @Override
    public void removeRoleFromMemberId(Integer memberId, Role role) {
        String sql = "DELETE FROM member_has_role WHERE member_id = :memberId AND role_id = :roleId";

        Map<String, Object> map = new HashMap<>();
        map.put("memberId", memberId);
        map.put("roleId", role.getRoleId());

        namedParameterJdbcTemplate.update(sql, map);
    }
}
