import { Alert, Banner, Breadcrumb } from '../design-system'

// Texte de maquette : chaque section est à compléter et à faire valider par l'équipe.
function Mentions() {
  return (
    <>
      <Breadcrumb items={[{ label: 'Accueil', href: '#/' }, { label: 'Mentions légales' }]} />
      <Banner title="Mentions légales" subtitle="Informations sur la plateforme et vos données" />
      <main className="ds-container" style={{ maxWidth: '48rem' }}>
        <Alert tone="info" title="Texte de maquette">
          Ces mentions sont un exemple de structure. Elles doivent être complétées et validées avant toute mise en ligne.
        </Alert>

        <h2>Éditeur</h2>
        <p>
          DOP Platform est un projet académique de déclaration obligatoire du patrimoine, réalisé par une équipe d'étudiants.
          Il ne représente aucune institution officielle.
        </p>

        <h2>Hébergement</h2>
        <p>À compléter : nom et adresse de l'hébergeur de la plateforme.</p>

        <h2>Données personnelles</h2>
        <p>
          La plateforme traite des données personnelles (adresse e-mail, informations de déclaration) uniquement pour
          permettre le dépôt et le suivi des déclarations. À compléter : finalités, durée de conservation, droits d'accès et
          de rectification, et référence au cadre légal applicable (loi 09-08 sur la protection des données personnelles).
        </p>

        <h2>Accessibilité</h2>
        <p>
          L'interface vise la conformité WCAG 2.1 niveau AA : navigation au clavier, contrastes vérifiés et libellés reliés
          aux champs de formulaire.
        </p>
      </main>
    </>
  )
}

export default Mentions
