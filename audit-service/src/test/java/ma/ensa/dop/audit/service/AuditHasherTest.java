package ma.ensa.dop.audit.service;

import org.junit.jupiter.api.Test;

import java.time.Instant;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class AuditHasherTest {

    private static final Instant T = Instant.parse("2026-11-25T10:00:00Z");
    private static final String PREV = AuditService.GENESIS;

    private static String h(long seq, String actor, String action, String resource, String details, String prev) {
        return AuditHasher.hash(seq, T, actor, action, resource, details, prev);
    }

    @Test
    void empreinte_deterministe_de_64_caracteres_hexa() {
        String a = h(1, "alice", "LECTURE", "declaration/1", "x", PREV);
        assertEquals(a, h(1, "alice", "LECTURE", "declaration/1", "x", PREV));
        assertEquals(64, a.length());
        assertTrue(a.matches("[0-9a-f]{64}"));
    }

    @Test
    void chaque_champ_modifie_change_l_empreinte() {
        String base = h(1, "alice", "LECTURE", "declaration/1", "x", PREV);
        assertNotEquals(base, h(2, "alice", "LECTURE", "declaration/1", "x", PREV));
        assertNotEquals(base, h(1, "bob", "LECTURE", "declaration/1", "x", PREV));
        assertNotEquals(base, h(1, "alice", "SOUMISSION", "declaration/1", "x", PREV));
        assertNotEquals(base, h(1, "alice", "LECTURE", "declaration/2", "x", PREV));
        assertNotEquals(base, h(1, "alice", "LECTURE", "declaration/1", "y", PREV));
        assertNotEquals(base, h(1, "alice", "LECTURE", "declaration/1", "x", "1".repeat(64)));
        assertNotEquals(base, AuditHasher.hash(1, T.plusMillis(1), "alice", "LECTURE", "declaration/1", "x", PREV));
    }

    @Test
    void la_frontiere_entre_champs_est_non_ambigue() {
        assertNotEquals(h(1, "ab", "c", "r", "d", PREV), h(1, "a", "bc", "r", "d", PREV));
    }

    @Test
    void details_absent_equivaut_a_details_vide() {
        assertEquals(h(1, "alice", "A", "r", null, PREV), h(1, "alice", "A", "r", "", PREV));
    }
}
