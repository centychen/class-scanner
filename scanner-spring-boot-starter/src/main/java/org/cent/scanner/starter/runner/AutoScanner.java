package org.cent.scanner.starter.runner;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.cent.scanner.core.callback.ScannerCallback;
import org.cent.scanner.core.scanner.ClassScanner;
import org.cent.scanner.core.util.EmptyUtil;
import org.cent.scanner.starter.prop.ScannerProp;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;

import java.lang.annotation.Annotation;

/**
 * @author: cent
 * @email: 292462859@qq.com
 * @date: 2019/1/11.
 * @description: 类自动扫描器，继承ApplicationRunner类
 */
@Slf4j
@AllArgsConstructor
public class AutoScanner implements ApplicationRunner {

    private ScannerProp scannerProp;

    private ClassScanner classScanner;

    @Override
    public void run(ApplicationArguments args) {
        //未启用自动扫描，返回
        if (!scannerProp.isEnable()) {
            return;
        }

        //没有需要扫描的包，返回
        if (EmptyUtil.isEmpty(scannerProp.getPackages())) {
            log.warn("[class-scanner]packages not set.");
            return;
        }

        //回调函数为空，返回
        if (EmptyUtil.isEmpty(scannerProp.getCallback())) {
            log.warn("[class-scanner]callback not set.");
            return;
        }

        //获取配置注解类
        Class<Annotation> anno = null;
        if (EmptyUtil.isNotEmpty(scannerProp.getAnnotation())) {
            try {
                anno = (Class) Thread.currentThread().getContextClassLoader().loadClass(scannerProp.getAnnotation());
            } catch (ClassNotFoundException e) {
                log.error("[class-scanner]load annotation {} error.", scannerProp.getAnnotation());
                return;
            }
        }

        //获取配置的回调类实例
        ScannerCallback callback = null;
        try {
            callback = (ScannerCallback) Thread.currentThread().getContextClassLoader()
                    .loadClass(scannerProp.getCallback()).newInstance();
        } catch (InstantiationException | IllegalAccessException | ClassNotFoundException e) {
            log.error("[class-scanner]load callback {} error.", scannerProp.getCallback());
            return;
        }

        //执行扫描
        if (anno == null) {
            classScanner.scanAndCallback(scannerProp.getPackages(), callback);
        } else {
            classScanner.scanAndCallbackByAnno(scannerProp.getPackages(), anno, callback);
        }
    }
}
