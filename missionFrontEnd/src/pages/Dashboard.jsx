import { useEffect, useState } from "react";
import { Navigate } from "react-router-dom";
import { Users, BusFront, ClipboardList, ArrowDown, ChevronRight, Sun } from "lucide-react";
import Layout from "../components/Layout";
import PageBreadcrumb from "../components/PageBreadcrumb";
import { useAuth } from "../context/authContext";
import { chauffeurApi } from "../api/chauffeurs";
import { personnelApi } from "../api/personnels";
import { vehiculeApi } from "../api/vehicules";
import "./Dashboard.css";

const missions = [
  { id: "OM. 2026.150", route: "Tunis - Kairouan", chauffeur: "Ramzi felah", statut: "En cours" },
  { id: "OM. 2026.149", route: "Sousse - Tunis", chauffeur: "Fedi bay", statut: "En cours" },
  { id: "OM. 2026.148", route: "Tunis - Nabeul", chauffeur: "Ramzi felah", statut: "En cours" },
  { id: "OM. 2026.147", route: "Kasserine - Tunis", chauffeur: "Mariem lahmer", statut: "En cours" },
  { id: "OM. 2026.146", route: "Sousse - Tunis", chauffeur: "Ramzi felah", statut: "En cours" },
];

const vehicleLabels = {
  DISPONIBLE: "Disponibles",
  EN_MISSION: "En mission",
  EN_ENTRETIEN: "En maintenance",
  EN_PANNE: "Hors service",
};

function StatCard({ label, value, icon: Icon, tone }) {
  return (
    <article className="stat-card">
      <div className={`stat-icon stat-icon--${tone}`}><Icon size={24} strokeWidth={1.8} /></div>
      <div><p className="stat-value">{value}</p><p className="stat-label">{label}</p></div>
    </article>
  );
}

export default function Dashboard() {
  const { user } = useAuth();

  if (user?.role === "CHEF_DE_PARC") {
    return <Navigate to="/dashboard-chef-de-parc" replace />;
  }
  if (user?.role === "CHAUFFEUR") {
    return <Navigate to="/dashboard-chauffeur" replace />;
  }

  const [counts, setCounts] = useState({ personnels: 0, chauffeurs: 0, vehicules: 0 });

  useEffect(() => {
    Promise.all([
      personnelApi.getAll().catch(() => []),
      chauffeurApi.getAll().catch(() => []),
      vehiculeApi.getAll().catch(() => []),
    ]).then(([personnels, chauffeurs, vehicules]) => {
      setCounts({ personnels: personnels.length, chauffeurs: chauffeurs.length, vehicules: vehicules.length });
    });
  }, []);

  const stats = [
    { label: "Personnels", value: counts.personnels, icon: Users, tone: "blue" },
    { label: "Chauffeurs", value: counts.chauffeurs, icon: Users, tone: "blue" },
    { label: "Véhicules", value: counts.vehicules, icon: BusFront, tone: "green" },
    { label: "Demandes d'ordre de mission en attente", value: "17", icon: ClipboardList, tone: "pink" },
  ];

  const today = new Intl.DateTimeFormat("fr-FR", { day: "numeric", month: "long", year: "numeric" }).format(new Date());

  return (
    <Layout>
      <PageBreadcrumb current="Tableau de bord" />
      <section className="dashboard-heading">
        <div><h1 className="page-title">Bonjour, Directeur</h1><p className="page-subtitle">Voici l'essentiel de votre activité</p></div>
        <div className="dashboard-date">{today}</div>
      </section>
      <section className="stats-grid" aria-label="Résumé de l'activité">
        {stats.map((stat) => <StatCard key={stat.label} {...stat} />)}
      </section>
      <section className="dashboard-content-grid">
        <article className="dashboard-panel missions-panel">
          <div className="dashboard-panel-heading">
            <h2>Missions du jour</h2>
            <button type="button">Tout voir <ChevronRight size={18} /></button>
          </div>
          <div className="missions-table-wrap">
            <table className="missions-table">
              <thead><tr><th><span>Mission/trajet <ArrowDown size={17} /></span></th><th>Chauffeur</th><th>Statut</th></tr></thead>
              <tbody>{missions.map((mission) => (
                <tr key={mission.id}>
                  <td><strong>{mission.id}</strong><span>{mission.route}</span></td>
                  <td>{mission.chauffeur}</td>
                  <td><span className="mission-status">{mission.statut}</span></td>
                </tr>
              ))}</tbody>
            </table>
          </div>
        </article>
        <div className="dashboard-side">
          <article className="dashboard-panel weather-panel">
            <h2>Météo</h2>
            <div className="weather-value"><strong>28°C</strong><Sun size={58} strokeWidth={1.7} /><span>Sunny</span></div>
          </article>
          <article className="dashboard-panel vehicles-status-panel">
            <h2>Etat des véhicules</h2>
            <p className="vehicle-status-total"><strong>{counts.vehicules}</strong> Véhicules dans votre flotte</p>
            {["DISPONIBLE", "EN_MISSION", "EN_ENTRETIEN"].map((etat) => (
              <div className="vehicle-status-row" key={etat}>
                <span className={`vehicle-status-dot vehicle-status-dot--${etat.toLowerCase()}`} />
                <span>{vehicleLabels[etat]}</span>
              </div>
            ))}
          </article>
        </div>
      </section>
    </Layout>
  );
}
