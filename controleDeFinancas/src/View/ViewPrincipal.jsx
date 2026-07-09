import { useEffect, useState } from "react";
import CabecalhoPrincipal from "../Components/CabecalhoPrincipal";
import Button from "react-bootstrap/Button";
import HistoricoDeReceitas from "../Components/HistoricoDeReceitas";
import HistoricoDeDespesas from "../Components/HistoricoDeDespesas";
import GraficoReceitas from "../Components/GraficoReceitas";
import GraficoDespesas from "../Components/GraficoDespesas";
import GraficoPizzaReceitas from "../Components/GraficoPizzaReceitas";
import GraficoPizzaDespesas from "../Components/GraficoPizzaDespesas";
import { useAuth } from "../context/AuthProvider";
import { useNavigate } from "react-router-dom";
import { buscaUserAPI, getDespesas, getReceitas, saveDespesa, saveReceita } from "../service/Services";

const ViewPrincipal = () => {

    const {user, logout} = useAuth()
    const navigate = useNavigate()

    const [saldo, setSaldo] = useState(0);
    const [receitas, setReceitas] = useState([]);
    const [despesas, setDespesas] = useState([]);


    useEffect(() => {
        const carregarDados = async () => {
            if (user == null) {
                navigate("/login");
            } else {
                let receitasBuscadas = await getReceitas(user.id, user.token);
                let despesasBuscadas = await getDespesas(user.id, user.token);
                let userBuscado = await buscaUserAPI(user.nome, user.token)
                

                if (receitasBuscadas == null || despesasBuscadas == null || userBuscado == null) {
                    logout()
                    navigate("/login");
                }

                let saldo = userBuscado[0].saldo
                receitasBuscadas.forEach(item => {
                    saldo += item.valor
                });

                despesasBuscadas.forEach(item => {
                    saldo -= item.valor
                });

                setSaldo(saldo);
                setReceitas(receitasBuscadas);
                setDespesas(despesasBuscadas);
            }
        };

        carregarDados();
    }, []);

    const carregarDados = async () => {
        let receitasBuscadas = await getReceitas(user.id, user.token);
        let despesasBuscadas = await getDespesas(user.id, user.token);
        let userBuscado = await buscaUserAPI(user.nome, user.token)
        let saldo = userBuscado[0].saldo

        receitasBuscadas.forEach(item => {
            saldo += item.valor
        });

        despesasBuscadas.forEach(item => {
            saldo -= item.valor
        });

        setSaldo(saldo);
        setReceitas(receitasBuscadas);
        setDespesas(despesasBuscadas);
    };

    const [mostrarFormularioReceita, setMostrarFormularioReceita] = useState(false);
    const [receita, setReceita] = useState({
        "descricao": "",
        "valor": "",
        "tipo": "",
        "idUser": user.id
    })

    const [mostrarFormularioDespesa, setMostrarFormularioDespesa] = useState(false);
    const [despesa, setDesepesa] = useState({
        "descricao": "",
        "valor": "",
        "tipo": "",
        "idUser": user.id
    })


    const adicionarReceita = async () => {
        const valorNumerico = parseFloat(receita.valor);
        if (receita.descricao && !isNaN(valorNumerico) && valorNumerico > 0) {
            await saveReceita(receita, user.token)
            await carregarDados()
            fecharFormularioReceita();
        } else {
            alert("Por favor, preencha o nome e um valor válido para a receita.");
        }
    };

    const fecharFormularioReceita = () => {
        setMostrarFormularioReceita(false);
        setReceita({...receita, "descricao": "", "valor": "", "tipo": ""})
    };

    const adicionarDespesa = async () => {
        const valorNumerico = parseFloat(despesa.valor);
        if (despesa.descricao && !isNaN(valorNumerico) && valorNumerico > 0) {
            await saveDespesa(despesa, user.token)
            await carregarDados()
            fecharFormularioDespesa();
        } else {
            alert("Por favor, preencha o nome e um valor válido para a receita.");
        }
    };

    const fecharFormularioDespesa = () => {
        setMostrarFormularioDespesa(false);
        setDesepesa({...despesa, "descricao": "", "valor": "", "tipo": ""})
    };

    return (
        <>
            <CabecalhoPrincipal />
            <div className="app-shell flex justify-center p-4 md:p-12">
                <div className="w-full max-w-6xl">
                    <div className="mb-8">
                        <p className="text-sm font-bold uppercase tracking-[0.18em] text-[#3271FF] mb-2">Painel financeiro</p>
                        <h1 className="text-4xl font-black text-[#05070d]">Visão geral da carteira</h1>
                    </div>

                    <div className="grid grid-cols-1 md:grid-cols-2 gap-8 mb-10">
                        <div className="metric-card p-7">
                            <legend className="text-sm font-bold uppercase tracking-[0.16em] text-[#5d6678]">Saldo Atual</legend>
                            <p className="text-5xl font-black text-[#05070d] mt-3 mb-2">
                                {saldo.toLocaleString('pt-BR', { style: 'currency', currency: 'BRL' })}
                            </p>
                            <span className="text-[#5d6678]">Saldo consolidado com receitas e despesas.</span>
                        </div>

                        <div className="surface-card p-6 flex flex-col gap-4">
                            <div className="grid grid-cols-2 gap-4">
                                <Button variant="success" className="py-2" onClick={() => setMostrarFormularioReceita(true)} disabled={mostrarFormularioReceita}>
                                    Adicionar Receita
                                </Button>
                                <Button variant="danger" className="py-2" onClick={() => setMostrarFormularioDespesa(true)} disabled={mostrarFormularioDespesa}>
                                    Adicionar Despesa
                                </Button>
                            </div>

                            {mostrarFormularioReceita && (
                                <div className="flex flex-col gap-2 p-4 rounded-2xl bg-[#f4fbff] border border-[#cceaff] mt-2">
                                    <h3 className="font-black text-[#05070d]">Nova Receita</h3>
                                    <input type="text" placeholder="Descrição" value={receita.descricao} onChange={(e) => {setReceita({...receita, "descricao": e.target.value})}} className="border p-2 rounded" />
                                    <input type="number" placeholder="Valor" value={receita.valor} onChange={(e) => {setReceita({...receita, "valor": parseInt(e.target.value)})}} className="border p-2 rounded" />
                                    <select className="border p-2 rounded" value={receita.tipo} onChange={(e) => {setReceita({...receita, "tipo": e.target.value})}}>
                                        <option>Tipo</option>
                                        <option value="Salario">Salario</option>
                                        <option value="Tranferencias">Tranferencias</option>
                                        <option value="Outros">Outros</option>
                                    </select>
                                    <div className="flex gap-2 justify-end mt-2">
                                        <Button variant="outline-success" size="sm" onClick={adicionarReceita}>Salvar</Button>
                                        <Button variant="outline-secondary" size="sm" onClick={fecharFormularioReceita}>Cancelar</Button>
                                    </div>
                                </div>
                            )}

                            {mostrarFormularioDespesa && (
                                <div className="flex flex-col gap-2 p-4 rounded-2xl bg-white border border-[#dfe8f6] mt-2">
                                    <h3 className="font-black text-[#05070d]">Nova Despesa</h3>
                                    <input type="text" placeholder="Descrição" value={despesa.descricao} onChange={(e) => {setDesepesa({...despesa, "descricao": e.target.value})}} className="border p-2 rounded" />
                                    <input type="number" placeholder="Valor" value={despesa.valor} onChange={(e) => {setDesepesa({...despesa, "valor": parseInt(e.target.value)})}} className="border p-2 rounded" />
                                    <select className="border p-2 rounded" value={despesa.tipo} onChange={(e) => {setDesepesa({...despesa, "tipo": e.target.value})}}>
                                        <option>Tipo</option>
                                        <option value="Aluguel">Aluguel</option>
                                        <option value="Luz/Agua">Luz/Agua</option>
                                        <option value="Alimentação">Alimentação</option>
                                        <option value="Outros">Outros</option>
                                    </select>
                                    <div className="flex gap-2 justify-end mt-2">
                                        <Button variant="outline-danger" size="sm" onClick={adicionarDespesa}>Salvar</Button>
                                        <Button variant="outline-secondary" size="sm" onClick={fecharFormularioDespesa}>Cancelar</Button>
                                    </div>
                                </div>
                            )}
                        </div>
                    </div>

                    <div className="grid grid-cols-1 md:grid-cols-2 gap-8 mb-10">
                        <HistoricoDeReceitas receitas={receitas}/>
                        <HistoricoDeDespesas despesas={despesas}/>
                    </div>

                    <div className="grid grid-cols-1 md:grid-cols-2 gap-8 mb-8">
                        <div className="chart-panel">
                            <GraficoReceitas receitas={receitas}/>
                        </div>
                        <div className="chart-panel">
                            <GraficoDespesas despesas={despesas}/>
                        </div>
                    </div>

                    <div className="grid grid-cols-1 md:grid-cols-2 gap-8">
                        <div className="chart-panel h-[430px]">
                            <GraficoPizzaReceitas receitas={receitas}/>
                        </div>
                        <div className="chart-panel h-[430px]">
                            <GraficoPizzaDespesas despesas={despesas}/>
                        </div>
                    </div>
                </div>
            </div>
        </>
    );
};

export default ViewPrincipal;
