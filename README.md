# Tinker
本项目是基于Burpsuite平台的插件，用于JS文件敏感信息爬取和API自动Fuzz

## 使用说明

### Tinker告警

此功能是新增功能，用于提示Tinker发现隐藏API

![](https://github.com/L4ml3da/Tinker/blob/master/img/alarm.jpg)

### 配置

更新内容：

1.3.0版本：

1、多个过滤规则改为字符串配置，以英文逗号隔开。

2、新增配置"Root Directory"，该配置为手动重放设置，不影响AutoRepeater的请求路径

3、新增MIME响应的过滤配置，该配置默认为空

4、在配置完成后需要点击Apply进行配置保存

![](https://github.com/L4ml3da/Tinker/blob/master/img/config.jpg)

### 自动Fuzz

自动Fuzz针对于JS中发现的API接口，其接口路径将直接拼接在Host根目录后，可能会因为某些站点存在前置路径导致Fuzz出现大量404，如果需要手动Fuzz，可在后续的Sensitive面板中复制出接口列表再Intruder中手动Fuzz

![](https://github.com/L4ml3da/Tinker/blob/master/img/repeater.jpg)

### 信息面板

面板中展示JS中发现的所有敏感信息，列表中将反应不同信息类别的发现个数，可对信息类型进行Filter，并且可通过Copy按钮一键复制文本框内所有内容。

更新内容：

1.3.0版本：

1、添加Repeat功能，该功能主要是可以对"Sensitive Info"表中选中的行的Api地址进行重新Fuzz，该功能的设计初衷是为了防止AutoRepeater Fuzz的根路径并不是站点的真正根目录

2、可以在配置页面配置"Root Directory"，该配置是专为Repeat使用的，AutoRepeater不会使用该配置，该配置是用来更正网站根路径，可以为多个路径，例如admin,admin/manager，用逗号隔开，当然可以配置为空

3、选中表格中的某行后，点击repeat按钮，程序会将"Root Directory"配置中的路径拼接在所有API路径前，点击repeat按钮后状态将变为“working”，在fuzz结束前该按钮不能再次点击

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

  