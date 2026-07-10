import { useState } from "react";
import Button from "react-bootstrap/Button";
import Form from "react-bootstrap/Form";
import { Link, useNavigate } from "react-router-dom";
import CabecalhoHome from "../Components/CabecalhoHome";
import { useAuth } from "../context/AuthProvider";

const ViewLogin = () => {
  const [form, setForm] = useState({ email: "", password: "" });
  const [error, setError] = useState("");
  const [submitting, setSubmitting] = useState(false);
  const { login } = useAuth();
  const navigate = useNavigate();

  const submit = async (event) => {
    event.preventDefault(); setError(""); setSubmitting(true);
    try { await login(form.email, form.password); navigate("/principal"); }
    catch (err) { setError(err.message || "E-mail ou senha inválidos."); }
    finally { setSubmitting(false); }
  };

  return <><CabecalhoHome /><div className="hero-shell">
    <Form onSubmit={submit} className="surface-card w-full max-w-md p-8">
      <p className="eyebrow">Simpla Meta</p><h1 className="text-3xl font-black mb-6">Entrar</h1>
      <Form.Group className="mb-3"><Form.Label>E-mail</Form.Label><Form.Control required type="email" autoComplete="email" value={form.email} onChange={(e) => setForm({ ...form, email: e.target.value })} /></Form.Group>
      <Form.Group className="mb-3"><Form.Label>Senha</Form.Label><Form.Control required type="password" autoComplete="current-password" value={form.password} onChange={(e) => setForm({ ...form, password: e.target.value })} /></Form.Group>
      {error && <div className="alert alert-danger py-2">{error}</div>}
      <Button type="submit" variant="success" className="w-full py-2" disabled={submitting}>{submitting ? "Entrando..." : "Entrar"}</Button>
      <p className="text-center mt-4 mb-0">Ainda não tem conta? <Link to="/cadastro">Cadastre-se</Link></p>
    </Form>
  </div></>;
};
export default ViewLogin;
