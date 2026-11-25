import { ApiError, MESSAGES } from './errors'

// Seul endroit du front qui connaît le préfixe de l'API.
// En dev, Vite relaie /api vers la gateway ; en production, nginx fait de même.
const API_PREFIX = '/api'
const DEFAULT_TIMEOUT_MS = 10_000

// Le jeton vit en mémoire dans le contexte d'authentification (ticket S42),
// jamais dans localStorage. Le client ne fait que le lire via ce fournisseur.
let tokenProvider: () => string | null = () => null
// Appelé quand une requête authentifiée reçoit un 401 : le contexte d'authentification déconnecte l'utilisateur.
let unauthorizedHandler: () => void = () => {}

export function setTokenProvider(provider: () => string | null): void {
  tokenProvider = provider
}

export function setUnauthorizedHandler(handler: () => void): void {
  unauthorizedHandler = handler
}

export interface RequestOptions {
  method?: 'GET' | 'POST' | 'PUT' | 'DELETE'
  body?: unknown
  timeoutMs?: number
  /**
   * true (défaut) : ajoute le jeton s'il existe, et traite un 401 comme une session expirée.
   * false : pour login et register, où un 401 signifie « identifiants invalides ».
   */
  authenticated?: boolean
}

// Lit le corps JSON sans jamais lever d'erreur (corps vide, HTML d'un proxy, etc.).
async function readBody(response: Response): Promise<unknown> {
  try {
    const text = await response.text()
    return text ? JSON.parse(text) : null
  } catch {
    return null
  }
}

// Extrait le message d'erreur du backend ({ "error": "..." }) s'il existe.
function backendMessage(body: unknown): string | null {
  if (typeof body === 'object' && body !== null && 'error' in body) {
    const { error } = body as { error: unknown }
    if (typeof error === 'string' && error.length > 0) return error
  }
  return null
}

export async function request<T>(path: string, options: RequestOptions = {}): Promise<T> {
  const { method = 'GET', body, timeoutMs = DEFAULT_TIMEOUT_MS, authenticated = true } = options

  const headers: Record<string, string> = { Accept: 'application/json' }
  if (body !== undefined) headers['Content-Type'] = 'application/json'
  if (authenticated) {
    const token = tokenProvider()
    if (token) headers.Authorization = `Bearer ${token}`
  }

  const controller = new AbortController()
  const timer = setTimeout(() => controller.abort(), timeoutMs)

  let response: Response
  try {
    response = await fetch(`${API_PREFIX}${path}`, {
      method,
      headers,
      body: body === undefined ? undefined : JSON.stringify(body),
      signal: controller.signal,
    })
  } catch (error) {
    if (error instanceof DOMException && error.name === 'AbortError') {
      throw new ApiError('timeout', null, MESSAGES.timeout)
    }
    throw new ApiError('network', null, MESSAGES.network)
  } finally {
    clearTimeout(timer)
  }

  const data = await readBody(response)
  if (response.ok) return data as T

  const { status } = response
  if (status === 401) {
    if (authenticated) {
      unauthorizedHandler()
      throw new ApiError('unauthorized', status, MESSAGES.sessionExpired, data)
    }
    throw new ApiError('unauthorized', status, backendMessage(data) ?? MESSAGES.unauthorized, data)
  }
  if (status === 403) throw new ApiError('forbidden', status, MESSAGES.forbidden, data)
  if (status >= 500) throw new ApiError('server', status, MESSAGES.server, data)
  throw new ApiError('client', status, backendMessage(data) ?? MESSAGES.client, data)
}
