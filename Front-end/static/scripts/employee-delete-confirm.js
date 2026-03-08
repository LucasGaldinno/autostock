function confirmDeleteEmployee(event) {
    event.preventDefault();

    Swal.fire({
        title: "Você tem certeza que deseja exluir este funcionário?",
        text: "Esta ação não pode ser desfeita.",
        icon: "warning",
        showCancelButton: true,
        confirmButtonColor: "#d33",
        cancelButtonColor: "#6c757d",
        confirmButtonText: "Sim, excluir",
        cancelButtonText: "Cancelar",
        reverseButtons: true,
    }).then((result) => {
        if (result.isConfirmed) {
            event.target.submit();
        }
    });

    return false;
}
