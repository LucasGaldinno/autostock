const inputs = document.querySelectorAll('.code-inputs input');
  const hiddenInput = document.getElementById('codeValue');
  const form = document.getElementById('codeForm');

  // Input fantasma para autofill
  const ghost = document.createElement('input');
  ghost.type = 'text';
  ghost.inputMode = 'numeric';
  ghost.autocomplete = 'one-time-code';
  ghost.style.position = 'absolute';
  ghost.style.left = '-9999px';
  ghost.setAttribute("aria-hidden", "true"); // <-- CORRIGIDO
  document.body.appendChild(ghost);

  const montarCodigo = () =>
    Array.from(inputs).map(i => (i.value || '').replace(/\D/g, '')).join('');

  const setCodeAndMaybeSubmit = () => {
    hiddenInput.value = montarCodigo();
    if (hiddenInput.value.length === inputs.length) {
      form.requestSubmit();
    }
  };

  inputs.forEach((input, index) => {

    input.addEventListener('keypress', (e) => {
      if (!/[0-9]/.test(e.key)) e.preventDefault();
    });

    input.addEventListener('input', () => {
      input.value = input.value.replace(/\D/g, '').slice(0, 1);

      if (input.value && index < inputs.length - 1) {
        inputs[index + 1].focus();
        inputs[index + 1].select();
      }
      setCodeAndMaybeSubmit();
    });

    input.addEventListener('keydown', (e) => {
      if (e.key === 'Backspace' && !input.value && index > 0) {
        inputs[index - 1].focus();
        inputs[index - 1].select();
      }
    });

    input.addEventListener('paste', (e) => {
      e.preventDefault();
      const pasted = (e.clipboardData || window.clipboardData)
        .getData('text')
        .replace(/\D/g, '');

      if (!pasted) return;

      if (pasted.length >= inputs.length) {
        for (let i = 0; i < inputs.length; i++) {
          inputs[i].value = pasted[i] || '';
        }
        hiddenInput.value = montarCodigo();
        return;
      }

      let p = 0;
      for (let i = index; i < inputs.length && p < pasted.length; i++, p++) {
        inputs[i].value = pasted[p];
      }
      inputs[Math.min(index + pasted.length, inputs.length - 1)].focus();
      hiddenInput.value = montarCodigo();
    });
  });

  const pasteTargets = [document.querySelector('.code-inputs'), form, document.body, ghost];

  pasteTargets.forEach(t => {
    t.addEventListener('paste', (e) => {
      const pasted = (e.clipboardData || window.clipboardData).getData('text') || '';
      const digits = pasted.replace(/\D/g, '');
      if (!digits) return;

      e.preventDefault();
      for (let i = 0; i < inputs.length; i++) {
        inputs[i].value = digits[i] || '';
      }
      hiddenInput.value = montarCodigo();
      inputs[Math.min(digits.length, inputs.length) - 1]?.focus();
    });
  });

  ghost.addEventListener('input', () => {
    const digits = (ghost.value || '').replace(/\D/g, '');
    if (digits.length) {
      for (let i = 0; i < inputs.length; i++) {
        inputs[i].value = digits[i] || '';
      }
      hiddenInput.value = montarCodigo();
      inputs[inputs.length - 1].focus();
    }
  });

  form.addEventListener('submit', (e) => {
    hiddenInput.value = montarCodigo();
    if (!/^\d{6}$/.test(hiddenInput.value)) {
      e.preventDefault();
      alert('Digite o código completo (6 dígitos).');
      const firstEmpty = Array.from(inputs).find(i => !i.value);
      (firstEmpty || inputs[0]).focus();
    }
  });

  inputs[0].focus();