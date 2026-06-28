<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<!-- Έλεγχος session -->
<c:if test="${empty sessionScope.user}">
    <c:redirect url="login.jsp"/>
</c:if>

<!DOCTYPE html>
<html lang="el">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Λεπτομέρειες Πτήσης ${flight.flightNumber}</title>
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
                <h2 class="fw-bold text-secondary">Πληροφορίες πτήσης: ${flight.flightNumber}</h2>
                <a href="FlightServlet?action=search" class="btn btn-outline-secondary fw-bold">
                    <i class="fa-solid fa-arrow-left me-2"></i>Επιστροφή
                </a>
            </div>

            <!-- Μηνύματα στο πάνω μέρος από servlets -->
            <c:if test="${param.msg == 'update_success'}">
                <div class="alert alert-success shadow-sm fw-bold mb-4 animate__animated animate__fadeIn">
                    <i class="fa-solid fa-circle-check me-2"></i>Η πτήση ενημερώθηκε.
                </div>
            </c:if>
            <c:if test="${not empty errorMessage}">
                <div class="alert alert-danger shadow-sm fw-bold mb-4 animate__animated animate__fadeIn">
                    <i class="fa-solid fa-triangle-exclamation me-2"></i>${errorMessage}
                </div>
            </c:if>

            <div class="row g-4">
                <!-- Στοιχεία πτήσης-->
                <div class="col-md-6">
                    <div class="card border-0 shadow-sm p-4 h-100">
                        <h5 class="fw-bold text-muted mb-4"><i class="fa-solid fa-plane me-2"></i>Στοιχεία πτήσης</h5>

                        <!-- Αριθμός πτήσης κοινό για όλους -->
                        <div class="mb-3 border-bottom pb-2">
                            <span class="text-muted small d-block fw-semibold">Αριθμός πτήσης:</span>
                            <span class="fs-5 fw-bold text-dark">${flight.flightNumber}</span>
                        </div>
                        <!-- Μοντέλο αεροπλάνου Διαχειριστή Πτήσεων -->
                        <c:if test="${sessionScope.user.role == 'FLIGHT_MANAGER'}">
                            <div class="mb-3 border-bottom pb-2">
                                <span class="text-muted small d-block fw-semibold">Μοντέλο αεροπλάνου:</span>
                                <span class="fs-5 fw-bold text-dark">${flight.airplane}</span>
                            </div>
                        </c:if>
                        <!-- Ημερομηνία -->
                        <div class="mb-3 border-bottom pb-2">
                            <span class="text-muted small d-block fw-semibold">Ημερομηνία αναχώρησης:</span>
                            <span class="fs-5 fw-bold text-dark">${flight.departureDate}</span>
                        </div>
                        <!-- Ώρα αναχώρησης-->
                        <div class="mb-3 border-bottom pb-2">
                            <span class="text-muted small d-block fw-semibold">Ώρα αναχώρησης:</span>
                            <span class="fs-5 fw-bold text-dark">${flight.departureTime}</span>
                        </div>
                        <!-- Θέσεις -->
                        <div class="mb-3 border-bottom pb-2">
                            <span class="text-muted small d-block fw-semibold">Συνολικές θέσεις:</span>
                            <span class="fs-5 fw-bold text-dark">
                                ${flight.seats}
                            </span>
                        </div>
                        <!-- Έξτρα στοιχεία -->
                        <c:if test="${sessionScope.user.role == 'FLIGHT_MANAGER'}">
                            <div class="mb-3 border-bottom pb-2">
                                <span class="text-muted small d-block fw-semibold">Συνολικές σειρές:</span>
                                <span class="fs-5 fw-bold text-dark">${flight.rows}</span>
                            </div>
                            <div class="mb-3 border-bottom pb-2">
                                <span class="text-muted small d-block fw-semibold">Θέσεις ανά σειρά:</span>
                                <span class="fs-5 fw-bold text-dark">${flight.seatsRow}</span>
                            </div>
                            <div class="mb-3 border-bottom pb-2">
                                <span class="text-muted small d-block fw-semibold">Σειρές Business:</span>
                                <span class="fs-5 fw-bold text-dark">${flight.rowsBusiness}</span>
                            </div>
                        </c:if>
                        <!-- Κατάσταση πτήσης -->
                        <div class="mb-3">
                            <span class="text-muted small d-block fw-semibold">Κατάσταση πτήσης:</span>
                            <span class="badge bg-secondary fs-6">
                                <c:choose>
                                    <c:when test="${flight.state == 'CREATED'}">ΔΗΜΙΟΥΡΓΗΜΕΝΗ</c:when>
                                    <c:when test="${flight.state == 'STAFFED'}">ΣΤΕΛΕΧΩΜΕΝΗ</c:when>
                                    <c:when test="${flight.state == 'COMPLETED'}">ΠΕΡΑΤΩΜΕΝΗ</c:when>
                                    <c:when test="${flight.state == 'CANCELLED'}">ΑΚΥΡΩΜΕΝΗ</c:when>
                                    <c:otherwise>${flight.state}</c:otherwise>
                                </c:choose>
                            </span>
                        </div>
                    </div>
                </div>

                <!-- Φόρμα κράτησης -->
                <div class="col-md-6">
                    <div class="card border-0 shadow-sm p-4 h-100">
                        <c:choose>
                            <c:when test="${sessionScope.user.role == 'CLIENT' && flight.state == 'CREATED'}">
                                <h5 class="fw-bold text-muted mb-4"><i class="fa-solid fa-ticket me-2"></i>Κράτηση
                                    εισιτηρίου</h5>
                                <form id="bookingForm" action="BookingServlet" method="POST"
                                      onsubmit="return validateBookingForm(event)">
                                    <input type="hidden" name="action" value="insert">
                                    <input type="hidden" name="flight_id" value="${flight.id}">

                                    <div class="mb-4">
                                        <label for="ticketType" class="form-label fw-semibold text-muted">Επέλεξε τύπο
                                            Θέσης</label>
                                        <select id="ticketType" name="ticket_type" class="form-select form-select-lg">
                                            <option value="">Επέλεξε τύπο εισιτηρίου</option>
                                            <option value="ECONOMY">ECONOMY
                                            </option>
                                            <option value="NORMAL">NORMAL
                                            </option>
                                            <option value="BUSINESS">BUSINESS
                                            </option>
                                        </select>
                                        <span id="ticketTypeError"
                                              class="text-danger small fw-bold mt-1 d-block"></span>
                                    </div>
                                    <button type="submit" class="btn btn-primary w-100 fw-bold shadow-sm py-3 fs-5">
                                        <i class="fa-solid fa-cart-shopping me-2"></i>Κράτηση
                                    </button>
                                </form>
                            </c:when>
                            <c:when test="${sessionScope.user.role == 'CLIENT' && flight.state != 'CREATED'}">
                                <div class="alert alert-warning text-center fw-bold m-0">
                                    <i class="fa-solid fa-lock me-2"></i>Οι κρατήσεις έχουν κλείσει, η κατάσταση είναι
                                    <c:choose>
                                        <c:when test="${flight.state == 'STAFFED'}">ΣΤΕΛΕΧΩΜΕΝΗ</c:when>
                                        <c:when test="${flight.state == 'COMPLETED'}">ΠΕΡΑΤΩΜΕΝΗ</c:when>
                                        <c:otherwise>ΑΚΥΡΩΜΕΝΗ</c:otherwise>
                                    </c:choose>).
                                </div>
                            </c:when>
                            <c:otherwise>
                                <h5 class="fw-bold text-muted mb-4"><i class="fa-solid fa-user-shield me-2"></i>Ενημέρωση
                                    πτήσης</h5>
                                <form action="FlightServlet" method="POST" class="row g-3">
                                    <input type="hidden" name="action" value="update">
                                    <input type="hidden" name="id" value="${flight.id}">

                                    <!-- Μοντέλο αεροπλάνου -->
                                    <div class="col-md-12">
                                        <label class="form-label small fw-semibold text-muted">Μοντέλο
                                            αεροπλάνου</label>
                                        <input type="text" name="airplane" class="form-control"
                                               value="${flight.airplane}" required>
                                    </div>
                                    <!-- Ημερομηνία αναχώρησης -->
                                    <div class="col-md-6">
                                        <label class="form-label small fw-semibold text-muted">Ημερομηνία
                                            αναχώρησης</label>
                                        <input type="date" name="departure_date" class="form-control"
                                               value="${flight.departureDate}" required>
                                    </div>
                                    <!-- Ώρα αναχώρησης -->
                                    <div class="col-md-6">
                                        <label class="form-label small fw-semibold text-muted">Ώρα αναχώρησης</label>
                                        <input type="time" name="departure_time" class="form-control"
                                               value="${flight.departureTime}" required>
                                    </div>
                                    <!-- Συνολικές Θέσεις -->
                                    <div class="col-md-6">
                                        <label class="form-label small fw-semibold text-muted">Αριθμός Θέσεων</label>
                                        <input type="number" name="seats" class="form-control" value="${flight.seats}"
                                               required min="1">
                                    </div>
                                    <!-- Σειρές -->
                                    <div class="col-md-6">
                                        <label class="form-label small fw-semibold text-muted">Αριθμός σειρών</label>
                                        <input type="number" name="rows" class="form-control" value="${flight.rows}"
                                               required min="1">
                                    </div>
                                    <!-- Θέσεις ανά σειρά -->
                                    <div class="col-md-6">
                                        <label class="form-label small fw-semibold text-muted">Θέσεις ανά σειρά</label>
                                        <input type="number" name="seats_row" class="form-control"
                                               value="${flight.seatsRow}" required min="1">
                                    </div>
                                    <!-- Σειρές business -->
                                    <div class="col-md-6">
                                        <label class="form-label small fw-semibold text-muted">Αριθμός business
                                            σειρών</label>
                                        <input type="number" name="rows_business" class="form-control"
                                               value="${flight.rowsBusiness}" required min="0">
                                    </div>
                                    <!-- Κατάσταση πτήσης -->
                                    <div class="col-md-12 mb-2">
                                        <label for="flightStateSelect" class="form-label fw-semibold text-muted">Κατάσταση
                                            πτήσης</label>
                                        <select id="flightStateSelect" name="state" class="form-select form-select-lg"
                                                onchange="handleTicketTypeChange()">
                                            <option value="CREATED"
                                                    <c:if test="${flight.state == 'CREATED'}">selected</c:if>>
                                                ΔΗΜΙΟΥΡΓΗΜΕΝΗ
                                            </option>
                                            <option value="STAFFED"
                                                    <c:if test="${flight.state == 'STAFFED'}">selected</c:if>>
                                                ΣΤΕΛΕΧΩΜΕΝΗ
                                            </option>
                                            <option value="COMPLETED"
                                                    <c:if test="${flight.state == 'COMPLETED'}">selected</c:if>>
                                                ΠΕΡΑΤΩΜΕΝΗ
                                            </option>
                                            <option value="CANCELLED"
                                                    <c:if test="${flight.state == 'CANCELLED'}">selected</c:if>>
                                                ΑΚΥΡΩΜΕΝΗ
                                            </option>
                                        </select>
                                    </div>

                                    <div class="col-12">
                                        <button type="submit"
                                                class="btn btn-warning w-100 fw-bold shadow-sm py-2 text-dark">
                                            <i class="fa-solid fa-floppy-disk me-2"></i>Αποθήκευση αλλαγών
                                        </button>
                                    </div>
                                </form>
                            </c:otherwise>
                        </c:choose>
                    </div>
                </div>
            </div>
        </main>
    </div>
</div>
<!-- JavaScript που χρειάζεται -->
<script src="${pageContext.request.contextPath}/js/viewFlight.js"></script>
</body>
</html>