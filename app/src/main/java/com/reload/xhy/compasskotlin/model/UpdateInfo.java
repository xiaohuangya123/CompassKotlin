package com.reload.xhy.compasskotlin.model;

/**
 * {
 *     "data": {
 *         "downloadUrl": "http://a5.pc6.com/cx3/weixin.pc6.apk",
 *         "version": "1.0.1",
 *         "versionCode": 2,
 *         "versionDesc": "主要修改:     1.增加多项新功能;     2.修复已知bug。"
 *     },
 *     "errCode": 0,
 *     "errMsg": "",
 *     "success": true
 * }
 */
public class UpdateInfo {
    private Data data;
    private int errCode;
    private String errMsg;
    private boolean success;

    public Data getData() {
        return data;
    }

    public void setData(Data data) {
        this.data = data;
    }

    public int getErrCode() {
        return errCode;
    }

    public void setErrCode(int errCode) {
        this.errCode = errCode;
    }

    public String getErrMsg() {
        return errMsg;
    }

    public void setErrMsg(String errMsg) {
        this.errMsg = errMsg;
    }

    public boolean isSuccess() {
        return success;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }

}
