package org.cent.scanner.starter.prop;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

import java.util.LinkedList;
import java.util.List;

/**
 * @author: cent
 * @email: 292462859@qq.com
 * @date: 2019/1/11.
 * @description: 类扫描器配置文件
 */
@ConfigurationProperties(prefix = "org.scanner.auto")
@Data
public class ScannerProp {

    /**
     * 是否自动启动
     */
    private boolean enable = false;
    /**
     * 需扫描的包列表
     */
    private List<String> packages = new LinkedList<>();
    /**
     * 回调函数
     */
    private String callback;
    /**
     * 标识注解
     */
    private String annotation;


}
