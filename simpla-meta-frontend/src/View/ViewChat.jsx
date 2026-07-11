import { useState } from "react";
import Button from "react-bootstrap/Button";
import Form from "react-bootstrap/Form";
import CabecalhoPrincipal from "../Components/CabecalhoPrincipal";
import { useAuth } from "../context/AuthProvider";
import { aiApi, goalsApi } from "../service/Services";

const money = (value) => Number(value || 0).toLocaleString("pt-BR", { style: "currency", currency: "BRL" });
const initialMessage = { role: "assistant", content: "Olá! Conte qual meta financeira você quer alcançar. Vou ajudar a definir valor e prazo." };
const isAffirmative = (value) => /^(sim|s|confirmo|pode criar|criar)(\s|[,.!]|$)/i.test(value.trim());
const monthlyAmountFor = (targetAmount, deadline) => {
  const now = new Date();
  const [year, month] = deadline.split("-").map(Number);
  const months = Math.max(1, (year - now.getFullYear()) * 12 + month - (now.getMonth() + 1) + 1);
  return Math.round((Number(targetAmount) / months) * 100) / 100;
};
const ViewChat = () => {
  const { token } = useAuth();
  const [messages, setMessages] = useState([initialMessage]);
  const [input, setInput] = useState(""); const [pendingGoal, setPendingGoal] = useState(null); const [plan, setPlan] = useState(null); const [questions, setQuestions] = useState([]); const [loading, setLoading] = useState(false); const [error, setError] = useState(""); const [created, setCreated] = useState(false);

  const send = async (event) => {
    event.preventDefault(); const content = input.trim(); if (!content || loading) return;
    const nextMessages = [...messages, { role: "user", content }]; setMessages(nextMessages); setInput(""); setLoading(true); setError(""); setCreated(false);
    if (pendingGoal && isAffirmative(content)) {
      await createGoal(pendingGoal);
      return;
    }
    try {
      const history = nextMessages.slice(-20).map(({ role, content: text }) => ({ role, content: text }));
      const response = await aiApi.chat(token, history);
      setMessages((current) => [...current, { role: "assistant", content: response.message }]);
      setQuestions(response.followUpQuestions || []);
      const actionGoal = response.actions?.find((action) => action.type?.toUpperCase().includes("CREATE") && action.payload)?.payload;
      const suggestion = response.suggestedGoal || actionGoal;
      if (suggestion?.name && suggestion?.targetAmount && suggestion?.deadline) {
        setPlan({ ...(response.savingsPlan || {}), monthlyAmount: monthlyAmountFor(suggestion.targetAmount, suggestion.deadline) });
        setPendingGoal({ name: suggestion.name, description: suggestion.description || "Meta planejada com o Assistente Simpla", targetAmount: Number(suggestion.targetAmount), deadline: suggestion.deadline });
      } else { setPlan(response.savingsPlan); setPendingGoal(null); }
    } catch (e) { setError(e.message); } finally { setLoading(false); }
  };

  const createGoal = async (goal) => {
    try { await goalsApi.create(token, goal); setCreated(true); setMessages((current) => [...current, { role: "assistant", content: `Meta “${goal.name}” criada com sucesso! Você já pode acompanhar e registrar aportes na área de Metas.` }]); setPendingGoal(null); setQuestions([]); }
    catch (e) { setError(e.message); } finally { setLoading(false); }
  };

  const confirmGoal = async () => {
    if (!pendingGoal) return; setLoading(true); setError("");
    await createGoal(pendingGoal);
  };

  const startNewGoal = () => {
    setMessages([initialMessage]);
    setInput("");
    setPendingGoal(null);
    setPlan(null);
    setQuestions([]);
    setError("");
    setCreated(false);
  };

  return <><CabecalhoPrincipal /><main className="app-shell p-4 md:p-10"><div className="max-w-5xl mx-auto"><p className="eyebrow">Planejamento com IA</p><h1 className="text-4xl font-black mb-2">Assistente de metas</h1><p className="text-secondary mb-6">Converse para construir uma meta. A criação só acontece depois da sua confirmação.</p>
    <div className="chat-layout"><section className="surface-card chat-card"><div className="chat-messages">{messages.map((message, index) => <div className={`chat-message ${message.role}`} key={`${message.role}-${index}`}>{message.content}</div>)}{loading && <div className="chat-message assistant">Pensando...</div>}</div><Form onSubmit={send} className="chat-input"><Form.Control as="textarea" rows={2} maxLength={2000} placeholder="Ex.: Quero guardar R$ 10 mil para uma viagem no próximo ano" value={input} onChange={(e) => setInput(e.target.value)} /><Button type="submit" variant="success" disabled={loading || !input.trim()}>Enviar</Button></Form></section>
      <aside className="flex flex-col gap-4">{pendingGoal && <div className="surface-card p-6 confirmation-card"><p className="eyebrow">Confirmação necessária</p><h2 className="text-2xl font-black">Criar esta meta?</h2><dl><dt>Nome</dt><dd>{pendingGoal.name}</dd><dt>Objetivo</dt><dd>{money(pendingGoal.targetAmount)}</dd><dt>Prazo</dt><dd>{new Date(`${pendingGoal.deadline}T12:00:00`).toLocaleDateString("pt-BR")}</dd>{pendingGoal.description && <><dt>Descrição</dt><dd>{pendingGoal.description}</dd></>}</dl>{plan && <p className="plan-highlight">Plano sugerido: {money(plan.monthlyAmount)} por mês</p>}<div className="flex gap-2"><Button variant="success" onClick={confirmGoal} disabled={loading}>Sim, criar meta</Button><Button variant="outline-secondary" onClick={() => setPendingGoal(null)}>Ainda não</Button></div></div>}
        {!!questions.length && !pendingGoal && <div className="surface-card p-5"><strong>Para continuar:</strong><ul className="mt-2 mb-0">{questions.map((question) => <li key={question}>{question}</li>)}</ul></div>}
        {created && <div className="alert alert-success"><p className="mb-3">Meta criada e pronta para receber aportes.</p><Button variant="success" onClick={startNewGoal}>Criar nova meta</Button></div>}{error && <div className="alert alert-danger">{error}</div>}
      </aside>
    </div>
  </div></main></>;
};
export default ViewChat;
