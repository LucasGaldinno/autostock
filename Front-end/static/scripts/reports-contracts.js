(() => {
  function safeParse(value) {
    if (!value || value === "null") return [];
    try { return Array.isArray(value) ? value : JSON.parse(value); }
    catch(e) { console.error("Erro ao converter JSON:", value); return []; }
  }

  const reportMonths   = safeParse(window.reportMonths);
  const reportSales    = safeParse(window.reportSales).map(v => Number(v) || 0);
  const reportProfits  = safeParse(window.reportProfits).map(v => Number(v) || 0);
  const brandLabels    = safeParse(window.brandLabels);
  const brandData      = safeParse(window.brandData).map(v => Number(v) || 0);
  const quantityLabels = safeParse(window.quantityLabels);
  const quantityData   = safeParse(window.quantityData).map(v => Number(v) || 0);

  const colors = {
    blue:  "#3b82f6",
    green: "#22c55e",
    yellow:"#eab308",
    red:   "#ef4444",
    purple:"#8b5cf6",
    cyan:  "#06b6d4"
  };

  if(reportMonths.length) {
    const optionsBar = {
      chart: { type: 'bar', height: 300, animations: { enabled: true, easing: 'easeOutCubic', speed: 800 } },
      series: [
        { name: 'Vendas (R$)', data: reportSales },
        { name: 'Lucro (R$)',  data: reportProfits }
      ],
      colors: [colors.blue, colors.green],
      plotOptions: { bar: { borderRadius: 8, columnWidth: '45%' } },
      dataLabels: { enabled: false }, 
      xaxis: { categories: reportMonths, labels: { style: { colors: '#334155' } } },
      yaxis: { 
        labels: { 
          style: { colors: '#334155' },
        }
      },
      legend: { labels: { colors: '#0f172a' } },
	  tooltip: {
	    shared: true,
	    intersect: false,
	    y: {
	      formatter: function (value, { seriesIndex }) {
	        const label = seriesIndex === 0 ? "Vendas" : "Lucro";
	        return `${label}: R$ ${value.toLocaleString('pt-BR', {
	          minimumFractionDigits: 2,
	          maximumFractionDigits: 2
	        })}`;
	      }
	    }
	  }
    };
    new ApexCharts(document.querySelector("#contractsChart"), optionsBar).render();
  }

  if(brandLabels.length) {
    const optionsPie = {
      chart: { type: 'pie', height: 280 },
      series: brandData.length === 1 ? [brandData[0], 0] : brandData,
      labels: brandData.length === 1 ? [brandLabels[0], 'Outros'] : brandLabels,
      colors: [colors.blue, colors.green, colors.yellow, colors.red, colors.purple, colors.cyan, '#d1d5db'],
      legend: { labels: { colors: '#0f172a' } },
      tooltip: { theme: 'light' }
    };
    new ApexCharts(document.querySelector("#brandChart"), optionsPie).render();
  }

  if(quantityLabels.length) {
    const mesesPT = {
      January: 'Jan', February: 'Fev', March: 'Mar', April: 'Abr',
      May: 'Mai', June: 'Jun', July: 'Jul', August: 'Ago',
      September: 'Set', October: 'Out', November: 'Nov', December: 'Dez'
    };

    const labelsPT = quantityLabels.map(m => mesesPT[m] || m); 

    const optionsLine = {
      chart: {
        type: 'line',
        height: '93%',
        animations: { enabled: true, easing: 'easeOutCubic', speed: 1200 }
      },
      series: [{ name: 'Contratos', data: quantityData }],
      stroke: { curve: 'smooth', width: 3 },
      markers: { size: 5 },
      fill: { type: 'solid', opacity: 0.18 },
      xaxis: { categories: labelsPT, labels: { style: { colors: '#334155' } } },
      yaxis: { 
        labels: { 
          style: { colors: '#334155' },
          formatter: v => Number(v).toFixed(0) 
        },
        min: 0,
        forceNiceScale: true
      },
      tooltip: { theme: 'light' },
      colors: ['#3b82f6'],
      legend: { labels: { colors: '#0f172a' } }
    };
    
    new ApexCharts(document.querySelector("#salesCountChart"), optionsLine).render();
  }

  window.filterReport = function(event) {
    event.preventDefault();
    const startDate = document.querySelector("[name='startDate']").value;
    const endDate   = document.querySelector("[name='endDate']").value;

    if (!startDate || !endDate) return alert("Selecione um intervalo de datas.");
    if (new Date(startDate) > new Date(endDate)) return alert("A data inicial não pode ser maior que a data final.");

    window.location.href = `/contract-reports?startDate=${startDate}&endDate=${endDate}`;
  };
})();

document.addEventListener("DOMContentLoaded", () => {

    const btn = document.getElementById("menuToggle");
    const sideMenu = document.querySelector(".side-menu");
    const overlay = document.querySelector(".menu-overlay");

    if (!btn || !sideMenu) {
        console.warn("Menu mobile não carregado nesta página.");
        return;
    }
	
    btn.addEventListener("click", () => {

        const isOpen = sideMenu.classList.toggle("active");

        btn.classList.toggle("active");

        if (overlay) overlay.classList.toggle("active");

        const icon = btn.querySelector("i");

        if (icon) {
            icon.classList.remove("fa-bars", "fa-xmark");
            icon.classList.add(isOpen ? "fa-xmark" : "fa-bars");
        }
    });

    if (overlay) {
        overlay.addEventListener("click", () => {
            sideMenu.classList.remove("active");
            btn.classList.remove("active");
            overlay.classList.remove("active");

            const icon = btn.querySelector("i");
            if (icon) {
                icon.classList.remove("fa-xmark");
                icon.classList.add("fa-bars");
            }
        });
    }

    window.filterReport = function(event) {
        event.preventDefault();
        const startDate = document.querySelector("[name='startDate']").value;
        const endDate   = document.querySelector("[name='endDate']").value;

        if (!startDate || !endDate) {
            alert("Selecione um intervalo de datas.");
            return;
        }

        if (new Date(startDate) > new Date(endDate)) {
            alert("A data inicial não pode ser maior que a final.");
            return;
        }

        window.location.href = `/contract-reports?startDate=${startDate}&endDate=${endDate}`;
    };
});

