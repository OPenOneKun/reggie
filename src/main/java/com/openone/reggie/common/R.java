package com.openone.reggie.common;

import lombok.Data;

import java.util.HashMap;
import java.util.Map;


/**
 * @author OPenOne
 *  通用的返回结果，服务器响应的数据都封装在这个类
 */
@Data

public class R<T> {

    //编码：1成功，0和其它数字为失败
    private Integer code;
    //封装的数据
    private T data;
    //响应信息
    private String msg;
    //动态数据
    private Map map = new HashMap();

    /**
     * @param object
     * @param <T>
     *如果业务执行结果为成功, 构建R对象时, 只需要调用 success 方法;
     * 如果需要返回数据传递 object 参数, 如果无需返回, 可以直接传递null。
     */
    public static <T> R<T> success(T object) {
        R<T> r = new R<T>();
        r.data = object;
        r.code = 1;
        return r;
    }

    /**
     *
     * @param msg
     * @param <T>
     * 如果业务执行结果为失败, 构建R对象时, 只需要调用error 方法, 传递错误提示信息即可。
     */
    public static <T> R<T> error(String msg) {
        R r = new R();
        r.msg = msg;
        r.code = 0;
        return r;
    }

    /**
     *
     * @param key
     * @param value
     * @return 添加方法
     */
    public R<T> add(String key, Object value) {
        this.map.put(key, value);
        return this;
    }
}
