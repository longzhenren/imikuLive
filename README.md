## <div style="text-align:center"><b style="color:#39c5bb">imiku</b><b style="color:#fdefbe">Live</b></div>

基本可用的直播网站

---

`imikuLive` 是以 [Node-Media-Server](https://github.com/illuspas/Node-Media-Server) 作为流媒体实现的直播网站。

计划逐步实现注册用户直播间管理、推流权限管理、在线播放与弹幕等基本功能。采用 `AGPLv3` 协议开源。

#### 许可证

```
    imikuLive, A basically functional webcast site
    Copyright (C) 2022 Operacon.

    This program is free software: you can redistribute it and/or modify
    it under the terms of the GNU Affero General Public License as published
    by the Free Software Foundation, either version 3 of the License, or
    (at your option) any later version.

    This program is distributed in the hope that it will be useful,
    but WITHOUT ANY WARRANTY; without even the implied warranty of
    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
    GNU Affero General Public License for more details.

    You should have received a copy of the GNU Affero General Public License
    along with this program.  If not, see <https://www.gnu.org/licenses/>.

    To contact the author, E-Mail <operacon@outlook.com>
```

#### 功能

-   [x] 用户登陆注册
-   [x] 用户个人信息及头像修改
-   [x] 用户邮箱验证及密码重置
-   [x] 直播间信息设置和页面
-   [x] 直播间自动关闭
-   [x] 直播间页面
-   [ ] 主播自定密码的私人直播间
-   [x] 弹幕
-   [ ] 主播使用的弹幕显示页
-   [x] 拉流鉴权
-   [x] 主页
-   [x] 搜索功能和页面

#### 部署方法

软件分为三部分，包括 Spring 部分、数据库部分和 [Node-Media-Server](https://github.com/illuspas/Node-Media-Server) （以后简称 NMS）流媒体服务器部分。

##### Spring 部分

依照自身服务器配置情况，修改 `gateway/src/main/resources/sample_application.yml` 和 `live/src/main/resources/sample_application.yml` 中的配置项，并各自在同目录下另存为或重命名为 `application.yml`。文件的注释中应当详细描述了应当填写的配置项及其意义。

项目目前配置为 JDK11，可以根据需要修改响应 `pom.xml`。
完成配置后，使用 Maven 打包两个项目并按常规方法部署。

TODO：配置文件隔离和热修改
TODO：编写 Dockerfile

##### 数据库部分

任意数据库实例，导入项目根目录的 `live.sql` 即可。注意配置文件中的数据源应当同步修改。

##### 流媒体服务器部分

参照 [Node-Media-Server](https://github.com/illuspas/Node-Media-Server) 进行部署。
参照文档对 `app.js` 进行编辑，必须启用 api 以进行服务器负载查询。注意配置文件中的 NMS 相关项应当同步修改。

#### 目前的问题

-   直播间关闭后，推流地址依然可以继续工作。
-   上一次（但未超过 48 小时失效时限）的推流地址依然可以继续工作。与上一个问题的解决方案类似，即当房间手动或自动关闭时，应该让 NMS 拒绝这些地址的传入连接。但是 NMS 并没有提供拒绝连接的 api，因此这些问题的解决可能需要对 NMS 进行二次开发或者更换流媒体服务器。
