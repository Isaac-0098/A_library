package cn.agehaa.utils;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.JSONObject;

public class Result {
    public static String okGetString(String message){
        Map<String, Object> map = new HashMap<>();
        map.put("code", 200);
        map.put("message", message);
        String s = JSONObject.toJSONString(map);
        return s;
    }

    public static String errorGetString(String message) {
        Map<String, Object> map = new HashMap<>();
        map.put("code", 400);
        map.put("message", message);
        return JSONObject.toJSONString(map);
    }

    public static <T> String okGetList(String msg, List<T> data) {
        Map<String, Object> result = new HashMap<>();
        result.put("code", 200);
        result.put("msg", msg);
        result.put("data", data);
        return JSON.toJSONString(result);
    }

    public static String okGetData(String msg, Map<String, Object> data) {
        Map<String, Object> result = new HashMap<>();
        result.put("code", 200);
        result.put("message", msg);
        result.put("data", data);
        return JSON.toJSONString(result);
    }
}
