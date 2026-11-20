import { Home } from "lucide-react";

export default function PageBreadcrumb({ current }) {
  return (
    <nav className="breadcrumb" aria-label="Fil d’Ariane">
      <Home size={17} aria-hidden="true" />
      <span>Accueil</span>
      <span aria-hidden="true">›</span>
      <span className="breadcrumb-current" aria-current="page">{current}</span>
    </nav>
  );
}
