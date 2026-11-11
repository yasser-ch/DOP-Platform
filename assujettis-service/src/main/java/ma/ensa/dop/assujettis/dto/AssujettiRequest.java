package ma.ensa.dop.assujettis.dto;

import jakarta.validation.constraints.*;

import java.time.LocalDate;

/**
 * Une ligne de la liste nominative. Mêmes règles de validation pour le formulaire (JSON)
 * ET pour chaque ligne du fichier CSV.
 */
public record AssujettiRequest(
    @NotBlank @Pattern(regexp = "^[A-Za-z]{1,2}\\d{5,6}$", message = "CIN invalide (ex: AB123456)")
    String cin,

    @NotBlank @Size(max = 100) @Pattern(regexp = Patterns.TEXTE_SUR, message = "caractère interdit en début de champ")
    String nom,

    @NotBlank @Size(max = 100) @Pattern(regexp = Patterns.TEXTE_SUR, message = "caractère interdit en début de champ")
    String prenom,

    @NotBlank @Email @Size(max = 254)
    String email,

    @NotBlank @Size(max = 150) @Pattern(regexp = Patterns.TEXTE_SUR, message = "caractère interdit en début de champ")
    String fonction,

    @NotBlank @Size(max = 150) @Pattern(regexp = Patterns.TEXTE_SUR, message = "caractère interdit en début de champ")
    String organisme,

    @NotNull @PastOrPresent
    LocalDate dateEntreeFonction) {

    /** À appeler APRÈS validation : CIN en majuscules, e-mail en minuscules, espaces retirés. */
    public AssujettiRequest normalise() {
        return new AssujettiRequest(
            cin.trim().toUpperCase(), nom.trim(), prenom.trim(), email.trim().toLowerCase(),
            fonction.trim(), organisme.trim(), dateEntreeFonction);
    }
}
