1. 建立自己的频道 [教程](https://docs.midjourney.com/docs/quick-start)
2. 往自己频道添加discord机器人,拿到discord的usertoken，频道ID channel id，服务器ID guild id[教程](https://discord.com/developers/applications)
3. 阿里云开通机器翻译[教程](aliyun.md)获取AccessKey ID和AccessKey Secret
4. 登录midjourney获取user id和user token[教程](midjourney.md)
5. 在自己的数据库里创建数据库midjourney

6. 配置src/main/resources/application.yaml

7. 根目录下运行 mvn package -DskipTests 出现  BUILD SUCCESS就行

8. 根目录下运行

    java -jar mj-QQ-mirai-1.0.0.jar 扫描验证码

   会在当前目录下生成cache文件夹，如果服务器上运行需要连同这个一起上传。因为服务器登录，异地扫码会不成功

9. 服务器后台运行 nohup java -jar  mj-QQ-mirai-1.0.0.jar >> qq.log &

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

