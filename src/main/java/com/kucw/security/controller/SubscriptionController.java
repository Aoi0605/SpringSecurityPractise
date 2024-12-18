package com.kucw.security.controller;

import com.kucw.security.Dto.SubscribeRequest;
import com.kucw.security.Dto.UnsubscribeRequest;
import com.kucw.security.dao.MemberDao;
import com.kucw.security.dao.RoleDao;
import com.kucw.security.model.Role;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
public class SubscriptionController {

    @Autowired
    private MemberDao memberDao;

    @Autowired
    private RoleDao roleDao;

    //訂閱用 API(新增權限)
    @PostMapping("/subscribe")
    public String subscribe(@RequestBody SubscribeRequest subscribeRequest){
        Integer memberId = subscribeRequest.getMemberId();
        List<Role> roleList = memberDao.getRolesByMemberId(memberId);

        // 檢查訂閱狀態
        boolean isSubscribed = checkSubscribeStatus(roleList);

        // 根據訂閱狀態決定是否執行操作
        if (isSubscribed) {
            return "已訂閱過，不需重複訂閱";

        } else {
            //飯粒有簡化實作，訂閱日期跟金額都沒有處理
            Role vipRole = roleDao.getRoleByName("ROLE_VIP_MEMBER");
            memberDao.addRoleForMemberId(memberId, vipRole);
            //改變使用者權限需要刪除 Cookie 改變才會生效，因此前端需要做這方面的設置
            return "訂閱成功！請刪除 Cookie 重新登入";
        }
    }

    //取消訂閱(降低權限)
    @PostMapping("/unsubscribe")
    public String unsubscribe(@RequestBody UnsubscribeRequest unsubscribeRequest) {

        Integer memberId = unsubscribeRequest.getMemberId();
        List<Role> roleList = memberDao.getRolesByMemberId(memberId);

        // 檢查訂閱狀態
        boolean isSubscribed = checkSubscribeStatus(roleList);

        // 根據訂閱狀態決定是否執行操作
        if (isSubscribed) {
            Role vipRole = roleDao.getRoleByName("ROLE_VIP_MEMBER");
            memberDao.removeRoleFromMemberId(memberId, vipRole);

            return "取消訂閱成功！請刪除 Cookie 重新登入";

        } else {
            return "尚未訂閱，無法執行取消訂閱操作";
        }
    }

    //這個方法將檢查欲訂閱者是否是 ROLE_VIP_MEMBER 權限
    private boolean checkSubscribeStatus(List<Role> roleList) {
        boolean isSubscribed = false;

        for (Role role : roleList) {
            if (role.getRoleName().equals("ROLE_VIP_MEMBER")) {
                isSubscribed = true;
            }
        }

        return isSubscribed;
    }
}
