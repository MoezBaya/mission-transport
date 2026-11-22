import { useEffect, useMemo, useState } from "react";
import { ArrowDown, Eye, Pencil, Plus, Search, Trash2, Users, X } from "lucide-react";
import Layout from "../components/Layout";
import PageBreadcrumb from "../components/PageBreadcrumb";
import "./StaffDirectory.css";

const roleLabels = {
  DIRECTEUR: "Directeur",
  DIRECTEUR_LOGISTIQUE: "Directeur logistique",
  CHEF_DE_PARC: "Chef de parc",
};

function fullName(person) {
  return [person.prenom, person.nom].filter(Boolean).join(" ");
}

function emptyForm(fields) {
  return Object.fromEntries(fields.map(({ name, initialValue = "" }) => [name, initialValue]));
}

export default function StaffDirectory({
  title,
  entityLabel,
  current,
  api,
  fields,
  columns,
  filters,
  searchFields,
  rowName = fullName,
  addLabel,
  formatters = {},
  summaryCards,
}) {
  const [rows, setRows] = useState([]);
  const [loading, setLoading] = useState(true);
  const [pageError, setPageError] = useState("");
  const [search, setSearch] = useState("");
  const [filter, setFilter] = useState("all");
  const [dialog, setDialog] = useState(null);
  const [form, setForm] = useState({});
  const [formError, setFormError] = useState("");
  const [saving, setSaving] = useState(false);
  const identityName = (row) => rowName(row) || "cet élément";

  useEffect(() => {
    let cancelled = false;

    api.getAll()
      .then((data) => {
        if (!cancelled) setRows(Array.isArray(data) ? data : []);
      })
      .catch((error) => {
        if (!cancelled) {
          setPageError(error?.response?.data?.message || `Impossible de charger ${entityLabel.toLowerCase()}.`);
        }
      })
      .finally(() => {
        if (!cancelled) setLoading(false);
      });

    return () => {
      cancelled = true;
    };
  }, [api, entityLabel]);

  const filteredRows = useMemo(() => {
    const query = search.trim().toLocaleLowerCase();
    return rows.filter((row) => {
      const searchable = (searchFields
        ? searchFields.map((field) => typeof field === "function" ? field(row) : row[field])
        : [rowName(row), row.email, row.telephone])
        .filter(Boolean)
        .join(" ")
        .toLocaleLowerCase();
      const matchesSearch = searchable.includes(query);
      const matchesFilter = filter === "all" || filters.matches(row, filter);
      return matchesSearch && matchesFilter;
    });
  }, [filter, filters, rowName, rows, search, searchFields]);

  function openDialog(mode, row = null) {
    setForm(mode === "create" ? emptyForm(fields) : { ...row, motDePasse: "" });
    setFormError("");
    setDialog({ mode, row });
  }

  function closeDialog() {
    if (!saving) setDialog(null);
  }

  function updateField(name, value) {
    setForm((currentForm) => ({ ...currentForm, [name]: value }));
  }

  async function handleSubmit(event) {
    event.preventDefault();
    setFormError("");
    setSaving(true);
    try {
      const saved = dialog.mode === "create"
        ? await api.create(form)
        : await api.update(dialog.row.id, form);

      setRows((currentRows) => dialog.mode === "create"
        ? [...currentRows, saved]
        : currentRows.map((row) => row.id === saved.id ? saved : row));
      setDialog(null);
    } catch (error) {
      setFormError(error?.response?.data?.message || `Impossible d'enregistrer ${entityLabel.toLowerCase()}.`);
    } finally {
      setSaving(false);
    }
  }

  async function handleDelete(row) {
    if (!window.confirm(`Voulez-vous supprimer ${identityName(row)} ?`)) return;
    setPageError("");
    try {
      await api.remove(row.id);
      setRows((currentRows) => currentRows.filter((item) => item.id !== row.id));
    } catch (error) {
      setPageError(error?.response?.data?.message || `Impossible de supprimer ${entityLabel.toLowerCase()}.`);
    }
  }

  function formatCell(row, column) {
    if (formatters[column.name]) return formatters[column.name](row[column.name], row);
    if (column.name === "nomComplet") return fullName(row) || "—";
    if (column.name === "role") return roleLabels[row.role] || row.role || "—";
    if (column.name === "disponible") return row.disponible ? "Disponible" : "Indisponible";
    return row[column.name] || "—";
  }

  return (
    <Layout>
      <PageBreadcrumb current={current} />

      <div className="staff-page-header">
        <div>
          <h1 className="page-title">{title}</h1>
          <p className="page-subtitle">Gérez les {entityLabel.toLowerCase()} de votre organisation.</p>
        </div>
        <button className="button staff-add-button" type="button" onClick={() => openDialog("create")}>
          <Plus size={19} strokeWidth={2.5} />
          {addLabel || `Ajouter ${entityLabel === "Personnel" ? "un personnel" : "un chauffeur"}`}
        </button>
      </div>

      {summaryCards && (
        <section className="staff-summary-grid" aria-label="Résumé">
          {summaryCards(rows).map((card) => (
            <article className={`staff-summary-card staff-summary-card--${card.tone}`} key={card.label}>
              <span>{card.label}</span>
              <strong>{loading ? "—" : card.value}</strong>
            </article>
          ))}
        </section>
      )}

      <section className="staff-table-card" aria-label={title}>
        <div className="staff-toolbar">
          <label className="staff-search">
            <Search size={21} aria-hidden="true" />
            <input
              type="search"
              placeholder="Rechercher un nom, une adresse e-mail..."
              value={search}
              onChange={(event) => setSearch(event.target.value)}
              aria-label={`Rechercher ${entityLabel.toLowerCase()}`}
            />
          </label>
          <select
            className="form-control staff-filter"
            value={filter}
            onChange={(event) => setFilter(event.target.value)}
            aria-label={filters.label}
          >
            {filters.options.map(({ value, label }) => <option key={value} value={value}>{label}</option>)}
          </select>
        </div>

        {pageError && <p className="staff-page-error" role="alert">{pageError}</p>}

        <div className="staff-table-scroll">
          <table className="staff-table">
            <thead>
              <tr>
                {columns.map((column, index) => (
                  <th key={column.name}>
                    {index === 0 ? <span className="staff-sort-heading">{column.label}<ArrowDown size={18} /></span> : column.label}
                  </th>
                ))}
                <th className="staff-actions-heading">Actions</th>
              </tr>
            </thead>
            <tbody>
              {loading ? (
                <tr><td className="staff-empty-cell" colSpan={columns.length + 1}>Chargement...</td></tr>
              ) : filteredRows.length === 0 ? (
                <tr>
                  <td className="staff-empty-cell" colSpan={columns.length + 1}>
                    <Users size={27} aria-hidden="true" />
                    <span>{pageError ? "Les données ne sont pas disponibles." : `Aucun ${entityLabel.toLowerCase()} trouvé.`}</span>
                  </td>
                </tr>
              ) : filteredRows.map((row) => (
                <tr key={row.id}>
                  {columns.map((column) => <td key={column.name}>{formatCell(row, column)}</td>)}
                  <td className="staff-actions-cell">
                    <button className="staff-icon-button" type="button" title="Voir" aria-label={`Voir ${identityName(row)}`} onClick={() => openDialog("view", row)}>
                      <Eye size={21} />
                    </button>
                    <button className="staff-icon-button" type="button" title="Modifier" aria-label={`Modifier ${identityName(row)}`} onClick={() => openDialog("edit", row)}>
                      <Pencil size={20} />
                    </button>
                    <button className="staff-icon-button staff-icon-button--danger" type="button" title="Supprimer" aria-label={`Supprimer ${identityName(row)}`} onClick={() => handleDelete(row)}>
                      <Trash2 size={20} />
                    </button>
                  </td>
                </tr>
              ))}
            </tbody>
          </table>
        </div>
        <div className="staff-table-footer">
          Affichage de {loading ? "—" : filteredRows.length} {entityLabel.toLowerCase()}(s)
        </div>
      </section>

      {dialog && (
        <div className="staff-modal-overlay" onMouseDown={(event) => event.target === event.currentTarget && closeDialog()}>
          <section className="staff-modal" role="dialog" aria-modal="true" aria-labelledby="staff-dialog-title">
            <div className="staff-modal-header">
              <div>
                <h2 id="staff-dialog-title">
                  {dialog.mode === "create" ? `Ajouter ${entityLabel === "Personnel" ? "un personnel" : "un chauffeur"}` :
                    dialog.mode === "edit" ? `Modifier ${entityLabel.toLowerCase()}` : `Détails ${entityLabel.toLowerCase()}`}
                </h2>
                <p>{dialog.mode === "view" ? "Informations enregistrées dans le système." : "Renseignez les informations ci-dessous."}</p>
              </div>
              <button className="staff-icon-button" type="button" aria-label="Fermer" onClick={closeDialog} disabled={saving}><X size={21} /></button>
            </div>

            {dialog.mode === "view" ? (
              <dl className="staff-details">
                {fields.filter((field) => field.name !== "motDePasse" && field.name !== "nom").map((field) => (
                  <div key={field.name}>
                    <dt>{field.name === "prenom" ? "Nom complet" : field.label}</dt>
                    <dd>{field.name === "prenom" ? fullName(dialog.row) :
                        field.name === "role" ? roleLabels[dialog.row.role] || dialog.row.role :
                          field.name === "disponible" ? (dialog.row.disponible ? "Disponible" : "Indisponible") :
                            dialog.row[field.name] || "—"}</dd>
                  </div>
                ))}
              </dl>
            ) : (
              <form className="staff-form" onSubmit={handleSubmit}>
                {fields.map((field) => {
                  if (field.name === "role" && dialog.mode === "edit") {
                    return (
                      <label className="staff-field" key={field.name}>
                        <span>{field.label}</span>
                        <input className="form-control" value={roleLabels[dialog.row.role] || dialog.row.role} readOnly />
                      </label>
                    );
                  }
                  const value = form[field.name] ?? "";
                  return (
                    <label className="staff-field" key={field.name}>
                      <span>{field.label}</span>
                      {field.options ? (
                        <select className="form-control" value={value} onChange={(event) => updateField(field.name, field.parse ? field.parse(event.target.value) : event.target.value)} required={field.required}>
                          {field.options.map(({ value: optionValue, label }) => <option key={String(optionValue)} value={optionValue}>{label}</option>)}
                        </select>
                      ) : field.type === "checkbox" ? (
                        <span className="staff-checkbox">
                          <input type="checkbox" checked={Boolean(value)} onChange={(event) => updateField(field.name, event.target.checked)} />
                          {field.helpText}
                        </span>
                      ) : (
                        <input
                          className="form-control"
                          type={field.type || "text"}
                          value={value}
                          onChange={(event) => updateField(field.name, event.target.value)}
                          placeholder={field.placeholder}
                          required={field.required && (dialog.mode === "create" || field.name !== "motDePasse")}
                          autoComplete={field.name === "motDePasse" ? "new-password" : undefined}
                        />
                      )}
                    </label>
                  );
                })}
                {formError && <p className="form-message form-message--error" role="alert">{formError}</p>}
                <div className="staff-modal-actions">
                  <button className="button button--secondary" type="button" onClick={closeDialog} disabled={saving}>Annuler</button>
                  <button className="button" type="submit" disabled={saving}>{saving ? "Enregistrement..." : "Enregistrer"}</button>
                </div>
              </form>
            )}
            {dialog.mode === "view" && (
              <div className="staff-modal-actions">
                <button className="button button--secondary" type="button" onClick={closeDialog}>Fermer</button>
                <button className="button" type="button" onClick={() => openDialog("edit", dialog.row)}>Modifier</button>
              </div>
            )}
          </section>
        </div>
      )}
    </Layout>
  );
}
