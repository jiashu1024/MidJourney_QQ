package com.zjs.mj.util;

import cn.hutool.core.io.StreamProgress;
import cn.hutool.core.lang.Console;
import cn.hutool.http.HttpUtil;
import com.zjs.mj.service.WxBotService;
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

        String name = url.substring(url.lastIndexOf("/") + 1);
        File tempFile = cn.hutool.core.io.FileUtil.createTempFile();

        int count = 0;
        while (count++ < MAX_RETRY) {
            try {

                long l = HttpUtil.downloadFile(url, tempFile, new StreamProgress() {
                    @Override
                    public void start() {
                        log.info("[{}]开始下载。。。。", name);
                    }

                    @Override
                    public void progress(long total, long progressSize) {
                        log.info("[{}]已下载：{}", name, cn.hutool.core.io.FileUtil.readableFileSize(progressSize));
                    }

                    @Override
                    public void finish() {
                        log.info("[{}]下载完成！", name);
                    }
                });
            } catch (Exception e) {
                log.error("Download file [{}] failed,begin retry {}", name, count + 1);
                WxBotService.sendText("Download file [" + name + "] failed,begin retry " + count + 1);
            }

            if (url.endsWith(".webp")) {
                log.info("Downloaded file {} success", name);
                log.info("convert webp to png");
                BufferedImage image = ImageIO.read(tempFile);
                File png = cn.hutool.core.io.FileUtil.createTempFile();
                ImageIO.write(image, "png", png);
                return png;
            }
            return tempFile;
        }
        return null;
    }
}