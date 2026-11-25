package ma.ensa.dop.assujettis.service;

import java.time.LocalDate;
import java.time.Month;
import java.time.YearMonth;
import java.time.temporal.ChronoUnit;

/**
 * S13 - Règles de calcul des échéances légales.
 * Classe pure (sans Spring, sans base de données) : facile à tester et à modifier si la règle change.
 */
public final class EcheanceCalculator {

    /** Au-delà de ce nombre de jours restants, l'échéance est "A_VENIR" ; en dessous, "PROCHE". */
    public static final int SEUIL_PROCHE_JOURS = 30;

    public enum Statut { A_VENIR, PROCHE, DEPASSEE }

    private EcheanceCalculator() {}

    /** Déclaration initiale : date d'entrée en fonction + 3 mois. */
    public static LocalDate echeanceDeclaration(LocalDate dateEntreeFonction) {
        return dateEntreeFonction.plusMonths(3);
    }

    /**
     * Renouvellement : +3 ans après l'année de la déclaration initiale, au mois de février.
     * On retient le dernier jour de février (28 ou 29 selon l'année bissextile) comme date limite.
     */
    public static LocalDate echeanceRenouvellement(LocalDate dateEntreeFonction) {
        int anneeRenouvellement = echeanceDeclaration(dateEntreeFonction).getYear() + 3;
        return YearMonth.of(anneeRenouvellement, Month.FEBRUARY).atEndOfMonth();
    }

    /** Jours restants avant l'échéance (négatif si elle est dépassée). */
    public static long joursRestants(LocalDate echeance, LocalDate aujourdHui) {
        return ChronoUnit.DAYS.between(aujourdHui, echeance);
    }

    public static Statut statut(LocalDate echeance, LocalDate aujourdHui) {
        long jours = joursRestants(echeance, aujourdHui);
        if (jours < 0) return Statut.DEPASSEE;
        if (jours <= SEUIL_PROCHE_JOURS) return Statut.PROCHE;
        return Statut.A_VENIR;
    }
}
