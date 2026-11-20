import { BrowserRouter, Routes, Route, Navigate } from "react-router-dom";
import { AuthProvider } from "./context/authContext";
import PrivateRoute from "./components/PrivateRoute";
import Layout from "./components/Layout";
import Login from "./pages/Login";
import Dashboard from "./pages/Dashboard";
import Personnels from "./pages/Personnels";
import ChangerMdp from "./pages/ChangerMdp";

// Pages temporaires en attendant de les développer
function Placeholder({ title }) {
  return (
    <Layout>
      <h1>{title}</h1>
      <p>Page en cours de développement...</p>
    </Layout>
  );
}

export default function App() {
  return (
    <AuthProvider>
      <BrowserRouter>
        <Routes>
          <Route path="/login" element={<Login />} />

          <Route path="/dashboard" element={
            <PrivateRoute><Dashboard /></PrivateRoute>
          } />
          <Route path="/personnels" element={
            <PrivateRoute><Personnels /></PrivateRoute>
          } />
          <Route path="/change-password" element={
            <PrivateRoute><ChangerMdp /></PrivateRoute>
          } />

          {/* Routes du menu (placeholder pour l'instant) */}
         
          
          <Route path="/chauffeurs" element={
            <PrivateRoute><Placeholder title="Liste des chauffeurs" /></PrivateRoute>
          } />
         

          {/* Le catch-all renvoie vers /dashboard (protégée), JAMAIS /login */}
          <Route path="*" element={<Navigate to="/dashboard" replace />} />
        </Routes>
      </BrowserRouter>
    </AuthProvider>
  );
}
