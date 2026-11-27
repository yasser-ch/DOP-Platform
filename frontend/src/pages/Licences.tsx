import { Table } from '../design-system'
import { LegalPage } from './LegalPage'

function Licences() {
  return (
    <LegalPage title="Licences et crédits" subtitle="Les polices, images et bibliothèques utilisées" draft={false}>
      <h2>Polices</h2>
      <p>La police est hébergée avec le site (aucun service externe) et distribuée sous licence libre.</p>
      <Table caption="Police utilisée">
        <thead>
          <tr>
            <th scope="col">Police</th>
            <th scope="col">Usage</th>
            <th scope="col">Auteurs</th>
            <th scope="col">Licence</th>
          </tr>
        </thead>
        <tbody>
          <tr>
            <td>IBM Plex Sans Arabic</td>
            <td>Titres et texte</td>
            <td>IBM Corp.</td>
            <td>
              <a href="/licenses/OFL-IBM-Plex-Sans-Arabic.txt" target="_blank" rel="noopener noreferrer">
                SIL Open Font License 1.1
              </a>
            </td>
          </tr>
        </tbody>
      </Table>

      <h2>Images</h2>
      <ul>
        <li>
          <strong>Sceau DOP, motifs en étoile à huit branches (zellige) et icônes</strong> : dessinés pour le projet avec une
          aide par IA, sous la licence du projet. Le sceau ne reprend aucun emblème officiel.
        </li>
        <li>
          <strong>Images d'illustration</strong> : générées par IA (outil Lovable). Elles ne représentent aucun lieu officiel,
          aucune personne ni aucune institution réelle. À compléter avant publication : vérification des conditions
          d'utilisation de l'outil.
        </li>
      </ul>

      <h2>Bibliothèques</h2>
      <p>
        L'interface est construite avec React (licence MIT), embarqué dans le site. Les outils de développement (Vite,
        TypeScript) ne sont pas distribués avec le site.
      </p>
    </LegalPage>
  )
}

export default Licences
