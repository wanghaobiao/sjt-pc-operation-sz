package com.acrabsoft.web.service.sjt.pc.operation.web.manager.pojo;

import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;
import java.util.Date;

@Entity
@Table(name = "t_sys_menu")
public class MenuInfo {

    public static final String TOP_MENU_ID = "000"; // 顶级菜单的ID

    /**
     * 主键
     */
    @Id
    @GenericGenerator(name = "PKUUID", strategy = "uuid2")
    @GeneratedValue(generator = "PKUUID")
    @Column(name = "OBJ_ID", length = 36)
    private String objId;

    /**
     * 菜单名称
     */
    @Column(name = "MENU_NAME", length = 200)
    private String menuName;

    /**
     * 菜单路径
     */
    @Column(name = "MENU_PATH", length = 2000)
    private String menuPath;

    /**
     * 父级菜单id
     */
    @Column(name = "PARENT_ID", length = 40)
    private String parentId;

    /**
     * 是否是父级菜单
     */
    @Column(name = "ISPARENT", length = 2)
    private String isparent = "0";  // 0 表示无下级（否）  1表示有下级（是）

    /**
     * 创建时间
     */
    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "CREATE_TIME")
    private Date createTime;

    /**
     * 更新时间
     */
    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "UPDATE_TIME")
    private Date updateTime;

    /**
     * 菜单图标
     */
    @Column(name = "ICON", length = 50)
    private String icon;

    /**
     * 菜单描述
     */
    @Column(name = "DESCRIPTION", length = 200)
    private String description;

    @Column(name = "CREATE_USER", length = 200)
    private String createUser;

    @Column(name = "CREATE_USER_NAME", length = 200)
    private String createUserName;

    @Column(name = "CREATE_DEPT", length = 200)
    private String createDept;

    @Column(name = "CREATE_DEPT_NAME", length = 200)
    private String createDeptName;

    /**
     * 序号
     */
    @Column(name = "ORDER_NUM", length = 20)
    private String orderNum;

    /**
     * 是否删除
     */
    @Column(name = "DELETED", length = 2)
    private String deleted = "0";

    public String getObjId() {
        return objId;
    }

    public void setObjId(String objId) {
        this.objId = objId;
    }

    public String getMenuName() {
        return menuName;
    }

    public void setMenuName(String menuName) {
        this.menuName = menuName;
    }

    public String getMenuPath() {
        return menuPath;
    }

    public void setMenuPath(String menuPath) {
        this.menuPath = menuPath;
    }

    public Date getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }

    public String getIcon() {
        return icon;
    }

    public void setIcon(String icon) {
        this.icon = icon;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getOrderNum() {
        return orderNum;
    }

    public void setOrderNum(String orderNum) {
        this.orderNum = orderNum;
    }

    public String getDeleted() {
        return deleted;
    }

    public void setDeleted(String deleted) {
        this.deleted = deleted;
    }

    public String getParentId() {
        return parentId;
    }

    public void setParentId(String parentId) {
        this.parentId = parentId;
    }

    public String getIsparent() {
        return isparent;
    }

    public void setIsparent(String isparent) {
        this.isparent = isparent;
    }

    public String getCreateUser() {
        return createUser;
    }

    public void setCreateUser(String createUser) {
        this.createUser = createUser;
    }

    public String getCreateUserName() {
        return createUserName;
    }

    public void setCreateUserName(String createUserName) {
        this.createUserName = createUserName;
    }

    public String getCreateDept() {
        return createDept;
    }

    public void setCreateDept(String createDept) {
        this.createDept = createDept;
    }

    public String getCreateDeptName() {
        return createDeptName;
    }

    public void setCreateDeptName(String createDeptName) {
        this.createDeptName = createDeptName;
    }

    public Date getUpdateTime() {
        return updateTime;
    }

    public void setUpdateTime(Date updateTime) {
        this.updateTime = updateTime;
    }
}
