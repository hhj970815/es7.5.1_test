package cn.es.search.model.bo;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;

/**
 * @author dengbh
 * @date 2020-06-16
 * <p>
 * 返回给前台的通用包装
 */

@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ResponseData<T> {

    public static final String DEFAULT_SUCCESS_MESSAGE = "请求成功";

    public static final String DEFAULT_ERROR_MESSAGE = "网络异常";

    public static final Integer DEFAULT_SUCCESS_CODE = 200;

    public static final Integer DEFAULT_ERROR_CODE = 999;

    public static final String DEFAULT_ERROR_NO = "somethingError";

    /**
     * 响应状态码
     */
    private Integer code;

    /**
     * 响应信息
     */
    private String msg;

    /**
     * 成功的响应信息
     */
    private T data;

    public ResponseData(Integer code, String msg, T data) {
        this.code = code;
        this.msg = msg;
        this.data = data;
    }

    public static <T> ResponseData success(T object) {
        return new ResponseData(DEFAULT_SUCCESS_CODE, DEFAULT_SUCCESS_MESSAGE, object);
    }

    public static ResponseData success() {
        return new ResponseData(DEFAULT_SUCCESS_CODE, DEFAULT_SUCCESS_MESSAGE, null);
    }

    public static <T> ResponseData success(String msg, T object) {
        return new ResponseData(DEFAULT_SUCCESS_CODE, msg, object);
    }
}



