package ma.ensa.dop.auth.dto;

/**
 * Informations d'enrôlement renvoyées une seule fois à l'utilisateur authentifié.
 * Contient le secret TOTP en clair : ne jamais le journaliser.
 *
 * @param secret     secret Base32 (saisie manuelle)
 * @param otpauthUri URI à encoder en QR code
 */
public record MfaSetupResponse(String secret, String otpauthUri) {}
