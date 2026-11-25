package ma.ensa.dop.declaration.exception;

/** Un brouillon de ce type existe déjà : l'agent doit le reprendre au lieu d'en créer un second. */
public class BrouillonDejaExistantException extends RuntimeException {
    private final Long declarationId;
    public BrouillonDejaExistantException(Long declarationId) { this.declarationId = declarationId; }
    public Long getDeclarationId() { return declarationId; }
}
