document.addEventListener("DOMContentLoaded", () => {
    const toastElList = [].slice.call(document.querySelectorAll('.toast'));

    toastElList.forEach((toastEl) => {
        const toast = new bootstrap.Toast(toastEl, {
            delay: 3500,
            autohide: true
        });
        toast.show();
    });
});
