import { BrowserRouter, Routes, Route, Navigate } from "react-router-dom";
import { AuthProvider } from "./context/authContext";
import PrivateRoute from "./components/PrivateRoute";
import Login from "./pages/Login";
import Dashboard from "./pages/Dashboard";
import DashboardChefDeParc from "./pages/DashboardChefDeParc";
import DashboardChauffeur from "./pages/DashboardChauffeur";
import Personnels from "./pages/Personnels";
import Chauffeurs from "./pages/Chauffeurs";
import ChangerMdp from "./pages/ChangerMdp";
import Vehicules from "./pages/Vehicules";

export default function App() {
  return (
    <AuthProvider>
      <BrowserRouter>
        <Routes>
          <Route path="/login" element={<Login />} />

          <Route path="/dashboard" element={
            <PrivateRoute><Dashboard /></PrivateRoute>
          } />
          <Route path="/dashboard-chef-de-parc" element={
            <PrivateRoute><DashboardChefDeParc /></PrivateRoute>
          } />
          <Route path="/dashboard-chauffeur" element={
            <PrivateRoute><DashboardChauffeur /></PrivateRoute>
          } />
          <Route path="/personnels" element={
            <PrivateRoute><Personnels /></PrivateRoute>
          } />
          <Route path="/change-password" element={
            <PrivateRoute><ChangerMdp /></PrivateRoute>
          } />

          <Route path="/chauffeurs" element={
            <PrivateRoute><Chauffeurs /></PrivateRoute>
          } />
          <Route path="/vehicules" element={
            <PrivateRoute><Vehicules /></PrivateRoute>
          } />

          {/* Le catch-all renvoie vers /dashboard (protégée), JAMAIS /login */}
          <Route path="*" element={<Navigate to="/dashboard" replace />} />
        </Routes>
      </BrowserRouter>
    </AuthProvider>
  );
}
