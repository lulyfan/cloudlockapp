package com.ut.base;

/**
 * author : chenjiajun
 * time   : 2018/12/26
 * desc   :
 */
public class VersionInfo {

    private long id;
    private String version;
    private int typeId;
    private String fileUrl;
    private long fizeSize;
    private String description;
    private long createTime;
    private String extProps;

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    public int getTypeId() {
        return typeId;
    }

    public void setTypeId(int typeId) {
        this.typeId = typeId;
    }

    public String getFileUrl() {
        return fileUrl;
    }

    public void setFileUrl(String fileUrl) {
        this.fileUrl = fileUrl;
    }

    public long getFizeSize() {
        return fizeSize;
    }

    public void setFizeSize(long fizeSize) {
        this.fizeSize = fizeSize;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public long getCreateTime() {
        return createTime;
    }

    public void setCreateTime(long createTime) {
        this.createTime = createTime;
    }

    public String getExtProps() {
        return extProps;
    }

    public void setExtProps(String extProps) {
        this.extProps = extProps;
    }
}
