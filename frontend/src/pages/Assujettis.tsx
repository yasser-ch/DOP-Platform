import { useState } from 'react'
import { Banner, Breadcrumb, Card, Field, Input, Select, StatusBadge, Table } from '../design-system'
import type { StatusTone } from '../design-system'
import type { StatutEcheance } from '../api/types'
import { MOCK_ASSUJETTIS, STATUT_LABELS } from './mockData'

const TONES: Record<StatutEcheance, StatusTone> = { A_VENIR: 'info', PROCHE: 'warning', DEPASSEE: 'danger' }

function Assujettis() {
  const [search, setSearch] = useState('')
  const [statut, setStatut] = useState<'' | StatutEcheance>('')

  const rows = MOCK_ASSUJETTIS.filter(
    (a) =>
      (statut === '' || a.statutEcheance === statut) &&
      `${a.nom} ${a.prenom} ${a.organisme}`.toLowerCase().includes(search.trim().toLowerCase()),
  )
  const count = (s: StatutEcheance) => MOCK_ASSUJETTIS.filter((a) => a.statutEcheance === s).length

  return (
    <>
      <Breadcrumb items={[{ label: 'Accueil', href: '#/' }, { label: 'Assujettis' }]} />
      <Banner title="Liste des assujettis" subtitle="Suivi des échéances de déclaration" />
      <main className="ds-container">
        <div style={{ display: 'grid', gap: 'var(--space-4)', gridTemplateColumns: 'repeat(auto-fit, minmax(12rem, 1fr))' }}>
          <Card title={`${MOCK_ASSUJETTIS.length} assujettis`}>
            <p style={{ margin: 0 }}>Sur la liste importée.</p>
          </Card>
          <Card title={`${count('PROCHE')} échéances proches`}>
            <p style={{ margin: 0 }}>À rappeler prochainement.</p>
          </Card>
          <Card title={`${count('DEPASSEE')} échéance dépassée`}>
            <p style={{ margin: 0 }}>À traiter en priorité.</p>
          </Card>
        </div>

        <h2>Assujettis</h2>
        <div style={{ display: 'grid', gap: 'var(--space-4)', gridTemplateColumns: 'repeat(auto-fit, minmax(14rem, 1fr))' }}>
          <Field label="Rechercher">
            <Input type="search" value={search} onChange={(e) => setSearch(e.target.value)} placeholder="Nom, prénom ou organisme" />
          </Field>
          <Field label="Statut de l'échéance">
            <Select value={statut} onChange={(e) => setStatut(e.target.value as '' | StatutEcheance)}>
              <option value="">Tous</option>
              <option value="A_VENIR">À venir</option>
              <option value="PROCHE">Échéance proche</option>
              <option value="DEPASSEE">Échéance dépassée</option>
            </Select>
          </Field>
        </div>

        <Table caption={`${rows.length} assujetti(s) affiché(s), données fictives`}>
          <thead>
            <tr>
              <th scope="col">Nom</th>
              <th scope="col">Organisme</th>
              <th scope="col">Échéance</th>
              <th scope="col">Jours restants</th>
              <th scope="col">Statut</th>
            </tr>
          </thead>
          <tbody>
            {rows.map((a) => (
              <tr key={a.id}>
                <td>
                  {a.nom} {a.prenom}
                </td>
                <td>{a.organisme}</td>
                <td>{a.echeanceDeclaration}</td>
                <td>{a.joursRestants}</td>
                <td>
                  <StatusBadge tone={TONES[a.statutEcheance]}>{STATUT_LABELS[a.statutEcheance]}</StatusBadge>
                </td>
              </tr>
            ))}
            {rows.length === 0 && (
              <tr>
                <td colSpan={5}>Aucun assujetti ne correspond à votre recherche. Modifiez le texte ou le statut.</td>
              </tr>
            )}
          </tbody>
        </Table>
      </main>
    </>
  )
}

export default Assujettis
