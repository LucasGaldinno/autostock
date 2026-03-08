document.addEventListener("DOMContentLoaded", () => {

    const errorStep = /*[[${errorStep}]]*/ null;

    if (errorStep) {
        const stepNum = parseInt(errorStep) - 1;
        if (!isNaN(stepNum)) {
            currentStep = stepNum;
            showStep(currentStep);

            const firstAlert = document.querySelector('.alert-danger');
            if (firstAlert) {
                firstAlert.scrollIntoView({ behavior: 'smooth', block: 'center' });
            }
        }
    }
});

const steps = document.querySelectorAll('.step');
const circles = document.querySelectorAll('.circle');

let currentStep = 0;
updateIndicators();


function updateIndicators() {
    circles.forEach((circle, index) => {

        if (index < currentStep) {
            circle.className = "circle completed";
            circle.textContent = "✓";
        }
        else if (index === currentStep) {
            circle.className = "circle active";
            circle.textContent = circle.dataset.step;
        }
        else {
            circle.className = "circle upcoming";
            circle.textContent = circle.dataset.step;
        }
    });

    document.documentElement.style.setProperty("--progress", currentStep);
}

function showStep(n) {
    steps.forEach((step, i) => {
        step.classList.toggle("active", i === n);
    });

    updateIndicators();
}

const validateStep = (stepIndex) => {
    const inputs = steps[stepIndex].querySelectorAll('input[required]');
    let valid = true;

    inputs.forEach(input => {
        if (!input.value.trim()) {
            input.classList.add('is-invalid');
            valid = false;
        } else {
            input.classList.remove('is-invalid');
        }
    });

    if (stepIndex === 0) {
        const cnpjInput = document.getElementById("cnpj");
        const cnpjError = document.getElementById("cnpjError");
        const cnpj = cnpjInput.value.replace(/[^\d]/g, '');

        const invalid = (cnpj.length !== 14 || /^(\d)\1{13}$/.test(cnpj));

        if (invalid) {
            cnpjInput.classList.add("is-invalid");
            cnpjError.textContent = "CNPJ inválido. Verifique e tente novamente.";
            valid = false;
        } else {
            cnpjInput.classList.remove("is-invalid");
            cnpjError.textContent = "";
        }
    }

    return valid;
};

document.getElementById('next1').onclick = () => {
    const cnpjInput = document.getElementById("cnpj");
    cnpjInput.value = cnpjInput.value.replace(/[^\d]/g, '');

    if (validateStep(0)) {
        currentStep = 1;
        showStep(currentStep);
    }
};

document.getElementById('next2').onclick = () => {
    if (validateStep(1)) {
        currentStep = 2;
        showStep(currentStep);
    }
};

document.getElementById('back1').onclick = () => {
    currentStep = 0;
    showStep(currentStep);
};

document.getElementById('back2').onclick = () => {
    currentStep = 1;
    showStep(currentStep);
};


document.getElementById("cnpj").addEventListener("blur", async function () {
    const cnpjInput = this;
    const cnpjError = document.getElementById("cnpjError");
    const cnpj = cnpjInput.value.replace(/[^\d]/g, '');

    if (!cnpj) return;

    if (cnpj.length !== 14 || /^(\d)\1{13}$/.test(cnpj)) {
        cnpjInput.classList.add('is-invalid');
        cnpjError.textContent = "CNPJ inválido.";
        return;
    }

    cnpjInput.classList.remove("is-invalid");
    cnpjError.textContent = "";

    try {
        const response = await fetch(`/api/cnpj/${cnpj}`);

        if (!response.ok) {
            cnpjInput.classList.add('is-invalid');
            cnpjError.textContent = "Erro ao validar o CNPJ.";
            return;
        }

        const data = await response.json();

        if (data.situacao && data.situacao.toUpperCase() !== "ATIVA") {
            cnpjInput.classList.add('is-invalid');
            cnpjError.textContent = "CNPJ não está ativo.";
            return;
        }

        document.getElementById("razaoSocial").value = data.razao_social || '';
        document.getElementById("nomeFantasia").value = data.nome_fantasia || '';
        document.getElementById("cep").value = data.cep || '';
        document.getElementById("bairro").value = data.bairro || '';
        document.getElementById("uf").value = data.uf || '';
        document.getElementById("logradouro").value = data.logradouro || '';
        document.getElementById("cidade").value = data.municipio || data.cidade || '';

    } catch (err) {
        cnpjInput.classList.add('is-invalid');
        cnpjError.textContent = "Erro ao buscar CNPJ.";
    }
});


document.getElementById("cep").addEventListener("blur", function () {
    const cep = this.value.replace(/[^\d]/g, '');
    if (cep.length !== 8) return;

    fetch(`/api/cep/${cep}`)
        .then(res => res.json())
        .then(data => {
            document.getElementById("logradouro").value = data.logradouro || '';
            document.getElementById("bairro").value = data.bairro || '';
            document.getElementById("cidade").value = data.cidade || data.localidade || '';
            document.getElementById("uf").value = data.uf || '';
        })
        .catch(() => alert("Erro ao buscar CEP."));
});

const telefoneInput = document.getElementById("telefone");
const telefoneError = document.getElementById("telefoneError");

telefoneInput.addEventListener("input", function () {
    let v = this.value.replace(/\D/g, '');

    if (v.length > 2 && v.length <= 7)
        v = `(${v.slice(0, 2)}) ${v.slice(2)}`;
    else if (v.length > 7)
        v = `(${v.slice(0, 2)}) ${v.slice(2, 7)}-${v.slice(7, 11)}`;

    this.value = v;

    const valido = /^\(\d{2}\)\s?\d{5}-\d{4}$/.test(v);

    if (!valido && this.value.length >= 10) {
        this.classList.add("is-invalid");
        telefoneError.textContent = "Telefone inválido. Use (11) 99999-9999.";
    } else {
        this.classList.remove("is-invalid");
        telefoneError.textContent = "";
    }
});

setTimeout(() => {
    const alert = document.querySelector('.alert.alert-success');
    if (alert) {
        alert.classList.remove('show');
        alert.style.opacity = '0';
        setTimeout(() => alert.remove(), 600);
    }
}, 6000);

document.addEventListener("DOMContentLoaded", () => {
    const cnpjField = document.getElementById("cnpj");
    if (cnpjField) {
        IMask(cnpjField, { mask: "00.000.000/0000-00" });
    }
});

