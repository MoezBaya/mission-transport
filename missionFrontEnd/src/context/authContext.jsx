
import {
  createContext,
  useContext,
  useState,
  useEffect,
} from "react";

import * as authApi from "../api/auth";

const AuthContext = createContext(null);

export function AuthProvider({ children }) {
  const [user, setUser] = useState(null);
  const [loading, setLoading] = useState(true);

  // ===== Récupérer l'utilisateur au démarrage =====
  useEffect(() => {
    try {
      const stored = localStorage.getItem("user");

      if (stored) {
        setUser(JSON.parse(stored));
      }
    } catch (error) {
      console.error("Erreur récupération utilisateur:", error);
      localStorage.removeItem("user");
      localStorage.removeItem("token");
    } finally {
      setLoading(false);
    }
  }, []);

  // ===== Connexion =====
  async function doLogin(email, motDePasse) {
    const data = await authApi.login(email, motDePasse);

    localStorage.setItem("token", data.token);
    localStorage.setItem("user", JSON.stringify(data));

    setUser(data);

    return data;
  }

  // ===== Déconnexion =====
  async function doLogout() {
    try {
      await authApi.logout();
    } catch (error) {
      console.error("Logout failed:", error);
    }

    localStorage.removeItem("token");
    localStorage.removeItem("user");

    setUser(null);
  }

  return (
    <AuthContext.Provider
      value={{
        user,
        loading,
        doLogin,
        doLogout,
      }}
    >
      {children}
    </AuthContext.Provider>
  );
}

export function useAuth() {
  return useContext(AuthContext);
}