##About My MiniWebServer 
* 解压后你可以看到几个文件
* README.md
* QzhWebServer文件夹，存放着我的eclipse工程，包括源代码和可执行jar包
* MyWebServer文件夹，该文件夹下放着我实验所用的html文件，txt文件和img文件等
* imageResource，包含此markdown文件需要显示的图片文件，所以最好不要删掉啦，不然这个文件没法显示相关图片了
* *.pdf是我的实验报告
###如何在你的电脑上运行此服务器 
####1.部署文件  
1.将MyWebServer文件夹随便放在某个目录 
####2.运行服务器，设置服务器参数
1.服务器的主界面如下  
![服务器界面]( "./imageResource/WebMain.jpg" "这是主界面")  
2.首先先填好本地端口参数，最好设置1024以上的端口号，确保端口号没有被占用  
3.将步骤1中MyWebServer复制放到工作目录参数栏中  
如下图，MyWebServer文件夹在e:/Document/下，此时只需要将"e:/Document/MywebServer"填入工作目录参数栏即可

![工作目录]( "./imageResource/WorkDirectory.jpg" "这是工作目录")  
####3.运行服务器，
* 这是eclipse工程，如果你想重新build一次或者自己修改代码跑一次，务必记得将QzhWebServer/lib文件夹下的TableLayout.jar添加到build path中，具体怎么添加的话，只要eclipse工程打开，右键点击TableLayout.jar->add to build path即可，实在不行自行google
* 如果想直接运行我的服务器，那么在安装有java 的jre运行环境下双击QzhWebServer/可执行jar包文件夹下的.jar文件即可。  

####4.主界面功能介绍  
* 开始按钮点击即可开始停止点击结束
* 运行状态栏可以看到是在运行还是已经停机
* 右侧空白编辑栏是运行日志文件的动态刷新显示，当信息过多的时候会自行清理旧的日志信息  

![服务器界面]( "./imageResource/WebMain.jpg" "这是主界面")  

####5.如何访问服务器
* 如图，浏览器栏填入如下信息，第一个是服务器的ip地址，第二个是自己设置的服务器端口号
* 关于端口号的文件，首先文件名必须存在，至于/image.html前面的文件夹，在本程序中是不关心的，只要看到html文件，程序会自动在本地的html文件夹下寻找对应的文件，下面的访问地址也可以改成 127.0.0.1:806/***/image.html

![服务器界面]( "./imageResource/AccessServer.jpg" "这是主界面")  

####6.如何添加本地资源文件  
* 只需要将访问需要的html等类型文件按照文件类型放在MywebServer如下对应类型的文件夹下即可，每个html文件夹中嵌套需要的.jpg文件记得全部放在Img文件夹下，暂时没有扩展其他类型的图片，当然你看懂了程序修改也是很简单的事情 
![服务器界面]( "./imageResource/resource.jpg" "这是主界面") 

####7.写在最后  

* 最后如果有什么bug或则问题可以联系我xlq1120@yahoo.com
* 祝一切顺利