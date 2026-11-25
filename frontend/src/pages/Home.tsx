import { useState } from 'react'
import { getHealth } from '../api/endpoints'
import { Alert, Banner, Button, Card, Spinner, Stat, StatGrid, StatusBadge, Tile, TileGrid } from '../design-system'
import type { StatusTone } from '../design-system'
import { useT } from '../i18n'

type HealthStatus = 'idle' | 'loading' | 'UP' | 'DOWN' | 'unreachable'

const TONES: Record<Exclude<HealthStatus, 'idle' | 'loading'>, StatusTone> = {
  UP: 'success',
  DOWN: 'danger',
  unreachable: 'warning',
}
const LABELS = { UP: 'UP', DOWN: 'DOWN', unreachable: 'injoignable' } as const

const iconProps = { viewBox: '0 0 40 40', fill: 'none', stroke: 'currentColor', strokeWidth: 2, strokeLinejoin: 'round', strokeLinecap: 'round' } as const
const CheckIcon = (
  <svg {...iconProps}>
    <circle cx="20" cy="20" r="15" />
    <path d="M12 20.5l5.5 5.5L28 15" />
  </svg>
)
const UserPlusIcon = (
  <svg {...iconProps}>
    <circle cx="16" cy="14" r="6" />
    <path d="M4 35c1-8 5-12 12-12s9 2 11 6M30 24v10M25 29h10" />
  </svg>
)
const LockIcon = (
  <svg {...iconProps}>
    <rect x="8" y="18" width="24" height="16" rx="2" />
    <path d="M13 18v-5a7 7 0 0114 0v5" />
  </svg>
)
const ListIcon = (
  <svg {...iconProps}>
    <path d="M13 11h20M13 20h20M13 29h20" />
    <circle cx="7" cy="11" r="1.5" />
    <circle cx="7" cy="20" r="1.5" />
    <circle cx="7" cy="29" r="1.5" />
  </svg>
)

function Home() {
  const t = useT()
  const [status, setStatus] = useState<HealthStatus>('idle')

  async function checkHealth() {
    setStatus('loading')
    try {
      const health = await getHealth()
      setStatus(health.status === 'UP' ? 'UP' : 'DOWN')
    } catch {
      setStatus('unreachable')
    }
  }

  return (
    <>
      <Banner title={t('homeTitle')} subtitle={t('homeSubtitle')} />
      <main className="ds-container">
        <StatGrid>
          <Stat value="1 250" label="assujettis suivis (chiffre fictif)" />
          <Stat value="87 %" label="déclarations déposées à temps (chiffre fictif)" />
          <Stat value="48" label="organismes concernés (chiffre fictif)" />
        </StatGrid>

        <h2>Que souhaitez-vous faire ?</h2>
        <TileGrid>
          <Tile title="Je vérifie ma situation" icon={CheckIcon} href="#/espace-declarant" moreLabel={t('seeMore')}>
            Savoir si je figure sur la liste des assujettis et connaître mon échéance.
          </Tile>
          <Tile title="Je crée mon compte" icon={UserPlusIcon} href="#/inscription" moreLabel={t('seeMore')}>
            M'inscrire avec mon adresse e-mail pour accéder à mon espace.
          </Tile>
          <Tile title="Je me connecte" icon={LockIcon} href="#/connexion" moreLabel={t('seeMore')}>
            Retrouver mon espace déclarant ou la liste des assujettis.
          </Tile>
          <Tile title="Je consulte les assujettis" icon={ListIcon} href="#/assujettis" moreLabel={t('seeMore')}>
            Suivre les échéances de déclaration (autorité de tutelle).
          </Tile>
        </TileGrid>

        <h2>Les étapes</h2>
        <ol style={{ paddingInlineStart: 'var(--space-5)', maxWidth: '40rem' }}>
          <li>Créez votre compte et connectez-vous.</li>
          <li>Vérifiez que vous êtes soumis à l'obligation de déclarer.</li>
          <li>Déposez votre déclaration avant l'échéance.</li>
        </ol>

        <h2>État de la plateforme</h2>
        <Card>
          <p>Vérifiez que l'API répond.</p>
          <Button variant="secondary" onClick={checkHealth} disabled={status === 'loading'}>
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
        </Card>
      </main>
    </>
  )
}

export default Home
