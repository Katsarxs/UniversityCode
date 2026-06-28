<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>

<!DOCTYPE html>
<html lang="el">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Εφαρμογή Πτήσεων</title>
    <!-- Εικονίδιο σελίδας -->
    <link rel="icon" href="images/logo.ico">
    <!-- Bootstrap 5 CSS -->
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.8/dist/css/bootstrap.min.css" rel="stylesheet"
          integrity="sha384-sRIl4kxILFvY47J16cr9ZwB07vP4J8+LH7qKQnuqkuIAvNWLzeN8tE5YBujZqJLB" crossorigin="anonymous">
    <!-- Δικό μου CSS -->
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/style.css">
</head>
<body class="bg-light min-vh-100 d-flex flex-column">

<!-- navigation -->
<nav class="navbar navbar-expand-lg navbar-dark bg-dark shadow-sm">
    <div class="container">
        <a href="index.jsp">
            <img src="${pageContext.request.contextPath}/images/logo.png" alt="Logo" class="img-fluid"
                 style="max-height: 50px;">
        </a>
        <div class="d-flex gap-2">
            <c:choose>
                <%-- Αν υπάρχει ενεργό session χρήστη, δείξε μόνο το κουμπί Είσοδος --%>
                <c:when test="${not empty sessionScope.user}">
                    <a href="dashboard.jsp" class="btn btn-success fw-bold px-4 shadow-sm">
                        <i class="fa-solid fa-right-to-bracket me-2"></i>Είσοδος
                    </a>
                </c:when>

                <%-- Διαφορετικά, αν ο επισκέπτης δεν είναι συνδεδεμένος, δείξε Σύνδεση & Εγγραφή --%>
                <c:otherwise>
                    <a href="login.jsp" class="btn btn-outline-light fw-bold px-4">
                        <i class="fa-solid fa-right-to-bracket me-2"></i>Σύνδεση
                    </a>
                    <a href="register.jsp" class="btn btn-primary fw-bold px-4">
                        <i class="fa-solid fa-user-plus me-2"></i>Εγγραφή
                    </a>
                </c:otherwise>
            </c:choose>
        </div>
    </div>
</nav>

<!-- Μηνύματα στο πάνω μέρος από servlets -->
<c:if test="${param.msg == 'success'}">
    <div class="alert alert-success shadow-sm fw-bold mb-4">Εγγράφηκες. Τώρα, κάνε σύνδεση.</div>
</c:if>
<c:if test="${param.msg == 'logged_out'}">
    <div class="alert alert-success shadow-sm fw-bold mb-4">Αποσυνδέθηκες με επιτυχία.</div>
</c:if>

<!-- header -->
<header class="hero-section text-center shadow-sm">
    <div class="container py-4">
        <h1 class="display-4 fw-bold mb-2">Εφαρμογή Αεροπορικής</h1>
        <p class="lead fs-4 opacity-75">Έτος Υλοποίησης: 2026</p>
        <p class="fs-5 fw-medium text-warning">Πτήσεις - Κρατήσεις για τα ταξίδια σου</p>
    </div>
</header>

<!-- Περιεχόμενο -->
<main class="container my-5 flex-grow-1 d-flex justify-content-center align-items-center">
    <div class="row w-100 justify-content-center">
        <div class="col-md-5">
            <div class="card creator-card shadow p-4 rounded-3">
                <div class="card-body">
                    <div class="text-primary text-center mb-4">
                        <i class="fa-solid fa-user-gear display-1"></i>
                    </div>
                    <div class="d-flex flex-column gap-3 fs-6">
                        <div class="d-flex justify-content-between border-bottom pb-2">
                            <span class="fw-bold text-dark">Φοιτητής:</span>
                            <span class="text-secondary fw-semibold">Νικόλαος Κατσαρός</span>
                        </div>

                        <div class="d-flex justify-content-between border-bottom pb-2">
                            <span class="fw-bold text-dark">Μάθημα:</span>
                            <span class="text-secondary fw-semibold">Προγραμματισμός στο Διαδίκτυο</span>
                        </div>

                        <div class="d-flex justify-content-between border-bottom pb-2">
                            <span class="fw-bold text-dark">Διδάσκων:</span>
                            <span class="text-secondary fw-semibold">Κρητικός Κυριάκος</span>
                        </div>

                        <div class="d-flex justify-content-between pb-1">
                            <span class="fw-bold text-dark">Διδάσκων Εργαστηρίου:</span>
                            <span class="text-secondary fw-semibold">Λεουτσάκος Θεόδωρος</span>
                        </div>
                    </div>
                </div>
            </div>
        </div>
    </div>
</main>

<!-- footer -->
<footer class="bg-dark text-white-50 text-center py-3 mt-auto border-top border-secondary">
    <div class="container">
        <small>@ 2026 Εφαρμοφή Πτήσεων.</small>
    </div>
</footer>
</body>
</html>
