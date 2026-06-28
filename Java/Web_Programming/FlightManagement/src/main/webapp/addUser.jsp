<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<!-- Έλεγχος session και ρόλου -->
<c:if test="${empty sessionScope.user || sessionScope.user.role != 'SYSTEM_MANAGER'}">
    <c:redirect url="index.jsp"/>
</c:if>

<!DOCTYPE html>
<html lang="el">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Εισαγωγή Χρήστη</title>
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
                <h2 class="fw-bold text-secondary">Νέος Χρήστης</h2>
                <a href="UserServlet?action=search&query=" class="btn btn-outline-secondary fw-bold">
                    <i class="fa-solid fa-arrow-left me-2"></i>Επιστροφή
                </a>
            </div>

            <!-- Μηνύματα στο πάνω μέρος από servlets -->
            <c:if test="${not empty errorMessage}">
                <div class="alert alert-danger shadow-sm fw-bold mb-4">${errorMessage}</div>
            </c:if>

            <!-- Φόρμα χρήστη -->
            <div class="card border-0 shadow-sm p-4 mb-5" style="max-width: 850px;">
                <form id="addUserForm" action="${pageContext.request.contextPath}/UserServlet" method="POST"
                      onsubmit="return validateRegisterForm(event)">
                    <!-- Το action είναι register-->
                    <input type="hidden" name="action" value="register">

                    <div class="row">
                        <!-- Όνομα χρήστη -->
                        <div class="col-md-6 mb-3">
                            <label for="username" class="form-label fw-semibold text-muted">Όνομα χρήστη</label>
                            <input type="text" id="username" name="username" class="form-control" required>
                            <span id="usernameError" class="text-danger small fw-bold mt-1 d-block"></span>
                        </div>
                        <!-- Διεύθυνση email -->
                        <div class="col-md-6 mb-3">
                            <label for="email" class="form-label fw-semibold text-muted">Διεύθυνση email</label>
                            <input type="email" id="email" name="email" class="form-control" required>
                            <span id="emailError" class="text-danger small fw-bold mt-1 d-block"></span>
                        </div>
                    </div>

                    <div class="row">
                        <!-- Κωδικός πρόσβασης-->
                        <div class="col-md-6 mb-3">
                            <label for="password" class="form-label fw-semibold text-muted">Κωδικός πρόσβασης</label>
                            <input type="password" id="password" name="password" class="form-control" required>
                            <span id="passwordError" class="text-danger small fw-bold mt-1 d-block"></span>
                        </div>
                        <!-- Ονοματεπώνυμο -->
                        <div class="col-md-6 mb-3">
                            <label for="fullname" class="form-label fw-semibold text-muted">Ονοματεπώνυμο</label>
                            <input type="text" id="fullname" name="fullname" class="form-control" required>
                            <span id="fullnameError" class="text-danger small fw-bold mt-1 d-block"></span>
                        </div>
                    </div>

                    <div class="row">
                        <!-- Αριθμός ταυτότητας -->
                        <div class="col-md-6 mb-3">
                            <label for="id_number" class="form-label fw-semibold text-muted">Αριθμός ταυτότητας
                                ΑΤ</label>
                            <input type="text" id="id_number" name="id_number" class="form-control" required>
                            <span id="id_numberError" class="text-danger small fw-bold mt-1 d-block"></span>
                        </div>
                        <!-- Ρόλος -->
                        <div class="col-md-6 mb-3">
                            <label for="role" class="form-label fw-semibold text-muted">Ρόλος χρήστη</label>
                            <select id="role" name="role" class="form-select" onchange="showRoleFields()" required>
                                <option value="">Επέλεξε Ρόλο</option>
                                <option value="CLIENT">Πελάτης</option>
                                <option value="FLIGHT_MANAGER">Διαχειριστής Πτήσεων</option>
                                <option value="SYSTEM_MANAGER">Διαχειριστής Συστήματος</option>
                            </select>
                            <span id="roleError" class="text-danger small fw-bold mt-1 d-block"></span>
                        </div>
                    </div>

                    <!-- Αν είναι πελάτης -->
                    <div id="clientFields" class="d-none border-top pt-3 mt-2">
                        <h5 class="text-muted mb-3"><i class="fa-solid fa-user me-2"></i>Στοιχεία πελάτη</h5>
                        <div class="row">
                            <!-- ΑΦΜ -->
                            <div class="col-md-6 mb-3">
                                <label for="afm" class="form-label fw-semibold text-muted">ΑΦΜ</label>
                                <input type="text" id="afm" name="afm" class="form-control" maxlength="9">
                                <span id="afmError" class="text-danger small fw-bold mt-1 d-block"></span>
                            </div>
                            <!-- Διεύθυνση κατοικίας -->
                            <div class="col-md-6 mb-3">
                                <label for="home_address" class="form-label fw-semibold text-muted">Διεύθυνση
                                    κατοικίας</label>
                                <input type="text" id="home_address" name="home_address" class="form-control">
                                <span id="home_addressError" class="text-danger small fw-bold mt-1 d-block"></span>
                            </div>
                        </div>
                    </div>

                    <!-- Αν είναι υπάλληλος, δηλαδή διαχειριστής πτήσεων ή συστήματος -->
                    <div id="employeeFields" class="d-none border-top pt-3 mt-2">
                        <h5 class="text-muted mb-3"><i class="fa-solid fa-briefcase me-2"></i>Στοιχεία υπαλλήλου</h5>
                        <div class="mb-3" style="max-width: 400px;">
                            <!-- Κωδικός υπαλλήλου -->
                            <label for="employee_code" class="form-label fw-semibold text-muted">Κωδικός
                                υπαλλήλου</label>
                            <input type="text" id="employee_code" name="employee_code" class="form-control">
                            <span id="employee_codeError" class="text-danger small fw-bold mt-1 d-block"></span>
                        </div>
                    </div>

                    <div class="mt-4 text-end">
                        <button type="submit" class="btn btn-success fw-bold px-4 py-2 shadow-sm">
                            <i class="fa-solid fa-user-plus me-2"></i>Δημιουργία χρήστη
                        </button>
                    </div>
                </form>
            </div>
        </main>
    </div>
</div>
<!-- JavaScript που χρειάζεται -->
<script src="${pageContext.request.contextPath}/js/register.js"></script>
</body>
</html>