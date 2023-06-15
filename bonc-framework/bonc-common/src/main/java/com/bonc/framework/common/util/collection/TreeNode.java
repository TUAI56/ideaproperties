package com.bonc.framework.common.util.collection;

import java.util.List;

/**
 * 树图模型的标准
 */
public interface TreeNode<T> {

    /**
     * 获取编号
     * @return
     */
    String  getDirId();

    /**
     * 设置编号
     * @param dirId
     */
    void  setDirId(String dirId);
    /**
     * 获取父级编号
     * @return
     */
    String  getParentDirId();
    /**
     * 设置父级编号
     * @return
     */
    void setParentDirId(String parentCode);
    /**
     * 获取所有的子元素
     * @return
     */
    List<T> getChildren();
    /**
     * 设置子元素
     * @return
     */
    void setChildren(List<T> list);



}
