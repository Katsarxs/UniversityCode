<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>

<!DOCTYPE html>
<html lang="el">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Εγγραφή Χρήστη</title>
    <!-- Εικονίδιο σελίδας -->
    <link rel="icon" href="images/logo.ico">
    <!-- Bootstrap 5 CSS -->
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.8/dist/css/bootstrap.min.css" rel="stylesheet"
          integrity="sha384-sRIl4kxILFvY47J16cr9ZwB07vP4J8+LH7qKQnuqkuIAvNWLzeN8tE5YBujZqJLB" crossorigin="anonymous">
    <!-- Δικό μου CSS -->
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/style.css">
</head>
<body class="d-flex flex-column align-items-center justify-content-center my-5 bg-light">

<div class="d-flex flex-column align-items-center gap-3 w-100" style="max-width: 600px; padding: 15px;">

    <!-- Η κάρτα εγγραφής -->
    <div class="card register-card shadow-sm p-4 w-100 m-0">
        <h2 class="text-center mb-4 text-secondary">Εγγραφή</h2>

        <!-- Μηνύματα στο πάνω μέρος από servlets -->
        <c:if test="${not empty errorMessage}">
            <div class="alert alert-danger text-center fw-bold small py-2">${errorMessage}</div>
        </c:if>

        <form id="registerForm" action="${pageContext.request.contextPath}/UserServlet" method="POST"
              onsubmit="return validateRegisterForm(event)">
            <!-- Κρυφό πεδίο action register για το UserServlet -->
            <input type="hidden" name="action" value="register">

            <div class="row">
                <div class="col-md-6 mb-3">
                    <label for="username" class="form-label fw-semibold text-muted">Όνομα χρήστη</label>
                    <input type="text" id="username" name="username" class="form-control">
                    <span id="usernameError" class="text-danger small fw-bold mt-1 d-block"></span>
                </div>
                <div class="col-md-6 mb-3">
                    <label for="email" class="form-label fw-semibold text-muted">Διεύθυνση email</label>
                    <input type="text" id="email" name="email" class="form-control">
                    <span id="emailError" class="text-danger small fw-bold mt-1 d-block"></span>
                </div>
            </div>

            <div class="row">
                <div class="col-md-6 mb-3">
                    <label for="password" class="form-label fw-semibold text-muted">Κωδικός πρόσβασης</label>
                    <input type="password" id="password" name="password" class="form-control">
                    <span id="passwordError" class="text-danger small fw-bold mt-1 d-block"></span>
                </div>
                <div class="col-md-6 mb-3">
                    <label for="fullname" class="form-label fw-semibold text-muted">Ονοματεπώνυμο</label>
                    <input type="text" id="fullname" name="fullname" class="form-control">
                    <span id="fullnameError" class="text-danger small fw-bold mt-1 d-block"></span>
                </div>
            </div>

            <div class="row">
                <!-- Αριθμός ταυτότητας -->
                <div class="col-md-12 mb-3">
                    <label for="id_number" class="form-label fw-semibold text-muted">Αριθμός ταυτότητας ΑΤ</label>
                    <input type="text" id="id_number" name="id_number" class="form-control">
                    <span id="id_numberError" class="text-danger small fw-bold mt-1 d-block"></span>
                </div>
                <input type="hidden" id="role" name="role" value="CLIENT">
            </div>

            <!-- Δυναμικά πεδία πελάτη -->
            <div class="row">
                <div class="col-md-6 mb-3">
                    <label for="afm" class="form-label fw-semibold text-muted">ΑΦΜ</label>
                    <input type="text" id="afm" name="afm" class="form-control" maxlength="9">
                    <span id="afmError" class="text-danger small fw-bold mt-1 d-block"></span>
                </div>
                <div class="col-md-6 mb-3">
                    <label for="home_address" class="form-label fw-semibold text-muted">Διεύθυνση κατοικίας</label>
                    <input type="text" id="home_address" name="home_address" class="form-control">
                    <span id="home_addressError" class="text-danger small fw-bold mt-1 d-block"></span>
                </div>
            </div>

            <button type="submit" class="btn btn-success w-100 fw-bold py-2 mt-4 shadow-sm">Εγγραφή</button>
        </form>

        <div class="text-center mt-3 small">
            Διαφορετικά - <a href="${pageContext.request.contextPath}/login.jsp" class="text-decoration-none fw-bold">Είσοδος
            εδώ</a>
        </div>
    </div>

    <a href="index.jsp" class="btn btn-light bg-white border text-secondary w-100 fw-bold shadow-sm py-2">
        <i class="fa-solid fa-arrow-left me-2"></i>Επιστροφή στην αρχική
    </a>

</div>
<!-- JavaScript που χρειάζεται -->
<script src="${pageContext.request.contextPath}/js/register.js"></script>
</body>
</html>
