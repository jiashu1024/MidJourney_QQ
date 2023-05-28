```shell
#docker 安装
curl -fsSL https://test.docker.com -o test-docker.sh
 sudo sh test-docker.sh
 
#docker-compose安装
sudo curl -L "https://github.com/docker/compose/releases/download/v2.6.1/docker-compose-$(uname -s)-$(uname -m)" -o /usr/local/bin/docker-compose

sudo chmod +x /usr/local/bin/docker-compose

docker-compose --version
```

