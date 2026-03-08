document.addEventListener('DOMContentLoaded', () => {
  const fipeInput = document.getElementById('codigoFipe');
  if (!fipeInput) return;

  // Remove hífen ao sair do input
  fipeInput.addEventListener('blur', () => {
    fipeInput.value = fipeInput.value.replace(/-/g, '').trim();
  });

  // Remove hífen ao colar
  fipeInput.addEventListener('paste', () => {
    setTimeout(() => {
      fipeInput.value = fipeInput.value.replace(/-/g, '').trim();
    }, 0);
  });
});
