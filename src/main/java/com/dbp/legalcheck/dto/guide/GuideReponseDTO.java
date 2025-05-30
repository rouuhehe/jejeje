package com.dbp.legalcheck.dto.guide;

import java.time.Instant;
import java.util.UUID;

import com.dbp.legalcheck.common.enums.GuideType;
import com.dbp.legalcheck.domain.guide.Guide;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class GuideReponseDTO {
    private UUID id;
    private UUID authorId;
    private String title;
    private String content;
    private GuideType type;
    private Instant createdAt;
    private Instant updatedAt;

    public GuideReponseDTO(Guide guide) {
        this.id = guide.getId();
        if (guide.getAuthor() != null) {
            this.authorId = guide.getAuthor().getId();
        }
        this.title = guide.getTitle();
        this.content = guide.getContent();
        this.createdAt = guide.getCreatedAt();
        this.updatedAt = guide.getUpdatedAt();
    }
}
