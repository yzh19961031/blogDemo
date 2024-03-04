package main

import (
	"fmt"
	"github.com/gin-gonic/gin"
	"github.com/nacos-group/nacos-sdk-go/v2/clients"
	"github.com/nacos-group/nacos-sdk-go/v2/clients/config_client"
	"github.com/nacos-group/nacos-sdk-go/v2/clients/naming_client"
	"github.com/nacos-group/nacos-sdk-go/v2/common/constant"
	"github.com/nacos-group/nacos-sdk-go/v2/model"
	"github.com/nacos-group/nacos-sdk-go/v2/vo"
	"gopkg.in/yaml.v3"
	"io"
	"net"
	"net/http"
	"strings"
)

var NamingClient naming_client.INamingClient
var ConfigClient config_client.IConfigClient
var ServiceName = "client-go"

func main() {
	// nacos 初始化
	NacosSetup()
	// 初始化web服务器
	ServerSetup()
}

// ============= nacos初始化 ============

func NacosSetup() {
	sc := []constant.ServerConfig{
		*constant.NewServerConfig("localhost", 8848, constant.WithContextPath("/nacos")),
	}

	cc := *constant.NewClientConfig(
		constant.WithNamespaceId("xxx"),
		constant.WithTimeoutMs(5000),
		constant.WithNotLoadCacheAtStart(true),
		constant.WithLogDir("/tmp/nacos/log"),
		constant.WithCacheDir("/tmp/nacos/cache"),
		constant.WithUsername("nacos"),
		constant.WithPassword("nacos"),
	)

	client, _ := clients.NewNamingClient(
		vo.NacosClientParam{
			ClientConfig:  &cc,
			ServerConfigs: sc,
		},
	)

	configClient, _ := clients.NewConfigClient(
		vo.NacosClientParam{
			ClientConfig:  &cc,
			ServerConfigs: sc,
		},
	)

	ConfigClient = configClient
	NamingClient = client

	// 注册服务
	registerServiceInstance(client, vo.RegisterInstanceParam{
		Ip:          getHostIp(),
		Port:        8777,
		ServiceName: ServiceName,
		Weight:      10,
		Enable:      true,
		Healthy:     true,
		Ephemeral:   true,
	})
}

// 获取一个健康的实例
func selectOneHealthyInstance(client naming_client.INamingClient, serviceName string) (instance *model.Instance) {
	instances, err := client.SelectOneHealthyInstance(vo.SelectOneHealthInstanceParam{
		ServiceName: serviceName,
	})
	if err != nil {
		panic("SelectOneHealthyInstance failed!")
	}
	return instances
}

// 注册服务
func registerServiceInstance(nacosClient naming_client.INamingClient, param vo.RegisterInstanceParam) {
	success, err := nacosClient.RegisterInstance(param)
	if !success || err != nil {
		panic("register Service Instance failed!")
	}
}

// 获取本机ip地址
func getHostIp() string {
	conn, err := net.Dial("udp", "8.8.8.8:53")
	if err != nil {
		fmt.Println("get current host ip err: ", err)
		return ""
	}
	addr := conn.LocalAddr().(*net.UDPAddr)
	ip := strings.Split(addr.String(), ":")[0]
	return ip
}

// ============= web端初始化 ============

func ServerSetup() {
	r := gin.Default()
	r.GET("/hello/:name", hello)
	r.GET("/config", getSpecifiedConfig)
	r.Run(":8777")
}

func hello(c *gin.Context) {
	name := c.Param("name")
	instance := selectOneHealthyInstance(NamingClient, "server-java")
	url := fmt.Sprintf("http://%s:%d/hello/%s", instance.Ip, instance.Port, name)
	resp, _ := http.Get(url)
	defer resp.Body.Close()
	body, _ := io.ReadAll(resp.Body)
	c.String(http.StatusOK, string(body))
}

// 获取指定的配置
func getSpecifiedConfig(c *gin.Context) {
	param := c.DefaultQuery("name", "")
	config, _ := ConfigClient.GetConfig(vo.ConfigParam{
		DataId: ServiceName,
	})
	fmt.Printf("config is " + config)
	// 解析YAML数据
	var data map[string]interface{}
	err := yaml.Unmarshal([]byte(config), &data)
	if err != nil {
		fmt.Println("error unmarshalling YAML", err)
	}
	value := getValue(data, param)
	c.String(http.StatusOK, "param [ "+param+" ] value is "+value)
}

func getValue(data map[string]interface{}, keyPath string) string {
	keys := strings.Split(keyPath, ".")
	var value interface{} = data
	for _, key := range keys {
		if v, ok := value.(map[string]interface{}); ok {
			value = v[key]
		} else {
			return ""
		}
	}
	if v, ok := value.(string); ok {
		return v
	}
	return fmt.Sprintf("%v", value)
}
