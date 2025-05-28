package com.dbp.legalcheck.domain.guide;

import com.dbp.legalcheck.common.enums.GuideType;
import com.dbp.legalcheck.common.enums.UserRole;
import com.dbp.legalcheck.domain.lawyer.Lawyer;

import org.junit.jupiter.api.Test;

import java.time.Instant;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

class GuideTest {

    @Test
    void guideSettersAndGettersWorkCorrectly() {
        UUID guideId = UUID.randomUUID();
        Lawyer author = new Lawyer();
        author.setFirstName("María");
        author.setLastName("Fernández");
        author.setEmail("maria@example.com");
        author.setPhoneNumber("123456789");
        author.setTuitionNumber("T001");
        author.setYearExperience(8);
        author.setRole(UserRole.ABOGADO);
        author.setPassword("securepass");

        Instant now = Instant.now();

        Guide guide = new Guide();
        guide.setId(guideId);
        guide.setAuthor(author);
        guide.setTitle("Cómo denunciar violencia familiar");
        guide.setContent("Esta guía explica paso a paso...");
        guide.setType(GuideType.PENAL);
        guide.setCreatedAt(now);
        guide.setUpdatedAt(now);

        assertEquals(guideId, guide.getId());
        assertEquals(author, guide.getAuthor());
        assertEquals("Cómo denunciar violencia familiar", guide.getTitle());
        assertEquals("Esta guía explica paso a paso...", guide.getContent());
        assertEquals(GuideType.PENAL, guide.getType());
        assertEquals(now, guide.getCreatedAt());
        assertEquals(now, guide.getUpdatedAt());
    }
}
