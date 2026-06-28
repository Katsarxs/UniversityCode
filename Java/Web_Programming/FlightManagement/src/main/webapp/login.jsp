<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>

<!DOCTYPE html>
<html lang="el">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Σύνδεση στο Σύστημα Πτήσεων</title>
    <!-- Εικονίδιο σελίδας -->
    <link rel="icon" href="images/logo.ico">
    <!-- Bootstrap 5 CSS -->
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.8/dist/css/bootstrap.min.css" rel="stylesheet"
          integrity="sha384-sRIl4kxILFvY47J16cr9ZwB07vP4J8+LH7qKQnuqkuIAvNWLzeN8tE5YBujZqJLB" crossorigin="anonymous">
    <!-- Δικό μου CSS -->
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/style.css">
</head>
<body class="d-flex align-items-center justify-content-center vh-100 bg-light">

<!-- Κάρτα για τη φόρμα σύνδεσης -->
<div class="d-flex flex-column align-items-center gap-3" style="width: 100%; max-width: 450px; padding: 15px;">
    <div class="card login-card shadow-sm p-4 w-100 m-0">
        <h2 class="text-center mb-4 text-secondary">Συνδέσου</h2>

        <!-- Μηνύματα στο πάνω μέρος από servlets -->
        <c:if test="${not empty errorMessage}">
            <div class="alert alert-danger text-center fw-bold small py-2">${errorMessage}</div>
        </c:if>

        <!-- Φόρμα σύνδεσης -->
        <form id="loginForm" action="LoginServlet" method="POST" onsubmit="return validateLoginForm(event)">
            <div class="mb-3">
                <label for="username" class="form-label fw-semibold text-muted">Όνομα χρήστη</label>
                <input type="text" id="username" name="username" class="form-control">
                <span id="usernameError" class="text-danger small fw-bold mt-1 d-block"></span>
            </div>
            <div class="mb-4">
                <label for="password" class="form-label fw-semibold text-muted">Κωδικός πρόσβασης</label>
                <input type="password" id="password" name="password" class="form-control">
                <span id="passwordError" class="text-danger small fw-bold mt-1 d-block"></span>
            </div>
            <button type="submit" class="btn btn-primary w-100 fw-bold py-2 shadow-sm">Σύνδεση</button>
        </form>

        <div class="text-center mt-3 small">
            Διαφορετικά - <a href="register.jsp" class="text-decoration-none fw-bold">Εγγραφή εδώ</a>
        </div>
    </div>

    <a href="index.jsp" class="btn btn-light bg-white border text-secondary w-100 fw-bold shadow-sm py-2">
        <i class="fa-solid fa-arrow-left me-2"></i>Επιστροφή στην αρχική
    </a>

</div>
<!-- JavaScript που χρειάζεται -->
<script src="${pageContext.request.contextPath}/js/login.js"></script>
</body>
</html>