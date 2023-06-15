package com.bonc.assetservice.assetsync.model;

import lombok.Data;

import java.io.Serializable;
import java.util.List;

@Data
public class GroupInfoMsgVO implements Serializable {
    private static final long serialVersionUID = 1L;
    /**
     * 目录编码
     */
    private String code;
    /**
     *
     */
    private String linkId;

    /**
     *
     */
    private String isSelect;

    /**
     *目录名称
     */
    private String name;
    /**
     *资产数
     */
    private String count;
    /**
     *目录id
     */
    private String id;
    /**
     *目录全路径
     */
    private String title;
    /**
     *是否主干
     */
    private String trunk;
    /**
     *子级目录
     */
    private List<GroupInfoMsgVO> children;
    /**
     *父级目录id
     */
    private String parentId;

}
