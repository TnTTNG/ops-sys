package ops.example.backend.exception;

/**
 * @author ZhanHui TONG
 * @version 1.0
 * @created_date 2025-04-26-23:58
 */
public class CustomerException extends RuntimeException {
    private String code;

    public CustomerException(String message) {
        super(message);
        this.code = "500";
    }

    public CustomerException(String code, String message) {
        super(message);
        this.code = code;
    }

    public String getCode() {
        return code;
    }

    public String getMsg() {
        return getMessage();
    }
}
