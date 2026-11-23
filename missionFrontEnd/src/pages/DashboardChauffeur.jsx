import { useEffect, useState } from "react";
import { BusFront, ClipboardList, MapPin, Fuel, Sun, ChevronRight, ArrowDown } from "lucide-react";
import Layout from "../components/Layout";
import PageBreadcrumb from "../components/PageBreadcrumb";
import { vehiculeApi } from "../api/vehicules";
import "./Dashboard.css";

const mesMissions = [
  { id: "OM. 2026.150", route: "Tunis - Kairouan", vehicule: "123 TUN 4521", date: "23/11/2026", statut: "En cours" },
  { id: "OM. 2026.148", route: "Tunis - Nabeul", vehicule: "123 TUN 4521", date: "21/11/2026", statut: "Terminée" },
  { id: "OM. 2026.145", route: "Sousse - Sfax", vehicule: "189 TUN 7812", date: "18/11/2026", statut: "Terminée" },
];

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

export default function DashboardChauffeur() {
  const [vehiculeAffecte, setVehiculeAffecte] = useState(null);

  useEffect(() => {
    vehiculeApi.getAll().then((data) => {
      const list = Array.isArray(data) ? data : [];
      if (list.length > 0) setVehiculeAffecte(list[0]);
    }).catch(() => {});
  }, []);

  const today = new Intl.DateTimeFormat("fr-FR", { day: "numeric", month: "long", year: "numeric" }).format(new Date());

  const stats = [
    { label: "Missions effectuées", value: mesMissions.filter((m) => m.statut === "Terminée").length, icon: ClipboardList, tone: "green" },
    { label: "Mission en cours", value: mesMissions.filter((m) => m.statut === "En cours").length, icon: MapPin, tone: "blue" },
    { label: "Véhicule affecté", value: vehiculeAffecte ? vehiculeAffecte.matricule : "—", icon: BusFront, tone: "orange" },
    { label: "Carburant restant", value: "—", icon: Fuel, tone: "pink" },
  ];

  return (
    <Layout>
      <PageBreadcrumb current="Tableau de bord" />
      <section className="dashboard-heading">
        <div><h1 className="page-title">Bonjour, Chauffeur</h1><p className="page-subtitle">Voici le résumé de vos missions et activités</p></div>
        <div className="dashboard-date">{today}</div>
      </section>
      <section className="stats-grid" aria-label="Résumé de l'activité">
        {stats.map((stat) => <StatCard key={stat.label} {...stat} />)}
      </section>
      <section className="dashboard-content-grid">
        <article className="dashboard-panel missions-panel">
          <div className="dashboard-panel-heading">
            <h2>Mes missions</h2>
            <button type="button">Historique <ChevronRight size={18} /></button>
          </div>
          <div className="missions-table-wrap">
            <table className="missions-table">
              <thead><tr><th><span>Mission/trajet <ArrowDown size={17} /></span></th><th>Véhicule</th><th>Date</th><th>Statut</th></tr></thead>
              <tbody>{mesMissions.map((mission) => (
                <tr key={mission.id}>
                  <td><strong>{mission.id}</strong><span>{mission.route}</span></td>
                  <td>{mission.vehicule}</td>
                  <td>{mission.date}</td>
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
          {vehiculeAffecte && (
            <article className="dashboard-panel vehicles-status-panel">
              <h2>Mon véhicule</h2>
              <p className="vehicle-status-total"><strong>{vehiculeAffecte.matricule}</strong></p>
              <div className="vehicle-status-row">
                <span>Marque</span>
                <strong style={{ marginLeft: "auto" }}>{vehiculeAffecte.marque || "—"}</strong>
              </div>
              <div className="vehicle-status-row">
                <span>Modèle</span>
                <strong style={{ marginLeft: "auto" }}>{vehiculeAffecte.modele || "—"}</strong>
              </div>
              <div className="vehicle-status-row">
                <span>Kilométrage</span>
                <strong style={{ marginLeft: "auto" }}>{vehiculeAffecte.kilometrage ? `${vehiculeAffecte.kilometrage} km` : "—"}</strong>
              </div>
              <div className="vehicle-status-row">
                <span className={`vehicle-status-dot vehicle-status-dot--${(vehiculeAffecte.etat || "disponible").toLowerCase()}`} />
                <span>{vehiculeAffecte.etat === "DISPONIBLE" ? "Disponible" : vehiculeAffecte.etat === "EN_MISSION" ? "En mission" : vehiculeAffecte.etat || "—"}</span>
              </div>
            </article>
          )}
        </div>
      </section>
    </Layout>
  );
}

