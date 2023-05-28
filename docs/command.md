admin和plus用户默认使用fast模式作图，normal用户默认使用relax

admin不限请求次数，normal和plus用户额度可在user表中更改

当一个qq用户第一次向机器人发起请求时，默认是normal用户，可在default_role_count表中调整用户的初始额度

normal和plus用户fast模式的余额每月1号刷新成默认值，relax模式余额每天0点刷新

------



1. **/imagine作图** 私聊直接说文案就行  文案后带--fast使用fast作图,--relax使用relax模式作图，可正常携带其他参数，群聊则需要At机器人直接说文案就行。

   <img src="https://tc.mustache.top/picGo/202305271900121.png" alt="image-20230527190047068" style="zoom:50%;" />

   <img src="https://tc.mustache.top/picGo/202305271901888.png" alt="image-20230527190119870" style="zoom:33%;" />

   

2. **UV操作**

引用回复图片回复U1 U2 U3 U4放大，V1 V2 V3 V4变换

![image-20230527190540605](https://tc.mustache.top/picGo/202305271905630.png)

![image-20230527190609949](https://tc.mustache.top/picGo/202305271906979.png)

<img src="https://tc.mustache.top/picGo/202305271907637.png" alt="image-20230527190749589" style="zoom:50%;" />

对于UV操作的作图模式，会延续你Imagine命令的作图模式

------

目前，用户身份设置暂时只能通过修改数据库表字段实现，可通过可视化数据库软件例如Navicat连接数据库进行设置，后期将以admin与机器人聊天的方式按照固定文案进行用户权限设置，可作图余额设置。

每个任务的详情暂时也只能通过查看数据库字段。后期将加入接口查看。
