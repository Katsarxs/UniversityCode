function validateProfileForm(event) {
    const errors = document.querySelectorAll(".text-danger");
    errors.forEach(span => span.textContent = "");
    const emailInput = document.getElementById("email");
    const fullnameInput = document.getElementById("fullname");
    const afmInput = document.getElementById("afm");
    const addressInput = document.getElementById("homeAddress");
    const employeeCodeInput = document.getElementById("employeeCode");
    let isValid = true;

    if (!fullnameInput || fullnameInput.value.trim() === "") {
        if (fullnameInput) fullnameInput.value = "";
        document.getElementById("fullnameError").textContent = "Το πεδίο είναι υποχρεωτικό.";
        isValid = false;
    }
    if (!emailInput || emailInput.value.trim() === "") {
        if (emailInput) emailInput.value = "";
        document.getElementById("emailError").textContent = "Το πεδίο είναι υποχρεωτικό.";
        isValid = false;
    }
    if (afmInput && afmInput.value.trim() === "") {
        afmInput.value = "";
        document.getElementById("afmError").textContent = "Το πεδίο είναι υποχρεωτικό..";
        isValid = false;
    }
    if (addressInput && addressInput.value.trim() === "") {
        addressInput.value = "";
        document.getElementById("homeAddressError").textContent = "Το πεδίο είναι υποχρεωτικό.";
        isValid = false;
    }
    if (employeeCodeInput && !employeeCodeInput.readOnly && employeeCodeInput.value.trim() === "") {
        employeeCodeInput.value = "";
        document.getElementById("employeeCodeError").textContent = "Το πεδίο είναι υποχρεωτικό.";
        isValid = false;
    }
    if (!isValid) {
        event.preventDefault();
        return false;
    }
    return true;
}

// Φωτίζει το σωστό κουμπί στο sidebar
const links = document.querySelectorAll('.sidebar .nav-link');
links.forEach(link => {
    if (link.textContent.includes('Ο λογαριασμός σου')) {
        link.classList.add('active', 'fw-semibold');
        link.classList.remove('text-white-50');
    }
});
