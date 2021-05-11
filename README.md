# 鞍山天恒信息技术有限公司后台基础架构

## 介绍
基于springboot基础后台架构

## 软件架构
#####一 应用技术栈

1.springboot-2.1.8 基础架构版本  
2.jwt 用户身份验证  
3.redis 登录后票据缓存  
4.mybatis 数据库架构,mybatis-3.4.0 mybatis框架增强    
5.swagger2 文档调试工具
6.lombok 封装简化器  
7.POI Excel操作类  
8.logback 日志系统  
9.druid 阿里巴巴连接池  
10 fastJson JSON操作工具  


#####二 代码机构说明

└─com  

    └─th  
        └─workbase
            │  WorkBaseApplication.java 启动类
            │
            ├─bean JAVA BEAN 
            │  │  BaseDto.java 基础bean结构
            │  │  
            │  ├─report 报表模块bean 
            │  │      ReportInfoDto.java
            │  │      
            │  └─system 基础模块bean
            │          ResponseResultDto.java 统一回复前台结构Bean
            │          SysDictDto.java 字典bean
            │          SysLogDto.java 系统日志bean
            │          SysLoginDto.java 登录bean
            │          SysOrganizationDto.java 机构bean
            │          SysOrganizationTreeDto.java 机构树形bean
            │          SysRoleDto.java 角色bean
            │          SysRoleRightDto.java 权限bean
            │          SysUserDto.java 用户bean
            │          SysUserRoleDto.java 用户角色bean
            │          
            ├─common 公共函数应用
            │  ├─aop 切面应用
            │  │      LogAopAspect.java 
            │  │      
            │  ├─system 系统常量以及枚举类
            │  │      ContentSystem.java 系统常量
            │  │      ErrorEnum.java 错误回复枚举
            │  │      SuccessEnum.java 成功回复枚举
            │  │      
            │  ├─tools
            │  │      CodeGenerator.java mybatis-plus 代码生成器
            │  │      
            │  └─utils
            │          DESUtil.java DES加密工具类
            │          FileToolUtil.java 文件操作工具类
            │          HttpUtil.java http请求工具类
            │          RedisUtil.java redis操作工具类
            │          StringUtil.java 常用工具类
            │          
            ├─config
            │  │  InitBeanConfig.java mvc容器初始化配置
            │  │  JwtConfig.java jwt配置
            │  │  MetaDataConfig.java 通用数据转换配置
            │  │  MybatisConfig.java mybatis配置
            │  │  RedisConfig.java redis配置
            │  │  Swagger2Config.java swagger配置
            │  │  TokenInterceptor.java 过滤器配置
            │  │  WebConfig.java mvc基础配置
            │  │  
            │  └─annotation 自定义注解
            │          InLogAnnotation.java 自定义日志注解
            │          InQueryAnnotation.java 自定义查询注解
            │          
            ├─controller 控制器
            │  ├─report 报表模块
            │  │      ReportInfoController.java
            │  │      
            │  └─system 基础模块
            │          SysDictController.java
            │          SysLogController.java
            │          SysLoginController.java
            │          SysOrganizationController.java
            │          SysRoleController.java
            │          SysUserController.java
            │          
            ├─mapper 数据库操作接口类
            │  ├─report 报表数据库操作接口
            │  │      ReportInfoMapper.java
            │  │      
            │  └─system 基础数据模块操作接口
            │          SysDictMapper.java
            │          SysLogMapper.java
            │          SysOrganizationMapper.java
            │          SysRoleMapper.java
            │          SysRoleRightMapper.java
            │          SysUserMapper.java
            │          SysUserRoleMapper.java
            │          
            ├─Exception 异常
            │      GlobalExceptionHandler.java 全局异常
            │      TokenException.java 登录异常
            │      
            └─service 服务类
                ├─report 报表服务类
                │  │  ReportInfoService.java 服务接口
                │  │  
                │  └─Impl 服务实现
                │          ReportInfoServiceImpl.java
                │          
                └─system 基础数据 服务接口
                    │  SysDictService.java
                    │  SysLogService.java
                    │  SysOrganizationService.java
                    │  SysRoleRightService.java
                    │  SysRoleService.java
                    │  SysUserRoleService.java
                    │  SysUserService.java
                    │  
                    └─Impl 基础数据服务实现
                            SysDictServiceImpl.java
                            SysLogServiceImpl.java
                            SysOrganizationServiceImpl.java
                            SysRoleRightServiceImpl.java
                            SysRoleServiceImpl.java
                            SysUserRoleServiceImpl.java
                            SysUserServiceImpl.java
  resource  
  
    │  application-dev.properties 生成环境配置文件
    │  application-local.properties 个人本地配置文件
    │  application-test.properties 测试环境配置文件
    │  application.properties 主配置文件
    │  logback-spring.xml 日志配置文件依赖于不同环境配置文件决定日志生成路径
    │  
    ├─doc 项目文件
    │      BaseSql.sql 初始化建表语句
    │      
    ├─mappers 动态sql文件 不同文件夹代表不同数据库，支持扩展
    │  ├─mysql 当配置为mysql数据库时启用
    │  │      ReportInfoMapper.xml
    │  │      SharedMapper.xml
    │  │      
    │  └─oracle 当配置为oracle数据库时启用
    │          ReportInfoMapper.xml
    │          SharedMapper.xml
    │ 
                            
## 开发规范

 1.包名开头小写  
 2.文件名开头大写  
 3.属于某一类的文件带某一类的尾缀 例如:xxxDto xxxMapper xxxService  
 4.同一模块前缀相同 例如:SysXXXController ReportXXXController
         

## 安装教程

1.  git clone https://gitee.com/asth/th-work-base-be.git

## 常规操作git
    合并 dev 代码

  git checkout dev  
  git pull origin dev  
  git checkout dev-wangxiaohui  
  git merge origin/dev  

    把代码推到自己的分支上

  git add .  
  git commit -m 'someth.'  
  git push origin dev-wangxiaohui  
  
  

## 分支说明


|  分支名称   | 说明  |
|  ----  | ----  |
| master  | 生产环境 |
| dev  | 开发环境 |
dev-wangxiaohui  | wxh 开发环境 |
dev-hutie  | hut 开发环境 |
dev-wenhepeng  | whp 开发环境 |
dev-yaoqingle  | yql 开发环境 |


