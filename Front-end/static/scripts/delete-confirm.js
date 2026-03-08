function confirmDelete(event, message = "Você tem certeza que deseja excluir este usuário?") {
    event.preventDefault();

    Swal.fire({
        title: message,
        text: "Esta ação não pode ser desfeita.",
        icon: "warning",
        iconHtml: '<i class="fa-solid fa-exclamation"></i>',
        customClass: {
            popup: "swal-popup",
            title: "swal-title",
            htmlContainer: "swal-text",
            confirmButton: "swal-confirm-btn",
            cancelButton: "swal-cancel-btn"
        },
        buttonsStyling: false,
        showCancelButton: true,
        confirmButtonText: "Sim, excluir",
        cancelButtonText: "Cancelar",
        reverseButtons: true,
    }).then(result => {
        if (result.isConfirmed) {
            event.target.submit();
        }
    });

    return false;
}
