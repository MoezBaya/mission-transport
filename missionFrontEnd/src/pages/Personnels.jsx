import StaffDirectory from "./StaffDirectory";
import { personnelApi } from "../api/personnels";

const roles = [
  { value: "CHEF_DE_PARC", label: "Chef de parc" },
  { value: "DIRECTEUR_LOGISTIQUE", label: "Directeur logistique" },
  { value: "DIRECTEUR", label: "Directeur" },
];

const fields = [
  { name: "prenom", label: "Prénom", required: true },
  { name: "nom", label: "Nom", required: true },
  { name: "cin", label: "CIN", required: true },
  { name: "telephone", label: "Téléphone" },
  { name: "email", label: "Adresse e-mail", type: "email", required: true },
  { name: "motDePasse", label: "Mot de passe", type: "password", required: true },
  { name: "role", label: "Rôle", initialValue: "CHEF_DE_PARC", options: roles },
];

export default function Personnels() {
  return (
    <StaffDirectory
      title="Gestion des personnels"
      entityLabel="Personnel"
      current="Liste des personnels"
      api={personnelApi}
      fields={fields}
      columns={[
        { name: "nomComplet", label: "Nom complet" },
        { name: "email", label: "E-mail" },
        { name: "role", label: "Rôle" },
      ]}
      filters={{
        label: "Filtrer par rôle",
        options: [{ value: "all", label: "Tous les rôles" }, ...roles],
        matches: (row, value) => row.role === value,
      }}
    />
  );
}
