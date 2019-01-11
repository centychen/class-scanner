package org.cent.scanner.core.scanner;

import lombok.extern.slf4j.Slf4j;
import org.cent.scanner.core.anno.Scannable;
import org.cent.scanner.core.callback.ScannerCallback;
import org.cent.scanner.core.scanner.impl.DefaultClassScanner;
import org.junit.Test;
import sun.jvm.hotspot.utilities.Assert;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.util.Arrays;
import java.util.List;

/**
 * @author: cent
 * @email: 292462859@qq.com
 * @date: 2019/1/8.
 * @description:
 */
@Scannable
@ClassScannerTestCase.CustomScannable
@Slf4j
public class ClassScannerTestCase {

    private final List<String> scanPkgs = Arrays.asList(
            "org", "lombok", "com.sun", "javax"
    );
    private ClassScanner classScanner = new DefaultClassScanner();

    /**
     * 测试用例：扫描多个包
     */
    @Test
    public void testScan() {
        List<Class> classList = classScanner.scan(scanPkgs);
        Assert.that(classList.size() > 0, "扫描失败，返回为空！");
        log.info("共扫描到{}个类", classList.size());
    }

    /**
     * 测试用例：扫描多个包下带有Scannable注解的类
     */
    @Test
    public void testScanByAnno() {
        List<Class> classList = classScanner.scanByAnno(scanPkgs, Scannable.class);
        Assert.that(classList.size() > 0, "扫描失败，返回为空！");
        log.info("共扫描到{}个类", classList.size());
    }

    /**
     * 测试用例：扫描多个包并执行callback方法
     */
    @Test
    public void testScanAndCallback() {
        classScanner.scanAndCallback(scanPkgs, new TestCallback());
    }

    /**
     * 测试用例：扫描多个包带有CustomScannable注解的类，并执行callback。
     */
    @Test
    public void testScanAndCallbackByAnno() {
        classScanner.scanAndCallbackByAnno(scanPkgs, CustomScannable.class, new TestCallback());
    }


    /**
     * callback方法
     */
    @Slf4j
    static class TestCallback implements ScannerCallback {
        @Override
        public void callback(List<Class> clzs) {
            clzs.forEach(clz -> log.info(clz.getName()));
            Assert.that(clzs.size() > 0, "扫描结果数量错误！");
        }
    }

    /**
     * 自定义注解
     */
    @Target(ElementType.TYPE)
    @Retention(RetentionPolicy.RUNTIME)
    static @interface CustomScannable {

    }
}
