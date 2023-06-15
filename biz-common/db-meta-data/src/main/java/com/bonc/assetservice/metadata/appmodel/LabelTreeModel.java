package com.bonc.assetservice.metadata.appmodel;

import com.bonc.framework.common.util.collection.TreeNode;
import lombok.Data;

import java.io.Serializable;
import java.util.List;


@Data
public class LabelTreeModel implements Serializable, TreeNode<LabelTreeModel> {
    private static final long serialVersionUID = 1L;
    /**
     * 分组编号
     */
    private String  dirId;
    /**
     * 分组名称
     */
    private String  dirName;
    /**
     * 父级分组编号
     */
    private String  parentDirId;

    /**
     * 子集列表
     */
    private List<LabelTreeModel> children;
    /**
     * 标签资产列表
     */
    private List<MetaAssetModel> assets;

    @Override
    public String getDirId() {
        return dirId;
    }
    @Override
    public void setDirId(String dirId) {
        this.dirId = dirId;
    }

    public String getDirName() {
        return dirName;
    }

    public void setDirName(String dirName) {
        this.dirName = dirName;
    }
    @Override
    public String getParentDirId() {
        return parentDirId;
    }
    @Override
    public void setParentDirId(String parentDirId) {
        this.parentDirId = parentDirId;
    }

    @Override
    public List<LabelTreeModel> getChildren() {
        return children;
    }
    @Override
    public void setChildren(List<LabelTreeModel> children) {
        this.children = children;
    }

    public List<MetaAssetModel> getAssets() {
        return assets;
    }

    public void setAssets(List<MetaAssetModel> assets) {
        this.assets = assets;
    }

}
