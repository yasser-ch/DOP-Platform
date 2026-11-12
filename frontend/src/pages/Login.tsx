import { Alert, Banner, Breadcrumb, Button, Card, Field, Input } from '../design-system'

// Maquette : le formulaire n'est pas encore relié à l'API (ticket DODP-56).
function Login() {
  return (
    <>
      <Breadcrumb items={[{ label: 'Accueil', href: '#/' }, { label: 'Connexion' }]} />
      <Banner title="Connexion" subtitle="Accédez à votre espace" />
      <main className="ds-container" style={{ maxWidth: '34rem' }}>
        <Card>
          <form onSubmit={(event) => event.preventDefault()} noValidate>
            <Field label="Adresse e-mail">
              <Input type="email" autoComplete="email" />
            </Field>
            <Field label="Mot de passe">
              <Input type="password" autoComplete="current-password" />
            </Field>
            <Button type="submit">Se connecter</Button>
          </form>
          <p style={{ marginTop: 'var(--space-4)', marginBottom: 0 }}>
            Pas encore de compte ? <a href="#/inscription">Créer un compte</a>
          </p>
        </Card>
        <div style={{ marginTop: 'var(--space-4)' }}>
          <Alert tone="info">Maquette : la connexion sera reliée à l'API avec le ticket DODP-56.</Alert>
        </div>
      </main>
    </>
  )
}

export default Login
