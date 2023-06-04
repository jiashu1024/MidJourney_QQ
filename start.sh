#!/bin/bash

# 检查Docker是否已经安装
if ! command -v docker &> /dev/null; then
    echo "Docker未安装，开始安装..."
    # 使用curl下载Docker安装脚本
    curl -fsSL https://test.docker.com -o test-docker.sh

    # 执行Docker安装脚本
    sudo sh test-docker.sh

    # 检查安装结果
    if [ $? -eq 0 ]; then
        echo "Docker安装成功！"
    else
        echo "Docker安装失败。"
        exit 1
    fi
else
    echo "Docker已经安装，跳过安装步骤。"
fi

# 检查mj-mysql容器是否在运行
if ! sudo docker container ls --format '{{.Names}}' | grep -q '^mj-mysql$'; then
    echo "mj-mysql 容器未在运行，启动容器..."

    # 启动 mj-mysql 容器
    sudo docker run --name mj-mysql -e MYSQL_ROOT_PASSWORD=zhangsan -e MYSQL_DATABASE=midjourney -p 3306:3306 -v "$(pwd)"/sql:/docker-entrypoint-initdb.d -d mysql:8.0

    # 检查容器启动是否成功
    if [ $? -eq 0 ]; then
        echo "mj-mysql 容器启动成功！"
        echo "Waiting for MySQL initialization..."
        sleep 10
    else
        echo "mj-mysql 容器启动失败。"
        exit 1
    fi
else
    echo "mj-mysql 容器正在运行，跳过启动步骤。"
fi

# 复制 mj-QQ-mirai-1.0.0.jar 到上一级目录
echo "Copying mj-QQ-mirai-1.0.0.jar to the parent directory..."
cp target/mj-QQ-mirai-1.0.0.jar ./

# 检查复制是否成功
if [ $? -eq 0 ]; then
    echo "mj-QQ-mirai-1.0.0.jar copied successfully!"
else
    echo "Failed to copy mj-QQ-mirai-1.0.0.jar."
    exit 1
fi
current_dir=$(pwd)

# 打印当前目录的绝对路径
echo "当前目录：$current_dir"

# 启动 mj-QQ-mirai-1.0.0.jar
echo "Starting mj-QQ-mirai-1.0.0.jar..."


java -Dspring.config.location="$current_dir/application.yaml" -jar mj-QQ-mirai-1.0.0.jar