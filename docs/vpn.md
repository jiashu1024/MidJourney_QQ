项目根目录下运行

```shell
docker-compose  up -d

docker ps -a #查看vpn容器是否成功启动
docker logs -f {name}#容器名称查看日志
```

![image-20230528183517673](https://tc.mustache.top/picGo/202305281835727.png)

使用手机自带的vpn进行连接

**打开服务器4500，500两个端口**     **udp协议**

如果是阿里云，到控制面板里防火墙进行配置

**安卓手机**

1. 将生成的 `.p12` 文件(项目根目录下的vpn文件夹内)你的 Android 设备。
2. 启动 **设置** App。
3. 进入 安全 -> 高级 -> 加密与凭据。(电脑传输到手机qq或者微信，直接用证书安装程序安装)
4. 单击 **安装证书**。
5. 单击 **VPN 和应用用户证书**。
6. 选择你从服务器传送过来的 `.p12` 文件。
   **注：** 要查找 `.p12` 文件，单击左上角的抽拉式菜单，然后浏览到你保存文件的目录。
7. 为证书输入名称，然后单击 **确定**。
8. 进入 设置 -> 网络和互联网 -> VPN，然后单击 "+" 按钮。
9. 为 VPN 配置文件输入名称。
10. 在 **类型** 下拉菜单选择 **IKEv2/IPSec RSA**。
11. 在 **服务器地址** 字段中输入 `你的 VPN 服务器 IP` （或者域名）。
    **注：** 它必须与 IKEv2 辅助脚本输出中的服务器地址 **完全一致**。
12. 在 **IPSec 标识符** 字段中输入任意内容（例如 `empty`）。
    **注：** 该字段不应该为必填。它是 Android 的一个 bug。
13. 在 **IPSec 用户证书** 下拉菜单选择你导入的证书。
14. 在 **IPSec CA 证书** 下拉菜单选择你导入的证书。
15. 在 **IPSec 服务器证书** 下拉菜单选择 **(来自服务器)**。
16. 单击 **保存**。然后单击新的 VPN 连接并单击 **连接**。

如果你的手机上没有**IKEv2/IPSec RSA**选项，下面要输入的信息可在docker-compose.yaml中环境变量找到，也可在vpn容器的输出日志中找到

1. 启动 **设置** 应用程序。
2. 单击 **网络和互联网**。或者，如果你使用 Android 7 或更早版本，在 **无线和网络** 部分单击 **更多...**。
3. 单击 **VPN**。
4. 单击 **添加VPN配置文件** 或窗口右上角的 **+**。
5. 在 **名称** 字段中输入任意内容。
6. 在 **类型** 下拉菜单选择 **L2TP/IPSec PSK**。
7. 在 **服务器地址** 字段中输入`你的 VPN 服务器 IP`。
8. 保持 **L2TP 密钥** 字段空白。
9. 保持 **IPSec 标识符** 字段空白。
10. 在 **IPSec 预共享密钥** 字段中输入`你的 VPN IPsec PSK`。
11. 单击 **保存**。
12. 单击新的VPN连接。
13. 在 **用户名** 字段中输入`你的 VPN 用户名`。
14. 在 **密码** 字段中输入`你的 VPN 密码`。
15. 选中 **保存帐户信息** 复选框。
16. 单击 **连接**。

**最后**，[测试自己的ip](https://www.ipchicken.com/)是否在服务器所在ip



苹果手机详情见[教程](https://github.com/hwdsl2/setup-ipsec-vpn/blob/master/docs/ikev2-howto-zh.md)