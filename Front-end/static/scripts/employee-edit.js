document.addEventListener("DOMContentLoaded", () => {

    const cepInput = document.querySelector("input[name='cep']");
    const logradouroInput = document.querySelector("input[name='logradouro']");
    const numeroInput = document.querySelector("input[name='numero']");
    const complementoInput = document.querySelector("input[name='complemento']");
    const bairroInput = document.querySelector("input[name='bairro']");
    const cidadeInput = document.querySelector("input[name='cidade']");
    const ufInput = document.querySelector("input[name='uf']");
    const phoneInput = document.querySelector("input[name='phone']");

    // -----------------------------
    // MÁSCARA DE TELEFONE (opcional)
    // -----------------------------
    if (phoneInput) {
        phoneInput.addEventListener("input", () => {
            phoneInput.value = phoneInput.value
                .replace(/\D/g, "")
                .replace(/(\d{2})(\d)/, "($1) $2")
                .replace(/(\d{5})(\d)/, "$1-$2")
                .substring(0, 15);
        });
    }

    // -----------------------------
    // MÁSCARA DE CEP
    // -----------------------------
    cepInput.addEventListener("input", () => {
        cepInput.value = cepInput.value
            .replace(/\D/g, "")
            .replace(/(\d{5})(\d)/, "$1-$2")
            .substring(0, 9);
    });

    // -----------------------------
    // FUNÇÃO PARA LIMPAR CAMPOS
    // -----------------------------
    function limparEndereco() {
        logradouroInput.value = "";
        bairroInput.value = "";
        cidadeInput.value = "";
        ufInput.value = "";
    }

    // -----------------------------
    // BUSCA CEP NO BACKEND
    // -----------------------------
    cepInput.addEventListener("blur", async () => {
        let cep = cepInput.value.replace(/\D/g, "");

        if (cep.length !== 8) {
            limparEndereco();
            return;
        }

        try {
            const response = await fetch(`/api/cep/${cep}`);

            if (!response.ok) {
                alert("Erro ao consultar CEP.");
                limparEndereco();
                return;
            }

            const data = await response.json();

            if (data.erro) {
                alert("CEP não encontrado.");
                limparEndereco();
                return;
            }

            // Preenche os campos automaticamente
            logradouroInput.value = data.logradouro || "";
            bairroInput.value = data.bairro || "";
            cidadeInput.value = data.localidade || "";
            ufInput.value = data.uf || "";

            // Move o foco para número
            numeroInput.focus();

        } catch (e) {
            console.error("Erro ao buscar CEP:", e);
            limparEndereco();
        }
    });

});
