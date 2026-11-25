import { useState } from 'react'
import { Alert, Banner, Breadcrumb, Button, Card } from '../design-system'

// Maquette : l'appel à l'API n'existe pas encore (voir README : endpoint de désabonnement à prévoir côté notification-service).
function Desabonnement() {
  const [subscribed, setSubscribed] = useState(true)

  return (
    <>
      <Breadcrumb items={[{ label: 'Accueil', href: '#/' }, { label: 'Se désabonner des e-mails' }]} />
      <Banner title="Se désabonner des e-mails" subtitle="Gérez les rappels que vous recevez" />
      <main className="ds-container" style={{ maxWidth: '40rem' }}>
        <Card title="Rappels d'échéance par e-mail">
          <p>
            Vous recevez des rappels avant l'échéance de votre déclaration. En vous désabonnant, vous ne recevrez plus aucun
            e-mail de la plateforme. Vos notifications restent visibles dans l'application.
          </p>
          {subscribed ? (
            <Button variant="danger" onClick={() => setSubscribed(false)}>
              Me désabonner
            </Button>
          ) : (
            <>
              <Alert tone="success" title="Vous êtes désabonné">
                Vous ne recevrez plus d'e-mails de rappel.
              </Alert>
              <p style={{ marginTop: 'var(--space-4)' }}>Un clic par erreur ?</p>
              <Button variant="secondary" onClick={() => setSubscribed(true)}>
                Me réabonner
              </Button>
            </>
          )}
        </Card>
        <div style={{ marginTop: 'var(--space-4)' }}>
          <Alert tone="info">
            Maquette : le désabonnement n'est pas encore relié à l'API. Chaque e-mail envoyé devra contenir ce lien.
          </Alert>
        </div>
      </main>
    </>
  )
}

export default Desabonnement
