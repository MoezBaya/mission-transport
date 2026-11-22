import StaffDirectory from "./StaffDirectory";
import { vehiculeApi } from "../api/vehicules";

const etatLabels = {
  DISPONIBLE: "Disponible",
  EN_MISSION: "En mission",
  EN_PANNE: "Hors service",
  EN_ENTRETIEN: "En maintenance",
};

const fields = [
  { name: "matricule", label: "Matricule", required: true },
  { name: "marque", label: "Marque", required: true },
  { name: "modele", label: "Modèle", required: true },
  { name: "annee", label: "Année", type: "number" },
  { name: "type", label: "Type" },
  { name: "kilometrage", label: "Kilométrage", type: "number" },
  {
    name: "etat",
    label: "État",
    initialValue: "DISPONIBLE",
    options: [
      { value: "DISPONIBLE", label: "Disponible" },
      { value: "EN_MISSION", label: "En mission" },
      { value: "EN_PANNE", label: "Hors service" },
      { value: "EN_ENTRETIEN", label: "En maintenance" },
    ],
  },
];

const vehicleName = (row) => [row.matricule, row.marque, row.modele].filter(Boolean).join(" ");
const chauffeurName = (row) => row.chauffeur?.nomComplet
  || row.chauffeurNom
  || [row.chauffeur?.prenom, row.chauffeur?.nom].filter(Boolean).join(" ")
  || "—";

export default function Vehicules() {
  return (
    <StaffDirectory
      title="Gestion des véhicules"
      entityLabel="Véhicule"
      current="Liste des véhicules"
      api={vehiculeApi}
      fields={fields}
      rowName={vehicleName}
      addLabel="+ Ajouter un véhicule"
      searchFields={["matricule", "marque", "modele", chauffeurName]}
      columns={[
        { name: "matricule", label: "Matricules" },
        { name: "chauffeur", label: "Chauffeurs" },
        { name: "etat", label: "État" },
      ]}
      formatters={{
        matricule: (value, row) => value || `${row.marque || ""} ${row.modele || ""}`.trim() || "—",
        chauffeur: (_, row) => chauffeurName(row),
        etat: (value) => etatLabels[value] || value || "—",
      }}
      summaryCards={(rows) => [
        { label: "Véhicules au total", value: rows.length, tone: "total" },
        { label: "Véhicules disponibles", value: rows.filter((row) => row.etat === "DISPONIBLE").length, tone: "available" },
        { label: "Véhicules en mission", value: rows.filter((row) => row.etat === "EN_MISSION").length, tone: "mission" },
        { label: "Véhicules en maintenance", value: rows.filter((row) => row.etat === "EN_ENTRETIEN").length, tone: "maintenance" },
        { label: "Véhicules hors service", value: rows.filter((row) => row.etat === "EN_PANNE").length, tone: "panne" },
      ]}
      filters={{
        label: "Filtrer par état",
        options: [
          { value: "all", label: "Tous les véhicules" },
          { value: "DISPONIBLE", label: "Disponibles" },
          { value: "EN_MISSION", label: "En mission" },
          { value: "EN_ENTRETIEN", label: "En maintenance" },
          { value: "EN_PANNE", label: "Hors service" },
        ],
        matches: (row, value) => row.etat === value,
      }}
    />
  );
}
