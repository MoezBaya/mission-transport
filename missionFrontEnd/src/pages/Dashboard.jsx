import { Link, useNavigate } from "react-router-dom";
import { useAuth } from "../context/AuthContext";

export default function Dashboard() {
  const { user, doLogout } = useAuth();
  const navigate = useNavigate();

  async function handleLogout() {
    await doLogout();
    navigate("/login");
  }

  return (
    <div style={{ padding: "2rem" }}>
      <h1>Bonjour {user?.prenom} {user?.nom} 👋</h1>
      <p>Email : {user?.email}</p>
      <p>Rôle : {user?.role}</p>

      <div style={{ marginTop: "1rem", display: "flex", gap: "1rem" }}>
        <Link to="/change-password">Modifier mot de passe</Link>
        <button onClick={handleLogout}>Déconnexion</button>
      </div>
    </div>
  );
}