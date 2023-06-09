admin和plus用户默认使用fast模式作图，normal用户默认使用relax

admin不限请求次数，normal和plus用户额度可在user表中更改

当一个qq用户第一次向机器人发起请求时，默认是normal用户，可在default_role_count表中调整用户的初始额度

normal用户的fast模式的余额每月1号刷新成默认值，所有用户的relax模式余额每天0点刷新

------



* **imagine作图** 私聊或者群聊通过/imagine前缀即可触发作图  

<img src="https://tc.mustache.top/picGo/202306092227351.png" alt="image-20230609222721276" style="zoom:25%;" />

​	文案后添加--fast或者--relax参数可切换fast，relax作图模式

​	如果不加，admin和plus用户默认使用fast，normal使用relax模式

* **imagine垫图**

​	引用图片，按照“/”开头，即可触发垫图操作。(需要保证引用的图片发出的时候，机器人在后台运行)

<img src="https://tc.mustache.top/picGo/202306092236171.png" alt="image-20230609223615146" style="zoom: 25%;" />

<img src="https://tc.mustache.top/picGo/202306092236287.png" alt="image-20230609223648266" style="zoom:25%;" />

* **UV操作**

引用回复图片回复U1 U2 U3 U4放大，V1 V2 V3 V4变换，对于UV操作的作图模式(fast or relax)，会延续你Imagine命令的作图模式

<img src="https://tc.mustache.top/picGo/202305271905630.png" alt="image-20230527190540605" style="zoom:25%;" />

<img src="https://tc.mustache.top/picGo/202305271906979.png" alt="image-20230527190609949" style="zoom:25%;" />

<img src="https://tc.mustache.top/picGo/202305271907637.png" alt="image-20230527190749589" style="zoom: 25%;" />





------

目前，用户身份设置暂时只能通过修改数据库表字段实现，可通过可视化数据库软件例如Navicat连接数据库进行设置，后期将以admin与机器人聊天的方式按照固定文案进行用户权限设置，可作图余额设置。

每个任务的详情暂时也只能通过查看数据库字段。后期将加入接口查看。
