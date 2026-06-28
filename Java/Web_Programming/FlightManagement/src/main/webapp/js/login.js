function validateLoginForm(event) {
    const errors = document.querySelectorAll(".text-danger");
    errors.forEach(span => span.textContent = "");
    let usernameInput = document.getElementById("username");
    let passwordInput = document.getElementById("password");
    let isValid = true;

    if (usernameInput.value.trim() === "") {
        usernameInput.value = "";
        document.getElementById("usernameError").textContent = "Το πεδίο είναι υποχρεωτικό.";
        isValid = false;
    }
    if (passwordInput.value.trim() === "") {
        passwordInput.value = "";
        document.getElementById("passwordError").textContent = "Το πεδίο είναι υποχρεωτικό.";
        isValid = false;
    }
    if (!isValid) {
        event.preventDefault();
        return false;
    }

    return true;
}
