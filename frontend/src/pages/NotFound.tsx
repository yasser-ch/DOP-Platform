import { Banner, ButtonLink } from '../design-system'

function NotFound() {
  return (
    <>
      <Banner title="Page introuvable" subtitle="L'adresse demandée n'existe pas ou a été déplacée." />
      <main className="ds-container">
        <p>Vérifiez l'adresse saisie, ou revenez à l'accueil pour retrouver votre chemin.</p>
        <ButtonLink href="#/">Retour à l'accueil</ButtonLink>
      </main>
    </>
  )
}

export default NotFound
