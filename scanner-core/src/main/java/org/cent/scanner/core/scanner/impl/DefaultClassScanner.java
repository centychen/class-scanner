package org.cent.scanner.core.scanner.impl;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.cent.scanner.core.callback.ScannerCallback;
import org.cent.scanner.core.scanner.ClassScanner;
import org.cent.scanner.core.util.ExecutorUtil;
import org.cent.scanner.core.util.ScannerUtil;

import java.lang.annotation.Annotation;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.*;
import java.util.stream.Collectors;

/**
 * @author: cent
 * @email: 292462859@qq.com
 * @date: 2019/1/7.
 * @description: 默认类扫描器实现类
 */
@Slf4j
public class DefaultClassScanner implements ClassScanner {

    @Override
    public List<Class> scan(String... scanBasePackages) {
        List<Class> classList = new LinkedList<>();

        //没有需要扫描的包，返回空列表
        if (scanBasePackages.length == 0) {
            return classList;
        }

        //创建异步线程
        List<FutureTask<List<Class>>> tasks = new LinkedList<>();
        Arrays.asList(scanBasePackages)
                .forEach(pkg -> {
                    ScannerCallable call = new ScannerCallable(pkg);
                    FutureTask<List<Class>> task = new FutureTask(call);
                    ExecutorUtil.executeInPool(new Thread(task));
                    tasks.add(task);
                });

        //等待返回结果
        tasks.parallelStream().forEach(task -> {
            try {
                classList.addAll(task.get());
            } catch (InterruptedException | ExecutionException e) {
                log.error(e.getMessage(), e);
            }
        });


        return classList;
    }

    @Override
    public List<Class> scanByAnno(Class<? extends Annotation> anno, String... scanBasePackages) {
        List<Class> classList = this.scan(scanBasePackages);

        //根据Annotation过滤并返回
        return classList.parallelStream()
                .filter(clz -> {
                    if (clz.getAnnotation(anno) == null) {
                        return false;
                    }
                    return true;
                })
                .collect(Collectors.toList());
    }

    @Override
    public void scanAndCallback(ScannerCallback callback, String... scanBasePackages) {
        List<Class> classList = this.scan(scanBasePackages);
        callback.callback(classList);
    }

    @Override
    public void scanAndCallbackByAnno(ScannerCallback callback, Class<? extends Annotation> anno, String... scanBasePackages) {
        List<Class> classList = this.scanByAnno(anno, scanBasePackages);
        callback.callback(classList);
    }

    /**
     * 扫描器线程类
     */
    @AllArgsConstructor
    class ScannerCallable implements Callable<List<Class>> {

        /**
         * 扫描的包名称
         */
        private String pkg;

        @Override
        public List<Class> call() {
            return ScannerUtil.scan(pkg);
        }
    }
}
