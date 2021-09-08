package models.structs;

public class RespStatus {
    int code;
    int status;
    String msg;

    public RespStatus(int code, int status, String msg) {
        this.code = code;
        this.status = status;
        this.msg = msg;
    }

    public RespStatus() {
        this.code = -1;
        this.status = -1;
        this.msg = null;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public int getStatus() {
        return status;
    }

    public String getMsg() {
        return msg;
    }

    public int getCode() {
        return code;
    }
}
