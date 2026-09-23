// SecureBank - client-side enhancements

document.addEventListener('DOMContentLoaded', function () {
    // Auto-dismiss alert messages after 4 seconds
    document.querySelectorAll('.alert').forEach(function (alertEl) {
        setTimeout(function () {
            alertEl.style.transition = 'opacity 0.5s ease';
            alertEl.style.opacity = '0';
            setTimeout(function () { alertEl.remove(); }, 500);
        }, 4000);
    });

    // Prevent negative or zero amounts from being submitted client-side
    document.querySelectorAll('input[type="number"]').forEach(function (input) {
        input.addEventListener('input', function () {
            if (input.value !== '' && Number(input.value) <= 0) {
                input.setCustomValidity('Amount must be greater than zero');
            } else {
                input.setCustomValidity('');
            }
        });
    });
});
