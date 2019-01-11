package org.cent.scanner.core.util;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.cent.scanner.core.consts.CommonConsts;
import org.cent.scanner.core.consts.ProtocolTypes;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.net.URL;
import java.net.URLDecoder;
import java.util.Arrays;
import java.util.Enumeration;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.*;
import java.util.jar.JarEntry;
import java.util.jar.JarInputStream;

import static org.cent.scanner.core.consts.CommonConsts.PACKAGE_SEPARATOR;
import static org.cent.scanner.core.consts.CommonConsts.PATH_SEPARATOR;


/**
 * @author: cent
 * @email: 292462859@qq.com
 * @date: 2019/1/8.
 * @description: 扫描工具类，实现文件相关扫描方法
 */
@Slf4j
public enum ScannerUtil {
    ;

    /**
     * 扫描某个包下所有Class类
     *
     * @param pkg 扫描报名
     * @return Class列表
     */
    public static List<Class> scan(String pkg) {
        List<Class> classList = new LinkedList<>();
        try {
            //包名转化为路径名
            String pathName = ClassUtil.package2Path(pkg);
            //获取路径下URL
            Enumeration<URL> urls = Thread.currentThread().getContextClassLoader().getResources(pathName);
            //循环扫描路径
            classList = scanUrls(pkg, urls);
        } catch (Exception e) {
            log.error("扫描包路径出错：{}", pkg, e);
        }

        return classList;
    }

    /**
     * 扫描多个Url路径，找出符合包名的Class类
     *
     * @param pkg
     * @param urls
     * @return Class列表
     * @throws IOException
     */
    private static List<Class> scanUrls(String pkg, Enumeration<URL> urls) {
        List<Class> classList = new LinkedList<>();

        //Enumeration转list
        List<URL> urlList = new LinkedList<>();
        while (urls.hasMoreElements()) {
            urlList.add(urls.nextElement());
        }

        //创建异步任务
        List<FutureTask<List<Class>>> tasks = new LinkedList<>();
        urlList.forEach(url -> {
            UrlScanCallable call = new UrlScanCallable(pkg, url);
            FutureTask<List<Class>> task = new FutureTask<>(call);
            ExecutorUtil.executeInPool(new Thread(task));
            tasks.add(task);
        });

        //等待并处理返回结果
        tasks.parallelStream().forEach(task -> {
            try {
                classList.addAll(task.get());
            } catch (Exception e) {
                log.error(e.getMessage(), e);
            }
        });

        return classList;
    }

    /**
     * 递归扫描指定文件路径下的Class文件
     *
     * @param pkg
     * @param filePath
     * @return Class列表
     */
    private static List<Class> recursiveScan4Path(String pkg, String filePath) {
        List<Class> classList = new LinkedList<>();

        //读取文件
        File file = new File(filePath);
        if (!file.exists() || !file.isDirectory()) {
            return classList;
        }

        //处理类文件
        File[] classes = file.listFiles(child -> ClassUtil.isClass(child.getName()));
        Arrays.asList(classes).forEach(child -> {
            String className = ClassUtil.classFile2SimpleClass(
                    new StringBuilder().append(pkg).append(PACKAGE_SEPARATOR).append(child.getName()).toString()
            );

            try {
                Class clz = Thread.currentThread().getContextClassLoader().loadClass(className);
                classList.add(clz);
            } catch (ClassNotFoundException | LinkageError e) {
                log.warn("can load class:{}", className);
            }
        });

        //处理目录
        File[] dirs = file.listFiles(child -> child.isDirectory());
        Arrays.asList(dirs).forEach(child -> {
            String childPackageName = new StringBuilder()
                    .append(pkg)
                    .append(PACKAGE_SEPARATOR)
                    .append(child.getName())
                    .toString();
            String childPath = new StringBuilder()
                    .append(filePath)
                    .append(PATH_SEPARATOR)
                    .append(child.getName())
                    .toString();
            classList.addAll(
                    recursiveScan4Path(childPackageName, childPath)
            );
        });

        return classList;
    }

    /**
     * 递归扫描Jar文件内的Class类
     *
     * @param pkg
     * @param jarPath
     * @return Class列表
     * @throws IOException
     */
    private static List<Class> recursiveScan4Jar(String pkg, String jarPath) throws IOException {
        List<Class> classList = new LinkedList<>();

        //读取Jar文件
        JarInputStream jin = new JarInputStream(new FileInputStream(jarPath));
        JarEntry entry = jin.getNextJarEntry();
        while (entry != null) {
            String name = entry.getName();
            entry = jin.getNextJarEntry();

            //包名不匹配，跳过
            if (!name.contains(ClassUtil.package2Path(pkg))) {
                continue;
            }

            //判断是否类文件
            if (ClassUtil.isClass(name)) {
                if (ClassUtil.isAnonymousInnerClass(name)) {
                    //是匿名内部类，跳过
                    continue;
                }

                //文件名转类名
                String className = ClassUtil.classFile2SimpleClass(ClassUtil.path2Package(name));
                try {
                    //加载类文件
                    Class clz = Thread.currentThread().getContextClassLoader().loadClass(className);
                    classList.add(clz);
                } catch (ClassNotFoundException | LinkageError e) {
                    log.warn("can load class:{}", name);
                }
            }
        }

        return classList;
    }

    /**
     * URL扫描线程类
     */
    @AllArgsConstructor
    @Slf4j
    static class UrlScanCallable implements Callable<List<Class>> {

        /**
         * 需匹配的包名
         */
        private String pkg;
        /**
         * 需扫描的URL
         */
        private URL url;

        @Override
        public List<Class> call() {
            //获取协议
            String protocol = url.getProtocol();

            List<Class> classList = new LinkedList<>();
            try {
                if (ProtocolTypes.file.name().equals(protocol)) {
                    //文件
                    String path = URLDecoder.decode(url.getFile(), CommonConsts.DEFAULT_CHARSET);
                    classList.addAll(recursiveScan4Path(pkg, path));

                } else if (ProtocolTypes.jar.name().equals(protocol)) {
                    //jar包
                    String jarPath = URLUtil.getJarPathFormUrl(url);
                    classList.addAll(recursiveScan4Jar(pkg, jarPath));
                }
            } catch (Exception e) {
                log.error(e.getMessage(), e);
            }

            return classList;
        }
    }
}
