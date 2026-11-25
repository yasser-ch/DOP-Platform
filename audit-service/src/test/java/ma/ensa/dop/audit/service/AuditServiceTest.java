package ma.ensa.dop.audit.service;

import ma.ensa.dop.audit.entity.AuditEvent;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.jdbc.core.JdbcTemplate;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Tests de la chaîne de hachages sur la vraie base H2. Les altérations sont simulées en SQL direct,
 * comme le ferait quelqu'un qui contourne l'application.
 */
@SpringBootTest(properties = "dop.jwt.secret=" + AuditServiceTest.SECRET)
class AuditServiceTest {
    static final String SECRET = "test-only-secret-0123456789-abcdefghij";

    @Autowired AuditService service;
    @Autowired JdbcTemplate jdbc;

    /** Le dépôt n'a pas de delete (voulu) : on vide la table en SQL pour isoler les tests. */
    @BeforeEach
    void vider() { jdbc.update("delete from audit_event"); }

    private void troisEntrees() {
        service.append("alice@test.ma", "DECLARATION_CONSULTEE", "declaration/1", null);
        service.append("bob@test.ma", "DECLARATION_SOUMISE", "declaration/2", "ok");
        service.append("alice@test.ma", "DECLARATION_CONSULTEE", "declaration/3", "lecture");
    }

    @Test
    void les_entrees_sont_numerotees_et_chainees() {
        AuditEvent a = service.append("alice@test.ma", "DECLARATION_CONSULTEE", "declaration/1", null);
        AuditEvent b = service.append("bob@test.ma", "DECLARATION_SOUMISE", "declaration/2", "ok");

        assertEquals(1, a.getSeqNo());
        assertEquals(2, b.getSeqNo());
        assertEquals(AuditService.GENESIS, a.getPreviousHash());
        assertEquals(a.getHash(), b.getPreviousHash());
    }

    @Test
    void un_journal_intact_est_valide() {
        troisEntrees();
        VerificationResult r = service.verify();

        assertTrue(r.valid());
        assertEquals(3, r.checked());
        assertNull(r.firstBrokenSequence());
        assertEquals(64, r.headHash().length());
    }

    @Test
    void un_journal_vide_est_valide() {
        VerificationResult r = service.verify();
        assertTrue(r.valid());
        assertEquals(0, r.checked());
        assertEquals(AuditService.GENESIS, r.headHash());
    }

    @Test
    void une_entree_modifiee_en_base_est_detectee() {
        troisEntrees();
        jdbc.update("update audit_event set details = ? where seq_no = ?", "falsifie", 2);

        VerificationResult r = service.verify();

        assertFalse(r.valid());
        assertEquals(2L, r.firstBrokenSequence());
        assertEquals(1, r.checked());
    }

    @Test
    void un_acteur_modifie_en_base_est_detecte() {
        troisEntrees();
        jdbc.update("update audit_event set actor = ? where seq_no = ?", "admin@test.ma", 3);

        VerificationResult r = service.verify();

        assertFalse(r.valid());
        assertEquals(3L, r.firstBrokenSequence());
    }

    @Test
    void une_entree_supprimee_au_milieu_est_detectee() {
        troisEntrees();
        jdbc.update("delete from audit_event where seq_no = ?", 2);

        VerificationResult r = service.verify();

        assertFalse(r.valid());
        assertEquals(2L, r.firstBrokenSequence());
    }

    @Test
    void une_entree_supprimee_au_debut_est_detectee() {
        troisEntrees();
        jdbc.update("delete from audit_event where seq_no = ?", 1);

        VerificationResult r = service.verify();

        assertFalse(r.valid());
        assertEquals(1L, r.firstBrokenSequence());
    }

    @Test
    void la_liste_est_triee_du_plus_recent_au_plus_ancien_et_filtrable() {
        troisEntrees();

        List<AuditEvent> tout = service.list(null, null, 0, 10);
        assertEquals(3, tout.size());
        assertEquals(3, tout.get(0).getSeqNo());

        assertEquals(2, service.list("alice@test.ma", null, 0, 10).size());
        assertEquals(1, service.list(null, "DECLARATION_SOUMISE", 0, 10).size());
        assertEquals(2, service.list(null, null, 0, 2).size());
    }
}
