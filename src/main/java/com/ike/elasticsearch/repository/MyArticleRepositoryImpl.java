package com.ike.elasticsearch.repository;

import com.ike.elasticsearch.entity.Article;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.elasticsearch.core.ElasticsearchOperations;
import org.springframework.data.elasticsearch.core.IndexOperations;
import org.springframework.data.elasticsearch.core.document.Document;
import org.springframework.data.elasticsearch.core.mapping.IndexCoordinates;

import java.time.Instant;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author <a href=mailto://idiotpre@outlook.com>IKE</a> 2/7/2025
 */
public class MyArticleRepositoryImpl implements MyArticleRepository<Article>{
    private static final Logger logger = LoggerFactory.getLogger(MyArticleRepositoryImpl.class);

    private final ElasticsearchOperations elasticsearchOperations;

    private final ConcurrentHashMap<String, IndexCoordinates> knownIndexCoordinates = new ConcurrentHashMap<>();

    public MyArticleRepositoryImpl(ElasticsearchOperations elasticsearchOperations) {
        this.elasticsearchOperations = elasticsearchOperations;
    }

    private Document mapping;

    @Override
    public <S extends Article> S save(S entity) {
        IndexCoordinates indexCoordinates = getIndexCoordinates(entity);
        S saved = elasticsearchOperations.save(entity, indexCoordinates);
        logger.info("数据已写入ES: {}", entity);

        // FIXME: 生产环境不建议主动刷新索引, 请直接注释下面这行
        elasticsearchOperations.indexOps(indexCoordinates).refresh();

        return saved;
    }



    /**
     * 将会在进入此方法时检查/创建ES索引, 这将会带来程序健壮性上的提升,
     * 假设取实体中的时间字段的年月(如2025-07)用于动态创建索引, 最终创建的ES索引就是 `idx-article-2025-07`,
     * 类似地, 你还可以基于现实时间, 或实体类的其他字段来创建索引,
     * 但是请注意, es对索引命名有一些限制, 可以参考下面的文档:
     * <a href="https://docs.spring.io/spring-data/elasticsearch/docs/current/api/org/springframework/data/elasticsearch/annotations/Document.html#indexName()">spring-data-elasticsearch</a>
     */
    private <S extends Article> IndexCoordinates getIndexCoordinates(S article) {
        /*
         * 你还可以在此处增加一些逻辑, 避免每次都尝试创建新的索引
         */
        Instant time = article.getTime();
        ZonedDateTime zonedDateTime = time.atZone(ZoneId.systemDefault());
        var fmt = DateTimeFormatter.ofPattern("yyyy-MM");
        String formatted = zonedDateTime.format(fmt);

        /*
         * 注意: 这里的indexName前缀必须匹配实体(这里是`Article`)中设置的indexName,
         * spring data ES 始终会根据实体中设置的索引名称构建查询(当前Article实体中的是`idx-article-*`)
         */
        String indexName = "idx-article-" + formatted;
        logger.info("生成的索引名称: {}", indexName);

        return knownIndexCoordinates.computeIfAbsent(indexName, i -> {

                    IndexCoordinates indexCoordinates = IndexCoordinates.of(i);
                    IndexOperations indexOps = elasticsearchOperations.indexOps(indexCoordinates);

                    if (!indexOps.exists()) {
                        indexOps.create();

                        if (mapping == null) {
                            mapping = indexOps.createMapping(Article.class);
                        }
                        logger.info("索引创建成功: {}", indexName);
                        indexOps.putMapping(mapping);
                    }
                    return indexCoordinates;
                }
        );
    }
}
