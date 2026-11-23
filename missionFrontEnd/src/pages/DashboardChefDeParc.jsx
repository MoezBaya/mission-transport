import { useEffect, useState } from "react";
import { Users, BusFront, ClipboardList, ArrowDown, ChevronRight, Sun, Fuel } from "lucide-react";
import Layout from "../components/Layout";
import PageBreadcrumb from "../components/PageBreadcrumb";
import { chauffeurApi } from "../api/chauffeurs";
import { vehiculeApi } from "../api/vehicules";
import "./Dashboard.css";

const missions = [
  { id: "OM. 2026.150", route: "Tunis - Kairouan", chauffeur: "Ramzi felah", vehicule: "123 TUN 4521", statut: "En cours" },
  { id: "OM. 2026.149", route: "Sousse - Tunis", chauffeur: "Fedi bay", vehicule: "189 TUN 7812", statut: "En cours" },
  { id: "OM. 2026.148", route: "Tunis - Nabeul", chauffeur: "Ramzi felah", vehicule: "123 TUN 4521", statut: "Terminée" },
  { id: "OM. 2026.147", route: "Kasserine - Tunis", chauffeur: "Mariem lahmer", vehicule: "200 TUN 3344", statut: "En attente" },
  { id: "OM. 2026.146", route: "Sousse - Tunis", chauffeur: "Ramzi felah", vehicule: "123 TUN 4521", statut: "En cours" },
];

const vehicleLabels = {
  DISPONIBLE: "Disponibles",
  EN_MISSION: "En mission",
  EN_ENTRETIEN: "En maintenance",
  EN_PANNE: "Hors service",
};

const statusColors = {
  "En cours": "#73a7f7",
  "Terminée": "#82ef87",
  "En attente": "#f3b663",
};

function StatCard({ label, value, icon: Icon, tone }) {
  return (
    <article className="stat-card">
      <div className={`stat-icon stat-icon--${tone}`}><Icon size={24} strokeWidth={1.8} /></div>
      <div><p className="stat-value">{value}</p><p className="stat-label">{label}</p></div>
    </article>
  );
}

export default function DashboardChefDeParc() {
  const [counts, setCounts] = useState({ chauffeurs: 0, vehicules: 0, vehiculesDispo: 0, vehiculesEnMission: 0 });
  const [vehicules, setVehicules] = useState([]);

  useEffect(() => {
    Promise.all([
      chauffeurApi.getAll().catch(() => []),
      vehiculeApi.getAll().catch(() => []),
    ]).then(([chauffeurs, vehiculesData]) => {
      const vList = Array.isArray(vehiculesData) ? vehiculesData : [];
      setCounts({
        chauffeurs: chauffeurs.length,
        vehicules: vList.length,
        vehiculesDispo: vList.filter((v) => v.etat === "DISPONIBLE").length,
        vehiculesEnMission: vList.filter((v) => v.etat === "EN_MISSION").length,
      });
      setVehicules(vList);
    });
  }, []);

  const today = new Intl.DateTimeFormat("fr-FR", { day: "numeric", month: "long", year: "numeric" }).format(new Date());

  const stats = [
    { label: "Chauffeurs", value: counts.chauffeurs, icon: Users, tone: "blue" },
    { label: "Véhicules", value: counts.vehicules, icon: BusFront, tone: "green" },
    { label: "Véhicules disponibles", value: counts.vehiculesDispo, icon: BusFront, tone: "orange" },
    { label: "Demandes en attente", value: "5", icon: ClipboardList, tone: "pink" },
  ];

  return (
    <Layout>
      <PageBreadcrumb current="Tableau de bord" />
      <section className="dashboard-heading">
        <div><h1 className="page-title">Bonjour, Chef de parc</h1><p className="page-subtitle">Gérez votre parc automobile et vos missions</p></div>
        <div className="dashboard-date">{today}</div>
      </section>
      <section className="stats-grid" aria-label="Résumé de l'activité">
        {stats.map((stat) => <StatCard key={stat.label} {...stat} />)}
      </section>
      <section className="dashboard-content-grid">
        <article className="dashboard-panel missions-panel">
          <div className="dashboard-panel-heading">
            <h2>Missions en cours</h2>
            <button type="button">Tout voir <ChevronRight size={18} /></button>
          </div>
          <div className="missions-table-wrap">
            <table className="missions-table">
              <thead><tr><th><span>Mission/trajet <ArrowDown size={17} /></span></th><th>Chauffeur</th><th>Véhicule</th><th>Statut</th></tr></thead>
              <tbody>{missions.map((mission) => (
                <tr key={mission.id}>
                  <td><strong>{mission.id}</strong><span>{mission.route}</span></td>
                  <td>{mission.chauffeur}</td>
                  <td>{mission.vehicule}</td>
                  <td><span className="mission-status" style={{ background: statusColors[mission.statut] || "#73a7f7" }}>{mission.statut}</span></td>
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
            <h2>État des véhicules</h2>
            <p className="vehicle-status-total"><strong>{counts.vehicules}</strong> Véhicules dans le parc</p>
            {["DISPONIBLE", "EN_MISSION", "EN_ENTRETIEN", "EN_PANNE"].map((etat) => (
              <div className="vehicle-status-row" key={etat}>
                <span className={`vehicle-status-dot vehicle-status-dot--${etat.toLowerCase()}`} />
                <span>{vehicleLabels[etat]}</span>
                <strong style={{ marginLeft: "auto" }}>{vehicules.filter((v) => v.etat === etat).length}</strong>
              </div>
            ))}
          </article>
        </div>
      </section>
    </Layout>
  );
}

