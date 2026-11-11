import { useState } from 'react'
import { Alert, Button, Card, Field, Input, Modal, Select, Spinner, StatusBadge, Table } from '../design-system'

// Guide de style (/ui) : présente chaque composant du design system avec ses variantes.
function UiGuide() {
  const [modalOpen, setModalOpen] = useState(false)

  return (
    <main className="ds-container">
      <h1>Guide de style</h1>
      <p>Composants du design system de la plateforme DOP. Cette page sert de référence visuelle.</p>

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
  )
}

export default UiGuide
