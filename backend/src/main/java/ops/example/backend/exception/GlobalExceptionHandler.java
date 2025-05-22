package ops.example.backend.exception;

/**
 * @author ZhanHui TONG
 * @version 1.0
 * @created_date 2025-04-20-17:56
 */

import ops.example.backend.common.Result;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;

/**
 * 全局异常捕获器
 */
@ControllerAdvice("ops.example.backend.controller")
public class GlobalExceptionHandler {

    private static final Logger log = LoggerFactory.getLogger(GlobalExceptionHandler.class);
    // @ExceptionHandler(Exception.class)
    // @ResponseBody
    // public Result error(Exception e) {
    //     log.error("系统异常", e);
    //     return Result.error("系统异常");
    // }

    // @ExceptionHandler(UsersException.class)
    // @ResponseBody
    // public Result UserError(UsersException e) {
    //     log.error("自定义错误", e);
    //     return Result.error(e.getCode(), e.getMsg());
    // }

    @ExceptionHandler(CustomerException.class)
    @ResponseBody
    public Result CustomerError(CustomerException e) {
        log.error("自定义错误", e);
        Result result = Result.error("400", e.getMsg());
        result.setMsg(e.getMsg());
        return result;
    }
}
