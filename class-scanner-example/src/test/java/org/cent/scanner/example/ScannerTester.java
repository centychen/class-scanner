package org.cent.scanner.example;

import lombok.extern.slf4j.Slf4j;
import org.cent.scanner.core.scanner.ClassScanner;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import sun.jvm.hotspot.utilities.Assert;

import java.util.Arrays;
import java.util.List;

/**
 * @author: cent
 * @email: 292462859@qq.com
 * @date: 2019/1/11.
 * @description:
 */
@RunWith(SpringRunner.class)
@SpringBootTest(classes = {Application.class})
@Slf4j
public class ScannerTester {

    @Autowired
    private ClassScanner classScanner;

    @Test
    public void testScan() {
        List<Class> classList = classScanner.scan(Arrays.asList("org", "lombok"));
        Assert.that(classList.size() > 0, "scan failed!");
        log.info("[class-scanner-testcase-scan-simple]scan total {} classes.", classList.size());
    }


}
