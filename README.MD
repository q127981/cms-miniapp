CMS自媒体/资讯/新闻/文章应用 小程序 前端+后端
www.tcyhw.top 小程序
功能简介：
含文章、搜索、详情、评论、回复、点赞、收藏、分享、用户等。 
uniapp编写，一键发布百度小程序、微信小程序、抖音小程序

###目录结构：
--api-server 后端
--web  前端
--miniapp_dev.sql  后端数据库


###说明：
我本来想照 www.tcyhw.top 的风格做一个小程序，看到有现场的小程序源码模板，我就随便改了改。
前端基于：https://ext.dcloud.net.cn/plugin?id=1988 二次修改，更改了部分不符合微信上线要求的功能和逻辑
后端是用springboot完全根据小程序的接口格式随便写的，未严格遵循编码格式和rest风格。原作者的是PHP做的后端，未开源。

###体验地址

小程序：![https://img.ww125.cn/img/10](https://img.ww125.cn/img/10)

功能截图：
![https://img.ww125.cn/img/9](https://img.ww125.cn/img/9)



###使用步骤：
1.clone项目

2.新建一个数据库并导入提供的示例sql

3.修改后端application.yml中的数据库连接地址和账号密码

4.修改后端application.yml中的小程序APPID等信息

5.启动后端服务

6.修改前端配置：
    6.1 修改api接口地址
    6.2 修改小程序APPID等配置
   
7.运行


##利用下班时间写的，喜欢的话可以给瓶水钱，谢谢!
![https://img.ww125.cn/img/53](https://img.ww125.cn/img/53)