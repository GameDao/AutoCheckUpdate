# AutoCheckUpdate
自动检查更新模块
程序启动时检查网络端是否有更新，点击更新按钮会在通知栏显示下载进度，下载完成后点击通知栏即可安装

2016-3-4

在新项目：https://github.com/Qrilee/DevAsimpleApp 中将更新模块进行了改进，此处暂不进行更新，具体请跳转到新工程中查看

使用okhttp代替传统的httpurlconnection，大幅减少代码量，优化通知栏显示等等

2016-1-14


取消依赖Material Dialog库，有需要的自行添加


单例化检查更新类


修改下载服务，在下载完成后自动清除通知栏状态，并弹出安装界面
