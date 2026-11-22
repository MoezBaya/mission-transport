import StaffDirectory from "./StaffDirectory";
import { chauffeurApi } from "../api/chauffeurs";

const fields = [
  { name: "prenom", label: "Prénom", required: true },
  { name: "nom", label: "Nom", required: true },
  { name: "cin", label: "CIN", required: true },
  { name: "telephone", label: "Téléphone" },
  { name: "email", label: "Adresse e-mail", type: "email", required: true },
  { name: "motDePasse", label: "Mot de passe", type: "password", required: true },
  { name: "numeroPermis", label: "Numéro de permis", required: true },
  { name: "categoriePermis", label: "Catégorie du permis" },
  { name: "dateValiditePermis", label: "Validité du permis", type: "date" },
  {
    name: "disponible",
    label: "Disponibilité",
    initialValue: true,
    options: [
      { value: true, label: "Disponible" },
      { value: false, label: "Indisponible" },
    ],
    parse: (value) => value === "true",
  },
];

export default function Chauffeurs() {
  return (
    <StaffDirectory
      title="Gestion des chauffeurs"
      entityLabel="Chauffeur"
      current="Liste des chauffeurs"
      api={chauffeurApi}
      fields={fields}
      columns={[
        { name: "nomComplet", label: "Nom complet" },
        { name: "email", label: "E-mail" },
        { name: "telephone", label: "Téléphone" },
      ]}
      filters={{
        label: "Filtrer par disponibilité",
        options: [
          { value: "all", label: "Tous les chauffeurs" },
          { value: "true", label: "Disponibles" },
          { value: "false", label: "Indisponibles" },
        ],
        matches: (row, value) => String(Boolean(row.disponible)) === value,
      }}
    />
  );
}
