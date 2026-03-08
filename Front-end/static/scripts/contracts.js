document.addEventListener("DOMContentLoaded", () => {

  const sel = document.getElementById('vehicleSelect');
  const out = document.getElementById('vehiclePreview');

  sel?.addEventListener('change', () => atualizarPreview());

  function atualizarPreview() {
      const opt = sel.selectedOptions[0];

      if (!opt || !opt.dataset.plate) {
          out.innerHTML = `<p class='text-muted m-0'>Selecione um veículo para ver os detalhes.</p>`;
          return;
      }

      const priceNumber = opt.dataset.price ? Number(opt.dataset.price) : 0;
      const price = priceNumber.toLocaleString("pt-BR", {
          style: "currency",
          currency: "BRL"
      });

	  const kmNumber = Number(opt.dataset.km || 0);
	  const kmFormatted = kmNumber.toLocaleString("pt-BR");
	  
	  out.innerHTML = `
	      <div class="vehicle-summary-grid">

	          <div>
	              <div class="vehicle-summary-item-label"><i class="fa-solid fa-flag"></i> Marca:</div>
	              <div class="vehicle-summary-item-value">${opt.dataset.brand}</div>
	          </div>

	          <div>
	              <div class="vehicle-summary-item-label"><i class="fa-solid fa-car-side"></i> Modelo:</div>
	              <div class="vehicle-summary-item-value">${opt.dataset.model}</div>
	          </div>

	          <div>
	              <div class="vehicle-summary-item-label"><i class="fa-solid fa-layer-group"></i> Versão:</div>
	              <div class="vehicle-summary-item-value">${opt.dataset.version}</div>
	          </div>

	          <div>
	              <div class="vehicle-summary-item-label"><i class="fa-solid fa-calendar"></i> Ano:</div>
	              <div class="vehicle-summary-item-value">${opt.dataset.year}</div>
	          </div>

	          <div>
	              <div class="vehicle-summary-item-label"><i class="fa-solid fa-id-card"></i> Placa:</div>
	              <div class="vehicle-summary-item-value">${opt.dataset.plate}</div>
	          </div>

	          <div>
	              <div class="vehicle-summary-item-label"><i class="fa-solid fa-palette"></i> Cor:</div>
	              <div class="vehicle-summary-item-value">${opt.dataset.color}</div>
	          </div>

	          <div>
	              <div class="vehicle-summary-item-label"><i class="fa-solid fa-road"></i> KM:</div>
	              <div class="vehicle-summary-item-value">${kmFormatted}</div>
	          </div>

	          <div>
	              <div class="vehicle-summary-item-label"><i class="fa-solid fa-money-bill-wave"></i> Preço de Compra:</div>
	              <div class="vehicle-summary-item-value text-primary">${price}</div>
	          </div>

	      </div>
	  `;
  }



  const cpfField = document.getElementById('cpfField');
  const cepField = document.getElementById('cepField');
  const phoneField = document.getElementById('phoneField');

  if (cpfField) IMask(cpfField, { mask: '000.000.000-00' });
  if (cepField) IMask(cepField, { mask: '00000-000' });
  if (phoneField) IMask(phoneField, { mask: '(00) 00000-0000' });

  const form = document.querySelector('form');

  form?.addEventListener('submit', () => {
    const btn = document.getElementById('submitBtn');
    btn.disabled = true;
    btn.innerHTML = '<i class="bi bi-hourglass-split"></i> Gerando contrato...';
  });

  const addressInput = document.getElementById('address');
  const neighborhoodInput = document.getElementById('neighborhood');
  const cityInput = document.getElementById('city');
  const ufSelect = document.getElementById('ufSelect');

  let tomUF = null;

  // Inicializa o TomSelect
  document.addEventListener("DOMContentLoaded", () => {
      tomUF = new TomSelect("#ufSelect", {
          create: false,
          sortField: {
              field: "text",
              direction: "asc"
          },
          placeholder: "UF"
      });
  });

  cepField?.addEventListener('blur', async () => {
      const cep = cepField.value.replace(/\D/g, '');
      if (cep.length !== 8) {
          alert('⚠️ O CEP precisa ter 8 dígitos.');
          return;
      }

      try {
          cepField.placeholder = "Buscando...";

          const response = await fetch(`https://viacep.com.br/ws/${cep}/json/`);
          if (!response.ok) throw new Error('Erro ao consultar CEP');

          const data = await response.json();

          if (data.erro) {
              alert('❌ CEP não encontrado.');
              return;
          }

          addressInput.value = data.logradouro || '';
          neighborhoodInput.value = data.bairro || '';
          cityInput.value = data.localidade || '';

          // Atualiza UF no TomSelect ou no select normal
          if (tomUF) {
              tomUF.setValue(data.uf);
          } else if (ufSelect) {
              ufSelect.value = data.uf;
              ufSelect.dispatchEvent(new Event("change"));
          }

      } catch (error) {
          alert('⚠️ Erro ao buscar CEP: ' + error.message);
      } finally {
          cepField.placeholder = "CEP";
      }
  });

});

document.addEventListener("DOMContentLoaded", () => {
    
    const priceInput = document.getElementById("salePrice");

    if (priceInput) {

        const mask = IMask(priceInput, {
            mask: Number,
            scale: 2,
            signed: false,
            thousandsSeparator: '.',
            padFractionalZeros: true,
            radix: ',',
            mapToRadix: ['.']
        });

        priceInput.addEventListener("keydown", (e) => {
            if (e.key === "-" || e.key === "+") {
                e.preventDefault();
            }
        });

        priceInput.addEventListener("paste", (e) => {
            const data = (e.clipboardData || window.clipboardData).getData("text");
            if (data.includes("-") || data.includes("+")) {
                e.preventDefault();
            }
        });

        priceInput.addEventListener("input", () => {
            priceInput.value = priceInput.value.replace(/-/g, "").replace(/\+/g, "");
        });
    }

});
