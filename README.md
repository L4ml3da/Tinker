# Tinker
本项目是基于Burpsuite平台的插件，用于JS文件敏感信息爬取和API自动Fuzz

## 使用说明

### 配置

Auto和Get请求默认启用，勾选状态码时，将自动过滤配置中的响应码，该配置将再下一次JS解析中生效，不需要重启Burpsuite

![](https://github.com/L4ml3da/Tinker/blob/master/img/config.jpg)

### 自动Fuzz

自动Fuzz针对于JS中发现的API接口，其接口路径将直接拼接在Host根目录后，可能会因为某些站点存在前置路径导致Fuzz出现大量404，如果需要手动Fuzz，可在后续的Sensitive面板中复制出接口列表再Intruder中手动Fuzz

![](https://github.com/L4ml3da/Tinker/blob/master/img/repeater.jpg)

### 信息面板

面板中展示JS中发现的所有敏感信息，列表中将反应不同信息类别的发现个数，可对信息类型进行Filter，并且可通过Copy按钮一键复制文本框内所有内容

![](https://github.com/L4ml3da/Tinker/blob/master/img/sensitive.jpg)

## TODO：

1、支持更多配置项，例如过滤的文件类型

2、支持对403响应的绕过尝试

3、其他存在的Bug修复

## Tips
+ 谨慎使用WithCookie功能可能造成敏感接口Fuzz导致的错误请求
+ 部分时候UI可能会出现无法滑动状态，可能是后台Fuzz导致，稍等即可恢复
+ 如果有好的建议或发现Bug欢迎指教

- ## 免责申明

  本项目仅供学习交流使用，请勿用于违法犯罪行为。

  本软件不得用于从事违反中国人民共和国相关法律所禁止的活动，由此导致的任何法律问题与本项目和开发人员无关。

  