<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>

<!DOCTYPE html>
<html lang="el">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Σφάλμα Συστήματος</title>
    <!-- Εικονίδιο σελίδας -->
    <link rel="icon" href="images/logo.ico">
    <!-- Bootstrap 5 CSS -->
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.8/dist/css/bootstrap.min.css" rel="stylesheet"
          integrity="sha384-sRIl4kxILFvY47J16cr9ZwB07vP4J8+LH7qKQnuqkuIAvNWLzeN8tE5YBujZqJLB" crossorigin="anonymous">
    <!-- Δικό μου CSS -->
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/style.css">
</head>
<body class="bg-light d-flex align-items-center justify-content-center vh-100">

<div class="card p-5 text-center shadow-sm login-card" style="max-width: 500px; border: none; border-radius: 12px;">
    <div class="text-danger mb-4">
        <i class="fa-solid fa-circle-exclamation fa-4x"></i>
    </div>
    <h3 class="fw-bold text-dark mb-3">Αδύνατη η ολοκλήρωση</h3>

    <!-- Μηνύματα από servlets -->
    <p class="text-muted mb-4 fs-5">
        <c:choose>
            <c:when test="${not empty errorMessage}">
                <c:out value="${errorMessage}"/>
            </c:when>
            <c:otherwise>
                Κάτι πήγε λάθος.
            </c:otherwise>
        </c:choose>
    </p>

    <a href="dashboard.jsp" class="btn btn-primary fw-bold py-2 px-4 shadow-sm">
        <i class="fa-solid fa-house me-2"></i>Επιστροφή
    </a>
</div>
</body>
</html>
