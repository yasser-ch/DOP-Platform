import { useState } from 'react'
import { getHealth } from './api/endpoints'
import { Alert, Button, Spinner, StatusBadge } from './design-system'
import type { StatusTone } from './design-system'

// Résultat possible du test de santé de la plateforme.
type HealthStatus = 'idle' | 'loading' | 'UP' | 'DOWN' | 'unreachable'

const TONES: Record<Exclude<HealthStatus, 'idle' | 'loading'>, StatusTone> = {
  UP: 'success',
  DOWN: 'danger',
  unreachable: 'warning',
}

const LABELS = { UP: 'UP', DOWN: 'DOWN', unreachable: 'injoignable' } as const

function App() {
  const [status, setStatus] = useState<HealthStatus>('idle')

  async function checkHealth() {
    setStatus('loading')
    try {
      // Règle du projet : les composants passent par src/api, jamais de fetch direct.
      const health = await getHealth()
      setStatus(health.status === 'UP' ? 'UP' : 'DOWN')
    } catch {
      // Réseau coupé, gateway arrêtée, délai dépassé ou erreur serveur.
      setStatus('unreachable')
    }
  }

  return (
    <main className="ds-container">
      <h1>DOP Platform</h1>
      <p>Déclaration obligatoire du patrimoine.</p>
      <Button onClick={checkHealth} disabled={status === 'loading'}>
        Vérifier l'état de l'API
      </Button>
      <div style={{ marginTop: 'var(--space-4)' }} aria-live="polite">
        {status === 'loading' && <Spinner label="Vérification en cours…" />}
        {status !== 'idle' && status !== 'loading' && (
          <p>
            État de l'API : <StatusBadge tone={TONES[status]}>{LABELS[status]}</StatusBadge>
          </p>
        )}
        {status === 'unreachable' && (
          <Alert tone="warning">L'API Gateway ne répond pas. Vérifiez qu'elle est démarrée.</Alert>
        )}
      </div>
    </main>
  )
}

export default App
