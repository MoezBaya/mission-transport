export function LoginForm({ email, motDePasse, error, loading, onEmailChange, onPasswordChange, onSubmit }) {
  return (
    <form className="login-form" onSubmit={onSubmit}>
      <label className="field-label" htmlFor="login-email">Adresse e-mail</label>
      <input className="form-control" id="login-email" type="email" autoComplete="username" placeholder="nom@exemple.com" value={email} onChange={(event) => onEmailChange(event.target.value)} required />

      <label className="field-label" htmlFor="login-password">Mot de passe</label>
      <input className="form-control" id="login-password" type="password" autoComplete="current-password" placeholder="Votre mot de passe" value={motDePasse} onChange={(event) => onPasswordChange(event.target.value)} required />

      {error && <p className="form-message form-message--error" role="alert">{error}</p>}
      <button className="button login-submit" type="submit" disabled={loading}>{loading ? "Connexion..." : "Se connecter"}</button>
    </form>
  );
}
