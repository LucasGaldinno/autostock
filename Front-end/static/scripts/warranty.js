document.addEventListener("DOMContentLoaded", () => {

    const activeCount = parseInt(document.getElementById("activeCount")?.textContent || 0);
    const expiringCount = parseInt(document.getElementById("expiringCount")?.textContent || 0);
    const expiredCount = parseInt(document.getElementById("expiredCount")?.textContent || 0);

    console.log("Garantias Ativas:", activeCount);
    console.log("Expirando em 30 dias:", expiringCount);
    console.log("Expiradas:", expiredCount);

    const warrantyRows = document.querySelectorAll("table.custom-table tbody tr");

    warrantyRows.forEach(row => {
        const cols = row.querySelectorAll("td");

        if (cols.length >= 8) {
            const warrantyEndDate = cols[5].textContent.trim(); 

            const endDate = new Date(warrantyEndDate.split("/").reverse().join("-")); 

            const currentDate = new Date();

            if (endDate < currentDate) {
                cols[6].textContent = "Expirada";  
                row.style.backgroundColor = "#ffeeee";  
            } else if (endDate - currentDate <= 30 * 24 * 60 * 60 * 1000) {
                cols[6].textContent = "Expirando em 30 dias";  
                row.style.backgroundColor = "#ffffcc"; 
            } else {
                cols[6].textContent = "Ativa"; 
                row.style.backgroundColor = "#ffffff"; 
            }
        }
    });

});
