document.addEventListener("DOMContentLoaded", () => {

    const form = document.getElementById("editUserForm");

    const cnpjInput = document.getElementById("cnpj");
    const cepInput = document.getElementById("cep");
    const telInput = document.getElementById("telefone");

    const logradouro = document.getElementById("logradouro");
    const bairro = document.getElementById("bairro");
    const cidade = document.getElementById("cidade");
    const uf = document.getElementById("uf");

    /* ==================================================
       FUNÇÕES DE ERRO VISUAL
    ================================================== */
    function showError(input, errorElement) {
        input.classList.add("input-error");
        errorElement.style.display = "block";
    }

    function clearError(input, errorElement) {
        input.classList.remove("input-error");
        errorElement.style.display = "none";
    }

    /* ==================================================
       MÁSCARAS DOS CAMPOS
    ================================================== */

    // Máscara CNPJ
    cnpjInput.addEventListener("input", () => {
        let v = cnpjInput.value.replace(/\D/g, "");
        if (v.length > 14) v = v.slice(0, 14);

        v = v.replace(/^(\d{2})(\d)/, "$1.$2");
        v = v.replace(/^(\d{2})\.(\d{3})(\d)/, "$1.$2.$3");
        v = v.replace(/\.(\d{3})(\d)/, ".$1/$2");
        v = v.replace(/(\d{4})(\d)/, "$1-$2");

        cnpjInput.value = v;
    });

    // Máscara CEP
    cepInput.addEventListener("input", () => {
        let v = cepInput.value.replace(/\D/g, "");
        if (v.length > 8) v = v.slice(0, 8);

        if (v.length > 5)
            v = v.replace(/^(\d{5})(\d)/, "$1-$2");

        cepInput.value = v;
    });

    // Máscara Telefone
    telInput.addEventListener("input", () => {
        let v = telInput.value.replace(/\D/g, "");

        if (v.length > 11) v = v.slice(0, 11);

        if (v.length >= 2)
            v = `(${v.slice(0, 2)}) ${v.slice(2)}`;

        if (v.length >= 10)
            v = v.replace(/(\(\d{2}\)\s)(\d{5})(\d{4})/, "$1$2-$3");
        else if (v.length >= 9)
            v = v.replace(/(\(\d{2}\)\s)(\d{4})(\d{4})/, "$1$2-$3");

        telInput.value = v;
    });

    /* ==================================================
       VALIDAÇÕES
    ================================================== */

    const validarTelefone = () => {
        return /^\(\d{2}\)\s\d{4,5}-\d{4}$/.test(telInput.value);
    };

    const validarCEP = () => {
        return /^\d{5}-\d{3}$/.test(cepInput.value);
    };

    const validarCNPJ = () => {
        const cnpj = cnpjInput.value.replace(/\D/g, "");

        if (cnpj.length !== 14) return false;

        let tamanho = cnpj.length - 2;
        let numeros = cnpj.substring(0, tamanho);
        let digitos = cnpj.substring(tamanho);
        let soma = 0;
        let pos = tamanho - 7;

        for (let i = tamanho; i >= 1; i--) {
            soma += numeros.charAt(tamanho - i) * pos--;
            if (pos < 2) pos = 9;
        }

        let resultado = soma % 11 < 2 ? 0 : 11 - soma % 11;
        if (resultado != digitos.charAt(0)) return false;

        tamanho = tamanho + 1;
        numeros = cnpj.substring(0, tamanho);
        soma = 0;
        pos = tamanho - 7;

        for (let i = tamanho; i >= 1; i--) {
            soma += numeros.charAt(tamanho - i) * pos--;
            if (pos < 2) pos = 9;
        }

        resultado = soma % 11 < 2 ? 0 : 11 - soma % 11;
        return resultado == digitos.charAt(1);
    };

    /* ==================================================
       EXIBIR ERRO AO VIVO ENQUANTO DIGITA
    ================================================== */

    cnpjInput.addEventListener("input", () => {
        const error = document.getElementById("cnpj-error");
        validarCNPJ() ? clearError(cnpjInput, error) : showError(cnpjInput, error);
    });

    telInput.addEventListener("input", () => {
        const error = document.getElementById("telefone-error");
        validarTelefone() ? clearError(telInput, error) : showError(telInput, error);
    });

    cepInput.addEventListener("input", () => {
        const error = document.getElementById("cep-error");
        validarCEP() ? clearError(cepInput, error) : showError(cepInput, error);
    });

    /* ==================================================
       BUSCA VIA CEP
    ================================================== */
    cepInput.addEventListener("blur", async () => {

        if (!validarCEP()) return;

        let cep = cepInput.value.replace(/\D/g, "");

        try {
            const response = await fetch(`https://viacep.com.br/ws/${cep}/json/`);
            const data = await response.json();

            if (data.erro) {
                showError(cepInput, document.getElementById("cep-error"));
                return;
            }

            clearError(cepInput, document.getElementById("cep-error"));

            logradouro.value = data.logradouro || "";
            bairro.value = data.bairro || "";
            cidade.value = data.localidade || "";
            uf.value = data.uf || "";

        } catch (e) {
            console.error("Erro ao buscar CEP:", e);
        }
    });

    /* ==================================================
       BLOQUEIO DO SUBMIT SE TIVER ERRO
    ================================================== */

    form.addEventListener("submit", (e) => {

        let bloqueado = false;

        if (!validarCNPJ()) {
            showError(cnpjInput, document.getElementById("cnpj-error"));
            bloqueado = true;
        }

        if (!validarTelefone()) {
            showError(telInput, document.getElementById("telefone-error"));
            bloqueado = true;
        }

        if (!validarCEP()) {
            showError(cepInput, document.getElementById("cep-error"));
            bloqueado = true;
        }

        if (bloqueado) e.preventDefault();
    });

});
