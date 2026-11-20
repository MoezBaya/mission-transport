import { useState } from "react";
import { useNavigate } from "react-router-dom";
import Layout from "../components/Layout";
import PageBreadcrumb from "../components/PageBreadcrumb";
import { changerMotDePasse } from "../api/auth";
import "./ChangerMdp.css";

export default function ChangerMdp() {
  const [ancien, setAncien] = useState("");
  const [nouveau, setNouveau] = useState("");
  const [confirmation, setConfirmation] = useState("");
  const [message, setMessage] = useState("");
  const [error, setError] = useState("");
  const [saving, setSaving] = useState(false);
  const navigate = useNavigate();

  async function handleSubmit(event) {
    event.preventDefault();
    setMessage("");
    setError("");
    setSaving(true);

    try {
      await changerMotDePasse(ancien, nouveau, confirmation);
      setMessage("Mot de passe modifié avec succès.");
      window.setTimeout(() => navigate("/dashboard"), 1500);
    } catch (requestError) {
      const backendMessage = requestError.response?.data;
      setError(typeof backendMessage === "string" ? backendMessage : backendMessage?.message || requestError.message);
    } finally {
      setSaving(false);
    }
  }

  return (
    <Layout>
      <PageBreadcrumb current="Modifier le mot de passe" />
      <section className="password-card">
        <h1 className="page-title">Modifier le mot de passe</h1>
        <p className="page-subtitle">Choisissez un nouveau mot de passe pour protéger votre compte.</p>
        <form className="password-form" onSubmit={handleSubmit}>
          <label className="field-label" htmlFor="password-old">Ancien mot de passe</label>
          <input className="form-control" id="password-old" type="password" autoComplete="current-password" value={ancien} onChange={(event) => setAncien(event.target.value)} required />
          <label className="field-label" htmlFor="password-new">Nouveau mot de passe</label>
          <input className="form-control" id="password-new" type="password" autoComplete="new-password" value={nouveau} onChange={(event) => setNouveau(event.target.value)} required />
          <label className="field-label" htmlFor="password-confirm">Confirmer le nouveau mot de passe</label>
          <input className="form-control" id="password-confirm" type="password" autoComplete="new-password" value={confirmation} onChange={(event) => setConfirmation(event.target.value)} required />
          {message && <p className="form-message form-message--success" role="status">{message}</p>}
          {error && <p className="form-message form-message--error" role="alert">{error}</p>}
          <button className="button" type="submit" disabled={saving}>{saving ? "Enregistrement..." : "Enregistrer le nouveau mot de passe"}</button>
        </form>
      </section>
    </Layout>
  );
}
