import { request } from './client'
import { ApiError } from './errors'
import type {
  AssujettiResponse,
  HealthResponse,
  LoginRequest,
  LoginResponse,
  RegisterRequest,
  UserResponse,
  VerificationResponse,
} from './types'

// Les composants appellent uniquement ces fonctions : aucun chemin d'API n'est écrit ailleurs.

export function register(data: RegisterRequest): Promise<UserResponse> {
  return request<UserResponse>('/auth/register', { method: 'POST', body: data, authenticated: false })
}

export function login(data: LoginRequest): Promise<LoginResponse> {
  return request<LoginResponse>('/auth/login', { method: 'POST', body: data, authenticated: false })
}

// Réservé aux rôles AUTORITE_TUTELLE et ADMIN (sinon 403).
export function listerAssujettis(): Promise<AssujettiResponse[]> {
  return request<AssujettiResponse[]>('/assujettis')
}

// Réservé au rôle DECLARANT (sinon 403).
export function verifierMonStatut(): Promise<VerificationResponse> {
  return request<VerificationResponse>('/assujettis/me/verification')
}

function isHealthBody(body: unknown): body is HealthResponse {
  return typeof body === 'object' && body !== null && typeof (body as HealthResponse).status === 'string'
}

// Actuator répond 503 avec { "status": "DOWN" } quand un composant est en panne :
// c'est une réponse valide pour nous, pas une erreur.
export async function getHealth(): Promise<HealthResponse> {
  try {
    return await request<HealthResponse>('/actuator/health', { authenticated: false })
  } catch (error) {
    if (error instanceof ApiError && error.status === 503 && isHealthBody(error.body)) {
      return error.body
    }
    throw error
  }
}
