package com.hhj.hjbi.model.dto.file;

import java.io.Serializable;
import lombok.Data;

/**
 * 文件上传请求
 *
 * @author <a href="https://github.com/hl0513">程序员泥嚎鸭</a>
 * @from <a href="https://www.yuque.com/dashboard/books">泥嚎鸭的语雀笔记</a>
 */
@Data
public class UploadFileRequest implements Serializable {

    /**
     * 业务
     */
    private String biz;

    private static final long serialVersionUID = 1L;
}