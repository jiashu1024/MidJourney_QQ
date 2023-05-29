# 使用官方的Java开发套件8和Maven镜像作为基础镜像
FROM maven:3.6.3-jdk-8

# 更换 Maven 镜像源到阿里云，防止下载依赖过慢
#RUN mkdir -p /root/.m2 \
#    && { echo '<settings>'; \
#         echo '  <mirrors>'; \
#         echo '    <mirror>'; \
#         echo '      <id>nexus-aliyun</id>'; \
#         echo '      <mirrorOf>central</mirrorOf>'; \
#         echo '      <name>Nexus aliyun</name>'; \
#         echo '      <url>http://maven.aliyun.com/nexus/content/groups/public</url>'; \
#         echo '    </mirror>'; \
#         echo '  </mirrors>'; \
#         echo '</settings>'; \
#       } > /root/.m2/settings.xml



# 设置工作目录
WORKDIR /usr/src/app

# 将当前目录所有文件添加到工作目录
COPY src ./src
COPY pom.xml .
COPY application.yaml .

# 移动application.yaml文件
#RUN mv application.yaml src/main/resources/

# 使用Maven打包跳过测试
RUN mvn package -DskipTests

# 运行jar文件，这里的my-app-1.0.jar需要换成你真实的jar文件名
CMD ["java", "-jar", "target/mj-QQ-mirai-1.0.0.jar", "--spring.config.location=/usr/src/app/application.yaml"]
