// Types alignés sur les contrats du backend (auth-service, assujettis-service).
// Les dates (LocalDate / Instant côté Java) arrivent en chaînes ISO 8601.

export type Role = 'DECLARANT' | 'MAGISTRAT' | 'AUTORITE_TUTELLE' | 'ADMIN'

// ----- POST /auth/register -----
export interface RegisterRequest {
  email: string
  password: string // 8 à 72 caractères
}

export interface UserResponse {
  id: number
  email: string
  role: Role
}

// ----- POST /auth/login -----
export interface LoginRequest {
  email: string
  password: string
}

export interface LoginResponse {
  accessToken: string
  tokenType: string // "Bearer"
  expiresIn: number // secondes
}

// ----- GET /assujettis -----
export type StatutEcheance = 'A_VENIR' | 'PROCHE' | 'DEPASSEE'

export interface AssujettiResponse {
  id: number
  cin: string
  nom: string
  prenom: string
  email: string
  fonction: string
  organisme: string
  dateEntreeFonction: string
  echeanceDeclaration: string
  echeanceRenouvellement: string
  statutEcheance: StatutEcheance
  joursRestants: number
  importedAt: string
  importedBy: string
}

// ----- GET /assujettis/me/verification -----
// Les champs null sont omis du JSON par le backend : ils sont donc optionnels.
export interface VerificationResponse {
  assujetti: boolean
  accesFormulaire: boolean
  message?: string
  formulaireUrl?: string
  echeanceDeclaration?: string
  echeanceRenouvellement?: string
  statutEcheance?: StatutEcheance
}

// ----- GET /actuator/health (Spring Boot Actuator) -----
export interface HealthResponse {
  status: string
}
