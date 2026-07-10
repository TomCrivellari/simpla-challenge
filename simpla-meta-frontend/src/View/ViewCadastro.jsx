import { useState } from "react";
import Button from "react-bootstrap/Button";
import Form from "react-bootstrap/Form";
import { Link, useNavigate } from "react-router-dom";
import CabecalhoHome from "../Components/CabecalhoHome";
import { authApi } from "../service/Services";

const ViewCadastro = () => {
  const [form, setForm] = useState({ fullName: "", email: "", password: "" });
  const [error, setError] = useState(""); const [submitting, setSubmitting] = useState(false);
  const navigate = useNavigate();
  const submit = async (event) => {
    event.preventDefault(); setError(""); setSubmitting(true);
    try { await authApi.register(form); navigate("/login", { state: { registered: true } }); }
    catch (err) { setError(err.message); } finally { setSubmitting(false); }
  };
  return <><CabecalhoHome /><div className="hero-shell"><Form onSubmit={submit} className="surface-card w-full max-w-md p-8">
    <p className="eyebrow">Nova conta</p><h1 className="text-3xl font-black mb-6">Cadastro</h1>
    <Form.Group className="mb-3"><Form.Label>Nome completo</Form.Label><Form.Control required minLength={3} maxLength={150} value={form.fullName} onChange={(e) => setForm({ ...form, fullName: e.target.value })} /></Form.Group>
    <Form.Group className="mb-3"><Form.Label>E-mail</Form.Label><Form.Control required type="email" value={form.email} onChange={(e) => setForm({ ...form, email: e.target.value })} /></Form.Group>
    <Form.Group className="mb-3"><Form.Label>Senha</Form.Label><Form.Control required type="password" minLength={8} maxLength={72} value={form.password} onChange={(e) => setForm({ ...form, password: e.target.value })} /><Form.Text>Use de 8 a 72 caracteres.</Form.Text></Form.Group>
    {error && <div className="alert alert-danger py-2">{error}</div>}
    <Button type="submit" variant="success" className="w-full py-2" disabled={submitting}>{submitting ? "Criando..." : "Criar conta"}</Button>
    <p className="text-center mt-4 mb-0"><Link to="/login">Voltar ao login</Link></p>
  </Form></div></>;
};
export default ViewCadastro;
