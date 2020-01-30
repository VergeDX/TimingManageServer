# TimingManageServer
一个 “时间管理” 服务器 API，方便规划个人作息

## 部署
1. 请去 [releases](https://github.com/VergeDX/TimingManageServer/releases) 下载最新版本的 jar 包
2. 命令行执行 `java -jar TimingManageServer-x.x.x-jar-with-dependencies.jar 端口号 (默认为 2333)`
3. 服务器即在 `http://localhost:2333` 启动

## 构建
你可以通过构建来获得未发布的最新版本
1. `git clone https://github.com/VergeDX/TimingManageServer.git`
2. `cd TimingManageServer/`
3. `mvn assembly:assembly`

## [ApiDoc](https://github.com/VergeDX/TimingManageServer/blob/master/ApiDoc.md)
注：不同版本的 ApiDoc 可能会有变化，请查看 [releases](https://github.com/VergeDX/TimingManageServer/releases)