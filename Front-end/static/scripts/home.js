// Dados para o Chart.js (preenchidos pelo Thymeleaf)
const labels = /*[[${labels}]]*/ [];
const sales = /*[[${sales}]]*/ [];
const profits = /*[[${profits}]]*/ [];

if (labels.length > 0) {
    const ctx = document.getElementById('salesSummaryChart').getContext('2d');

    new Chart(ctx, {
        type: 'bar',
        data: {
            labels: labels,
            datasets: [
                {
                    label: 'Vendas (R$)',
                    data: sales,
                    backgroundColor: 'rgba(0, 123, 255, 0.7)',
                    borderColor: 'rgba(0, 123, 255, 1)',
                    borderWidth: 1
                },
                {
                    label: 'Lucro (R$)',
                    data: profits,
                    backgroundColor: 'rgba(40, 167, 69, 0.7)',
                    borderColor: 'rgba(40, 167, 69, 1)',
                    borderWidth: 1
                }
            ]
        },
        options: {
            plugins: {
                legend: {
                    labels: { color: 'white', font: { size: 13 } }
                }
            },
            scales: {
                x: { ticks: { color: 'white' }, grid: { color: '#444' } },
                y: { ticks: { color: 'white' }, grid: { color: '#444' } }
            }
        }
    });
}

if (window.location.pathname === "/profile") {
    window.location.href = "/home";  // Redireciona para a página home
}


// Alertinha que some automaticamente
document.addEventListener("DOMContentLoaded", () => {
    const alertBox = document.querySelector('.alert-update');
    if (alertBox) {
        setTimeout(() => {
            alertBox.style.opacity = '0';
            alertBox.style.transition = 'opacity .6s ease';
            setTimeout(() => alertBox.remove(), 600);
        }, 4000);
    }
});
