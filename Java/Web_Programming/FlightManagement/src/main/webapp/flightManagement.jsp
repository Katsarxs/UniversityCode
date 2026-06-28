<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<!-- Έλεγχος session -->
<c:if test="${empty sessionScope.user}">
    <c:redirect url="index.jsp"/>
</c:if>

<!DOCTYPE html>
<html lang="el">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Διαχείριση Πτήσεων</title>
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
                <h2 class="fw-bold text-secondary">Πτήσεις</h2>
                <c:if test="${sessionScope.user.role == 'FLIGHT_MANAGER'}">
                    <a href="addFlight.jsp" class="btn btn-success fw-bold">
                        <i class="fa-solid fa-plus me-2"></i>Νέα Πτήση
                    </a>
                </c:if>
            </div>

            <!-- Μηνύματα στο πάνω μέρος από servlets -->
            <c:if test="${not empty param.msg && param.msg.startsWith('imported_')}">
                <div class="alert alert-success shadow-sm fw-bold mb-4">
                    <i class="fa-solid fa-circle-check me-2"></i>
                    Έγινε εισαγωγή ${param.msg.substring(9)} πτήσεων.
                </div>
            </c:if>
            <c:if test="${param.msg == 'delete_success'}">
                <div class="alert alert-success shadow-sm fw-bold mb-4">Η πτήση ακυρώθηκε και οι κρατήσεις της ακυρώθηκαν.</div>
            </c:if>

            <!-- Φόρμα εισαγωγής πολλαπλών πτήσεων-->
            <c:if test="${sessionScope.user.role == 'FLIGHT_MANAGER'}">
                <div class="card border-0 shadow-sm p-4 mb-4">
                    <h5 class="fw-bold text-success mb-3">
                        <i class="fa-solid fa-file-import me-2"></i>Εισαγωγή πολλαπλών πτήσεων
                    </h5>
                    <form action="FlightServlet" method="POST" enctype="multipart/form-data">
                        <input type="hidden" name="action" value="import">

                        <div class="row g-3 align-items-end">
                            <div class="col-md-8">
                                <label for="csvFile" class="form-label small fw-semibold text-muted">Επέλεξε αρχείο
                                    .csv</label>
                                <input type="file" class="form-control" id="csvFile" name="csvFile" accept=".csv"
                                       required>
                            </div>
                            <div class="col-md-4 text-end">
                                <button type="submit" class="btn btn-success fw-bold w-100 px-4">
                                    <i class="fa-solid fa-upload me-2"></i>Εισαγωγή
                                </button>
                            </div>
                        </div>
                    </form>
                </div>
            </c:if>

            <!-- Φόρμα αναζήτησης πτήσης -->
            <div class="card border-0 shadow-sm p-4 mb-4">
                <h5 class="fw-bold text-muted mb-3">Φίλτρα αναζήτησης</h5>
                <form action="FlightServlet" method="GET">
                    <input type="hidden" name="action" value="search">
                    <div class="row g-3">
                        <!-- Αριθμός πτήσης -->
                        <div class="col-md-4">
                            <label for="flightNumber" class="form-label small fw-semibold text-muted">Αριθμός
                                πτήσης</label>
                            <input type="text" id="flightNumber" name="flight_number" class="form-control"
                                   value="${param.flight_number}">
                        </div>
                        <!-- Μοντέλο αεροπλάνου -->
                        <div class="col-md-4">
                            <label for="airplane" class="form-label small fw-semibold text-muted">Μοντέλο
                                αεροπλάνου</label>
                            <input type="text" id="airplane" name="airplane" class="form-control"
                                   value="${param.airplane}">
                        </div>
                        <!-- Ημερομηνία αναχώρησης -->
                        <div class="col-md-4">
                            <label for="departureDate" class="form-label small fw-semibold text-muted">Ημερομηνία
                                αναχώρησης</label>
                            <input type="date" id="departureDate" name="departure_date" class="form-control"
                                   value="${param.departure_date}">
                        </div>

                        <div class="col-12 text-end">
                            <button type="submit" class="btn btn-primary fw-bold px-4">
                                <i class="fa-solid fa-magnifying-glass me-2"></i>Αναζήτηση
                            </button>
                        </div>
                    </div>
                </form>
            </div>

            <!-- Αποτελέσματα πτήσεων σε πίνακα -->
            <div class="card border-0 shadow-sm p-4">
                <div class="table-responsive">
                    <table class="table table-striped table-hover align-middle m-0">
                        <thead class="table-dark">
                        <tr>
                            <th>Αριθμός πτήσης</th>
                            <th>Μοντέλο αεροπλάνου</th>
                            <th>Ημερομηνία αναχώρησης</th>
                            <th>Κατάσταση</th>
                            <th class="text-center">Ενέργειες</th>
                        </tr>
                        </thead>
                        <tbody>
                        <c:if test="${empty flightsList}">
                            <tr>
                                <td colspan="5" class="text-center text-muted py-4">Δεν βρέθηκαν πτήσεις.</td>
                            </tr>
                        </c:if>
                        <c:forEach var="flight" items="${flightsList}">
                            <tr>
                                <td class="fw-bold">${flight.flightNumber}</td>
                                <td>${flight.airplane}</td>
                                <td>${flight.departureDate}</td>
                                <td>
                                        <span class="badge bg-secondary">
                                            <c:choose>
                                                <c:when test="${flight.state == 'CREATED'}">ΔΗΜΙΟΥΡΓΗΜΕΝΗ</c:when>
                                                <c:when test="${flight.state == 'STAFFED'}">ΣΤΕΛΕΧΩΜΕΝΗ</c:when>
                                                <c:when test="${flight.state == 'COMPLETED'}">ΠΕΡΑΤΩΜΕΝΗ</c:when>
                                                <c:when test="${flight.state == 'CANCELLED'}">ΑΚΥΡΩΜΕΝΗ</c:when>
                                                <c:otherwise>${flight.state}</c:otherwise>
                                            </c:choose>
                                        </span>
                                </td>
                                <td class="text-center">
                                    <div class="d-flex justify-content-center gap-2">
                                        <a href="FlightServlet?action=view&id=${flight.id}"
                                           class="btn btn-sm btn-outline-primary fw-bold">
                                            <i class="fa-solid fa-eye me-1"></i>Προβολή
                                        </a>
                                        <c:if test="${sessionScope.user.role == 'FLIGHT_MANAGER'}">
                                            <a href="FlightServlet?action=delete&id=${flight.id}"
                                               class="btn btn-sm btn-outline-danger fw-bold">
                                                <i class="fa-solid fa-trash me-1"></i>Διαγραφή
                                            </a>
                                        </c:if>
                                    </div>
                                </td>

                            </tr>
                        </c:forEach>
                        </tbody>
                    </table>
                </div>
            </div>

        </main>
    </div>
</div>
<script>
    // Φωτίζει το σωστό κουμπί στο sidebar ανάλογα με τον ρόλο
    let element = document.getElementById("menu-flights") || document.getElementById("menu-flights-mgr");
    if (element) {
        element.classList.add("active", "fw-semibold");
        element.classList.remove("text-white-50");
    }
</script>
</body>
</html>
