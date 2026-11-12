import { useState } from 'react'
import type { FormEvent } from 'react'
import { Alert, Banner, Breadcrumb, Button, Card, CheckboxField, Field, Input } from '../design-system'

interface FormErrors {
  email?: string
  password?: string
  confirm?: string
  age?: string
  terms?: string
}

// Maquette : la validation montre les états d'erreur du design system ; l'envoi à l'API viendra avec DODP-56.
// Données demandées : uniquement l'e-mail et le mot de passe (minimisation). Aucune date de naissance :
// l'âge s'atteste par une case à cocher.
function Register() {
  const [errors, setErrors] = useState<FormErrors>({})
  const [valid, setValid] = useState(false)

  function handleSubmit(event: FormEvent<HTMLFormElement>) {
    event.preventDefault()
    const form = new FormData(event.currentTarget)
    const email = String(form.get('email') ?? '')
    const password = String(form.get('password') ?? '')
    const confirm = String(form.get('confirm') ?? '')
    const next: FormErrors = {}
    if (!/^\S+@\S+\.\S+$/.test(email)) next.email = 'Saisissez une adresse e-mail valide.'
    if (password.length < 8 || password.length > 72) next.password = 'Le mot de passe doit contenir entre 8 et 72 caractères.'
    if (confirm !== password) next.confirm = 'Les deux mots de passe ne correspondent pas.'
    if (form.get('age') !== 'on') next.age = 'Ce service est réservé aux personnes de 18 ans ou plus.'
    if (form.get('terms') !== 'on') next.terms = 'Vous devez accepter les conditions et la politique de confidentialité pour créer un compte.'
    setErrors(next)
    setValid(Object.keys(next).length === 0)
  }

  return (
    <>
      <Breadcrumb items={[{ label: 'Accueil', href: '#/' }, { label: 'Créer un compte' }]} />
      <Banner title="Créer un compte" subtitle="Inscrivez-vous pour accéder à votre espace" />
      <main className="ds-container" style={{ maxWidth: '34rem' }}>
        <Card>
          <p>Nous ne demandons que votre adresse e-mail et un mot de passe.</p>
          <form onSubmit={handleSubmit} noValidate>
            <Field label="Adresse e-mail" error={errors.email}>
              <Input name="email" type="email" autoComplete="email" />
            </Field>
            <Field label="Mot de passe" help="Entre 8 et 72 caractères." error={errors.password}>
              <Input name="password" type="password" autoComplete="new-password" />
            </Field>
            <Field label="Confirmation du mot de passe" error={errors.confirm}>
              <Input name="confirm" type="password" autoComplete="new-password" />
            </Field>

            <CheckboxField name="age" label="Je confirme avoir 18 ans ou plus." error={errors.age} />
            <CheckboxField
              name="terms"
              error={errors.terms}
              label={
                <>
                  J'ai lu et j'accepte les{' '}
                  <a href="#/conditions" target="_blank" rel="noopener noreferrer">
                    conditions d'utilisation
                  </a>{' '}
                  et la{' '}
                  <a href="#/confidentialite" target="_blank" rel="noopener noreferrer">
                    politique de confidentialité
                  </a>
                  .
                </>
              }
            />
            <CheckboxField name="emails" label="Je souhaite recevoir des rappels d'échéance par e-mail (facultatif, désabonnement possible à tout moment)." />

            <Button type="submit">Créer mon compte</Button>
          </form>
          <p style={{ marginTop: 'var(--space-4)', marginBottom: 0 }}>
            Déjà inscrit ? <a href="#/connexion">Se connecter</a>
          </p>
        </Card>
        <div style={{ marginTop: 'var(--space-4)' }}>
          {valid ? (
            <Alert tone="success" title="Formulaire valide">
              Maquette : aucun compte n'est créé tant que l'API n'est pas reliée (ticket DODP-56).
            </Alert>
          ) : (
            <Alert tone="info">Maquette : l'inscription sera reliée à l'API avec le ticket DODP-56.</Alert>
          )}
        </div>
      </main>
    </>
  )
}

export default Register
