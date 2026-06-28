function validateEditForm(event) {
    const errors = document.querySelectorAll(".text-danger");
    errors.forEach(span => span.textContent = "");
    let email = document.getElementById("email");
    let fullname = document.getElementById("fullname");
    let idNumber = document.getElementById("idNumber");
    let afm = document.getElementById("afm");
    let homeAddress = document.getElementById("homeAddress");
    let employeeCode = document.getElementById("employeeCode");
    let isValid = true;

    if (!email || email.value.trim() === "") {
        if (email) email.value = "";
        document.getElementById("emailError").textContent = "Το πεδίο είναι υποχρεωτικό.";
        isValid = false;
    }
    if (!fullname || fullname.value.trim() === "") {
        if (fullname) fullname.value = "";
        document.getElementById("fullnameError").textContent = "Το πεδίο είναι υποχρεωτικό.";
        isValid = false;
    }
    if (!idNumber || idNumber.value.trim() === "") {
        if (idNumber) idNumber.value = "";
        document.getElementById("idNumberError").textContent = "Το πεδίο είναι υποχρεωτικό.";
        isValid = false;
    }
    if (afm && afm.value.trim() === "") {
        afm.value = "";
        document.getElementById("afmError").textContent = "Το πεδίο είναι υποχρεωτικό.";
        isValid = false;
    }
    if (homeAddress && homeAddress.value.trim() === "") {
        homeAddress.value = "";
        document.getElementById("homeAddressError").textContent = "Το πεδίο είναι υποχρεωτικό.";
        isValid = false;
    }
    if (employeeCode && employeeCode.value.trim() === "") {
        employeeCode.value = "";
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
    if (link.textContent.includes('Διαχείριση χρηστών')) {
        link.classList.add('active', 'fw-semibold');
        link.classList.remove('text-white-50');
    }
});