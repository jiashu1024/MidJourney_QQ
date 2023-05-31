package com.zjs.mj.util;

import cn.hutool.core.io.StreamProgress;
import cn.hutool.core.lang.Console;
import cn.hutool.http.HttpUtil;
import com.luciad.imageio.webp.WebPReadParam;
import com.zjs.mj.service.WxBotService;
import lombok.extern.slf4j.Slf4j;

import javax.imageio.ImageIO;
import javax.imageio.ImageReader;
import javax.imageio.stream.FileImageInputStream;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.net.URL;

@Slf4j
public class FileUtil {

    public static final int MAX_RETRY = 3;

    private static volatile boolean ok = false;

    public static File downloadFile(String url) throws IOException {
        log.info("Downloading file from {}", url);
        if (url == null) {
            return null;
        }

        String name = url.substring(url.lastIndexOf("/") + 1);
        File tempFile = cn.hutool.core.io.FileUtil.createTempFile();

        // HttpUtil.downloadFile(url, tempFile);
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
                        ok = true;

                    }
                });
            } catch (Exception e) {
                log.error("Download file [{}] failed,begin retry {}", name, count + 1);
                WxBotService.sendText("Download file [" + name + "] failed,begin retry " + count + 1);
            }

            while (!ok) {
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }

           // BufferedImage image = ImageIO.read(new URL(url));

            if (url.endsWith(".webp")) {
                log.info("Downloaded file {} success", name);
                log.info("convert webp to png");

                File png = cn.hutool.core.io.FileUtil.createTempFile();
                webpToPng(tempFile, png);
                return png;
            }
            return tempFile;
        }
        return null;
    }

    public static File webpToPng(File webpFile, File pngFile) throws IOException {
        ImageReader reader = ImageIO.getImageReadersByMIMEType("image/webp").next();

        // Configure decoding parameters
        WebPReadParam readParam = new WebPReadParam();
        readParam.setBypassFiltering(true);

        // Configure the input on the ImageReader
        reader.setInput(new FileImageInputStream(webpFile));

        // Decode the image
        BufferedImage image = reader.read(0, readParam);

        //ImageIO.write(image, "png", new File(outputPngPath));
        ImageIO.write(image, "jpg", pngFile);
       // ImageIO.write(image, "jpeg", new File(outputJpegPath));
        return pngFile;
    }
}