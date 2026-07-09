import Button from "react-bootstrap/esm/Button"
import { deleteReceita } from "../service/Services"
import { useAuth } from "../context/AuthProvider"


const HistoricoDeReceitas = ({receitas}) => {

    const {user} = useAuth()

    const deletar = async (id) => {
        if (user == null) {
            return
        }

        try {
            await deleteReceita(id, user.token)
            window.location.reload();
        } catch {
            return
        }
    }

    return (
        <div className="surface-card p-6">
            <h3 className="text-xl font-black mb-4">Histórico de Receitas</h3>
            {receitas.length > 0 ? (
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
                            {receitas.map((item, index) => (
                                <tr key={index}>
                                    <td className="p-3">{item.descricao}</td>
                                    <td className="p-3 text-right text-[#3271FF] font-bold">
                                        {item.valor.toLocaleString('pt-BR', { style: 'currency', currency: 'BRL' })}
                                    </td>
                                    <td className="p-3 text-right">{item.tipo}</td>
                                    <td className="p-3 text-right"><Button variant="danger" size="sm" onClick={() => deletar(item.id)}>X</Button></td>
                                </tr>
                            ))}
                        </tbody>
                    </table>
                </div>
            ) : <p className="text-gray-500">Nenhuma receita adicionada ainda.</p>}
        </div>
    )
}

export default HistoricoDeReceitas
