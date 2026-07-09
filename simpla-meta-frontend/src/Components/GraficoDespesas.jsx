import { useEffect, useState } from "react";
import { Chart } from "react-google-charts";

const GraficoDespesas = ({despesas}) => {

    const [valorAluguel, setValorAluguel] = useState(0)
    const [valorLuzAgua, setValorLuzAgua] = useState(0)
    const [valorAlimentacao, setValorAlimentacao] = useState(0)
    const [valorOutros, setValorOutros] = useState(0)
    useEffect(()=>{


        let aluguel = 0
        let luzAgua = 0
        let alimentacao = 0
        let outros = 0

        despesas.forEach(item => {
            if (item.tipo == "Aluguel") {
                setValorAluguel(aluguel += item.valor)
            }
            if (item.tipo == "Luz/Agua") {
                setValorLuzAgua(luzAgua + item.valor)
            }
            if (item.tipo == "Alimentação") {
                setValorAlimentacao(alimentacao + item.valor)
            }
            if (item.tipo == "Outros") {
                setValorOutros(outros + item.valor)
            }
        });
    },[despesas])
    const data = [
        ["Tipo", "Valor", { role: "style" }],
        ["Aluguel", valorAluguel, "#05070d"],
        ["Luz/Agua", valorLuzAgua, "#3271FF"],
        ["Alimentação", valorAlimentacao, "#41B9FF"],
        ["Outros", valorOutros, "#8ca3c7"],
    ];

    const options = {
        title: "Despesas por categoria",
        legend: { position: "none" },
        backgroundColor: "transparent",
        chartArea: { width: "78%", height: "68%" },
    };

    return(
        <Chart chartType="ColumnChart" width="100%" height="100%" data={data} options={options} />
    )
}

export default GraficoDespesas
