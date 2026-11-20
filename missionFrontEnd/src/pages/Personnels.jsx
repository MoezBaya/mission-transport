import { useState, useMemo } from "react";
import { useNavigate } from "react-router-dom";
import { personnelApi } from "../api/personnels";

export default function Personnels() {
  const navigate = useNavigate();
  const [search, setSearch] = useState("");
  const [role, setRole] = useState("Tous les rôles");
  const [personnels, setPersonnels] = useState([
    // données de test — à remplacer par un useEffect + personnelApi.getAll()
    { id: 1, nomComplet: "Salah Tounsi", email: "salahtounsi@gmail.com", role: "Chef de parc" },
    { id: 2, nomComplet: "Salah Tounsi", email: "salahtounsi@gmail.com", role: "Chef de parc" },
    { id: 3, nomComplet: "Salah Tounsi", email: "salahtounsi@gmail.com", role: "Chef de parc" },
  ]);
  const [showModal, setShowModal] = useState(false);
  const [form, setForm] = useState({ nomComplet: "", email: "", role: "Chauffeur" });
  const [error, setError] = useState("");

  const filtered = useMemo(() => {
    return personnels.filter((p) => {
      const matchSearch =
        p.nomComplet.toLowerCase().includes(search.toLowerCase()) ||
        p.email.toLowerCase().includes(search.toLowerCase());
      const matchRole = role === "Tous les rôles" || p.role === role;
      return matchSearch && matchRole;
    });
  }, [personnels, search, role]);

  function handleAdd(e) {
    e.preventDefault();
    setError("");
    // quand l'API sera prête :
    // await personnelApi.create(form)
    setPersonnels((prev) => [
      ...prev,
      { ...form, id: Date.now() },
    ]);
    setForm({ nomComplet: "", email: "", role: "Chauffeur" });
    setShowModal(false);
  }

  function handleDelete(id) {
    if (window.confirm("Supprimer ce personnel ?")) {
      // await personnelApi.remove(id)
      setPersonnels((prev) => prev.filter((p) => p.id !== id));
    }
  }

  return (
    <div style={styles.page}>
      {/* En-tête */}
      <div style={styles.header}>
        <div>
          <h1 style={styles.title}>Gestion des personnels</h1>
          <p style={styles.subtitle}>Gérez les personnel</p>
        </div>
        <button style={styles.addBtn} onClick={() => setShowModal(true)}>
          + Ajouter un personnel
        </button>
      </div>

      {/* Barre de recherche + filtre */}
      <div style={styles.toolbar}>
        <div style={styles.searchBox}>
          <span style={styles.searchIcon}>🔍</span>
          <input
            style={styles.searchInput}
            placeholder="Rechercher un nom ou une adresse email..."
            value={search}
            onChange={(e) => setSearch(e.target.value)}
          />
        </div>
        <select style={styles.select} value={role} onChange={(e) => setRole(e.target.value)}>
          <option>Tous les rôles</option>
          <option>Chef de parc</option>
          <option>Chauffeur</option>
          <option>Directeur</option>
        </select>
      </div>

      {/* Tableau */}
      <div style={styles.tableCard}>
        <table style={styles.table}>
          <thead>
            <tr style={styles.theadRow}>
              <th style={styles.th}>Nom complet ↓</th>
              <th style={styles.th}>E-mail</th>
              <th style={styles.th}>Rôle</th>
              <th style={{ ...styles.th, textAlign: "right" }}>Actions</th>
            </tr>
          </thead>
          <tbody>
            {filtered.map((p) => (
              <tr key={p.id} style={styles.tr}>
                <td style={styles.td}>{p.nomComplet}</td>
                <td style={styles.td}>{p.email}</td>
                <td style={styles.td}>{p.role}</td>
                <td style={{ ...styles.td, textAlign: "right" }}>
                  <button
                    style={styles.iconBtn}
                    title="Voir"
                    onClick={() => navigate(`/personnels/${p.id}`)}
                  >
                    👁️
                  </button>
                  <button
                    style={styles.iconBtn}
                    title="Modifier"
                    onClick={() => navigate(`/personnels/${p.id}/edit`)}
                  >
                    ✏️
                  </button>
                  <button
                    style={styles.iconBtn}
                    title="Supprimer"
                    onClick={() => handleDelete(p.id)}
                  >
                    🗑️
                  </button>
                </td>
              </tr>
            ))}
            {filtered.length === 0 && (
              <tr>
                <td colSpan={4} style={{ ...styles.td, textAlign: "center", color: "#888" }}>
                  Aucun personnel trouvé
                </td>
              </tr>
            )}
          </tbody>
        </table>
      </div>

      {/* Modal ajout */}
      {showModal && (
        <div style={styles.overlay} onClick={() => setShowModal(false)}>
          <div style={styles.modal} onClick={(e) => e.stopPropagation()}>
            <h2 style={{ marginTop: 0 }}>Ajouter un personnel</h2>
            <form onSubmit={handleAdd} style={{ display: "flex", flexDirection: "column", gap: "0.9rem" }}>
              <input
                style={styles.input}
                placeholder="Nom complet"
                value={form.nomComplet}
                onChange={(e) => setForm({ ...form, nomComplet: e.target.value })}
                required
              />
              <input
                style={styles.input}
                type="email"
                placeholder="E-mail"
                value={form.email}
                onChange={(e) => setForm({ ...form, email: e.target.value })}
                required
              />
              <select
                style={styles.input}
                value={form.role}
                onChange={(e) => setForm({ ...form, role: e.target.value })}
              >
                <option>Chauffeur</option>
                <option>Chef de parc</option>
                <option>Directeur</option>
              </select>
              {error && <p style={{ color: "red", margin: 0 }}>{error}</p>}
              <div style={{ display: "flex", gap: "0.5rem", justifyContent: "flex-end" }}>
                <button type="button" style={styles.cancelBtn} onClick={() => setShowModal(false)}>
                  Annuler
                </button>
                <button type="submit" style={styles.saveBtn}>Enregistrer</button>
              </div>
            </form>
          </div>
        </div>
      )}
    </div>
  );
}

const styles = {
  page: { padding: "2rem", background: "#f0f2f5", minHeight: "100vh" },
  header: { display: "flex", justifyContent: "space-between", alignItems: "flex-start", marginBottom: "1.5rem" },
  title: { fontSize: "1.8rem", margin: 0 },
  subtitle: { color: "#666", margin: "0.25rem 0 0" },
  addBtn: {
    background: "#8b9cf9", color: "white", border: "none", borderRadius: "10px",
    padding: "0.85rem 1.4rem", fontSize: "1rem", cursor: "pointer", fontWeight: 600,
  },
  toolbar: { display: "flex", gap: "1rem", marginBottom: "1rem" },
  searchBox: {
    flex: 1, display: "flex", alignItems: "center", background: "white",
    borderRadius: "10px", border: "1px solid #ccc", padding: "0 0.9rem",
  },
  searchIcon: { marginRight: "0.5rem" },
  searchInput: { flex: 1, border: "none", outline: "none", padding: "0.85rem 0", fontSize: "1rem", background: "transparent" },
  select: {
    padding: "0.85rem 1rem", borderRadius: "10px", border: "1px solid #ccc",
    background: "white", fontSize: "1rem", cursor: "pointer", minWidth: "180px",
  },
  tableCard: { background: "white", borderRadius: "12px", overflow: "hidden", boxShadow: "0 1px 4px rgba(0,0,0,0.08)" },
  table: { width: "100%", borderCollapse: "collapse" },
  theadRow: { borderBottom: "2px solid #eee" },
  th: { textAlign: "left", padding: "1rem", fontSize: "1rem" },
  tr: { borderBottom: "1px solid #eee" },
  td: { padding: "1rem", fontSize: "0.95rem" },
  iconBtn: { background: "none", border: "none", fontSize: "1.1rem", cursor: "pointer", marginLeft: "0.4rem" },
  overlay: {
    position: "fixed", inset: 0, background: "rgba(0,0,0,0.4)",
    display: "flex", alignItems: "center", justifyContent: "center", zIndex: 1000,
  },
  modal: {
    background: "white", borderRadius: "12px", padding: "1.8rem",
    width: "400px", boxShadow: "0 10px 30px rgba(0,0,0,0.2)",
  },
  input: { padding: "0.75rem", borderRadius: "8px", border: "1px solid #ccc", fontSize: "1rem" },
  cancelBtn: {
    padding: "0.7rem 1.2rem", borderRadius: "8px", border: "1px solid #ccc",
    background: "white", cursor: "pointer",
  },
  saveBtn: {
    padding: "0.7rem 1.2rem", borderRadius: "8px", border: "none",
    background: "#8b9cf9", color: "white", cursor: "pointer", fontWeight: 600,
  },
};