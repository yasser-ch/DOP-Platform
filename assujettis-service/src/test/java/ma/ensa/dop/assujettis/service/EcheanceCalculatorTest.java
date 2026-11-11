package ma.ensa.dop.assujettis.service;

import ma.ensa.dop.assujettis.service.EcheanceCalculator.Statut;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;

import static org.assertj.core.api.Assertions.assertThat;

/** S13 - Tests purs (sans Spring) des règles de calcul. */
class EcheanceCalculatorTest {

    @Test void declaration_is_entry_date_plus_3_months() {
        assertThat(EcheanceCalculator.echeanceDeclaration(LocalDate.of(2026, 3, 15)))
            .isEqualTo(LocalDate.of(2026, 6, 15));
    }

    @Test void declaration_handles_end_of_month() {
        // 31 janvier + 3 mois : avril n'a que 30 jours
        assertThat(EcheanceCalculator.echeanceDeclaration(LocalDate.of(2026, 1, 31)))
            .isEqualTo(LocalDate.of(2026, 4, 30));
    }

    @Test void renewal_is_end_of_february_3_years_later() {
        // déclaration le 15/06/2026 -> renouvellement fin février 2029
        assertThat(EcheanceCalculator.echeanceRenouvellement(LocalDate.of(2026, 3, 15)))
            .isEqualTo(LocalDate.of(2029, 2, 28));
    }

    @Test void renewal_uses_29_february_on_leap_years() {
        assertThat(EcheanceCalculator.echeanceRenouvellement(LocalDate.of(2025, 3, 1)))
            .isEqualTo(LocalDate.of(2028, 2, 29));
    }

    @Test void renewal_follows_the_year_of_the_declaration_deadline() {
        // entrée 20/11/2026 -> déclaration 20/02/2027 -> renouvellement février 2030
        assertThat(EcheanceCalculator.echeanceRenouvellement(LocalDate.of(2026, 11, 20)))
            .isEqualTo(LocalDate.of(2030, 2, 28));
    }

    @Test void statut_depends_on_remaining_days() {
        LocalDate today = LocalDate.of(2026, 6, 1);
        assertThat(EcheanceCalculator.statut(LocalDate.of(2026, 5, 31), today)).isEqualTo(Statut.DEPASSEE);
        assertThat(EcheanceCalculator.statut(today, today)).isEqualTo(Statut.PROCHE);
        assertThat(EcheanceCalculator.statut(today.plusDays(30), today)).isEqualTo(Statut.PROCHE);
        assertThat(EcheanceCalculator.statut(today.plusDays(31), today)).isEqualTo(Statut.A_VENIR);
    }

    @Test void remaining_days_is_negative_when_overdue() {
        assertThat(EcheanceCalculator.joursRestants(LocalDate.of(2026, 5, 29), LocalDate.of(2026, 6, 1)))
            .isEqualTo(-3);
    }
}
