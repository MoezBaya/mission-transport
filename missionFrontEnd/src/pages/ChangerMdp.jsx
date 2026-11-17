import { useState } from "react";
import { useNavigate } from "react-router-dom";
import { changerMotDePasse } from "../api/auth";

export default function ChangerMdp() {
  const [ancien, setAncien] = useState("");
  const [nouveau, setNouveau] = useState("");
  const [confirmation, setConfirmation] = useState("");
  const [message, setMessage] = useState("");
  const [error, setError] = useState("");
  const navigate = useNavigate();

  async function handleSubmit(e) {
    e.preventDefault();
    setMessage("");
    setError("");
    try {
      await changerMotDePasse(ancien, nouveau, confirmation);
      setMessage("Mot de passe modifié ✅");
      setTimeout(() => navigate("/dashboard"), 1500);
    } catch (err) {
  // Affiche le message renvoyé par le backend
  const backendMessage = err.response?.data;
  setError(backendMessage || err.message);
  console.log("Erreur backend:", backendMessage);
}
  }

  return (
    <form onSubmit={handleSubmit} style={{ padding: "2rem", maxWidth: 400 }}>
      <h2>Modifier mot de passe</h2>

      <input
        type="password"
        placeholder="Ancien mot de passe"
        value={ancien}
        onChange={(e) => setAncien(e.target.value)}
        required
        style={{ display: "block", width: "100%", marginBottom: "1rem", padding: "0.5rem" }}
      />

      <input
        type="password"
        placeholder="Nouveau mot de passe"
        value={nouveau}
        onChange={(e) => setNouveau(e.target.value)}
        required
        style={{ display: "block", width: "100%", marginBottom: "1rem", padding: "0.5rem" }}
      />

      <input
        type="password"
        placeholder="Confirmation"
        value={confirmation}
        onChange={(e) => setConfirmation(e.target.value)}
        required
        style={{ display: "block", width: "100%", marginBottom: "1rem", padding: "0.5rem" }}
      />

      {message && <p style={{ color: "green" }}>{message}</p>}
      {error && <p style={{ color: "red" }}>{error}</p>}

      <button type="submit">Valider</button>
    </form>
  );
}