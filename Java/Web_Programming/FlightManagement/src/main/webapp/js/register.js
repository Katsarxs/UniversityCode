function showRoleFields() {
    let role = document.getElementById("role").value;
    let clientFields = document.getElementById("clientFields");
    let employeeFields = document.getElementById("employeeFields");

    if (role === "CLIENT") {
        clientFields.classList.remove("d-none");
        employeeFields.classList.add("d-none");
    } else if (role === "FLIGHT_MANAGER" || role === "SYSTEM_MANAGER") {
        clientFields.classList.add("d-none");
        employeeFields.classList.remove("d-none");
    } else {
        clientFields.classList.add("d-none");
        employeeFields.classList.add("d-none");
    }
}

function validateRegisterForm(event) {
    const errors = document.querySelectorAll(".text-danger");
    errors.forEach(span => span.textContent = "");
    let isValid = true;
    let requiredFields = ["username", "email", "password", "fullname", "id_number"];
    requiredFields.forEach(id => {
        let input = document.getElementById(id);
        if (!input || input.value.trim() === "") {
            if (input) input.value = "";
            document.getElementById(id + "Error").textContent = "Το πεδίο είναι υποχρεωτικό.";
            isValid = false;
        }
    });

    let role = document.getElementById("role").value;
    if (role === "CLIENT") {
        let afm = document.getElementById("afm");
        let address = document.getElementById("home_address");

        if (afm.value.trim() === "") {
            afm.value = "";
            document.getElementById("afmError").textContent = "Το ΑΦΜ είναι υποχρεωτικό.";
            isValid = false;
        }
        if (address.value.trim() === "") {
            address.value = "";
            document.getElementById("home_addressError").textContent = "Η διεύθυνση είναι υποχρεωτική.";
            isValid = false;
        }
    } else if (role === "FLIGHT_MANAGER" || role === "SYSTEM_MANAGER") {
        let empCode = document.getElementById("employee_code");
        if (empCode.value.trim() === "") {
            empCode.value = "";
            document.getElementById("employee_codeError").textContent = "Ο κωδικός υπαλλήλου είναι υποχρεωτικός.";
            isValid = false;
        }
    }

    if (!isValid) {
        event.preventDefault();
        return false;
    }

    return true;
}
