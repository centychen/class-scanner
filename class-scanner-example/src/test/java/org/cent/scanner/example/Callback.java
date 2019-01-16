package org.cent.scanner.example;

import lombok.extern.slf4j.Slf4j;
import org.cent.scanner.core.anno.Scannable;
import org.cent.scanner.core.callback.ScannerCallback;

import java.util.List;

/**
 * @author: cent
 * @email: 292462859@qq.com
 * @date: 2019/1/11.
 * @description: 测试回调
 */
@Slf4j
@Scannable
public class Callback implements ScannerCallback {
    @Override
    public void callback(List<Class> clzs) {
        log.info("[class-scanner-testcase-auto-callback]scan total {} classes.", clzs.size());
    }
}
