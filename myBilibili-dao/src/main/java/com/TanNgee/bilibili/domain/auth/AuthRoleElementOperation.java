package com.TanNgee.bilibili.domain.auth;

import java.util.Date;

/**
 * 角色元素操作权限关联表
 *
 * @Author TanNgee
 * @Date 2022/6/29 21:47
 **/
public class AuthRoleElementOperation {

    private Long id;

    private Long roleId;

    private Long elementOperationId;

    private Date createTime;

    private AuthElementOperation authElementOperation;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getRoleId() {
        return roleId;
    }

    public void setRoleId(Long roleId) {
        this.roleId = roleId;
    }

    public Long getElementOperationId() {
        return elementOperationId;
    }

    public void setElementOperationId(Long elementOperationId) {
        this.elementOperationId = elementOperationId;
    }

    public Date getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }

    public AuthElementOperation getAuthElementOperation() {
        return authElementOperation;
    }

    public void setAuthElementOperation(AuthElementOperation authElementOperation) {
        this.authElementOperation = authElementOperation;
    }
}
