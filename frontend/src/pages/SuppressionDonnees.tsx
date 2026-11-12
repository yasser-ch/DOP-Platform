import { useState } from 'react'
import type { FormEvent } from 'react'
import { Alert, Banner, Breadcrumb, Button, Card, CheckboxField, Field, Input, Modal } from '../design-system'

// Maquette : l'appel à l'API n'existe pas encore (voir README : endpoint de suppression à prévoir côté backend).
function SuppressionDonnees() {
  const [errors, setErrors] = useState<{ email?: string; confirm?: string }>({})
  const [confirming, setConfirming] = useState(false)
  const [sent, setSent] = useState(false)

  function handleSubmit(event: FormEvent<HTMLFormElement>) {
    event.preventDefault()
    const form = new FormData(event.currentTarget)
    const email = String(form.get('email') ?? '')
    const next: typeof errors = {}
    if (!/^\S+@\S+\.\S+$/.test(email)) next.email = 'Saisissez l\'adresse e-mail de votre compte.'
    if (form.get('confirm') !== 'on') next.confirm = 'Cochez la case pour confirmer que vous avez compris.'
    setErrors(next)
    if (Object.keys(next).length === 0) setConfirming(true)
  }

  return (
    <>
      <Breadcrumb items={[{ label: 'Accueil', href: '#/' }, { label: 'Supprimer mes données' }]} />
      <Banner title="Supprimer mes données" subtitle="Demandez la suppression de votre compte" />
      <main className="ds-container" style={{ maxWidth: '40rem' }}>
        <p>
          Vous pouvez demander la suppression de votre compte et des données qui y sont liées. Certaines informations peuvent
          devoir être conservées pour répondre à une obligation légale : la <a href="#/confidentialite">politique de confidentialité</a> le
          précise.
        </p>
        <Card>
          {sent ? (
            <Alert tone="success" title="Demande enregistrée">
              Nous avons bien reçu votre demande. Maquette : aucune donnée n'est réellement supprimée tant que l'API n'est pas reliée.
            </Alert>
          ) : (
            <form onSubmit={handleSubmit} noValidate>
              <Field label="Adresse e-mail de votre compte" help="Nous ne demandons rien d'autre." error={errors.email}>
                <Input name="email" type="email" autoComplete="email" />
              </Field>
              <CheckboxField
                name="confirm"
                label="Je comprends que cette suppression est définitive et que je ne pourrai plus accéder à mon compte."
                error={errors.confirm}
              />
              <Button type="submit" variant="danger">
                Demander la suppression
              </Button>
            </form>
          )}
        </Card>
        <div style={{ marginTop: 'var(--space-4)' }}>
          <Alert tone="info">Maquette : la demande n'est pas encore reliée à l'API.</Alert>
        </div>

        <Modal
          open={confirming}
          onClose={() => setConfirming(false)}
          title="Confirmer la suppression ?"
          footer={
            <>
              <Button variant="secondary" onClick={() => setConfirming(false)}>
                Annuler
              </Button>
              <Button
                variant="danger"
                onClick={() => {
                  setConfirming(false)
                  setSent(true)
                }}
              >
                Confirmer la suppression
              </Button>
            </>
          }
        >
          <p>Cette action est définitive. Votre compte et vos données seront supprimés.</p>
        </Modal>
      </main>
    </>
  )
}

export default SuppressionDonnees
