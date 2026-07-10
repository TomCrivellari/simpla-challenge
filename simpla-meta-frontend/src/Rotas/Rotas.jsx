import { BrowserRouter, Navigate, Outlet, Route, Routes } from "react-router-dom";
import { AuthProvider, useAuth } from "../context/AuthProvider";
import ViewHome from "../View/ViewHome";
import ViewLogin from "../View/ViewLogin";
import ViewCadastro from "../View/ViewCadastro";
import ViewPrincipal from "../View/ViewPrincipal";
import ViewTransacoes from "../View/ViewTransacoes";
import ViewMetas from "../View/ViewMetas";
import ViewChat from "../View/ViewChat";

const ProtectedRoute = () => {
  const { token, loading } = useAuth();
  if (loading) return <div className="app-shell grid place-items-center"><p>Carregando...</p></div>;
  return token ? <Outlet /> : <Navigate to="/login" replace />;
};

function Rotas() {
  return (
    <AuthProvider>
      <BrowserRouter>
        <Routes>
          <Route path="/" element={<ViewHome />} />
          <Route path="/login" element={<ViewLogin />} />
          <Route path="/cadastro" element={<ViewCadastro />} />
          <Route element={<ProtectedRoute />}>
            <Route path="/principal" element={<ViewPrincipal />} />
            <Route path="/transacoes" element={<ViewTransacoes />} />
            <Route path="/metas" element={<ViewMetas />} />
            <Route path="/assistente" element={<ViewChat />} />
          </Route>
          <Route path="*" element={<Navigate to="/" replace />} />
        </Routes>
      </BrowserRouter>
    </AuthProvider>
  );
}

export default Rotas;
