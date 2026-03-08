document.addEventListener("DOMContentLoaded", () => {

	// === Máscaras AutoNumeric ===
	new AutoNumeric('#mileage', {
		digitGroupSeparator: '.',
		decimalCharacter: ',',
		decimalPlaces: 0,
		unformatOnSubmit: true
	});

	new AutoNumeric('#purchasePrice', {
		digitGroupSeparator: '.',
		decimalCharacter: ',',
		decimalPlaces: 2,
		unformatOnSubmit: true
	});

	// === Consulta automática FIPE ===
	const FIPE_URL_BASE = /*[[@{/vehicles/fipe/}]]*/ "/vehicles/fipe/";
	const fipeCodeInput = document.getElementById("fipeCode");
	const fipeHidden = document.getElementById("fipeTable");
	const fipeDisplay = document.getElementById("fipeTableView");

	if (fipeCodeInput) {
		fipeCodeInput.addEventListener("blur", function() {
			const codigo = this.value.trim();
			if (!codigo) return;

			fipeDisplay.value = "Consultando...";
			fetch(FIPE_URL_BASE + encodeURIComponent(codigo))
				.then(res => {
					if (!res.ok) throw new Error("Erro FIPE " + res.status);
					return res.json();
				})
				.then(valor => {
					if (!isNaN(valor)) {
						fipeHidden.value = valor;
						fipeDisplay.value = Number(valor).toLocaleString("pt-BR", {
							style: "currency",
							currency: "BRL"
						});
					} else {
						throw new Error("Valor inválido");
					}
				})
				.catch(err => {
					alert("❌ Não foi possível consultar a FIPE. Verifique o código informado.");
					fipeHidden.value = "";
					fipeDisplay.value = "";
				});
		});
	}

	// === Múltiplas imagens ===
	let filesBuffer = [];

	window.addFiles = function(e) {
		const input = e.target;
		const novos = Array.from(input.files);
		filesBuffer = filesBuffer.concat(novos);

		const dt = new DataTransfer();
		filesBuffer.forEach(f => dt.items.add(f));
		input.files = dt.files;

		renderPreview();
	};

	window.removeFile = function(index) {
		filesBuffer.splice(index, 1);

		const dt = new DataTransfer();
		filesBuffer.forEach(f => dt.items.add(f));
		document.getElementById('files').files = dt.files;

		renderPreview();
	};

	function renderPreview() {
		const preview = document.getElementById('preview');
		preview.innerHTML = '';

		filesBuffer.forEach((f, idx) => {
			const url = URL.createObjectURL(f);

			const wrap = document.createElement('div');
			wrap.className = 'preview-item';

			const img = document.createElement('img');
			img.src = url;
			img.alt = f.name;

			const label = document.createElement('span');
			label.textContent = `Imagem ${idx + 1} — ${f.name}`;
			label.style.cssText = 'color:#ccc;font-size:0.9rem;';

			const btn = document.createElement('button');
			btn.type = 'button';
			btn.textContent = 'Remover';
			btn.className = 'btn btn-sm btn-danger';
			btn.onclick = () => removeFile(idx);

			wrap.append(img, label, btn);
			preview.appendChild(wrap);
		});
	}
});