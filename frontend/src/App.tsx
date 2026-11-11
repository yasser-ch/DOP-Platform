import { useState } from 'react'
import { getHealth } from './api/endpoints'

// Résultat possible du test de santé de la plateforme.
type HealthStatus = 'idle' | 'loading' | 'UP' | 'DOWN' | 'unreachable'

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

  const labels: Record<HealthStatus, string> = {
    idle: '—',
    loading: 'Vérification…',
    UP: 'UP',
    DOWN: 'DOWN',
    unreachable: 'injoignable',
  }

  return (
    <main style={{ fontFamily: 'sans-serif', maxWidth: 480, margin: '4rem auto' }}>
      <h1>DOP Platform</h1>
      <button onClick={checkHealth} disabled={status === 'loading'}>
        Vérifier l'état de l'API
      </button>
      <p>
        État : <strong>{labels[status]}</strong>
      </p>
    </main>
  )
}

export default App
