const container = document.querySelector(".container");
const pwShowHide = document.querySelectorAll(".showHidePw");
const pwFields = document.querySelectorAll(".password");

// js para mostrar/ocultar a senha e alterar o ícone
pwShowHide.forEach(eyeIcon => {
	eyeIcon.addEventListener("click", () => {
		pwFields.forEach(pwField => {
			if (pwField.type === "password") {
				pwField.type = "text";
				pwShowHide.forEach(icon => {
					icon.classList.replace("uil-eye-slash", "uil-eye");
				});
			} else {
				pwField.type = "password";
				pwShowHide.forEach(icon => {
					icon.classList.replace("uil-eye", "uil-eye-slash");
				});
			}
		});
	});
});

function checkConfirmPassword() {
	const password = document.querySelector('input[name=password]');
	const confirm = document.querySelector('input[name=confirm]');
	if (confirm.value === password.value) {
		confirm.setCustomValidity('');
	} else {
		confirm.setCustomValidity('As senhas não correspondem');
	}
}
