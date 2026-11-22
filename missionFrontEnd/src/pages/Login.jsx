import { useState } from "react";
import { useNavigate } from "react-router-dom";
import { useAuth } from "../context/authContext";
import { LoginForm } from "../components/forms/LoginForm";
import logo from "../assets/img/logo.png";
import suv from "../assets/img/SUV.webp";
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
        <div className="login-content">
          <img className="login-logo" src={logo} alt="Mission" />
          <h1 className="login-title" id="login-title">Connexion</h1>
          <LoginForm
            email={email}
            motDePasse={motDePasse}
            error={error}
            loading={loading}
            onEmailChange={setEmail}
            onPasswordChange={setMotDePasse}
            onSubmit={handleSubmit}
          />
        </div>
      </section>
      <div className="login-visual" aria-hidden="true">
        <img src={suv} alt="" />
      </div>
    </main>
  );
}
