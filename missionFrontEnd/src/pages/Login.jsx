import { useState } from "react";
import { useNavigate } from "react-router-dom";
import { useAuth } from "../context/authContext";
import { LoginForm } from "../components/forms/LoginForm";
import logo from "../assets/img/logo.png";
import "./Login.css";

export default function Login() {
  const [email, setEmail] = useState("");
  const [motDePasse, setMotDePasse] = useState("");
  const [error, setError] = useState("");
  const [loading, setLoading] = useState(false);
  const { doLogin } = useAuth();
  const navigate = useNavigate();

  async function handleSubmit(event) {
    event.preventDefault();
    setError("");
    setLoading(true);

    try {
      await doLogin(email, motDePasse);
      navigate("/dashboard");
    } catch (loginError) {
      setError(loginError.message);
    } finally {
      setLoading(false);
    }
  }

  return (
    <main className="login-page">
      <section className="login-card" aria-labelledby="login-title">
        <img className="login-logo" src={logo} alt="Mission" />
        <p className="login-eyebrow">Espace de gestion</p>
        <h1 className="page-title login-title" id="login-title">Connexion</h1>
        <p className="login-description">Connectez-vous à votre espace Mission.</p>
        <LoginForm
          email={email}
          motDePasse={motDePasse}
          error={error}
          loading={loading}
          onEmailChange={setEmail}
          onPasswordChange={setMotDePasse}
          onSubmit={handleSubmit}
        />
      </section>
    </main>
  );
}
