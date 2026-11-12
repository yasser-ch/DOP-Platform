import { Banner, Breadcrumb, Card, Details } from '../design-system'

// Texte de maquette : à compléter et à faire valider par l'équipe.
function Aide() {
  return (
    <>
      <Breadcrumb items={[{ label: 'Accueil', href: '#/' }, { label: 'Aide et contact' }]} />
      <Banner title="Aide et contact" subtitle="Les réponses aux questions les plus fréquentes" />
      <main className="ds-container" style={{ maxWidth: '48rem' }}>
        <h2 style={{ marginTop: 0 }}>Questions fréquentes</h2>
        <Details summary="Qui doit déposer une déclaration de patrimoine ?">
          <p>
            Les personnes figurant sur la liste des assujettis. Vous pouvez le vérifier dans votre espace déclarant, une
            fois connecté.
          </p>
        </Details>
        <Details summary="Comment savoir si je figure sur la liste ?">
          <p>
            Connectez-vous, puis ouvrez « Espace déclarant » : la plateforme indique si vous êtes concerné et quelle est
            votre échéance.
          </p>
        </Details>
        <Details summary="Que faire si mon échéance est dépassée ?">
          <p>Déposez votre déclaration dès que possible et contactez le support en précisant votre adresse e-mail de connexion.</p>
        </Details>
        <Details summary="J'ai oublié mon mot de passe">
          <p>La réinitialisation du mot de passe sera disponible dans une prochaine version de la plateforme.</p>
        </Details>
        <Details summary="Mes données sont-elles protégées ?">
          <p>Vos données ne sont accessibles qu'aux personnes habilitées, et chaque consultation est journalisée.</p>
        </Details>

        <h2>Nous contacter</h2>
        <Card>
          <p>
            Pour une question que vous ne trouvez pas ici, écrivez-nous à <a href="mailto:contact@dop.example">contact@dop.example</a>.
          </p>
          <p style={{ marginBottom: 0 }}>Adresse fictive : cette plateforme est un projet académique.</p>
        </Card>
      </main>
    </>
  )
}

export default Aide
