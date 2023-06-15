#  基础架构  
#### 基于SpringBoot Mybaits+MybatisPlus 实现的标签服务
#### git分支说明: 默认在dev分支开发，在release分支发布
#### 配置文件说明：
1. application.yaml：公共配置信息，例如组件名等
2. application-local.yaml：开发人员本地环境
3. application-dev.yaml：开发人员联调环境配置
4. application-test.yaml：测试环境配置
5. application-pro.yaml：生产环境配置


## 框架技术选型
基于 芋道源码 开发的Rouyi-Vue-pro 基础上开发的标签管理系统。


## 各模块说明:
1. bonc-framework 各种框架的依赖
2. biz-common：从各个微服务中抽取出的公共模块</br>
   _db-meta-data:_ RDS中meta-data的表结构</br>
   _asset-template:_ 新增微服务的模板,里面包含web, mybatis-plus, redis
4. api-server: 标签服务的接口服务，用于给全客、政企提供服务
5. label-manager:  与数据运营平台集成的有关功能


## 数据库信息
1. 标签的元组件信息存放到rds 数据库
2. 标签的实际数据存在的adb 数据库,需要调用adb 数据库的一些组件实现查询与文件下载





