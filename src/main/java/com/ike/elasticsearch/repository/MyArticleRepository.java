package com.ike.elasticsearch.repository;

/**
 * @author <a href=mailto://idiotpre@outlook.com>IKE</a> 2/7/2025
 */
public interface MyArticleRepository <T>{
    <S extends T> S save(S entity);
}
