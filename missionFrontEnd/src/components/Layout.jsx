import { useState } from "react";
import { NavLink, useNavigate } from "react-router-dom";
import { LayoutDashboard, ClipboardList, Fuel, Users, BusFront, ChevronDown, LockKeyhole, LogOut } from "lucide-react";
import { useAuth } from "../context/authContext";
import logo1 from "../assets/img/logo1.png";
import "./Layout.css";

const menuItems = [
  { to: "/dashboard", label: "Tableau de bord", icon: LayoutDashboard },
  { to: "/ordres-mission", label: "Ordres de mission", icon: ClipboardList },
  { to: "/ravitailllement", label: "Demandes de ravitaillement", icon: Fuel },
  { to: "/chauffeurs", label: "Liste des chauffeurs", icon: Users },
  { to: "/vehicules", label: "Liste des véhicules", icon: BusFront },
  { to: "/personnels", label: "Liste des personnels", icon: Users },
];

function UserMenu({ user, onLogout }) {
  const [isOpen, setIsOpen] = useState(false);
  const navigate = useNavigate();
  const initials = `${user?.prenom?.[0] || ""}${user?.nom?.[0] || ""}`.toUpperCase() || "U";

  return (
    <div className="user-menu">
      <div className="user-avatar" aria-hidden="true">{initials}</div>
      <div className="user-details">
        <p className="user-name">{user?.prenom} {user?.nom}</p>
        <p className="user-role">{user?.role}</p>
      </div>
      <button className="user-menu-toggle" type="button" aria-label="Ouvrir le menu utilisateur" aria-expanded={isOpen} onClick={() => setIsOpen((open) => !open)}><ChevronDown size={20} /></button>
      {isOpen && (
        <div className="user-dropdown">
          <button className="user-dropdown-item" type="button" onClick={() => { setIsOpen(false); navigate("/change-password"); }}><LockKeyhole size={17} />Modifier le mot de passe</button>
          <button className="user-dropdown-item user-dropdown-item--danger" type="button" onClick={onLogout}><LogOut size={17} />Déconnexion</button>
        </div>
      )}
    </div>
  );
}

function SidebarNavigation() {
  return (
    <aside className="app-sidebar">
      <p className="sidebar-heading">Espace directeur</p>
      <nav className="sidebar-nav" aria-label="Navigation principale">
        {menuItems.map(({ to, label, icon: Icon }) => (
          <NavLink className={({ isActive }) => `sidebar-link${isActive ? " sidebar-link--active" : ""}`} key={to} to={to}>
            <Icon size={21} strokeWidth={1.8} /><span>{label}</span>
          </NavLink>
        ))}
      </nav>
    </aside>
  );
}

export default function Layout({ children }) {
  const { user, doLogout } = useAuth();
  const navigate = useNavigate();

  async function handleLogout() {
    await doLogout();
    navigate("/login");
  }

  return (
    <div className="app-layout">
      <header className="app-header">
        <a className="app-brand" href="/dashboard" aria-label="Mission, accueil"><img src={logo1} alt="" /></a>
        <UserMenu user={user} onLogout={handleLogout} />
      </header>
      <div className="app-body">
        <SidebarNavigation />
        <main className="app-main"><div className="app-content">{children}</div></main>
      </div>
    </div>
  );
}
