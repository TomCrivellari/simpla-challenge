import { useCallback, useEffect, useState } from "react";
import Button from "react-bootstrap/Button";
import Form from "react-bootstrap/Form";
import CabecalhoPrincipal from "../Components/CabecalhoPrincipal";
import { useAuth } from "../context/AuthProvider";
import { financeApi } from "../service/Services";

const today = new Date().toISOString().slice(0, 10);
const emptyForm = { type: "EXPENSE", description: "", amount: "", category: "", transactionDate: today };
const money = (value) => Number(value).toLocaleString("pt-BR", { style: "currency", currency: "BRL" });

const ViewTransacoes = () => {
  const { token } = useAuth(); const [items, setItems] = useState([]); const [form, setForm] = useState(emptyForm); const [editingId, setEditingId] = useState(null); const [error, setError] = useState(""); const [saving, setSaving] = useState(false);
  const load = useCallback(() => financeApi.listTransactions(token).then(setItems).catch((e) => setError(e.message)), [token]);
  useEffect(() => { load(); }, [load]);
  const submit = async (event) => { event.preventDefault(); setSaving(true); setError(""); try { const payload = { ...form, amount: Number(form.amount) }; if (editingId) await financeApi.updateTransaction(token, editingId, payload); else await financeApi.createTransaction(token, payload); setForm(emptyForm); setEditingId(null); await load(); } catch (e) { setError(e.message); } finally { setSaving(false); } };
  const edit = (item) => { setEditingId(item.id); setForm({ type: item.type, description: item.description, amount: item.amount, category: item.category || "", transactionDate: item.transactionDate }); window.scrollTo({ top: 0, behavior: "smooth" }); };
  const remove = async (id) => { if (!window.confirm("Deseja excluir esta transação?")) return; try { await financeApi.deleteTransaction(token, id); await load(); } catch (e) { setError(e.message); } };
  return <><CabecalhoPrincipal /><main className="app-shell p-6 md:p-10"><div className="max-w-6xl mx-auto">
    <p className="eyebrow">Receitas e despesas</p><h1 className="text-4xl font-black mb-8">Transações</h1>
    <Form onSubmit={submit} className="surface-card p-6 mb-8"><h2 className="text-2xl font-black mb-5">{editingId ? "Editar transação" : "Nova transação"}</h2><div className="grid md:grid-cols-5 gap-4">
      <Form.Group><Form.Label>Tipo</Form.Label><Form.Select value={form.type} onChange={(e) => setForm({ ...form, type: e.target.value })}><option value="INCOME">Receita</option><option value="EXPENSE">Despesa</option></Form.Select></Form.Group>
      <Form.Group className="md:col-span-2"><Form.Label>Descrição</Form.Label><Form.Control required maxLength={150} value={form.description} onChange={(e) => setForm({ ...form, description: e.target.value })} /></Form.Group>
      <Form.Group><Form.Label>Valor</Form.Label><Form.Control required type="number" min="0.01" step="0.01" value={form.amount} onChange={(e) => setForm({ ...form, amount: e.target.value })} /></Form.Group>
      <Form.Group><Form.Label>Data</Form.Label><Form.Control required type="date" value={form.transactionDate} onChange={(e) => setForm({ ...form, transactionDate: e.target.value })} /></Form.Group>
      <Form.Group className="md:col-span-3"><Form.Label>Categoria</Form.Label><Form.Control maxLength={80} placeholder="Ex.: Moradia, Salário, Alimentação" value={form.category} onChange={(e) => setForm({ ...form, category: e.target.value })} /></Form.Group>
      <div className="md:col-span-2 flex gap-2 items-end"><Button type="submit" variant="success" disabled={saving}>{saving ? "Salvando..." : editingId ? "Salvar alterações" : "Adicionar"}</Button>{editingId && <Button variant="outline-secondary" onClick={() => { setEditingId(null); setForm(emptyForm); }}>Cancelar</Button>}</div>
    </div>{error && <div className="alert alert-danger mt-4 mb-0">{error}</div>}</Form>
    <section className="surface-card p-6 overflow-x-auto"><h2 className="text-2xl font-black mb-4">Histórico</h2><table className="finance-table w-full"><thead><tr><th>Data</th><th>Descrição</th><th>Categoria</th><th>Tipo</th><th className="text-right">Valor</th><th className="text-right">Ações</th></tr></thead><tbody>{items.map((item) => <tr key={item.id}><td>{new Date(`${item.transactionDate}T12:00:00`).toLocaleDateString("pt-BR")}</td><td>{item.description}</td><td>{item.category || "—"}</td><td>{item.type === "INCOME" ? "Receita" : "Despesa"}</td><td className={`text-right font-bold ${item.type === "INCOME" ? "text-success" : "text-danger"}`}>{money(item.amount)}</td><td className="text-right whitespace-nowrap"><Button size="sm" variant="outline-primary" onClick={() => edit(item)}>Editar</Button>{" "}<Button size="sm" variant="outline-danger" onClick={() => remove(item.id)}>Excluir</Button></td></tr>)}</tbody></table>{!items.length && <p className="empty-state">Nenhuma transação cadastrada.</p>}</section>
  </div></main></>;
};
export default ViewTransacoes;
