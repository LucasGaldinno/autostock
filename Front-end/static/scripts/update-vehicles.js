document.addEventListener("DOMContentLoaded", () => {
	const fipeCode = document.getElementById("fipeCode");
	const fipeHidden = document.getElementById("fipeTable");
	const fipeView = document.getElementById("fipeTableView");
	if (fipeCode) {
		fipeCode.addEventListener("blur", () => {
			const code = fipeCode.value.trim();
			if (!code) return;
			fipeView.value = "Consultando...";
			fetch(`/vehicles/fipe/${code}`)
				.then(res => res.ok ? res.json() : Promise.reject(res))
				.then(valor => {
					fipeHidden.value = valor;
					fipeView.value = new Intl.NumberFormat("pt-BR", { style: "currency", currency: "BRL" }).format(valor);
				})
				.catch(() => {
					alert("Não foi possível consultar a FIPE. Verifique o código informado.");
					fipeHidden.value = "";
					fipeView.value = "";
				});
		});
	}

	new AutoNumeric('#mileage', { digitGroupSeparator: '.', decimalCharacter: ',', decimalPlaces: 0, unformatOnSubmit: true });
	new AutoNumeric('#purchasePrice', { digitGroupSeparator: '.', decimalCharacter: ',', decimalPlaces: 2, unformatOnSubmit: true });
	new AutoNumeric('#expenses', { digitGroupSeparator: '.', decimalCharacter: ',', decimalPlaces: 2, unformatOnSubmit: true });

	let filesBuffer = [];

	window.addFiles = function(e) {
		const novos = Array.from(e.target.files);
		filesBuffer = filesBuffer.concat(novos);
		const dt = new DataTransfer();
		filesBuffer.forEach(f => dt.items.add(f));
		e.target.files = dt.files;
		renderPreview();
	};

	window.removeFile = function(index) {
		filesBuffer.splice(index, 1);
		const dt = new DataTransfer();
		filesBuffer.forEach(f => dt.items.add(f));
		document.getElementById("files").files = dt.files;
		renderPreview();
	};

	function renderPreview() {
		const preview = document.getElementById("preview");
		preview.innerHTML = "";
		filesBuffer.forEach((f, idx) => {
			const url = URL.createObjectURL(f);
			const wrap = document.createElement("div");
			wrap.className = "preview-item d-flex align-items-center gap-2";
			wrap.innerHTML = `
        <img src="${url}" alt="${f.name}" style="width:100px;height:70px;object-fit:cover;border-radius:4px;">
        <span style="color:#ccc;font-size:0.9rem;">Imagem ${idx + 1} — ${f.name}</span>
        <button type="button" class="btn btn-sm btn-danger" onclick="removeFile(${idx})">Remover</button>
      `;
			preview.appendChild(wrap);
		});
	}

	function atualizarRestricaoRemocao() {
	    const imageBlocks = document.querySelectorAll(".image-block");
	    const removeButtons = document.querySelectorAll(".image-block form button");

	    if (imageBlocks.length <= 1) {
	        removeButtons.forEach(btn => {
	            btn.disabled = true;
	            btn.classList.add("disabled");
	            btn.style.opacity = "0.6";
	            btn.style.cursor = "not-allowed";
	            btn.title = "O veículo precisa ter pelo menos uma imagem.";
	        });
	    } else {
	        removeButtons.forEach(btn => {
	            btn.disabled = false;
	            btn.classList.remove("disabled");
	            btn.style.opacity = "1";
	            btn.style.cursor = "pointer";
	            btn.title = "";
	        });
	    }
	}

	atualizarRestricaoRemocao();

	document.querySelectorAll(".image-block form").forEach(form => {
	    form.addEventListener("submit", (e) => {
	        const qtd = document.querySelectorAll(".image-block").length;
	        if (qtd <= 1) {
	            e.preventDefault();
	            alert("O veículo precisa ter pelo menos uma imagem.");
	        }
	    });
	});
});