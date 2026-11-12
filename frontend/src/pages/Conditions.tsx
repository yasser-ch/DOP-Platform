import { LegalPage } from './LegalPage'

function Conditions() {
  return (
    <LegalPage title="Conditions d'utilisation" subtitle="Les règles pour utiliser la plateforme">
      <h2>Objet</h2>
      <p>
        DOP Platform permet de vérifier son obligation de déclarer son patrimoine et de suivre ses échéances. Il s'agit d'un
        projet académique : il ne représente aucune institution officielle et ne produit aucun effet juridique.
      </p>

      <h2>Qui peut s'inscrire ?</h2>
      <p>
        Le service est réservé aux personnes de 18 ans ou plus. Lors de l'inscription, vous confirmez avoir atteint cet
        âge. Un compte est personnel et ne doit pas être partagé.
      </p>

      <h2>Vos engagements</h2>
      <ul>
        <li>fournir des informations exactes ;</li>
        <li>garder votre mot de passe confidentiel et nous prévenir en cas de perte ou de vol ;</li>
        <li>ne pas tenter de contourner la sécurité de la plateforme ni d'accéder aux données d'autrui.</li>
      </ul>

      <h2>Disponibilité</h2>
      <p>La plateforme est fournie en l'état, sans garantie de disponibilité continue.</p>

      <h2>Propriété intellectuelle</h2>
      <p>
        Le contenu et le code du projet appartiennent à leurs auteurs. Les polices et images utilisées sont détaillées dans
        la page <a href="#/licences">Licences et crédits</a>.
      </p>

      <h2>Données personnelles</h2>
      <p>
        Le traitement de vos données est décrit dans la <a href="#/confidentialite">politique de confidentialité</a>.
      </p>

      <h2>Modification et droit applicable</h2>
      <p>À compléter : conditions de modification de ces règles, droit applicable et juridiction compétente.</p>
    </LegalPage>
  )
}

export default Conditions
