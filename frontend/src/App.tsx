import { useState } from 'react'

// Résultat possible du test de santé de la plateforme.
type HealthStatus = 'idle' | 'loading' | 'UP' | 'DOWN' | 'unreachable'

// Forme minimale de la réponse de Spring Boot Actuator : { "status": "UP" }
interface HealthResponse {
  status: string
}

function App() {
  const [status, setStatus] = useState<HealthStatus>('idle')

  async function checkHealth() {
    setStatus('loading')
    try {
      // Règle du projet : le front n'appelle jamais que /api/...
      const response = await fetch('/api/actuator/health')
      // Actuator répond 503 avec { "status": "DOWN" } : on lit donc le corps dans tous les cas.
      const body = (await response.json()) as HealthResponse
      setStatus(body.status === 'UP' ? 'UP' : 'DOWN')
    } catch {
      // Réseau coupé, gateway arrêtée, ou réponse qui n'est pas du JSON.
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
