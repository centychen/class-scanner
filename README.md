# class-scanner

#### 介绍
class-scanner为一个Java类扫描器，用于获取指定包下的Class类，同时可根据指定Annotation进行过滤。

实现功能：
- 获取包下所有的Class类。
- 获取包下所有的Class类，并执行回调。
- 获取包下带有指定注解的Class类。
- 获取包下带有指定注解的Class类，并执行回调。
- SpringBootStarter特有功能：
    - 项目启动时，自动获取指定包下所有Class类，并执行回调。
    - 项目启动时，自动获取指定包下带有指定注解的Class类，并执行回调。

#### 项目结构
class-scanner
- scanner-core：核心包
- scanner-spring-boot-starter：Spring Boot的starter模块。
- class-scanner-example：示例包

#### 使用说明
由于项目并没有上传Maven中央仓库，使用的是码云项目托管，故需要在项目的仓库配置中增加以下配置：
```xml
<repositories>
    <repository>
        <id>cent-repo</id>
        <url>https://gitee.com/centy/maven/raw/master</url>
    </repository>
</repositories>
```

##### 1.使用核心包scanner-core
- 添加依赖（以maven为例，gradle自行参考）

在项目添加以下依赖包：
```xml
<dependency>
    <groupId>org.cent</groupId>
    <artifactId>scanner-core</artifactId>
    <version>1.0.1-SNAPSHOT</version>
</dependency>
```

- 示例代码

可参考以下测试用例代码：
```java
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

```

##### 2.SpringBoot项目使用Starter
- 添加依赖
```xml
<dependency>
    <groupId>org.cent</groupId>
    <artifactId>scanner-spring-boot-starter</artifactId>
    <version>1.0.1-SNAPSHOT</version>
</dependency>
```

- 示例代码【自动扫描】

a.开发回调函数
```java
package org.cent.demo.scanner.callback;

import lombok.extern.slf4j.Slf4j;
import org.cent.scanner.core.callback.ScannerCallback;

import java.util.List;

@Slf4j
public class CustomCallback implements ScannerCallback {

    @Override
    public void callback(List<Class> list) {
        list.forEach(clz -> {
            log.info(clz.getName());
        });
    }
}

```

b.在application.yml中增加以下配置：
```yaml
org:
  scanner:
    auto:
      enable: true
      callback: org.cent.demo.scanner.callback.CustomCallback
      packages:
        - org.cent
```

c.启动执行，输出如下
```
2019-01-16 15:53:50.713  INFO 4365 --- [           main] o.c.d.scanner.callback.CustomCallback    : org.cent.demo.scanner.Application
2019-01-16 15:53:50.714  INFO 4365 --- [           main] o.c.d.scanner.callback.CustomCallback    : org.cent.demo.scanner.callback.CustomCallback
2019-01-16 15:53:50.714  INFO 4365 --- [           main] o.c.d.scanner.callback.CustomCallback    : org.cent.scanner.starter.prop.ScannerProp
2019-01-16 15:53:50.714  INFO 4365 --- [           main] o.c.d.scanner.callback.CustomCallback    : org.cent.scanner.starter.config.ScannerAutoConfiguration
2019-01-16 15:53:50.714  INFO 4365 --- [           main] o.c.d.scanner.callback.CustomCallback    : org.cent.scanner.starter.runner.AutoScanner
2019-01-16 15:53:50.714  INFO 4365 --- [           main] o.c.d.scanner.callback.CustomCallback    : org.cent.scanner.core.util.URLUtil
2019-01-16 15:53:50.714  INFO 4365 --- [           main] o.c.d.scanner.callback.CustomCallback    : org.cent.scanner.core.util.ScannerUtil
2019-01-16 15:53:50.714  INFO 4365 --- [           main] o.c.d.scanner.callback.CustomCallback    : org.cent.scanner.core.util.EmptyUtil
2019-01-16 15:53:50.714  INFO 4365 --- [           main] o.c.d.scanner.callback.CustomCallback    : org.cent.scanner.core.util.ScannerUtil$UrlScanCallable
2019-01-16 15:53:50.714  INFO 4365 --- [           main] o.c.d.scanner.callback.CustomCallback    : org.cent.scanner.core.util.ClassUtil
2019-01-16 15:53:50.714  INFO 4365 --- [           main] o.c.d.scanner.callback.CustomCallback    : org.cent.scanner.core.util.NamedThreadFactory
...
```

- 示例代码【核心包】

默认扫描器已初始化到Spring容器中，可直接通过依赖注入，其他使用同直接使用核心包。
```
@Autowired
private ClassScanner classScanner;
```

#### 联系方式
- QQ：292462859
- Email：292462859@qq.com
- 博客：https://my.oschina.net/centychen