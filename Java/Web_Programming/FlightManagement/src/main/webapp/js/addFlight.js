function validateFlightForm(event) {
    const errors = document.querySelectorAll(".text-danger");
    errors.forEach(span => span.textContent = "");
    let flightNumber = document.getElementById("flightNumber");
    let airplane = document.getElementById("airplane");
    let departureDate = document.getElementById("departureDate");
    let departureTime = document.getElementById("departureTime");
    let totalSeats = document.getElementById("totalSeats");
    let rowsCount = document.getElementById("rowsCount");
    let seatsPerRow = document.getElementById("seatsPerRow");
    let rowsBusiness = document.getElementById("rowsBusiness");
    let isValid = true;

    // Κάνουμε validate όλα τα πεδία
    if (!flightNumber || flightNumber.value.trim() === "") {
        if (flightNumber) flightNumber.value = "";
        document.getElementById("flightNumberError").textContent = "Υποχρεωτικό πεδίο.";
        isValid = false;
    }
    if (!airplane || airplane.value.trim() === "") {
        if (airplane) airplane.value = "";
        document.getElementById("airplaneError").textContent = "Υποχρεωτικό πεδίο.";
        isValid = false;
    }
    if (!departureDate || departureDate.value.trim() === "") {
        document.getElementById("departureDateError").textContent = "Υποχρεωτικό πεδίο.";
        isValid = false;
    }
    if (!departureTime || departureTime.value.trim() === "") {
        document.getElementById("departureTimeError").textContent = "Υποχρεωτικό πεδίο.";
        isValid = false;
    }
    if (!totalSeats || totalSeats.value.trim() === "" || parseInt(totalSeats.value) <= 0) {
        if (totalSeats) totalSeats.value = "";
        document.getElementById("totalSeatsError").textContent = "Μη έγκυρος αριθμός.";
        isValid = false;
    }
    if (!rowsCount || rowsCount.value.trim() === "" || parseInt(rowsCount.value) <= 0) {
        if (rowsCount) rowsCount.value = "";
        document.getElementById("rowsCountError").textContent = "Μη έγκυρος αριθμός.";
        isValid = false;
    }
    if (!seatsPerRow || seatsPerRow.value.trim() === "" || parseInt(seatsPerRow.value) <= 0) {
        if (seatsPerRow) seatsPerRow.value = "";
        document.getElementById("seatsPerRowError").textContent = "Μη έγκυρος αριθμός.";
        isValid = false;
    }
    if (!rowsBusiness || rowsBusiness.value.trim() === "" || parseInt(rowsBusiness.value) < 0) {
        if (rowsBusiness) rowsBusiness.value = "";
        document.getElementById("rowsBusinessError").textContent = "Μη έγκυρος αριθμός.";
        isValid = false;
    }
    if (isValid && parseInt(rowsBusiness.value) > parseInt(rowsCount.value)) {
        document.getElementById("rowsBusinessError").textContent = "Οι business σειρές υπερβαίνουν τις συνολικές, άστοχο.";
        isValid = false;
    }
    if (!isValid) {
        event.preventDefault();
        return false;
    }

    return true;
}
