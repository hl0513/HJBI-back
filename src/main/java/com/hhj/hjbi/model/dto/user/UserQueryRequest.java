package com.hhj.hjbi.model.dto.user;

import com.hhj.hjbi.common.PageRequest;

import java.io.Serializable;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 用户查询请求
 *
 * @author <a href="https://github.com/hl0513">程序员泥嚎鸭</a>
 * @from <a href="https://www.yuque.com/dashboard/books">泥嚎鸭的语雀笔记</a>
 */
@EqualsAndHashCode(callSuper = true)
@Data
public class UserQueryRequest extends PageRequest implements Serializable {
    /**
     * id
     */
    private Long id;



    /**
     * 用户昵称
     */
    private String userName;


    /**
     * 用户角色：user/admin/ban
     */
    private String userRole;

    private static final long serialVersionUID = 1L;
}