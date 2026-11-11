// Catégories d'erreurs que les écrans peuvent distinguer sans connaître HTTP.
export type ApiErrorKind =
  | 'network' // serveur injoignable
  | 'timeout' // pas de réponse dans le délai
  | 'unauthorized' // 401
  | 'forbidden' // 403
  | 'server' // 5xx
  | 'client' // autres 4xx (données invalides, conflit, etc.)

export class ApiError extends Error {
  readonly kind: ApiErrorKind
  /** Code HTTP, ou null pour une erreur réseau / timeout. */
  readonly status: number | null
  /** Corps JSON de la réponse d'erreur, s'il y en avait un. */
  readonly body: unknown

  constructor(kind: ApiErrorKind, status: number | null, message: string, body: unknown = null) {
    super(message)
    this.name = 'ApiError'
    this.kind = kind
    this.status = status
    this.body = body
  }
}

// Messages par défaut en français. Un message renvoyé par le backend (champ "error")
// est utilisé en priorité pour les erreurs 4xx : il est déjà en français et plus précis.
export const MESSAGES = {
  network: 'Serveur injoignable. Vérifiez votre connexion et réessayez.',
  timeout: 'Le serveur met trop de temps à répondre. Réessayez dans un instant.',
  sessionExpired: 'Votre session a expiré. Veuillez vous reconnecter.',
  unauthorized: 'Identifiants invalides.',
  forbidden: "Vous n'avez pas les droits nécessaires pour effectuer cette action.",
  server: 'Le service est momentanément indisponible. Réessayez plus tard.',
  client: 'La requête a été refusée.',
} as const
