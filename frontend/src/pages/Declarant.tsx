import { Alert, Banner, Breadcrumb, Card, StatusBadge, Tile, TileGrid } from '../design-system'
import { MOCK_VERIFICATION, STATUT_LABELS } from './mockData'

const iconProps = { viewBox: '0 0 40 40', fill: 'none', stroke: 'currentColor', strokeWidth: 2, strokeLinejoin: 'round', strokeLinecap: 'round' } as const
const DocumentIcon = (
  <svg {...iconProps}>
    <path d="M10 4h14l8 8v24H10z" />
    <path d="M24 4v8h8M15 20h12M15 26h12M15 32h8" />
  </svg>
)
const UserIcon = (
  <svg {...iconProps}>
    <circle cx="20" cy="14" r="6" />
    <path d="M7 35c1-8 6-12 13-12s12 4 13 12" />
  </svg>
)

function Declarant() {
  const v = MOCK_VERIFICATION
  return (
    <>
      <Breadcrumb items={[{ label: 'Accueil', href: '#/' }, { label: 'Espace déclarant' }]} />
      <Banner title="Mon espace déclarant" subtitle="Votre situation et votre échéance" />
      <main className="ds-container">
        <Alert tone="info" title="Votre situation">
          {v.message}
        </Alert>

        <h2>Échéances</h2>
        <Card>
          <dl style={{ display: 'grid', gridTemplateColumns: 'max-content 1fr', gap: 'var(--space-2) var(--space-5)', margin: 0 }}>
            <dt>Déclaration à déposer avant le</dt>
            <dd style={{ margin: 0 }}>
              {v.echeanceDeclaration} <StatusBadge tone="info">{STATUT_LABELS[v.statutEcheance]}</StatusBadge>
            </dd>
            <dt>Renouvellement</dt>
            <dd style={{ margin: 0 }}>{v.echeanceRenouvellement}</dd>
          </dl>
        </Card>

        <h2>Mes démarches</h2>
        <TileGrid>
          <Tile title="Ma déclaration" icon={DocumentIcon}>
            Disponible prochainement.
          </Tile>
          <Tile title="Mon profil" icon={UserIcon}>
            Disponible prochainement.
          </Tile>
        </TileGrid>
      </main>
    </>
  )
}

export default Declarant
