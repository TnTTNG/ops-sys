package ops.example.backend.exception;

/**
 * @author ZhanHui TONG
 * @version 1.0
 * @created_date 2025-04-20-18:32
 */
public class UsersException extends RuntimeException {
    private String code;
    private String msg;

    public UsersException(String code, String msg) {
        this.code = code;
        this.code = msg;
    }

    public UsersException(){}
    public UsersException(String msg) {
        this.code = "500";
        this.msg = msg;
    }
    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }
}
