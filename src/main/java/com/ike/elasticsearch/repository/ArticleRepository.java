package com.ike.elasticsearch.repository;

import com.ike.elasticsearch.entity.Article;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;

/**
 * @author <a href=mailto://idiotpre@outlook.com>IKE</a> 2/7/2025
 */
public interface ArticleRepository extends ElasticsearchRepository<Article, String>, MyArticleRepository<Article> {
    Page<Article> findByTitleContains(String title, Pageable pageable);
}
