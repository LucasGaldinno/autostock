const indices = {};

function calculateTotalEstoque() {
	let total = 0;

	const priceElements = document.querySelectorAll('.price');

	priceElements.forEach((element) => {
		const price = parseFloat(element.textContent.replace('R$ ', '').replace('.', '').replace(',', '.'));

		if (!isNaN(price)) {
			total += price;
		}
	});
	document.querySelector('.kpi-value').textContent = formatCurrency(total);
}

function formatCurrency(value) {
	return new Intl.NumberFormat('pt-BR', {
		style: 'currency',
		currency: 'BRL',
		minimumFractionDigits: 2,
		maximumFractionDigits: 2
	}).format(value);
}

window.onload = calculateTotalEstoque;


function nextImage(vehicleId) {
	const input = document.getElementById(`images-${vehicleId}`);

	if (!input) {
		console.error(`Input with id 'images-${vehicleId}' not found.`);
		return;
	}

	const urls = input.value.split(",");
	if (!indices[vehicleId]) indices[vehicleId] = 0;

	indices[vehicleId] = (indices[vehicleId] + 1) % urls.length;

	const img = document.getElementById(`img-${vehicleId}`);
	if (img) {
		img.src = "/uploads/" + urls[indices[vehicleId]];
	} else {
		console.error(`Image with id 'img-${vehicleId}' not found.`);
	}
}

function prevImage(vehicleId) {
	const input = document.getElementById(`images-${vehicleId}`);

	if (!input) {
		console.error(`Input with id 'images-${vehicleId}' not found.`);
		return;
	}

	const urls = input.value.split(",");
	if (!indices[vehicleId]) indices[vehicleId] = 0;

	indices[vehicleId] = (indices[vehicleId] - 1 + urls.length) % urls.length;

	const img = document.getElementById(`img-${vehicleId}`);
	if (img) {
		img.src = "/uploads/" + urls[indices[vehicleId]];
	} else {
		console.error(`Image with id 'img-${vehicleId}' not found.`);
	}
}

document.addEventListener("DOMContentLoaded", () => {

	const searchInput = document.querySelector(".search-input");
	const cards = document.querySelectorAll(".vehicle-card");

	if (!searchInput || cards.length === 0) return;

	searchInput.addEventListener("input", () => {
		const termo = searchInput.value.toLowerCase();

		cards.forEach(card => {

			const brandModel = card.querySelector("h4")?.textContent.toLowerCase() || "";
			const plate = card.dataset.plate?.toLowerCase() || "";

			const match =
				brandModel.includes(termo) ||
				plate.includes(termo);

			card.style.display = match ? "block" : "none";
		});
	});

	document.querySelectorAll(".km-value").forEach(el => {
		const num = parseInt(el.textContent);

		if (!isNaN(num)) {
			el.textContent = num.toLocaleString("pt-BR") + " km";
		}
	});
});

function confirmDeleteVehicle(event) {
    event.preventDefault();

    Swal.fire({
        title: "Você tem certeza de que deseja excluir este veículo?",
        text: "Esta ação removerá o veículo e não poderá ser desfeita!",
        icon: "warning",
        showCancelButton: true,
        confirmButtonColor: "#d33",
        cancelButtonColor: "#6c757d",
        confirmButtonText: "Sim, excluir",
        cancelButtonText: "Cancelar",
        reverseButtons: true,
    }).then((result) => {
        if (result.isConfirmed) {
            event.target.submit();
        }
    });

    return false;
}

let modalIndex = 0;
let modalImages = [];

function openImageModal(vehicleId) {
    const hiddenInput = document.getElementById(`images-${vehicleId}`);
    if (!hiddenInput) return;

    modalImages = hiddenInput.value.split(",");
    modalIndex = 0;

    updateModalImage();

    document.getElementById("imageModal").style.display = "block";
}

function updateModalImage() {
    const modalImg = document.getElementById("modalImage");
    modalImg.src = "/uploads/" + modalImages[modalIndex];
}

function closeImageModal() {
    document.getElementById("imageModal").style.display = "none";
}

function nextModalImage() {
    modalIndex = (modalIndex + 1) % modalImages.length;
    updateModalImage();
}

function prevModalImage() {
    modalIndex = (modalIndex - 1 + modalImages.length) % modalImages.length;
    updateModalImage();
}

window.onclick = function(e) {
    if (e.target.id === "imageModal") {
        closeImageModal();
    }
};

