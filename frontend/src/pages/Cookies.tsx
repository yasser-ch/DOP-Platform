import { Button, Table } from '../design-system'
import { useConsent } from '../consent'
import { LegalPage } from './LegalPage'

function Cookies() {
  const consent = useConsent()
  const status =
    consent.choice === 'accepted'
      ? 'Vous avez accepté la mémorisation de vos préférences d\'affichage.'
      : consent.choice === 'refused'
        ? 'Vous avez refusé la mémorisation de vos préférences d\'affichage.'
        : 'Vous n\'avez pas encore fait de choix.'

  return (
    <LegalPage title="Politique de cookies" subtitle="Ce que le site garde sur votre appareil">
      <p>
        Ce site n'utilise <strong>aucun cookie publicitaire, aucune mesure d'audience et aucun service tiers</strong>. Il
        garde seulement quelques informations dans votre navigateur, listées ci-dessous.
      </p>

      <Table caption="Informations conservées sur votre appareil">
        <thead>
          <tr>
            <th scope="col">Nom</th>
            <th scope="col">À quoi ça sert</th>
            <th scope="col">Catégorie</th>
            <th scope="col">Durée</th>
            <th scope="col">Votre accord</th>
          </tr>
        </thead>
        <tbody>
          <tr>
            <td>dop.consent</td>
            <td>Retenir votre choix sur cette page</td>
            <td>Essentiel</td>
            <td>Jusqu'à ce que vous l'effaciez</td>
            <td>Non requis</td>
          </tr>
          <tr>
            <td>Jeton de connexion</td>
            <td>Vous garder connecté pendant votre session</td>
            <td>Essentiel</td>
            <td>En mémoire, 15 minutes, effacé à la fermeture de l'onglet</td>
            <td>Non requis</td>
          </tr>
          <tr>
            <td>dop.accessibility</td>
            <td>Retenir la taille du texte et le contraste choisis</td>
            <td>Préférence</td>
            <td>Jusqu'à ce que vous l'effaciez</td>
            <td>Requis</td>
          </tr>
        </tbody>
      </Table>

      <h2>Votre choix</h2>
      <p>{status}</p>
      <Button variant="secondary" onClick={consent.reopen}>
        Modifier mon choix
      </Button>
    </LegalPage>
  )
}

export default Cookies
