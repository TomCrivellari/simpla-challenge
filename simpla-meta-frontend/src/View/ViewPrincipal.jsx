import { useEffect, useState } from "react";
import { Link } from "react-router-dom";
import CabecalhoPrincipal from "../Components/CabecalhoPrincipal";
import { useAuth } from "../context/AuthProvider";
import { financeApi, goalsApi } from "../service/Services";

const money = (value = 0) => Number(value).toLocaleString("pt-BR", { style: "currency", currency: "BRL" });
const ViewPrincipal = () => {
  const { token, user } = useAuth(); const [dashboard, setDashboard] = useState(null); const [goals, setGoals] = useState([]); const [error, setError] = useState("");
  useEffect(() => { Promise.all([financeApi.dashboard(token), goalsApi.list(token)]).then(([d, g]) => { setDashboard(d); setGoals(g); }).catch((e) => setError(e.message)); }, [token]);
  return <><CabecalhoPrincipal /><main className="app-shell p-6 md:p-10"><div className="max-w-6xl mx-auto">
    <p className="eyebrow">Olá, {user?.fullName}</p><h1 className="text-4xl font-black mb-2">Sua visão financeira</h1><p className="text-secondary mb-8">Saldo, movimentações recentes e progresso das suas metas.</p>
    {error && <div className="alert alert-danger">{error}</div>}
    <div className="grid md:grid-cols-3 gap-5 mb-8">
      <div className="metric-card p-6"><span>Saldo atual</span><strong className="metric-value">{money(dashboard?.balance)}</strong></div>
      <div className="metric-card p-6"><span>Total de receitas</span><strong className="metric-value text-success">{money(dashboard?.totalIncomes)}</strong></div>
      <div className="metric-card p-6"><span>Total de despesas</span><strong className="metric-value text-danger">{money(dashboard?.totalExpenses)}</strong></div>
    </div>
    <div className="grid lg:grid-cols-2 gap-6">
      <section className="surface-card p-6"><div className="section-heading"><h2>Movimentações recentes</h2><Link to="/transacoes">Ver todas</Link></div>
        {(dashboard?.recentTransactions || []).map((item) => <div className="list-row" key={item.id}><div><strong>{item.description}</strong><small>{item.category || "Sem categoria"} · {new Date(`${item.transactionDate}T12:00:00`).toLocaleDateString("pt-BR")}</small></div><strong className={item.type === "INCOME" ? "text-success" : "text-danger"}>{item.type === "INCOME" ? "+ " : "- "}{money(item.amount)}</strong></div>)}
        {!dashboard?.recentTransactions?.length && <p className="empty-state">Nenhuma transação cadastrada.</p>}
      </section>
      <section className="surface-card p-6"><div className="section-heading"><h2>Metas em andamento</h2><Link to="/metas">Gerenciar</Link></div>
        {goals.slice(0, 4).map((goal) => <div className="goal-preview" key={goal.id}><div className="flex justify-between"><strong>{goal.name}</strong><span>{Number(goal.progressPercentage).toFixed(0)}%</span></div><div className="progress-track"><span style={{ width: `${Math.min(100, Number(goal.progressPercentage))}%` }} /></div><small>{money(goal.currentAmount)} de {money(goal.targetAmount)}</small></div>)}
        {!goals.length && <p className="empty-state">Crie sua primeira meta manualmente ou com o assistente.</p>}
      </section>
    </div>
  </div></main></>;
};
export default ViewPrincipal;
