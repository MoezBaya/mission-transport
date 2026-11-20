import { Users, BusFront, ClipboardList, Fuel } from "lucide-react";
import Layout from "../components/Layout";
import PageBreadcrumb from "../components/PageBreadcrumb";
import { useAuth } from "../context/authContext";
import "./Dashboard.css";

const stats = [
  { label: "Personnels", value: "7", icon: Users, tone: "blue" },
  { label: "Véhicules", value: "12", icon: BusFront, tone: "green" },
  { label: "Ordres de mission", value: "24", icon: ClipboardList, tone: "orange" },
  { label: "Demandes de ravitaillement", value: "5", icon: Fuel, tone: "pink" },
];

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

  return (
    <Layout>
      <PageBreadcrumb current="Tableau de bord" />
      <section className="dashboard-heading">
        <div><h1 className="page-title">Bonjour {user?.prenom} {user?.nom} <span aria-hidden="true">👋</span></h1><p className="page-subtitle">Bienvenue dans votre espace de gestion.</p></div>
      </section>
      <section className="stats-grid" aria-label="Résumé de l'activité">
        {stats.map((stat) => <StatCard key={stat.label} {...stat} />)}
      </section>
    </Layout>
  );
}
