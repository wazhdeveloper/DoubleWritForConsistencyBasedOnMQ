package com.mq.wz.bean;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.Date;

/**
 * @author wazh
 * @since 2023-11-29-19:21
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Category implements Serializable {
    private Long id;
    private String name;
    private String pid;
    private String description;
    private String status;
    private String createBy;
    private Date createTime;
    private String updateBy;
    private Date updateTime;
}
