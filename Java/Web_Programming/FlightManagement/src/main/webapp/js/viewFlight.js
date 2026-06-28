const businessRowsLimit = 4;

function handleTicketTypeChange() {
    const type = document.getElementById("ticketType").value;
    const gridArea = document.getElementById("seatSelectionArea");
    const seatBtns = document.querySelectorAll(".grid-seat-btn");
    document.getElementById("selectedRow").value = "";
    document.getElementById("selectedCol").value = "";
    const visualDiv = document.getElementById("selectedSeatVisual");
    visualDiv.classList.add("d-none");
    seatBtns.forEach(b => b.classList.remove("btn-success"));
    if (type === "ECONOMY" || type === "") {
        gridArea.classList.add("d-none");
    } else {
        gridArea.classList.remove("d-none"); // Εμφάνιση πλέγματος για Normal/Business
        seatBtns.forEach(btn => {
            if (btn.classList.contains("btn-danger") || btn.disabled && !btn.classList.contains("btn-light")) {
                return;
            }

            const rowNum = parseInt(btn.getAttribute("data-row"));
            if (type === "NORMAL" && rowNum <= businessRowsLimit) {
                btn.disabled = true;
                btn.classList.add("btn-light");
            } else if (type === "BUSINESS" && rowNum > businessRowsLimit) {
                btn.disabled = true;
                btn.classList.add("btn-light");
            } else {
                btn.disabled = false;
                btn.classList.remove("btn-light");
            }
        });
    }
}

function validateBookingForm(event) {
    document.getElementById("ticketTypeError").textContent = "";
    const seatError = document.getElementById("seatError");
    if(seatError) seatError.textContent = "";
    const type = document.getElementById("ticketType").value;
    const row = document.getElementById("selectedRow").value;
    if (type === "") {
        document.getElementById("ticketTypeError").textContent = "Επέλεξε τύπο θέσης.";
        event.preventDefault();
        return false;
    }
    if ((type === "NORMAL" || type === "BUSINESS") && row === "") {
        if(seatError) seatError.textContent = "Επέλεξε μια διαθέσιμη θέση.";
        event.preventDefault();
        return false;
    }

    return true;
}
