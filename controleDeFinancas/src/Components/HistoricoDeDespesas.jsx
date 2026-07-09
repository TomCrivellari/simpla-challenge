import Button from "react-bootstrap/esm/Button"
import { deleteDespesa } from "../service/Services"
import { useAuth } from "../context/AuthProvider"


const HistoricoDeDespesas = ({despesas}) => {

    const {user} = useAuth()
    
    const deletar = async (id) => {
        await deleteDespesa(id, user.token)
        window.location.reload();
    }

    return (
        <div className="surface-card p-6">
            <h3 className="text-xl font-black mb-4">Histórico de Despesas</h3>
            {despesas.length > 0 ? (
                <div className="overflow-x-auto">
                    <table className="finance-table w-full">
                        <thead>
                            <tr>
                                <th className="p-3 text-left font-semibold">Descrição</th>
                                <th className="p-3 text-right font-semibold">Valor</th>
                                <th className="p-3 text-right font-semibold">Tipo</th>
                                <th className="p-3 text-right font-semibold">Deletar</th>
                            </tr>
                        </thead>
                        <tbody>
                            {despesas.map((item, index) => (
                                <tr key={index}>
                                    <td className="p-3">{item.descricao}</td>
                                    <td className="p-3 text-right text-[#05070d] font-bold">
                                        {item.valor.toLocaleString('pt-BR', { style: 'currency', currency: 'BRL' })}
                                    </td>
                                    <td className="p-3 text-right">{item.tipo}</td>
                                    <td className="p-3 text-right"><Button variant="danger" size="sm" onClick={() => deletar(item.id)}>X</Button></td>
                                </tr>
                            ))}
                        </tbody>
                    </table>
                </div>
            ) : <p className="text-gray-500">Nenhuma despesa adicionada ainda.</p>}
        </div>
    )
}

export default HistoricoDeDespesas
