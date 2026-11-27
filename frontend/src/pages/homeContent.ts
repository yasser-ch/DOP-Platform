// Contenu FICTIF de l'accueil : il ne concerne que la plateforme (aucune personne ni institution réelle).
import type { Slide } from '../design-system'

const ALT = {
  patio: "Patio d'architecture marocaine bordé d'arcades sculptées, avec une fontaine au centre",
  dossiers: 'Dossiers et documents sur un bureau en bois, avec un stylo plume',
  zellige: 'Détail de zellige vert et blanc à côté d\'un panneau de bois sculpté',
  salle: 'Salle de réunion vide avec une longue table et de grandes fenêtres',
  couloir: 'Longue galerie à colonnes blanches et arcs',
  porte: 'Porte en bois sculpté encadrée de carreaux verts',
  donnees: "Ordinateur portable à côté d'un cadenas et d'un dossier fermé",
  archives: "Salle d'archives vide avec des étagères en bois",
}

export const SLIDES: Slide[] = [
  {
    date: '18 novembre 2026',
    title: 'Ouverture de la campagne de déclaration 2026',
    summary: 'La plateforme accepte les déclarations initiales, complémentaires et de renouvellement.',
    href: '#/espace-declarant',
    image: 'patio',
    alt: ALT.patio,
  },
  {
    date: '10 novembre 2026',
    title: "Rappel d'échéance : préparez votre dépôt",
    summary: 'Consultez votre échéance et finalisez votre déclaration avant la date limite.',
    href: '#/espace-declarant',
    image: 'dossiers',
    alt: ALT.dossiers,
  },
  {
    date: '2 novembre 2026',
    title: 'Le guide du déclarant mis à jour',
    summary: 'Les réponses aux questions les plus fréquentes sur le formulaire et les justificatifs.',
    href: '#/aide',
    image: 'zellige',
    alt: ALT.zellige,
  },
  {
    date: '20 octobre 2026',
    title: 'Protection des données : nos engagements',
    summary: 'Ce que la plateforme collecte, pourquoi, et comment demander la suppression.',
    href: '#/confidentialite',
    image: 'salle',
    alt: ALT.salle,
  },
]

export const NEWS = [
  { date: '2026-11-18', title: 'Ouverture de la campagne de déclaration 2026', href: '#/espace-declarant', image: 'patio' },
  { date: '2026-11-10', title: "Rappel d'échéance pour les renouvellements", href: '#/espace-declarant', image: 'dossiers' },
  { date: '2026-11-02', title: 'Guide du déclarant : nouvelle édition', href: '#/aide', image: 'zellige' },
  { date: '2026-10-20', title: 'Protection des données personnelles', href: '#/confidentialite', image: 'donnees' },
]

export const FEATURE = { title: 'Guide du déclarant : toutes les étapes expliquées', href: '#/aide', image: 'couloir', alt: ALT.couloir }

export const GALLERY = [
  { image: 'salle', alt: ALT.salle },
  { image: 'porte', alt: ALT.porte },
  { image: 'archives', alt: ALT.archives },
  { image: 'donnees', alt: ALT.donnees },
]
