package com.zjs.mj;

import com.zjs.mj.enums.ImagineMode;
import com.zjs.mj.entity.MjRequestResult;
import com.zjs.mj.service.MjService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
public class MjServiceTest {

    @Autowired
    MjService mjService;

    @Test
    public void testRelax() {
        MjRequestResult<Void> relax = mjService.relax();
        System.out.println(relax);
    }

    @Test
    public void testFast() {
        MjRequestResult<Void> fast = mjService.fast();
        System.out.println(fast);
    }

    @Test
    public void testImagine() throws InterruptedException {
        MjRequestResult<Void> res1 = mjService.imagine("a cat ,a horse", ImagineMode.RELAX);
        System.out.println(res1);
        Thread.sleep(4000);
        MjRequestResult<Void> res2 =  mjService.imagine("a cat ,a horse", ImagineMode.FAST);
        System.out.println(res2);
    }
}
