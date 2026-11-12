// Données FICTIVES pour les maquettes. Elles seront remplacées par les appels à l'API (src/api).
import type { StatutEcheance } from '../api/types'

export interface MockAssujetti {
  id: number
  nom: string
  prenom: string
  organisme: string
  echeanceDeclaration: string
  statutEcheance: StatutEcheance
  joursRestants: number
}

export const MOCK_ASSUJETTIS: MockAssujetti[] = [
  { id: 1, nom: 'Alaoui', prenom: 'Samira', organisme: 'Ministère (exemple)', echeanceDeclaration: '2027-03-31', statutEcheance: 'A_VENIR', joursRestants: 140 },
  { id: 2, nom: 'Bennani', prenom: 'Youssef', organisme: 'Collectivité territoriale (exemple)', echeanceDeclaration: '2026-12-05', statutEcheance: 'PROCHE', joursRestants: 24 },
  { id: 3, nom: 'El Idrissi', prenom: 'Karim', organisme: 'Établissement public (exemple)', echeanceDeclaration: '2026-10-15', statutEcheance: 'DEPASSEE', joursRestants: -27 },
  { id: 4, nom: 'Tazi', prenom: 'Nadia', organisme: 'Ministère (exemple)', echeanceDeclaration: '2027-06-30', statutEcheance: 'A_VENIR', joursRestants: 231 },
  { id: 5, nom: 'Chraibi', prenom: 'Omar', organisme: 'Établissement public (exemple)', echeanceDeclaration: '2026-11-30', statutEcheance: 'PROCHE', joursRestants: 19 },
  { id: 6, nom: 'Berrada', prenom: 'Salma', organisme: 'Collectivité territoriale (exemple)', echeanceDeclaration: '2027-01-15', statutEcheance: 'A_VENIR', joursRestants: 65 },
]

export const STATUT_LABELS: Record<StatutEcheance, string> = {
  A_VENIR: 'À venir',
  PROCHE: 'Échéance proche',
  DEPASSEE: 'Échéance dépassée',
}

// Réponse type de GET /assujettis/me/verification pour un déclarant figurant sur la liste.
export const MOCK_VERIFICATION: {
  message: string
  echeanceDeclaration: string
  echeanceRenouvellement: string
  statutEcheance: StatutEcheance
} = {
  message: 'Vous figurez sur la liste des assujettis : vous devez déposer une déclaration.',
  echeanceDeclaration: '2027-03-31',
  echeanceRenouvellement: '2030-03-31',
  statutEcheance: 'A_VENIR',
}
