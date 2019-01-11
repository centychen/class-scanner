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
    public void run(ApplicationArguments args) throws Exception {
        if (!scannerProp.isEnable()) {
            return;
        }

        if (EmptyUtil.isEmpty(scannerProp.getPackages())) {
            return;
        }

        if (EmptyUtil.isEmpty(scannerProp.getCallback())) {
            return;
        }

        Class<Annotation> anno = null;
        if (EmptyUtil.isNotEmpty(scannerProp.getAnnotation())) {
            anno = (Class) Thread.currentThread().getContextClassLoader()
                    .loadClass(scannerProp.getAnnotation());
        }

        ScannerCallback callback = (ScannerCallback) Thread.currentThread().getContextClassLoader()
                .loadClass(scannerProp.getCallback()).newInstance();

        if (anno == null) {
            classScanner.scanAndCallback(scannerProp.getPackages(), callback);
        } else {
            classScanner.scanAndCallbackByAnno(scannerProp.getPackages(), anno, callback);
        }
    }
}
