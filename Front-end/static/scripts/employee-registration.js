document.addEventListener("DOMContentLoaded", () => {

    const cpf = document.querySelector('#cpf');
    const phone = document.querySelector('#phone');
    const cep = document.querySelector('#cep');
    const rg = document.querySelector('[name="rg"]');

    if (cpf) {
        cpf.addEventListener("input", () => {
            cpf.value = cpf.value
                .replace(/\D/g, "")
                .replace(/(\d{3})(\d)/, "$1.$2")
                .replace(/(\d{3})(\d)/, "$1.$2")
                .replace(/(\d{3})(\d{1,2})$/, "$1-$2")
                .substring(0, 14);
        });
    }

    if (phone) {
        phone.addEventListener("input", () => {
            phone.value = phone.value
                .replace(/\D/g, "")
                .replace(/(\d{2})(\d)/, "($1) $2")
                .replace(/(\d{5})(\d)/, "$1-$2")
                .substring(0, 15);
        });
    }

    if (rg) {
        rg.addEventListener("input", () => {
            rg.value = rg.value
                .replace(/\D/g, "")
                .replace(/(\d{2})(\d)/, "$1.$2")
                .replace(/(\d{3})(\d)/, "$1.$2")
                .replace(/(\d{3})(\d{1})$/, "$1-$2")
                .substring(0, 12);
        });
    }

    if (cep) {
        cep.addEventListener("input", () => {
            cep.value = cep.value.replace(/\D/g, "").substring(0, 8);
        });

        cep.addEventListener("blur", async () => {
            const rawCep = cep.value;

            if (rawCep.length !== 8) return;

            try {
                const response = await fetch(`https://viacep.com.br/ws/${rawCep}/json/`);
                const data = await response.json();

                if (data.erro) {
                    alert("CEP não encontrado.");
                    return;
                }

                document.querySelector("[name='street']").value = data.logradouro || "";
                document.querySelector("[name='district']").value = data.bairro || "";
                document.querySelector("[name='city']").value = data.localidade || "";
                document.querySelector("[name='state']").value = data.uf || "";

            } catch (e) {
                console.error("Erro ao consultar CEP:", e);
            }
        });
    }

});
