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
    <title>Διαχείριση Κρατήσεων</title>
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
                <h2 class="fw-bold text-secondary">
                    <c:choose>
                        <c:when test="${sessionScope.user.role == 'CLIENT'}">Οι κρατήσεις σου</c:when>
                        <c:otherwise>Διαχείριση κρατήσεων</c:otherwise>
                    </c:choose>
                </h2>
            </div>

            <!-- Μηνύματα στο πάνω μέρος από servlets -->
            <div id="ajaxAlert" class="alert d-none fw-bold shadow-sm mb-4"></div>
            <c:if test="${param.msg == 'cancel_success'}">
                <div class="alert alert-success shadow-sm fw-bold mb-4">Η κράτηση ακυρώθηκε.</div>
            </c:if>

            <!-- Φόρμα για αναζήτηση -->
            <div class="card border-0 shadow-sm p-4 mb-4">
                <h5 class="fw-bold text-muted mb-3">Φίλτρα αναζήτησης</h5>
                <form action="BookingServlet" method="GET">
                    <input type="hidden" name="action" value="search">

                    <div class="row g-3">
                        <!-- Κοινά Φίλτρα για όλους -->
                        <div class="col-md-4">
                            <label for="bookingNumber" class="form-label small fw-semibold text-muted">Αριθμός
                                κράτησης</label>
                            <input type="text" id="bookingNumber" name="bookingNumber" class="form-control"
                                   value="${param.bookingNumber}">
                        </div>
                        <div class="col-md-4">
                            <label for="startDate" class="form-label small fw-semibold text-muted">Από
                                ημερομηνία</label>
                            <input type="date" id="startDate" name="startDate" class="form-control"
                                   value="${param.startDate}">
                        </div>
                        <div class="col-md-4">
                            <label for="endDate" class="form-label small fw-semibold text-muted">Έως ημερομηνία</label>
                            <input type="date" id="endDate" name="endDate" class="form-control"
                                   value="${param.endDate}">
                        </div>

                        <!-- Έξτρα φίλτρα για διαχειριστές πτήσεων -->
                        <c:if test="${sessionScope.user.role != 'CLIENT'}">
                            <div class="col-md-4">
                                <label for="fullname" class="form-label small fw-semibold text-muted">Ονοματεπώνυμο
                                    επιβάτη</label>
                                <input type="text" id="fullname" name="fullname" class="form-control"
                                       value="${param.fullname}">
                            </div>
                            <div class="col-md-4">
                                <label for="afm" class="form-label small fw-semibold text-muted">ΑΦΜ επιβάτη</label>
                                <input type="text" id="afm" name="afm" class="form-control" maxlength="9"
                                       value="${param.afm}">
                            </div>
                            <div class="col-md-4">
                                <label for="idNumber" class="form-label small fw-semibold text-muted">ΑΤ επιβάτη</label>
                                <input type="text" id="idNumber" name="idNumber" class="form-control"
                                       value="${param.idNumber}">
                            </div>
                        </c:if>

                        <div class="col-12 text-end">
                            <button type="submit" class="btn btn-primary fw-bold px-4">
                                <i class="fa-solid fa-magnifying-glass me-2"></i>Αναζήτηση
                            </button>
                        </div>
                    </div>
                </form>
            </div>

            <!-- Πίνακας για τις κρατήσεις -->
            <div class="card border-0 shadow-sm p-4">
                <div class="table-responsive">
                    <table class="table table-striped table-hover align-middle m-0">
                        <thead class="table-dark">
                        <tr>
                            <th>Κωδικός κράτησης</th>
                            <th>Ημερομηνία κράτησης</th>
                            <th>Τύπος εισιτηρίου</th>
                            <th>Θέση</th>
                            <th>Κατάσταση</th>
                            <th class="text-center">Ενέργειες</th>
                        </tr>
                        </thead>
                        <tbody>
                        <c:if test="${empty bookingsList}">
                            <tr>
                                <td colspan="6" class="text-center text-muted py-4">Δεν βρέθηκαν κρατήσεις.</td>
                            </tr>
                        </c:if>

                        <c:forEach var="booking" items="${bookingsList}">
                            <tr>
                                <td class="fw-bold">${booking.bookingNumber}</td>
                                <td>${booking.bookDate}</td>
                                <td>
                                        <span class="badge bg-light text-dark border">
                                            <c:choose>
                                                <c:when test="${booking.ticketType == 'ECONOMY'}">ECONOMY</c:when>
                                                <c:when test="${booking.ticketType == 'NORMAL'}">NORMAL</c:when>
                                                <c:when test="${booking.ticketType == 'BUSINESS'}">BUSINESS</c:when>
                                                <c:otherwise>${booking.ticketType}</c:otherwise>
                                            </c:choose>
                                        </span>
                                </td>
                                <td>
                                    <c:choose>
                                        <c:when test="${not empty booking.row && not empty booking.column}">
                                            Σειρά ${booking.row} - Θέση ${booking.column}
                                        </c:when>
                                        <c:otherwise>
                                            <span class="text-muted small">Δεν έχει επιλεγεί</span>
                                        </c:otherwise>
                                    </c:choose>
                                </td>
                                <td>
                                    <c:choose>
                                        <c:when test="${booking.state == 'CREATED'}">
                                            <span class="badge bg-info text-dark">ΔΗΜΙΟΥΡΓΗΜΕΝΗ</span>
                                        </c:when>
                                        <c:when test="${booking.state == 'COMPLETED'}">
                                            <span class="badge bg-success">ΠΕΡΑΤΩΜΕΝΗ</span>
                                        </c:when>
                                        <c:when test="${booking.state == 'CANCELLED'}">
                                            <span class="badge bg-danger">ΑΚΥΡΩΜΕΝΗ</span>
                                        </c:when>
                                        <c:otherwise>
                                            <span class="badge bg-secondary">${booking.state}</span>
                                        </c:otherwise>
                                    </c:choose>
                                </td>
                                <td class="text-center">
                                    <a href="BookingServlet?action=view&id=${booking.id}"
                                       class="btn btn-sm btn-outline-primary fw-bold">
                                        <i class="fa-solid fa-eye me-1"></i>Προβολή
                                    </a>
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
    const links = document.querySelectorAll('.sidebar .nav-link');
    links.forEach(link => {
        // Αν το κείμενο του συνδέσμου περιέχει τη λέξη Κρατήσεις, το ενεργοποιούμε
        if (link.textContent.includes('Διαχείριση κρατήσεων') || link.textContent.includes('Οι κρατήσεις σου')) {
            link.classList.add('active', 'fw-semibold');
            link.classList.remove('text-white-50');
        }
    });
</script>
</body>
</html>
