## Spring Data Elasticsearch 动态索引示例


### 概述
将直接通过操作repository动态生成ES索引, 实现在写入数据时动态创建需要的索引, 你需要关注`com/ike/elasticsearch/repository`目录下的内容


### 运行环境
a. 如果你要用分词器: 
```bash
# 获取ik分词器(根据你的ES版本修改后面的版本号), 你需要安装对应版本的分词器插件并重启ES
sudo wget https://release.infinilabs.com/analysis-ik/stable/elasticsearch-analysis-ik-9.0.2.zip
```

b. 如果不想配置分词器, 你需要先删除`src/main/java/com/ike/elasticsearch/entity/Article.java`字段注解中所有的`analyzer`和`searchAnalyzer`属性

