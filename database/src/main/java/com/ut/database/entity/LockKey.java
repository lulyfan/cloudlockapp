package com.ut.database.entity;

import android.arch.persistence.room.Entity;
import android.arch.persistence.room.Ignore;
import android.arch.persistence.room.Index;
import android.arch.persistence.room.PrimaryKey;
import android.os.Parcel;
import android.os.Parcelable;
import android.support.annotation.NonNull;

/**
 * author : zhouyubin
 * time   : 2018/12/04
 * desc   :
 * version: 1.0
 */

/**
 * modelNum : null
 * electric : 0
 * latitude : null
 * blueKey : 3+y1DcCg
 * type : 41072
 * mac : CF:B4:29:5B:E8:3C
 * adminPwd : /Z6GD7aV
 * createTime : 1545113635000
 * ruleType : 1
 * lockVersion : null
 * id : 100
 * initTime : 1545113635000
 * status : 0
 * longitude : null
 */
@Entity(tableName = "lock_key", indices = {@Index("name"), @Index("mac"), @Index("groupId")})
public class LockKey implements Parcelable {
    @PrimaryKey
    @NonNull
    private String id;
    private String name;
    //锁状态（锁后台管理）
    private int status;
    /**
     * {@link com.ut.database.entity.EnumCollection.KeyStatus}
     */
    private int keyStatus;
    //产品型号
    private int modelNum;
    //锁组id
    private int groupId;
    /**
     * {@link com.ut.database.entity.EnumCollection.UserType}
     */
    private int userType;
    /**
     * {@link com.ut.database.entity.EnumCollection.KeyRuleType}
     */
    private int ruleType;
    private int electric;

    private int type;
    private String mac;
    //开锁钥匙
    private String blueKey;
    private String latitude;
    //管理员钥匙
    private String adminPwd;
    private long createTime;
    private String lockVersion;
    //初始化时间
    private long initTime;
    private String longitude;

    //加密
    private int encryptType;
    private String encryptKey;

    //钥匙id
    private long keyId;

    public int getCanOpen() {
        return canOpen;
    }

    public void setCanOpen(int canOpen) {
        this.canOpen = canOpen;
    }

    //是否触摸开锁
    private int canOpen = 0;// 0：关闭  1：开启

    //启动日期
    private String startTime;// "",
    //停止日期
    private String endTime;// "",
    //生效时间
    private String startTimeRange;// "",
    //失效时间
    private String endTimeRange;// ""

    private String weeks;


    @Ignore
    String statusStr;//状态字符串
    @Ignore
    String lockTypeStr;//蓝牙锁字符串
    @Ignore
    String keyTypeStr;//永久钥匙或限时钥匙
    @Ignore
    String electricityStr;//电量字符串

    @NonNull
    public String getId() {
        return id;
    }

    public void setId(@NonNull String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public int getModelNum() {
        return modelNum;
    }

    public void setModelNum(int modelNum) {
        this.modelNum = modelNum;
    }

    public int getGroupId() {
        return groupId;
    }

    public void setGroupId(int groupId) {
        this.groupId = groupId;
    }

    public int getUserType() {
        return userType;
    }

    public void setUserType(int userType) {
        this.userType = userType;
    }

    public int getRuleType() {
        return ruleType;
    }

    public void setRuleType(int ruleType) {
        this.ruleType = ruleType;
    }

    public int getElectric() {
        return electric;
    }

    public void setElectric(int electric) {
        this.electric = electric;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public String getMac() {
        return mac;
    }

    public void setMac(String mac) {
        this.mac = mac;
    }

    public String getBlueKey() {
        return blueKey;
    }

    public void setBlueKey(String blueKey) {
        this.blueKey = blueKey;
    }

    public String getLatitude() {
        return latitude;
    }

    public void setLatitude(String latitude) {
        this.latitude = latitude;
    }

    public String getAdminPwd() {
        return adminPwd;
    }

    public void setAdminPwd(String adminPwd) {
        this.adminPwd = adminPwd;
    }

    public long getCreateTime() {
        return createTime;
    }

    public void setCreateTime(long createTime) {
        this.createTime = createTime;
    }

    public String getLockVersion() {
        return lockVersion;
    }

    public void setLockVersion(String lockVersion) {
        this.lockVersion = lockVersion;
    }

    public long getInitTime() {
        return initTime;
    }

    public void setInitTime(long initTime) {
        this.initTime = initTime;
    }

    public String getLongitude() {
        return longitude;
    }

    public void setLongitude(String longitude) {
        this.longitude = longitude;
    }

    public String getStatusStr() {
        return statusStr;
    }

    public void setStatusStr(String[] statusStrs) {
        this.statusStr = statusStrs[keyStatus];
    }

    public String getLockTypeStr() {
        return lockTypeStr;
    }

    public void setLockTypeStr(String[] lockTypeStrs) {
        this.lockTypeStr = lockTypeStrs[0];
    }

    public String getKeyTypeStr() {
        return keyTypeStr;
    }

    public void setKeyTypeStr(String[] keyTypeStrs) {
        this.keyTypeStr = keyTypeStrs[ruleType];
    }

    public void setKeyTypeStr(String typeStr) {
        this.keyTypeStr = typeStr;
    }

    public String getElectricityStr() {
        return electricityStr;
    }

    public void setElectricityStr() {
        this.electricityStr = String.valueOf(electric) + "%";
    }

    public int getKeyStatus() {
        return keyStatus;
    }

    public void setKeyStatus(int keyStatus) {
        this.keyStatus = keyStatus;
    }

    public int getEncryptType() {
        return encryptType;
    }

    public void setEncryptType(int encryptType) {
        this.encryptType = encryptType;
    }

    public String getEncryptKey() {
        return encryptKey;
    }

    public void setEncryptKey(String encryptKey) {
        this.encryptKey = encryptKey;
    }


    public long getKeyId() {
        return keyId;
    }

    public void setKeyId(long keyId) {
        this.keyId = keyId;
    }

    public String getStartTime() {
        return startTime;
    }

    public void setStartTime(String startTime) {
        this.startTime = startTime;
    }

    public String getEndTime() {
        return endTime;
    }

    public void setEndTime(String endTime) {
        this.endTime = endTime;
    }

    public String getStartTimeRange() {
        return startTimeRange;
    }

    public void setStartTimeRange(String startTimeRange) {
        this.startTimeRange = startTimeRange;
    }

    public String getEndTimeRange() {
        return endTimeRange;
    }

    public void setEndTimeRange(String endTimeRange) {
        this.endTimeRange = endTimeRange;
    }

    public String getWeeks() {
        return weeks;
    }

    public void setWeeks(String weeks) {
        this.weeks = weeks;
    }

    public boolean isKeyValid() {
        return EnumCollection.KeyStatus.isKeyValue(keyStatus);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.id);
        dest.writeString(this.name);
        dest.writeInt(this.status);
        dest.writeInt(this.keyStatus);
        dest.writeInt(this.modelNum);
        dest.writeInt(this.groupId);
        dest.writeInt(this.userType);
        dest.writeInt(this.ruleType);
        dest.writeInt(this.electric);
        dest.writeInt(this.type);
        dest.writeString(this.mac);
        dest.writeString(this.blueKey);
        dest.writeString(this.latitude);
        dest.writeString(this.adminPwd);
        dest.writeLong(this.createTime);
        dest.writeString(this.lockVersion);
        dest.writeLong(this.initTime);
        dest.writeString(this.longitude);
        dest.writeInt(this.encryptType);
        dest.writeString(this.encryptKey);
        dest.writeLong(this.keyId);
        dest.writeString(this.statusStr);
        dest.writeString(this.lockTypeStr);
        dest.writeString(this.keyTypeStr);
        dest.writeString(this.electricityStr);
        dest.writeString(this.startTime);
        dest.writeString(this.endTime);
        dest.writeString(this.startTimeRange);
        dest.writeString(this.endTimeRange);
        dest.writeString(this.weeks);
    }

    public LockKey() {
    }

    protected LockKey(Parcel in) {
        this.id = in.readString();
        this.name = in.readString();
        this.status = in.readInt();
        this.keyStatus = in.readInt();
        this.modelNum = in.readInt();
        this.groupId = in.readInt();
        this.userType = in.readInt();
        this.ruleType = in.readInt();
        this.electric = in.readInt();
        this.type = in.readInt();
        this.mac = in.readString();
        this.blueKey = in.readString();
        this.latitude = in.readString();
        this.adminPwd = in.readString();
        this.createTime = in.readLong();
        this.lockVersion = in.readString();
        this.initTime = in.readLong();
        this.longitude = in.readString();
        this.encryptType = in.readInt();
        this.encryptKey = in.readString();
        this.keyId = in.readInt();
        this.statusStr = in.readString();
        this.lockTypeStr = in.readString();
        this.keyTypeStr = in.readString();
        this.electricityStr = in.readString();
        this.startTime = in.readString();
        this.endTime = in.readString();
        this.startTimeRange = in.readString();
        this.endTimeRange = in.readString();
        this.weeks = in.readString();
    }

    public static final Parcelable.Creator<LockKey> CREATOR = new Parcelable.Creator<LockKey>() {
        @Override
        public LockKey createFromParcel(Parcel source) {
            return new LockKey(source);
        }

        @Override
        public LockKey[] newArray(int size) {
            return new LockKey[size];
        }
    };
}
