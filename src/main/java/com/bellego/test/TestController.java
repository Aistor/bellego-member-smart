package com.bellego.test;

import com.bellego.common.result.Result;
import com.bellego.common.result.ResultBuilder;
import com.bellego.utils.RedisUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.concurrent.TimeUnit;

@RestController
@RequestMapping("/test")
public class TestController {
    @Autowired
    private RedisUtils redisUtils;

    @PutMapping("/redis/{id}")
    public Result<String> redisSetTest(@PathVariable String id) {
        String key = "test:" + id;
        redisUtils.set(key, id, 600L, TimeUnit.SECONDS);
        return ResultBuilder.success();
    }

    @GetMapping("/redis/{id}")
    public Result<String> redisGetTest(@PathVariable String id) {
        String key = "test:" + id;
        String value = redisUtils.get(key).toString();
        return ResultBuilder.success(value);
    }
}
