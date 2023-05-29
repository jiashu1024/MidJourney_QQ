package com.zjs.mj.Bot;

import com.zjs.mj.config.Properties;
import com.zjs.mj.service.WxBotService;
import kotlin.coroutines.Continuation;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.mamoe.mirai.Bot;
import net.mamoe.mirai.auth.QRCodeLoginListener;
import net.mamoe.mirai.network.LoginFailedException;
import net.mamoe.mirai.utils.LoginSolver;
import net.mamoe.mirai.utils.StandardCharImageLoginSolver;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class QqLoginSolver extends LoginSolver {

    private final Properties properties;

    private int n = 2;

    @Nullable
    @Override
    public Object onSolvePicCaptcha(@NotNull Bot bot, @NotNull byte[] bytes, @NotNull Continuation<? super String> continuation) {
        return new StandardCharImageLoginSolver().onSolvePicCaptcha(bot, bytes, continuation);
    }

    @Nullable
    @Override
    public Object onSolveSliderCaptcha(@NotNull Bot bot, @NotNull String s, @NotNull Continuation<? super String> continuation) {
        return new StandardCharImageLoginSolver().onSolveSliderCaptcha(bot, s, continuation);
    }

    @NotNull
    @Override
    public QRCodeLoginListener createQRCodeLoginListener(@NotNull Bot bot) {
        if (properties.getWx().getWebHook() == null || properties.getWx().getWebHook().isEmpty()) {
            return new StandardCharImageLoginSolver().createQRCodeLoginListener(bot);
        }
        return new QRCodeLoginListener() {
            @Override
            public void onFetchQRCode(@NotNull Bot bot, @NotNull byte[] bytes) {
                log.info("使用手机QQ扫一扫登录，不要从相册扫描");
                WxBotService.sendText("使用手机QQ扫一扫登录，不要从相册扫描");
                WxBotService.sendImage(bytes);
            }

            @Override
            public void onStateChanged(@NotNull Bot bot, @NotNull QRCodeLoginListener.State state) {
                log.info("扫码登录状态变更：{}", state.name());
                WxBotService.sendText("扫码登录状态变更：" + state.name());
                if (state == State.TIMEOUT) {
                    if (n > 0) {
                        n--;
                        WxBotService.sendText("多次未扫码将自动取消登录");
                    } else {
                       throw  new RuntimeException("多次未扫码");
                    }
                }
            }
        };
    }


}
