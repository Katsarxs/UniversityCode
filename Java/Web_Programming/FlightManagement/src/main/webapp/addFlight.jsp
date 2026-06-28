<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<!-- Έλεγχος session και ρόλου -->
<c:if test="${empty sessionScope.user || sessionScope.user.role != 'FLIGHT_MANAGER'}">
    <c:redirect url="index.jsp"/>
</c:if>

<!DOCTYPE html>
<html lang="el">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Καταχώρηση Νέας Πτήσης</title>
    <!-- Εικονίδιο σελίδας -->
    <link rel="icon" href="images/logo.ico">
    <!-- Bootstrap 5 CSS -->
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.8/dist/css/bootstrap.min.css" rel="stylesheet"
          integrity="sha384-sRIl4kxILFvY47J16cr9ZwB07vP4J8+LH7qKQnuqkuIAvNWLzeN8tE5YBujZqJLB" crossorigin="anonymous">
    <!-- Δικό μου CSS -->
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/style.css">
</head>
<body>

<div class="container-fluid">
    <div class="row">
        <!-- Εισαγωγή του sidebar -->
        <jsp:include page="sidebar.jsp"/>
        <main class="col-md-9 ms-sm-auto col-lg-10 px-md-4 bg-light min-vh-100">
            <!-- Εισαγωγή του navigation bar -->
            <jsp:include page="navigationBar.jsp"/>

            <div class="d-flex justify-content-between align-items-center mb-4">
                <h2 class="fw-bold text-secondary">Καταχώρηση νέας πτήσης</h2>
                <a href="FlightServlet?action=search" class="btn btn-outline-secondary fw-bold">
                    <i class="fa-solid fa-arrow-left me-2"></i>Επιστροφή
                </a>
            </div>

            <!-- Μηνύματα στο πλανω μέρος από servlets-->
            <c:if test="${not empty errorMessage}">
                <div class="alert alert-danger shadow-sm fw-bold mb-4">${errorMessage}</div>
            </c:if>

            <!-- Φόρμα πτήσης -->
            <div class="card border-0 shadow-sm p-4 mb-5" style="max-width: 850px;">
                <form id="addFlightForm" action="FlightServlet" method="POST"
                      onsubmit="return validateFlightForm(event)">
                    <input type="hidden" name="action" value="insert">

                    <div class="row">
                        <!-- Αριθμός πτήσης -->
                        <div class="col-md-6 mb-3">
                            <label for="flightNumber" class="form-label fw-semibold text-muted">Αριθμός πτήσης</label>
                            <input type="text" id="flightNumber" name="flight_number" class="form-control">
                            <span id="flightNumberError" class="text-danger small fw-bold mt-1 d-block"></span>
                        </div>
                        <!-- Μοντέλο αεροπλάνου -->
                        <div class="col-md-6 mb-3">
                            <label for="airplane" class="form-label fw-semibold text-muted">Μοντέλο αεροπλάνου</label>
                            <input type="text" id="airplane" name="airplane" class="form-control">
                            <span id="airplaneError" class="text-danger small fw-bold mt-1 d-block"></span>
                        </div>
                    </div>

                    <div class="row">
                        <!-- Ώρα πτήσης -->
                        <div class="col-md-6 mb-3">
                            <label for="departureDate" class="form-label fw-semibold text-muted">Ημερομηνία
                                πτήσης</label>
                            <input type="date" id="departureDate" name="departure_date" class="form-control">
                            <span id="departureDateError" class="text-danger small fw-bold mt-1 d-block"></span>
                        </div>
                        <!-- Ημερομηνία πτήσης -->
                        <div class="col-md-6 mb-3">
                            <label for="departureTime" class="form-label fw-semibold text-muted">Ώρα πτήσης</label>
                            <input type="time" id="departureTime" name="departure_time" class="form-control">
                            <span id="departureTimeError" class="text-danger small fw-bold mt-1 d-block"></span>
                        </div>
                    </div>

                    <!-- Διάταξη Θέσεων αεροπλάνου-->
                    <div class="row border-top pt-3 mt-2">
                        <h5 class="text-muted mb-3"><i class="fa-solid fa-chair me-2"></i>Διάταξη Θέσεων αεροπλάνου</h5>
                        <!-- Θέσεις-->
                        <div class="col-md-4 mb-3">
                            <label for="totalSeats" class="form-label fw-semibold text-muted">Αριθμός Θέσεων</label>
                            <input type="number" id="totalSeats" name="seats" class="form-control" min="1">
                            <span id="totalSeatsError" class="text-danger small fw-bold mt-1 d-block"></span>
                        </div>
                        <!-- Σειρές-->
                        <div class="col-md-4 mb-3">
                            <label for="rowsCount" class="form-label fw-semibold text-muted">Αριθμός σειρών</label>
                            <input type="number" id="rowsCount" name="rows" class="form-control" min="1">
                            <span id="rowsCountError" class="text-danger small fw-bold mt-1 d-block"></span>
                        </div>
                        <!-- Θέσεις ανά σειρά-->
                        <div class="col-md-4 mb-3">
                            <label for="seatsPerRow" class="form-label fw-semibold text-muted">Θέσεις ανά σειρά</label>
                            <input type="number" id="seatsPerRow" name="seats_row" class="form-control" min="1">
                            <span id="seatsPerRowError" class="text-danger small fw-bold mt-1 d-block"></span>
                        </div>
                    </div>

                    <div class="row">
                        <!-- Business σειρές -->
                        <div class="col-md-4 mb-3">
                            <label for="rowsBusiness" class="form-label fw-semibold text-muted">Αριθμός business
                                σειρών</label>
                            <input type="number" id="rowsBusiness" name="rows_business" class="form-control" min="0">
                            <span id="rowsBusinessError" class="text-danger small fw-bold mt-1 d-block"></span>
                        </div>
                    </div>

                    <div class="mt-4 text-end">
                        <button type="submit" class="btn btn-success fw-bold px-4 py-2 shadow-sm">
                            <i class="fa-solid fa-cloud-upload me-2"></i>Δημιουργία πτήσης
                        </button>
                    </div>
                </form>
            </div>
        </main>
    </div>
</div>
<!-- JavaScript που χρειάζεται -->
<script src="${pageContext.request.contextPath}/js/addFlight.js"></script>
</body>
</html>
