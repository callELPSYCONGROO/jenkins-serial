# jenkins-serial
> Jenkins串行队列构建

## 项目说明

* 使用webhook监听Gitlab任意事件，使配置的项目能够在Jenkins中串行构建

* 依赖数据库储存webhook事件

* Jenkins版本2.x

* Gitlab版本11.1+

* 只能单机版运行

## 接口说明

### config

* 新增git项目源码与Jenkins项目之间的映射关系：```POST /config/gitRepositoryAndJobMapping/insert```

* 修改git项目源码与Jenkins项目之间的映射关系：```PUT /config/gitRepositoryAndJobMapping/update```

* 查看当前正在执行的构建：```GET /config/job/executing```

### gitlab

* webhook请求地址：```POST /gitlab/webhook/{jobName}```

    ```{jobName}```为Jenkins项目名称，暂不支持Jenkins2.x中文件工程下的项目
    
    请求头（暂不支持token）、请求体参数参考官方文档：[Gitlab-Webhooks](https://docs.gitlab.com/ee/user/project/integrations/webhooks.html)
