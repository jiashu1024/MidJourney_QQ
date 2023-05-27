package com.zjs.mj.util;

import cn.hutool.http.HttpUtil;
import lombok.extern.slf4j.Slf4j;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

@Slf4j
public class FileUtil {

    public static final int MAX_RETRY = 3;

    public static File downloadFile(String url) throws IOException {
        log.info("Downloading file from {}", url);
        if (url == null) {
            return null;
        }
        File tempFile = cn.hutool.core.io.FileUtil.createTempFile();
        long l = 0;
        int count = 0;
        while (count++ < MAX_RETRY) {
            try {
                l = HttpUtil.downloadFile(url, tempFile);
            } catch (Exception e) {
                log.error("Download file failed,begin retry {}", count+1);
            }
            if (l > 0) {
                break;
            }
        }

        if (l > 0 && url.endsWith(".webp")) {
            log.info("Downloaded file from {}, size: {}", url, l);
            log.info("convert webp to png");
            BufferedImage image = ImageIO.read(tempFile);
            File png = cn.hutool.core.io.FileUtil.createTempFile();
            ImageIO.write(image, "png", png);
            return png;
        }

        if (l > 0) {
            log.info("Downloaded file from {}, size: {}", url, l);
            return tempFile;
        } else {
            log.error("Download file from {} failed", url);
            return null;
        }
    }
}