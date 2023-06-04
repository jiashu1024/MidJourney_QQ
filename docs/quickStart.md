1. 建立自己的频道 [教程](https://docs.midjourney.com/docs/quick-start)
2. 往自己频道添加discord机器人,拿到discord的usertoken，频道ID channel id，服务器ID guild id[教程](https://github.com/novicezk/midjourney-proxy/blob/main/docs/discord-bot.md)
3. 阿里云开通机器翻译[教程](aliyun.md)获取AccessKey ID和AccessKey Secret
4. 登录midjourney获取user id和user token[教程](midjourney.md)
5. 企业微信创建机器人获取webhook[教程](https://developer.qiniu.com/console/kb/10490/receptionmanagement-wechat?category=kb)

------

## 部署

本程序需要jdk8环境，由于qq使用密码登录会触发验证，进行控制台交互，所以暂不支持docker部署运行，但是项目依赖的mysql数据库可用docker运行。一键运行脚本支持docker的自动安装和mysql数据库(docker)的启动



编辑application.yaml.sample配置文件，修改为自己的配置，然后重命名为application.yaml


```shell
#项目根目录下运行
chmod +x start.sh

bash start.sh  #前台运行，退出终端运行停止
bash start.sh bg  #后台运行，退出终端运行不会停止

##如果使用password方式登录，需要保证先前台运行，与控制台交互完成登录的验证，登录成功后再使用后台运行

##查看日志
tail -f logs/mj.log
```



如果是扫码方式登录，要在服务器运行：
1. 服务器先搭建mysql环境，可通过运行一键脚本创建mysql环境
1. 本地修改application.yaml配置文件，修改数据库配置为服务器ip地址，记得打开3306端口
1. 本地通过扫码登录成功后，会在项目根目录下生成cache登录凭证文件夹，将该文件夹上传服务器项目根目录下。再通过一键脚本后台运行


如果要直接在服务器上登录，由于扫码登录需要机器人登录设备和扫码设备在同一网络下，所以可自建vpn或者[教程](vpn.md)
