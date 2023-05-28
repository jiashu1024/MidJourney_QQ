1. 建立自己的频道 [教程](https://docs.midjourney.com/docs/quick-start)
2. 往自己频道添加discord机器人,拿到discord的usertoken，频道ID channel id，服务器ID guild id[教程](https://discord.com/developers/applications)
3. 阿里云开通机器翻译[教程](aliyun.md)获取AccessKey ID和AccessKey Secret
4. 登录midjourney获取user id和user token[教程](midjourney.md)

------

## 使用docker-compose

docker和docker-compose安装[教程](docker.md)

对于qq扫码登录，如果docker运行在服务器上，需要搭建vpn通道保证在同一网络下，否则异地扫码登录会失败

如果你运行程序在本地，可以跳过这一步骤,[vpn搭建](vpn.md)

如果你的程序在远端服务器运行，先确保自己vpn可用

```shell
#根目录下运行，会启动一个应用容器，一个mysql容器
docker-compose up -d

docker-compose logs -f #查看日志

```

接下来，如果不出意外的话，你就能看到日志里输出的二维码了

一般，可能会出意外

* qrcode没有出现二维码，只有报错，关闭容器，重新运行一次有机会解决

```shell
docker-compose down
```

* 扫码时，第一次没反应甚至失败，重新扫码一般来说能成功
* 二维码显示混乱，这跟你的ssh连接终端的窗口大小有关，在日志中，二维码的上面有图片的文件名，保存到了项目根目录下的pic文件夹内，可将该图片传输到本地，再进行扫码。**扫码时确保vpn连接有效**

qrcode目前只支持安卓手表协议和MACOS协议登录，根目录下的application.yaml进行配置

password登录方式目前由于腾讯风控较为严重，老号活跃号登录成功率较高

后期将尝试接入钉钉webhook，将二维码直接发送到手机，或者通过配置邮箱发送

## 源码编译运行

1. 在自己的数据库里创建数据库midjourney

2. 配置src/main/resources/application.yaml

3. 根目录下运行 mvn package -DskipTests 出现  BUILD SUCCESS就行

4. 根目录下运行

   java -jar mj-QQ-mirai-1.0.0.jar 扫描验证码

   会在当前目录下生成cache文件夹，如果服务器上运行需要连同这个一起上传。因为服务器登录，异地扫码会不成功

5. 服务器后台运行 nohup java -jar  mj-QQ-mirai-1.0.0.jar >> qq.log &

```shell
server:
  port: 9093

logging:
  level:
    ROOT: info
    com.zjs.mj: debug
    com.zjs.mj.mapper: info



config:
  mj:
    plan: 4  #可选值1,2,3,4 分别对应(免费订阅，$10,$30,$60)用于调整同时提交的任务数量
    guild-id: 123456 #服务器id
    channel-id: 123456 #频道id
    user-token: 123456 #discord的用户token
    bot-token: 123456 #机器人token
    user-id: 123456 #midjourney用户id
    token: 123456 #midjourney用户token
  aliyun:
    accessKeyId: 
    accessKeySecret: 
  qq:
    account:     #qq号
#    password:   #扫码登录，不用配置

spring:
  datasource:
    url: jdbc:mysql://localhost:3306/midjourney?useUnicode=true&characterEncoding=utf-8&useSSL=false&socketTimeout=60000&connectTimeout=30000&serverTimezone=Asia/Shanghai
    username: root
    password: 123456
    driver-class-name: com.mysql.cj.jdbc.Driver
    hikari:
      minimum-idle: 3
      maximum-pool-size: 10
      connection-test-query: SELECT 1
      max-lifetime: 1800000
      idle-timeout: 60000
      connection-timeout:   30000


```

