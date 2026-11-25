import { LegalPage } from './LegalPage'

function Confidentialite() {
  return (
    <LegalPage title="Politique de confidentialité" subtitle="Quelles données nous utilisons, pourquoi, et vos droits">
      <h2>Qui est responsable ?</h2>
      <p>
        DOP Platform est un projet académique réalisé par une équipe d'étudiants. À compléter : nom de l'établissement et
        adresse de contact du responsable du traitement.
      </p>

      <h2>Les données que nous collectons</h2>
      <p>Nous ne demandons que le strict nécessaire :</p>
      <ul>
        <li>votre adresse e-mail, pour identifier votre compte et vous écrire ;</li>
        <li>votre mot de passe, stocké uniquement sous forme hachée (BCrypt), jamais en clair ;</li>
        <li>les informations de déclaration que vous saisissez vous-même ;</li>
        <li>un journal des consultations d'une déclaration (qui, quoi, quand), exigé par la confidentialité légale.</li>
      </ul>
      <p>
        Nous ne demandons ni date de naissance, ni numéro de téléphone, ni localisation, et nous n'enregistrons aucune
        donnée sur votre appareil au-delà de ce que décrit la <a href="#/cookies">politique de cookies</a>.
      </p>

      <h2>Pourquoi ?</h2>
      <ul>
        <li>créer et sécuriser votre compte ;</li>
        <li>vérifier si vous êtes soumis à l'obligation de déclarer ;</li>
        <li>recevoir et suivre votre déclaration ;</li>
        <li>vous envoyer des rappels d'échéance par e-mail, uniquement si vous l'avez demandé.</li>
      </ul>

      <h2>Sur quelle base ?</h2>
      <p>
        Le traitement de votre déclaration relève de l'obligation de déclarer. Les e-mails de rappel et la mémorisation de
        vos préférences d'affichage reposent sur votre consentement, que vous pouvez retirer à tout moment. À faire
        valider par l'équipe.
      </p>

      <h2>Combien de temps ?</h2>
      <p>À compléter : durée de conservation du compte, des déclarations et du journal des consultations.</p>

      <h2>Qui voit vos données ?</h2>
      <p>
        Seules les personnes habilitées (vous, et les magistrats compétents pour votre déclaration). Vos données ne sont
        ni vendues, ni transmises à des tiers à des fins publicitaires. Les polices du site sont hébergées avec le site :
        aucune requête n'est envoyée à un service externe pour les charger.
      </p>

      <h2>Vos droits</h2>
      <p>Vous pouvez à tout moment :</p>
      <ul>
        <li>accéder à vos données et demander leur rectification ;</li>
        <li>
          demander leur suppression : <a href="#/suppression-donnees">formulaire de suppression des données</a> ;
        </li>
        <li>
          vous opposer aux e-mails : <a href="#/desabonnement">se désabonner</a> ;
        </li>
        <li>modifier votre choix sur les cookies, depuis le pied de page (« Gérer les cookies »).</li>
      </ul>
      <p>
        Vous pouvez aussi saisir la Commission nationale de contrôle de la protection des données à caractère personnel
        (CNDP). Cadre applicable à confirmer : loi 09-08.
      </p>

      <h2>Mineurs</h2>
      <p>
        Le service est réservé aux personnes de 18 ans ou plus. Nous ne cherchons pas à collecter de données concernant
        des mineurs : si nous en identifions, le compte et les données associées sont supprimés.
      </p>
    </LegalPage>
  )
}

export default Confidentialite
