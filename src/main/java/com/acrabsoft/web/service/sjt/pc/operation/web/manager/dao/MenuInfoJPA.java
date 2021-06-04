package com.acrabsoft.web.service.sjt.pc.operation.web.manager.dao;

import com.acrabsoft.web.service.sjt.pc.operation.web.manager.pojo.MenuInfo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface MenuInfoJPA extends JpaRepository<MenuInfo, String> {

    @Query(nativeQuery=true, value="select m.* from T_SYS_MENU m where exists ( " +
            "select * from ZD_SYS_ROLE_MENU rm , T_SYS_ROLE_USER ru " +
            "where rm.role_Id = ru.ROLE_ID and rm.DELETED = '0' and ru.DELETED = '0' and rm.menu_Id = m.OBJ_ID and " +
            "ru.USER_ID = :userObjId ) order BY parent_id, to_number(order_num)")
    List<MenuInfo> getUserRoledMenu(@Param("userObjId") String userObjId);

}
