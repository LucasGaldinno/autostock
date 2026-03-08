document.addEventListener("DOMContentLoaded", () => {

	const tel = document.getElementById("telefone");

	if (tel) {

	    const errorMsg = document.createElement("small");
	    errorMsg.className = "text-danger mt-1";
	    errorMsg.style.display = "none";
	    errorMsg.innerHTML = `<i class="fa-solid fa-circle-exclamation me-1"></i> Telefone inválido.`;
	    tel.insertAdjacentElement("afterend", errorMsg);

	    tel.addEventListener("input", () => {
	        let v = tel.value.replace(/\D/g, "");

	        if (v.length > 11) v = v.substring(0, 11);

	        if (v.length >= 11) {
	            tel.value = v.replace(/(\d{2})(\d{5})(\d{4})/, "($1) $2-$3");
	        } else if (v.length >= 7) {
	            tel.value = v.replace(/(\d{2})(\d{4})(\d{0,4})/, "($1) $2-$3");
	        } else if (v.length >= 3) {
	            tel.value = v.replace(/(\d{2})(\d{0,5})/, "($1) $2");
	        } else {
	            tel.value = v;
	        }

	        if (v.length < 10 || v.length > 11) {
	            errorMsg.style.display = "block";
	            tel.classList.add("is-invalid");
	        } else {
	            errorMsg.style.display = "none";
	            tel.classList.remove("is-invalid");
	        }
	    });
	}

    const cep = document.getElementById("cep");

    if (cep) {

        cep.addEventListener("input", () => {
            let v = cep.value.replace(/\D/g, "");
            if (v.length > 8) v = v.substring(0, 8);

            if (v.length >= 6)
                cep.value = v.replace(/(\d{5})(\d{0,3})/, "$1-$2");
            else
                cep.value = v;
        });

        cep.addEventListener("blur", async () => {
            let value = cep.value.replace(/\D/g, "");

            if (value.length !== 8) return;

            try {
                const res = await fetch(`https://viacep.com.br/ws/${value}/json/`);
                const data = await res.json();

                const logradouro = document.querySelector("[name='logradouro']");
                const bairro = document.querySelector("[name='bairro']");
                const cidade = document.querySelector("[name='cidade']");
                const ufSelect = document.querySelector("[name='uf']");

                if (data.erro) {
                    showCepError();
                    return;
                }

                if (logradouro) logradouro.value = data.logradouro || "";
                if (bairro) bairro.value = data.bairro || "";
                if (cidade) cidade.value = data.localidade || "";

				if (ufSelect && data.uf) {
				    const uf = data.uf.toUpperCase();

				    const optionExists = [...ufSelect.options].some(opt => opt.value === uf);

				    if (optionExists) {
				        ufSelect.value = uf;
				    } else {
				        console.warn("UF não existe no select:", uf);
				    }
				}

                clearCepError();

            } catch (e) {
                console.error("Erro ao buscar CEP:", e);
                showCepError();
            }
        });
    }

    function showCepError() {
        let msg = document.getElementById("cepErrorMsg");

        if (!msg) {
            const cepField = document.getElementById("cep");
            msg = document.createElement("small");
            msg.id = "cepErrorMsg";
            msg.className = "text-danger";
            msg.innerText = "CEP não encontrado.";
            cepField.insertAdjacentElement("afterend", msg);
        }
    }

    function clearCepError() {
        const msg = document.getElementById("cepErrorMsg");
        if (msg) msg.remove();
    }

    const cnpj = document.getElementById("cnpj");

    if (cnpj) {
        let v = cnpj.value.replace(/\D/g, "");

        if (v.length === 14) {
            cnpj.value = v.replace(
                /(\d{2})(\d{3})(\d{3})(\d{4})(\d{2})/,
                "$1.$2.$3/$4-$5"
            );
        }
    }

    const alert = document.getElementById("alertSuccess");

    if (alert) {
        setTimeout(() => {
            alert.style.transition = "all .5s ease";
            alert.style.opacity = "0";
            alert.style.transform = "translateY(-10px)";
        }, 3500);

        setTimeout(() => {
            alert.style.display = "none";
        }, 4200);
    }
});
