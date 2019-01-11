package org.cent.scanner.core.scanner;

import org.cent.scanner.core.callback.ScannerCallback;

import java.lang.annotation.Annotation;
import java.util.List;

/**
 * @author: cent
 * @email: 292462859@qq.com
 * @date: 2019/1/7.
 * @description:
 */
public interface ClassScanner {

    /**
     * 扫描某个包下的Class
     *
     * @param scanBasePackages
     * @return
     */
    List<Class> scan(List<String> scanBasePackages);

    /**
     * 扫描某个包下带有注解的Class
     *
     * @param scanBasePackages
     * @param anno
     * @return
     */
    List<Class> scanByAnno(List<String> scanBasePackages, Class<? extends Annotation> anno);

    /**
     * 扫描某个包下的Class，并执行回调
     *
     * @param scanBasePackages
     * @param callback
     */
    void scanAndCallback(List<String> scanBasePackages, ScannerCallback callback);

    /**
     * 扫描某个包下特定注解的Class，并执行回调
     *
     * @param scanBasePackages
     * @param anno
     * @param callback
     */
    void scanAndCallbackByAnno(List<String> scanBasePackages, Class<? extends Annotation> anno, ScannerCallback callback);
}
