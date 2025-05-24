package ops.example.backend.exception;

/**
 * @author ZhanHui TONG
 * @version 1.0
 * @created_date 2025-04-26-23:58
 */
public class CustomerException extends Throwable {

    private final String msg;
    public CustomerException(String string) {
        this.msg = string;
    }
    public String getMsg() {
        return msg;
    }
}
