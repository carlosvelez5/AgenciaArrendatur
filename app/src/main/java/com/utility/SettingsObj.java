package com.utility;

/**
 * Created by Carlos VÉLEZ on 01/11/2016.
 * SettingsObj: Clase padre de las settings de la App
 */

public class SettingsObj {
    private int mId;
    private String mkeyName;
    private String mKeyValue;
    private String mkeyType;
    private int mLength;

    public SettingsObj(int id, String keyName, String keyValue, String keyType, int length) {
        mId = id;
        mkeyName = keyName;
        mKeyValue = keyValue;
        mkeyType = keyType;
        mLength = length;
    }

    public int getId() {
        return mId;
    }

    public void setId(int id) {
        mId = id;
    }

    public String getKeyName() {
        return mkeyName;
    }

    public void setKeyName(String keyName) {
        mkeyName = keyName;
    }

    public String getKeyValue() {
        return mKeyValue;
    }

    public void setKeyValue(String keyValue) {
        mKeyValue = keyValue;
    }

    public String getKeyType() {
        return mkeyType;
    }

    public void setKeyType(String mkeyType) {
        mkeyType = mkeyType;
    }

    public int getLength() {
        return mLength;
    }

    public void setLength(int length) {
        mLength = length;
    }
}
