document.addEventListener("DOMContentLoaded", () => {
	// ========= ELEMENTOS DE TELA =========
	const loginInput = document.getElementById("login");
	const loginErrorBox = document.getElementById("login-error");
	const senhaErrorBox = document.getElementById("senha-error");
	const senhaForm = document.getElementById("senha-form");
	const loginForm = document.getElementById("login-form");

	// ✅ Proteção extra (evita null pointer)
	if (!loginInput || !loginErrorBox || !senhaErrorBox || !loginForm || !senhaForm) {
		console.error("❌ Elementos do formulário não encontrados. Verifique IDs no HTML.");
		return;
	}

	// ========= MÁSCARA DE CNPJ =========
	let maskInstance = null;

	const aplicarMascaraCnpj = () => {
		if (!maskInstance) {
			maskInstance = IMask(loginInput, { mask: "00.000.000/0000-00" });
		}
	};

	const removerMascara = () => {
		if (maskInstance) {
			maskInstance.destroy();
			maskInstance = null;
		}
	};

	loginInput.addEventListener("input", () => {
		const value = loginInput.value;
		if (/^\d/.test(value)) aplicarMascaraCnpj(); // começa com número → CNPJ
		if (/[a-zA-Z@]/.test(value)) removerMascara(); // contém letras/@ → e-mail
	});

	// ========= TRATAMENTO DE PARÂMETROS =========
	const params = new URLSearchParams(window.location.search);
	const savedLogin = sessionStorage.getItem("login");

	if (params.has("error") && savedLogin) {
		let tentativas = params.get("tentativas") || "";
		let mensagem = "Senha incorreta. Tente novamente.";
		if (tentativas) mensagem += ` Você ainda tem ${tentativas} tentativa(s).`;
		senhaErrorBox.textContent = mensagem;
		senhaErrorBox.classList.remove("hide");

		loginInput.value = savedLogin;
		loginInput.readOnly = true;
		loginForm.querySelector("button").classList.add("hide");
		document.getElementById("login-hidden").value = savedLogin;
		senhaForm.classList.remove("hide");
	}

	if (params.has("locked")) {
		senhaErrorBox.textContent = "Conta bloqueada. Aguarde 5 minutos para tentar novamente.";
		senhaErrorBox.classList.remove("hide");
		senhaForm.classList.add("hide");
	}

	if (params.has("bloqueado")) {
		senhaErrorBox.textContent = "Esta conta está bloqueada. Tente novamente mais tarde.";
		senhaErrorBox.classList.remove("hide");
		senhaForm.classList.add("hide");
	}

	// ========= EVENTO DO BOTÃO "AVANÇAR" =========
	loginForm.addEventListener("submit", async (e) => {
		e.preventDefault();
		loginErrorBox.classList.add("hide");
		senhaErrorBox.classList.add("hide");

		let login = loginInput.value.trim();
		if (!login) {
			loginErrorBox.textContent = "Informe seu e-mail ou CNPJ.";
			loginErrorBox.classList.remove("hide");
			return;
		}

		// 🔧 Remove máscara só se for CNPJ
		if (maskInstance) {
			maskInstance.updateValue();
			login = maskInstance.unmaskedValue;
		} else {
			login = login.trim();
		}

		console.log("📤 Enviando login para validação:", login);

		try {
			const response = await fetch(`/api/validate/login?login=${encodeURIComponent(login)}`);

			if (!response.ok) {
				loginErrorBox.textContent = "Erro ao verificar usuário. Tente novamente.";
				loginErrorBox.classList.remove("hide");
				return;
			}

			const raw = await response.text();
			const text = raw ? raw.trim().toLowerCase() : "";
			console.log("🔍 Resposta bruta do servidor:", raw);

			let exists = false;
			try {
				const data = JSON.parse(raw);
				exists = Boolean(
					data === true ||
					data === "true" ||
					(typeof data === "object" &&
						(data.exists === true || data.status === "ok" || data.success === true))
				);
			} catch {
				exists = text === "true" || text === "ok";
			}

			console.log("✅ Interpretação final:", exists);

			// 🚨 Usuário não existe
			if (!exists) {
				loginErrorBox.textContent = "Usuário não encontrado na base de dados.";
				loginErrorBox.classList.remove("hide");
				senhaForm.classList.add("hide");
				return;
			}

			// ✅ Usuário existe → mostra etapa de senha
			loginErrorBox.classList.add("hide");
			sessionStorage.setItem("login", login);
			document.getElementById("login-hidden").value = login;
			loginInput.readOnly = true;
			loginForm.querySelector("button").classList.add("hide");
			senhaForm.classList.remove("hide");

		} catch (err) {
			console.error("❌ Erro ao validar login:", err);
			loginErrorBox.textContent = "Erro de conexão com o servidor. Tente novamente.";
			loginErrorBox.classList.remove("hide");
		}
	});


});
