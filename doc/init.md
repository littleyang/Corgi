# 初始化工作  
使用这个版本发布系统，需要进行配置的初始化工作，主要是分为2部分，一部分是部署发布系统，一部分是服务器初始化。  

## 部署发布系统  
### web容器  
推荐使用tomcat7+版本，发布系统的日志推送使用到了websocket，所以根据目前所了解，tomcat7/8 是比较好的选择。
###部署  
发布系统主要是分为2个web模块，一个是发布系统，另外一个是log agent，专门用于接收日志。所以部署的时候，是要发布2个子系统，域名配置相应地也要2个。  

## 初始化  
### 发布系统所在的服务器
主要是java环境（1.8+） + tomcat + ansible + web用户配置。  
**ansible**  
ansible在发布系统里面充当非常重要的角色，他负责将编译脚本、发布脚本、打包文件传输到目标服务器，所以必须配置和安装ansible。  
````
ansible依赖环境安装
yum  -y  install  libffi  libffi-devel  pip  python-devel  python
pip  install  paramiko  PyYAML  Jinja2  httplib2
yum  install  libselinux-python  -y
pip  install  --upgrade  pip
#yum  install  ansible  -y
pip  install  ansible

ansible  all  -m  ping  --ask-pass

mkdir  /etc/ansible
cat  >  /etc/ansible/ansible.cfg  <<EOF
[defaults]
#pipelining  =  true
host_key_checking  =  False
#forks  =  100
inventory            =  /etc/ansible/hosts
remote_port        =  32200
log_path        =  /var/log/ansible.log
[privilege_escalation]
#become=True
#become_method=su
#become_user=root
#become_ask_pass=True
[paramiko_connection]
[ssh_connection]
[accelerate]
[selinux]
EOF

echo  "127.0.0.1"  >  /etc/ansible/hosts
````
**web用户配置**  
发布系统默认所有系统采用名为web的用户进行发布，调用ansible进行文件传输也是通过web用户进行传输，所以需要配置web用户相关的权限，建议让运维统一配置。  


### 编译服务器的从初始化  
