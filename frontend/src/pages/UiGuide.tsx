import { useState } from 'react'
import { Alert, AppHeader, Banner, Button, Card, Field, Input, Modal, Select, Spinner, StatusBadge, Table, Tile, TileGrid } from '../design-system'

// Icônes au trait de démonstration (SVG, couleur héritée du texte).
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
const CheckIcon = (
  <svg {...iconProps}>
    <circle cx="20" cy="20" r="15" />
    <path d="M12 20.5l5.5 5.5L28 15" />
  </svg>
)

// Guide de style (/ui) : présente chaque composant du design system avec ses variantes.
function UiGuide() {
  const [modalOpen, setModalOpen] = useState(false)

  return (
    <>
    <AppHeader />
    <Banner title="Guide de style" subtitle="Les composants de la plateforme DOP, avec toutes leurs variantes." />
    <main className="ds-container">
      <h2 style={{ marginTop: 0 }}>Tuiles d'accès rapide</h2>
      <TileGrid>
        <Tile title="Ma déclaration" icon={DocumentIcon} href="#">
          Consulter ou compléter votre déclaration.
        </Tile>
        <Tile title="Mon profil" icon={UserIcon} href="#">
          Gérer les informations de votre compte.
        </Tile>
        <Tile title="Mon statut" icon={CheckIcon}>
          Savoir si vous êtes soumis à l'obligation de déclarer.
        </Tile>
      </TileGrid>

      <h2>Boutons</h2>
      <p style={{ display: 'flex', gap: 'var(--space-3)', flexWrap: 'wrap' }}>
        <Button>Principal</Button>
        <Button variant="secondary">Secondaire</Button>
        <Button variant="danger">Danger</Button>
        <Button disabled>Désactivé</Button>
      </p>

      <h2>Champs de formulaire</h2>
      <Card>
        <Field label="Adresse e-mail" help="Utilisée pour vous connecter.">
          <Input type="email" autoComplete="email" />
        </Field>
        <Field label="Mot de passe" help="Entre 8 et 72 caractères." error="Le mot de passe est trop court.">
          <Input type="password" autoComplete="new-password" defaultValue="abc" />
        </Field>
        <Field label="Rôle">
          <Select defaultValue="DECLARANT">
            <option value="DECLARANT">Déclarant</option>
            <option value="MAGISTRAT">Magistrat</option>
            <option value="AUTORITE_TUTELLE">Autorité de tutelle</option>
          </Select>
        </Field>
      </Card>

      <h2>Alertes</h2>
      <div style={{ display: 'grid', gap: 'var(--space-3)' }}>
        <Alert tone="info" title="Information">
          Votre déclaration est en cours de traitement.
        </Alert>
        <Alert tone="success" title="Succès">
          Votre inscription a bien été enregistrée.
        </Alert>
        <Alert tone="warning" title="Attention">
          L'échéance de déclaration approche.
        </Alert>
        <Alert tone="danger" title="Erreur">
          Serveur injoignable. Vérifiez votre connexion et réessayez.
        </Alert>
      </div>

      <h2>Badges de statut</h2>
      <p style={{ display: 'flex', gap: 'var(--space-3)', flexWrap: 'wrap' }}>
        <StatusBadge tone="success">À jour</StatusBadge>
        <StatusBadge tone="warning">Échéance proche</StatusBadge>
        <StatusBadge tone="danger">Échéance dépassée</StatusBadge>
        <StatusBadge tone="info">En cours</StatusBadge>
        <StatusBadge>Brouillon</StatusBadge>
      </p>

      <h2>Tableau</h2>
      <Table caption="Exemple de liste d'assujettis (données fictives)">
        <thead>
          <tr>
            <th scope="col">Nom</th>
            <th scope="col">Organisme</th>
            <th scope="col">Échéance</th>
            <th scope="col">Statut</th>
          </tr>
        </thead>
        <tbody>
          <tr>
            <td>Exemple A</td>
            <td>Organisme 1</td>
            <td>2027-03-31</td>
            <td>
              <StatusBadge tone="success">À jour</StatusBadge>
            </td>
          </tr>
          <tr>
            <td>Exemple B</td>
            <td>Organisme 2</td>
            <td>2026-12-01</td>
            <td>
              <StatusBadge tone="warning">Échéance proche</StatusBadge>
            </td>
          </tr>
        </tbody>
      </Table>

      <h2>Chargement</h2>
      <p>
        <Spinner label="Chargement des données…" />
      </p>

      <h2>Fenêtre modale</h2>
      <Button variant="secondary" onClick={() => setModalOpen(true)}>
        Ouvrir la fenêtre
      </Button>
      <Modal
        open={modalOpen}
        onClose={() => setModalOpen(false)}
        title="Confirmer l'action"
        footer={
          <>
            <Button variant="secondary" onClick={() => setModalOpen(false)}>
              Annuler
            </Button>
            <Button onClick={() => setModalOpen(false)}>Confirmer</Button>
          </>
        }
      >
        <p>Cette fenêtre se ferme avec la touche Échap ou le bouton Annuler.</p>
      </Modal>
    </main>
    </>
  )
}

export default UiGuide
