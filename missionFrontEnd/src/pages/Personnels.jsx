
import { useState, useEffect, useMemo } from "react";
import { useNavigate } from "react-router-dom";
import Layout from "../components/Layout";
import { personnelApi } from "../api/personnels";
import PageBreadcrumb from "../components/PageBreadcrumb";
import "./Personnels.css";

import {
  Search,
  Eye,
  Pencil,
  Trash2,
  ArrowDown,
  X,
  Users,
} from "lucide-react";

export default function Personnels() {
  const navigate = useNavigate();

  const [search, setSearch] = useState("");
  const [role, setRole] = useState("Tous les rôles");
  const [personnels, setPersonnels] = useState([]);
  const [loading, setLoading] = useState(true);
  const [showModal, setShowModal] = useState(false);

  const [form, setForm] = useState({
    nomComplet: "",
    email: "",
    role: "Chauffeur",
    motDePasse: "",
  });

  const [error, setError] = useState("");
  const [saving, setSaving] = useState(false);

  // ===== Chargement des personnels =====
  useEffect(() => {
    let cancelled = false;

    async function load() {
      try {
        const data = await personnelApi.getAll();

        if (!cancelled) {
          setPersonnels(Array.isArray(data) ? data : []);
        }
      } catch (err) {
        if (!cancelled) {
          setPersonnels([
            {
              id: 1,
              nomComplet: "Salah Tounsi",
              email: "salahtounsi@gmail.com",
              role: "Chef de parc",
            },
            {
              id: 2,
              nomComplet: "Salah Tounsi",
              email: "salahtounsi@gmail.com",
              role: "Chef de parc",
            },
            {
              id: 3,
              nomComplet: "Salah Tounsi",
              email: "salahtounsi@gmail.com",
              role: "Chef de parc",
            },
          ]);
        }
      } finally {
        if (!cancelled) setLoading(false);
      }
    }

    load();

    return () => {
      cancelled = true;
    };
  }, []);

  // ===== Recherche + Filtrage =====
  const filtered = useMemo(() => {
    const q = search.toLowerCase().trim();

    return personnels.filter((p) => {
      const matchSearch =
        (p.nomComplet || "").toLowerCase().includes(q) ||
        (p.email || "").toLowerCase().includes(q);

      const matchRole =
        role === "Tous les rôles" || p.role === role;

      return matchSearch && matchRole;
    });
  }, [personnels, search, role]);

  // ===== Ajouter un personnel =====
  async function handleAdd(e) {
    e.preventDefault();

    setError("");
    setSaving(true);

    try {
      const created = await personnelApi.create(form);

      setPersonnels((prev) => [...prev, created]);

      setForm({
        nomComplet: "",
        email: "",
        role: "Chauffeur",
        motDePasse: "",
      });

      setShowModal(false);
    } catch (err) {
      setError(
        err?.response?.data?.message ||
        "Impossible d'ajouter ce personnel."
      );
    } finally {
      setSaving(false);
    }
  }

  // ===== Supprimer =====
  async function handleDelete(id) {
    if (!window.confirm("Voulez-vous supprimer ce personnel ?")) {
      return;
    }

    try {
      await personnelApi.remove(id);
      setPersonnels((prev) =>
        prev.filter((p) => p.id !== id)
      );
    } catch (err) {
      alert("Erreur lors de la suppression du personnel.");
    }
  }

  return (
    <Layout>

      {/* ===== BREADCRUMB ===== */}
      <PageBreadcrumb current="Liste des personnels" />

      {/* ===== PAGE HEADER ===== */}
      <div className="personnel-page-header">
        <div>
          <h1 className="page-title">
            Gestion des personnels
          </h1>

          <p className="page-subtitle">
            Gérez les personnels
          </p>
        </div>

        <button
          className="button"
          onClick={() => {
            setError("");
            setShowModal(true);
          }}
        >
          + Ajouter un personnel
        </button>
      </div>

      {/* ===== TABLE CONTAINER ===== */}
      <section className="personnel-table-card">

        {/* ===== SEARCH + FILTER ===== */}
        <div className="personnel-toolbar">

          <label className="personnel-search">
            <Search
              size={25}
              color="#333"
              strokeWidth={1.8}
            />

            <input
              className="personnel-search-input"
              type="text"
              placeholder="Rechercher un nom ou une adresse email..."
              value={search}
              onChange={(e) => setSearch(e.target.value)}
            />
          </label>

          <select
            className="form-control personnel-role-filter"
            value={role}
            onChange={(e) => setRole(e.target.value)}
          >
            <option>Tous les rôles</option>
            <option>Chef de parc</option>
            <option>Chauffeur</option>
            <option>Directeur</option>
          </select>

        </div>

        {/* ===== TABLE ===== */}
        <div className="personnel-table-scroll">
          <table className="personnel-table">

            <thead>
              <tr>

                <th>
                  <div className="personnel-sort-header">
                    Nom complet
                    <ArrowDown size={21} strokeWidth={2} />
                  </div>
                </th>

                <th>
                  E-mail
                </th>

                <th>
                  Rôle
                </th>

                <th className="personnel-actions-heading">
                  Actions
                </th>

              </tr>
            </thead>

            <tbody>

              {loading ? (
                <tr>
                  <td colSpan={4} className="personnel-empty-cell">
                    Chargement des personnels...
                  </td>
                </tr>
              ) : filtered.length === 0 ? (
                <tr>
                  <td colSpan={4} className="personnel-empty-cell">
                    <Users size={30} color="#9ca3af" />
                    <span>Aucun personnel trouvé</span>
                  </td>
                </tr>
              ) : (
                filtered.map((p) => (
                  <tr key={p.id}>

                    <td>
                      {p.nomComplet}
                    </td>

                    <td>
                      {p.email}
                    </td>

                    <td>
                      {p.role}
                    </td>

                    <td className="personnel-actions-cell">

                      {/* VOIR */}
                      <button
                        className="icon-button"
                        title="Voir"
                        onClick={() =>
                          navigate(`/personnels/${p.id}`)
                        }
                      >
                        <Eye size={28} strokeWidth={1.8} />
                      </button>

                      {/* MODIFIER */}
                      <button
                        className="icon-button"
                        title="Modifier"
                        onClick={() =>
                          navigate(`/personnels/${p.id}/edit`)
                        }
                      >
                        <Pencil size={27} strokeWidth={1.8} />
                      </button>

                      {/* SUPPRIMER */}
                      <button
                        className="icon-button icon-button--danger"
                        title="Supprimer"
                        onClick={() => handleDelete(p.id)}
                      >
                        <Trash2 size={27} strokeWidth={1.8} />
                      </button>

                    </td>

                  </tr>
                ))
              )}

            </tbody>
          </table>
        </div>

        {/* ===== FOOTER TABLE ===== */}
        {!loading && (
          <div className="personnel-table-footer">
            Affichage de {filtered.length} personnel(s)
          </div>
        )}

      </section>

      {/* ===== MODAL AJOUT ===== */}
      {showModal && (
        <div
          className="modal-overlay"
          onClick={() => !saving && setShowModal(false)}
        >
          <div
            className="personnel-modal"
            onClick={(e) => e.stopPropagation()}
          >

            <div className="modal-header">
              <h2 className="modal-title">
                Ajouter un personnel
              </h2>

              <button
                className="icon-button"
                onClick={() => setShowModal(false)}
                disabled={saving}
              >
                <X size={23} />
              </button>
            </div>

            <p className="modal-subtitle">
              Remplissez les informations du nouveau personnel.
            </p>

            <form
              onSubmit={handleAdd}
              className="personnel-form"
            >

              <label className="field-label" htmlFor="personnel-name">
                Nom complet
              </label>

              <input
                className="form-control"
                id="personnel-name"
                placeholder="Ex : Salah Tounsi"
                value={form.nomComplet}
                onChange={(e) =>
                  setForm({
                    ...form,
                    nomComplet: e.target.value,
                  })
                }
                required
              />

              <label className="field-label" htmlFor="personnel-email">
                Adresse e-mail
              </label>

              <input
                className="form-control"
                id="personnel-email"
                type="email"
                placeholder="Ex : exemple@gmail.com"
                value={form.email}
                onChange={(e) =>
                  setForm({
                    ...form,
                    email: e.target.value,
                  })
                }
                required
              />

              <label className="field-label" htmlFor="personnel-password">
                Mot de passe
              </label>

              <input
                className="form-control"
                id="personnel-password"
                type="password"
                placeholder="Mot de passe"
                value={form.motDePasse}
                onChange={(e) =>
                  setForm({
                    ...form,
                    motDePasse: e.target.value,
                  })
                }
              />

              <label className="field-label" htmlFor="personnel-role">
                Rôle
              </label>

              <select
                className="form-control"
                id="personnel-role"
                value={form.role}
                onChange={(e) =>
                  setForm({
                    ...form,
                    role: e.target.value,
                  })
                }
              >
                <option>Chauffeur</option>
                <option>Chef de parc</option>
                <option>Directeur</option>
              </select>

              {error && (
                <p className="form-message form-message--error">
                  {error}
                </p>
              )}

              <div className="modal-actions">

                <button
                  type="button"
                  className="button button--secondary"
                  onClick={() => setShowModal(false)}
                  disabled={saving}
                >
                  Annuler
                </button>

                <button
                  type="submit"
                  className="button"
                  disabled={saving}
                >
                  {saving ? "Enregistrement..." : "Enregistrer"}
                </button>

              </div>

            </form>
          </div>
        </div>
      )}

    </Layout>
  );
}

