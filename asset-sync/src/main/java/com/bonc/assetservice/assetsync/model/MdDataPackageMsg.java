package com.bonc.assetservice.assetsync.model;

import lombok.Data;

import java.util.Date;

@Data
public class MdDataPackageMsg {

    /**
     * 主键
     */
    private String oid;

    /**
     * 数据生产系统ID
     */
    private String systemId;

    /**
     * 数据包_英文名称，注册的本次数据的名称。必须唯一。
     * 上传文件时的文件名为：数据名称+账期时间_采集周期
     */
    private String dataCode;

    /**
     * 数据包中文名称，必须唯一
     */
    private String dataName;

    /**
     * 数据采集周期
     * 1 日 yyyyMMdd  --D
     * 2 月 yyyyMM  --M
     * 3 年 yyyy    --Y
     * 4 周 yyyyMMdd  --W
     * 5 小时  yyyyMMddHH--H
     * 6 实时  yyyyMMddHHmm--RT
     * 7 季度  yyyyMM  --Q
     */
    private String dataInterval;

    /**
     * 行数预估
     */
    private Integer rowCount;

    /**
     * 文件大小预估
     */
    private Integer dataSize;

    /**
     * 数据量单位：G,G级，T.T级，P.P级，Z.Z级
     */
    private String dataUnit;

    /**
     * 有效期
     */
    private Date valiTime;

    /**
     * 所属域(单选)
     */
    private String domainId;

    /**
     * 状态：1,注册但未启动，2.活跃，3.暂停，4.停用
     */
    private Integer valiDate;

    /**
     * 业务联系人
     */
    private String buConcactMan;

    /**
     * 业务联系人邮箱
     */
    private String buConcactMail;

    /**
     * 业务联系人电话
     */
    private String buConcactPhone;

    /**
     * 是否有效
     * (0有效,1删除）
     */
    private Integer deleteFlag;

    /**
     * 失效期
     */
    private Date endTime;

    /**
     * 注册类型(1外部数据导入注册，0系统数据包注册）
     */
    private Integer regiType;

    /**
     * 数据块
     */
    private Integer dataBlock;

    /**
     * 是否域主表(0 不是，1 是 )
     */
    private Integer isMaster;

    /**
     * 优先级(1 高,2 中,3 低)
     */
    private String priority;

    /**
     * 行分隔符
     */
    private String rowLimit;

    /**
     * 列分隔符
     */
    private String colLimit;

    /**
     * 重点任务(0 不是，1 是 )
     */
    private Integer important;
    /**
     * 是否是合并1是
     */
    private Integer collectFrequency;
    /**
     * 1已行列为结尾，0以行为结尾
     */
    private String loadtype;
    /**
     * user_id是否设为主键
     */
    private String isPrimary;
    /**
     * H横表，T纵表
     */
    private String tableType;

    private String provId;
    public String getProvId() {
        return provId;
    }

    public void setProvId(String provId) {
        this.provId = provId;
    }
    private static final long serialVersionUID = 1L;



    /**
     * 获取 主键
     *
     * @return oid 主键
     */
    public String getOid() {
        return this.oid;
    }

    /**
     * 设置 主键
     *
     * @param oid 主键
     */
    public void setOid(String oid) {
        this.oid = oid;
    }

    /**
     * 获取 数据生产系统ID
     *
     * @return systemId 数据生产系统ID
     */
    public String getSystemId() {
        return this.systemId;
    }

    /**
     * 设置 数据生产系统ID
     *
     * @param systemId 数据生产系统ID
     */
    public void setSystemId(String systemId) {
        this.systemId = systemId;
    }

    /**
     * 获取 数据包_英文名称，注册的本次数据的名称。必须唯一。     上传文件时的文件名为：数据名称+账期时间_采集周期
     *
     * @return dataCode 数据包_英文名称，注册的本次数据的名称。必须唯一。     上传文件时的文件名为：数据名称+账期时间_采集周期
     */
    public String getDataCode() {
        return this.dataCode;
    }

    /**
     * 设置 数据包_英文名称，注册的本次数据的名称。必须唯一。     上传文件时的文件名为：数据名称+账期时间_采集周期
     *
     * @param dataCode 数据包_英文名称，注册的本次数据的名称。必须唯一。     上传文件时的文件名为：数据名称+账期时间_采集周期
     */
    public void setDataCode(String dataCode) {
        this.dataCode = dataCode;
    }

    /**
     * 获取 数据包中文名称，必须唯一
     *
     * @return dataName 数据包中文名称，必须唯一
     */
    public String getDataName() {
        return this.dataName;
    }

    /**
     * 设置 数据包中文名称，必须唯一
     *
     * @param dataName 数据包中文名称，必须唯一
     */
    public void setDataName(String dataName) {
        this.dataName = dataName;
    }

    /**
     * 获取 数据采集周期     1 日 yyyyMMdd  --D     2 月 yyyyMM  --M     3 年 yyyy    --Y     4 周 yyyyMMdd  --W     5 小时  yyyyMMddHH--H     6 实时  yyyyMMddHHmm--RT     7 季度  yyyyMM  --Q
     *
     * @return dataInterval 数据采集周期     1 日 yyyyMMdd  --D     2 月 yyyyMM  --M     3 年 yyyy    --Y     4 周 yyyyMMdd  --W     5 小时  yyyyMMddHH--H     6 实时  yyyyMMddHHmm--RT     7 季度  yyyyMM  --Q
     */
    public String getDataInterval() {
        return this.dataInterval;
    }

    /**
     * 设置 数据采集周期     1 日 yyyyMMdd  --D     2 月 yyyyMM  --M     3 年 yyyy    --Y     4 周 yyyyMMdd  --W     5 小时  yyyyMMddHH--H     6 实时  yyyyMMddHHmm--RT     7 季度  yyyyMM  --Q
     *
     * @param dataInterval 数据采集周期     1 日 yyyyMMdd  --D     2 月 yyyyMM  --M     3 年 yyyy    --Y     4 周 yyyyMMdd  --W     5 小时  yyyyMMddHH--H     6 实时  yyyyMMddHHmm--RT     7 季度  yyyyMM  --Q
     */
    public void setDataInterval(String dataInterval) {
        this.dataInterval = dataInterval;
    }

    /**
     * 获取 行数预估
     *
     * @return rowCount 行数预估
     */
    public Integer getRowCount() {
        return this.rowCount;
    }

    /**
     * 设置 行数预估
     *
     * @param rowCount 行数预估
     */
    public void setRowCount(Integer rowCount) {
        this.rowCount = rowCount;
    }

    /**
     * 获取 文件大小预估
     *
     * @return dataSize 文件大小预估
     */
    public Integer getDataSize() {
        return this.dataSize;
    }

    /**
     * 设置 文件大小预估
     *
     * @param dataSize 文件大小预估
     */
    public void setDataSize(Integer dataSize) {
        this.dataSize = dataSize;
    }

    /**
     * 获取 数据量单位：GG级，T.T级，P.P级，Z.Z级
     *
     * @return dataUnit 数据量单位：GG级，T.T级，P.P级，Z.Z级
     */
    public String getDataUnit() {
        return this.dataUnit;
    }

    /**
     * 设置 数据量单位：GG级，T.T级，P.P级，Z.Z级
     *
     * @param dataUnit 数据量单位：GG级，T.T级，P.P级，Z.Z级
     */
    public void setDataUnit(String dataUnit) {
        this.dataUnit = dataUnit;
    }

    /**
     * 获取 有效期
     *
     * @return valiTime 有效期
     */
    public Date getValiTime() {
        return this.valiTime;
    }

    /**
     * 设置 有效期
     *
     * @param valiTime 有效期
     */
    public void setValiTime(Date valiTime) {
        this.valiTime = valiTime;
    }

    /**
     * 获取 所属域(单选)
     *
     * @return domainId 所属域(单选)
     */
    public String getDomainId() {
        return this.domainId;
    }

    /**
     * 设置 所属域(单选)
     *
     * @param domainId 所属域(单选)
     */
    public void setDomainId(String domainId) {
        this.domainId = domainId;
    }

    /**
     * 获取 状态：1注册但未启动，2.活跃，3.暂停，4.停用
     *
     * @return valiDate 状态：1注册但未启动，2.活跃，3.暂停，4.停用
     */
    public Integer getValiDate() {
        return this.valiDate;
    }

    /**
     * 设置 状态：1注册但未启动，2.活跃，3.暂停，4.停用
     *
     * @param valiDate 状态：1注册但未启动，2.活跃，3.暂停，4.停用
     */
    public void setValiDate(Integer valiDate) {
        this.valiDate = valiDate;
    }

    /**
     * 获取 业务联系人
     *
     * @return buConcactMan 业务联系人
     */
    public String getBuConcactMan() {
        return this.buConcactMan;
    }

    /**
     * 设置 业务联系人
     *
     * @param buConcactMan 业务联系人
     */
    public void setBuConcactMan(String buConcactMan) {
        this.buConcactMan = buConcactMan;
    }

    /**
     * 获取 业务联系人邮箱
     *
     * @return buConcactMail 业务联系人邮箱
     */
    public String getBuConcactMail() {
        return this.buConcactMail;
    }

    /**
     * 设置 业务联系人邮箱
     *
     * @param buConcactMail 业务联系人邮箱
     */
    public void setBuConcactMail(String buConcactMail) {
        this.buConcactMail = buConcactMail;
    }

    /**
     * 获取 业务联系人电话
     *
     * @return buConcactPhone 业务联系人电话
     */
    public String getBuConcactPhone() {
        return this.buConcactPhone;
    }

    /**
     * 设置 业务联系人电话
     *
     * @param buConcactPhone 业务联系人电话
     */
    public void setBuConcactPhone(String buConcactPhone) {
        this.buConcactPhone = buConcactPhone;
    }

    /**
     * 获取 是否有效     (0有效1删除）
     *
     * @return deleteFlag 是否有效     (0有效1删除）
     */
    public Integer getDeleteFlag() {
        return this.deleteFlag;
    }

    /**
     * 设置 是否有效     (0有效1删除）
     *
     * @param deleteFlag 是否有效     (0有效1删除）
     */
    public void setDeleteFlag(Integer deleteFlag) {
        this.deleteFlag = deleteFlag;
    }

    /**
     * 获取 失效期
     *
     * @return endTime 失效期
     */
    public Date getEndTime() {
        return this.endTime;
    }

    /**
     * 设置 失效期
     *
     * @param endTime 失效期
     */
    public void setEndTime(Date endTime) {
        this.endTime = endTime;
    }

    /**
     * 获取 注册类型(1外部数据导入注册，0系统数据包注册）
     *
     * @return regiType 注册类型(1外部数据导入注册，0系统数据包注册）
     */
    public Integer getRegiType() {
        return this.regiType;
    }

    /**
     * 设置 注册类型(1外部数据导入注册，0系统数据包注册）
     *
     * @param regiType 注册类型(1外部数据导入注册，0系统数据包注册）
     */
    public void setRegiType(Integer regiType) {
        this.regiType = regiType;
    }

    /**
     * 获取 数据块
     *
     * @return dataBlock 数据块
     */
    public Integer getDataBlock() {
        return this.dataBlock;
    }

    /**
     * 设置 数据块
     *
     * @param dataBlock 数据块
     */
    public void setDataBlock(Integer dataBlock) {
        this.dataBlock = dataBlock;
    }

    /**
     * 获取 是否域主表(0 不是，1 是 )
     *
     * @return isMaster 是否域主表(0 不是，1 是 )
     */
    public Integer getIsMaster() {
        return this.isMaster;
    }

    /**
     * 设置 是否域主表(0 不是，1 是 )
     *
     * @param isMaster 是否域主表(0 不是，1 是 )
     */
    public void setIsMaster(Integer isMaster) {
        this.isMaster = isMaster;
    }

    /**
     * 获取 优先级(1 高2 中3 低)
     *
     * @return priority 优先级(1 高2 中3 低)
     */
    public String getPriority() {
        return this.priority;
    }

    /**
     * 设置 优先级(1 高2 中3 低)
     *
     * @param priority 优先级(1 高2 中3 低)
     */
    public void setPriority(String priority) {
        this.priority = priority;
    }

    /**
     * 获取 行分隔符
     *
     * @return rowLimit 行分隔符
     */
    public String getRowLimit() {
        return this.rowLimit;
    }

    /**
     * 设置 行分隔符
     *
     * @param rowLimit 行分隔符
     */
    public void setRowLimit(String rowLimit) {
        this.rowLimit = rowLimit;
    }

    /**
     * 获取 列分隔符
     *
     * @return colLimit 列分隔符
     */
    public String getColLimit() {
        return this.colLimit;
    }

    /**
     * 设置 列分隔符
     *
     * @param colLimit 列分隔符
     */
    public void setColLimit(String colLimit) {
        this.colLimit = colLimit;
    }

    /**
     * 获取 重点任务(0 不是，1 是 )
     *
     * @return important 重点任务(0 不是，1 是 )
     */
    public Integer getImportant() {
        return this.important;
    }

    /**
     * 设置 重点任务(0 不是，1 是 )
     *
     * @param important 重点任务(0 不是，1 是 )
     */
    public void setImportant(Integer important) {
        this.important = important;
    }

    /**
     * 获取
     *
     * @return collectFrequency
     */
    public Integer getCollectFrequency() {
        return this.collectFrequency;
    }

    /**
     * 设置
     *
     * @param collectFrequency
     */
    public void setCollectFrequency(Integer collectFrequency) {
        this.collectFrequency = collectFrequency;
    }

    /**
     * 获取
     *
     * @return loadtype
     */
    public String getLoadtype() {
        return this.loadtype;
    }

    /**
     * 设置
     *
     * @param loadtype
     */
    public void setLoadtype(String loadtype) {
        this.loadtype = loadtype;
    }

    /**
     * 获取
     *
     * @return isPrimary
     */
    public String getIsPrimary() {
        return this.isPrimary;
    }

    /**
     * 设置
     *
     * @param isPrimary
     */
    public void setIsPrimary(String isPrimary) {
        this.isPrimary = isPrimary;
    }

    /**
     * 获取
     *
     * @return tableType
     */
    public String getTableType() {
        return this.tableType;
    }

    /**
     * 设置
     *
     * @param tableType
     */
    public void setTableType(String tableType) {
        this.tableType = tableType;
    }

    @Override
    public String toString() {
        return "MdDataPackageMsg{" +
                "oid='" + oid + '\'' +
                ", systemId='" + systemId + '\'' +
                ", dataCode='" + dataCode + '\'' +
                ", dataName='" + dataName + '\'' +
                ", dataInterval='" + dataInterval + '\'' +
                ", rowCount=" + rowCount +
                ", dataSize=" + dataSize +
                ", dataUnit='" + dataUnit + '\'' +
                ", valiTime=" + valiTime +
                ", domainId='" + domainId + '\'' +
                ", valiDate=" + valiDate +
                ", buConcactMan='" + buConcactMan + '\'' +
                ", buConcactMail='" + buConcactMail + '\'' +
                ", buConcactPhone='" + buConcactPhone + '\'' +
                ", deleteFlag=" + deleteFlag +
                ", endTime=" + endTime +
                ", regiType=" + regiType +
                ", dataBlock=" + dataBlock +
                ", isMaster=" + isMaster +
                ", priority='" + priority + '\'' +
                ", rowLimit='" + rowLimit + '\'' +
                ", colLimit='" + colLimit + '\'' +
                ", important=" + important +
                ", collectFrequency=" + collectFrequency +
                ", loadtype='" + loadtype + '\'' +
                ", isPrimary='" + isPrimary + '\'' +
                ", tableType='" + tableType + '\'' +
                '}';
    }
}
