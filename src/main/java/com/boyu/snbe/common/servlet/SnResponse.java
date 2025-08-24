package com.boyu.snbe.common.servlet;


import java.util.HashMap;

public class SnResponse extends HashMap<String, Object> {

    public static SnResponse ok() {
        SnResponse res = new SnResponse();
        res.put("code", 200);
        res.put("msg", "success");
        return res;
    }

    public static SnResponse error() {
        SnResponse res = new SnResponse();
        res.put("code", 500);
        res.put("msg", "未知异常，请联系管理员");
        return res;
    }

}
