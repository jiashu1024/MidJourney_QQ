1. 建立自己的频道 [教程](https://docs.midjourney.com/docs/quick-start)
2. 往自己频道添加discord机器人,拿到discord的usertoken，频道ID channel id，服务器ID guild id[教程](https://github.com/novicezk/midjourney-proxy/blob/main/docs/discord-bot.md)
3. 阿里云开通机器翻译[教程](aliyun.md)获取AccessKey ID和AccessKey Secret
4. 登录midjourney获取user id和user token[教程](midjourney.md)
5. 企业微信创建机器人获取webhook[教程](https://developer.qiniu.com/console/kb/10490/receptionmanagement-wechat?category=kb)

------

## 部署

本程序需要jdk8环境，由于qq使用密码登录会触发验证，进行控制台交互，所以暂不支持docker部署运行，但是项目依赖的mysql数据库可用docker运行。

1. 在项目根目录下运行以下命令，启动mysql数据库

```shell
docker run --name mj-mysql -e MYSQL_ROOT_PASSWORD=zhangsan -e MYSQL_DATABASE=midjourney -p 3306:3306 -v ./sql:/docker-entrypoint-initdb.d -d mysql:8.0
```

2. 下载release里的[jar包](https://github.com/1130600015/MidJourney_QQ/releases/tag/v1.0.0)，下载到项目根目录下
3. 修改application.yaml配置文件
4. java -jar mj-QQ-mirai-1.0.0.jar  

如果启动直接报错，除了报错禁止登录，可尝试重新启动项目，有很大几率会成功进入登录验证流程。



如果是在远程服务器上登录，由于扫码登录需要机器人和扫码在同一网络下，所以可自建vpn或者[教程](vpn.md)
