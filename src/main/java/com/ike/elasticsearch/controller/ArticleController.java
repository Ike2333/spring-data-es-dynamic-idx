package com.ike.elasticsearch.controller;

import co.elastic.clients.elasticsearch._types.query_dsl.QueryBuilders;
import com.ike.elasticsearch.entity.Article;
import com.ike.elasticsearch.repository.ArticleRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.elasticsearch.client.elc.NativeQueryBuilder;
import org.springframework.data.elasticsearch.core.ElasticsearchOperations;
import org.springframework.data.elasticsearch.core.SearchHit;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * @author <a href=mailto://idiotpre@outlook.com>IKE</a> 2/7/2025
 */
@RestController
@RequestMapping("/article")
public class ArticleController {

    private final ArticleRepository articleRepository;
    private final ElasticsearchOperations elasticsearchOperations;


    public ArticleController(ArticleRepository articleRepository, ElasticsearchOperations elasticsearchOperations) {
        this.articleRepository = articleRepository;
        this.elasticsearchOperations = elasticsearchOperations;
    }

    @PostMapping
    public Article create(@RequestBody ArticleDto dto) {
        Article article = dtoToEntity(dto);
        return articleRepository.save(article);
    }

    @GetMapping
    public Page<Article> findAll(@RequestParam(required = false) String keyword, Pageable pageable) {
        var nativeQuery = new NativeQueryBuilder()
                .withQuery(QueryBuilders.multiMatch(b ->
                        b.query(keyword).fields("author", "title"))
                )
                .withPageable(pageable)
                .build();
        var hits = elasticsearchOperations.search(nativeQuery, Article.class);
        List<Article> list = hits.stream().map(SearchHit::getContent).toList();
        return new PageImpl<>(list, pageable, hits.getTotalHits());
    }

    private Article dtoToEntity(ArticleDto dto) {
        return new Article(dto.title(), dto.content(), dto.author(), dto.time());
    }
}
