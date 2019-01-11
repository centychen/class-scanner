package org.cent.scanner.starter.config;

import org.cent.scanner.core.scanner.ClassScanner;
import org.cent.scanner.core.scanner.impl.DefaultClassScanner;
import org.cent.scanner.starter.prop.ScannerProp;
import org.cent.scanner.starter.runner.AutoScanner;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @author: cent
 * @email: 292462859@qq.com
 * @date: 2019/1/11.
 * @description: 自动化配置类
 */
@Configuration
@EnableConfigurationProperties({ScannerProp.class})
public class ScannerAutoConfiguration {

    /**
     * 初始化自动类扫描器
     *
     * @param scannerProp
     * @param classScanner
     * @return
     */
    @Bean
    public AutoScanner autoScanner(ScannerProp scannerProp, ClassScanner classScanner) {
        return new AutoScanner(scannerProp, classScanner);
    }

    /**
     * 初始化类扫描器
     *
     * @return
     */
    @Bean
    public ClassScanner classScanner() {
        return new DefaultClassScanner();
    }
}
