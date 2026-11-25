package ma.ensa.dop.audit.service;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.time.Instant;
import java.util.HexFormat;

/** Calcule l'empreinte SHA-256 d'une entrée à partir de son contenu et de l'empreinte précédente. */
public final class AuditHasher {

    private AuditHasher() { }

    public static String hash(long seqNo, Instant occurredAt, String actor, String action,
                              String resource, String details, String previousHash) {
        StringBuilder sb = new StringBuilder();
        field(sb, Long.toString(seqNo));
        field(sb, Long.toString(occurredAt.toEpochMilli()));
        field(sb, actor);
        field(sb, action);
        field(sb, resource);
        field(sb, details == null ? "" : details);
        field(sb, previousHash);
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            return HexFormat.of().formatHex(digest.digest(sb.toString().getBytes(StandardCharsets.UTF_8)));
        } catch (NoSuchAlgorithmException e) {
            throw new IllegalStateException("SHA-256 indisponible", e);
        }
    }

    /** Chaque champ est préfixé par sa longueur : ("ab","c") et ("a","bc") donnent des empreintes différentes. */
    private static void field(StringBuilder sb, String value) {
        sb.append(value.length()).append(':').append(value).append(';');
    }
}
