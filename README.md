## Spring Data Elasticsearch 动态索引示例


### 快速上手
将直接通过操作repository动态生成ES索引, 实现在写入数据时动态创建需要的索引, 你需要关注`com/ike/elasticsearch/repository`目录下的内容


### 运行环境
```bash
# 获取ik分词器(根据你的ES版本修改后面的版本号)
wget https://release.infinilabs.com/analysis-ik/stable/elasticsearch-analysis-ik-9.0.2.zip
```


