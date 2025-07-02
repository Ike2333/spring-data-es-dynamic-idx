package com.ike.elasticsearch.controller;

import java.time.Instant;

/**
 * @author <a href=mailto://idiotpre@outlook.com>IKE</a> 2/7/2025
 */
public record ArticleDto(
        String title,
        String content,
        String author,
        Instant time
) {
}
