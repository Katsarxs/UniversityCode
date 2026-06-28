const flightDataEl = document.getElementById('flightData');
const flightRowsBusiness = flightDataEl ? parseInt(flightDataEl.dataset.businessRows) : 0;

// Δέσμευση ή αλλαγή Θέσης ajax
function selectSeat(bId, row, column, mode) {
    const action = (mode === 'reserve') ? 'reserveSeat' : 'changeSeat';
    const url = `BookingServlet?action=${action}&id=${bId}&row=${row}&column=${column}`;
    showAlert('Περίμενε', 'info');

    fetch(url, {method: 'POST'})
        .then(response => response.json())
        .then(data => {
            if (data.status === 'success') {
                showAlert(data.message, 'success');
                document.getElementById('currentSeatInfo').textContent = `Σειρά ${row}, Θέση ${column}`;
                document.getElementById('btnUnreserve').classList.remove('d-none');

                updateGridUI(row, column, bId);
            } else {
                showAlert(data.message, 'danger');
            }
        })
        .catch(error => {
            showAlert('Παρουσιάστηκε σφάλμα κατά την επικοινωνία με τον server.', 'danger');
            console.error(error);
        });
}

// Αποδέσμευση Θέσης ajax
function unreserveSeat(bId) {
    const url = `BookingServlet?action=unreserveSeat&id=${bId}`;
    showAlert('Περίμενε', 'info');

    fetch(url, {method: 'GET'})
        .then(response => response.json())
        .then(data => {
            if (data.status === 'success') {
                showAlert(data.message, 'success');
                document.getElementById('currentSeatInfo').textContent = 'Δεν έχει επιλεγεί';
                document.getElementById('btnUnreserve').classList.add('d-none');

                setTimeout(() => {
                    window.location.reload();
                }, 1000);
            } else {
                showAlert(data.message, 'danger');
            }
        })
        .catch(() => {
            showAlert('Σφάλμα με server.', 'danger');
        });
}

// Βοηθητική συνάρτηση εμφάνισης μηνυμάτων
function showAlert(message, type) {
    const alertDiv = document.getElementById('ajaxAlert');
    if (alertDiv) {
        alertDiv.className = `alert alert-${type} fw-bold shadow-sm mb-4`;
        alertDiv.textContent = message;
        alertDiv.classList.remove('d-none');
    }
}

// Δυναμική ενημέρωση των των κουμπιών
function updateGridUI(activeRow, activeCol, bId) {
    const seats = document.querySelectorAll('.plane-grid .seat-btn');
    seats.forEach(btn => {
        const r = parseInt(btn.getAttribute('data-row'));
        const c = parseInt(btn.getAttribute('data-col'));
        if (btn.classList.contains('btn-danger')) {
            return;
        }
        if (r === activeRow && c === activeCol) {
            btn.className = 'btn btn-success seat-btn';
            btn.setAttribute('onclick', `selectSeat(${bId}, ${r}, ${c}, 'change')`);
        } else {
            if (r <= flightRowsBusiness) {
                btn.className = 'btn btn-outline-warning text-dark seat-btn';
            } else {
                btn.className = 'btn btn-outline-secondary seat-btn';
            }
            btn.setAttribute('onclick', `selectSeat(${bId}, ${r}, ${c}, 'change')`);
        }
    });
}

// Φωτίζει το σωστό κουμπί στο sidebar
const links = document.querySelectorAll('.sidebar .nav-link');
links.forEach(link => {
    if (link.textContent.includes('Οι κρατήσεις σου')) {
        link.classList.add('active', 'fw-semibold');
        link.classList.remove('text-white-50');
    }
});
