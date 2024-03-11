FROM alpine:3.19.1
WORKDIR /data
COPY go-pipline /data/go-pipline

# 设置阿里云仓库
RUN sed -i 's/dl-cdn.alpinelinux.org/mirrors.aliyun.com/g' /etc/apk/repositories

# 设置时区和安装bash
RUN apk add -U tzdata && \
    apk add -U bash && \
    ln -sf /usr/share/zoneinfo/Asia/Shanghai /etc/localtime && \
    echo "Asia/Shanghai" > /etc/timezone

EXPOSE 8080
ENTRYPOINT ["/data/go-pipline"]