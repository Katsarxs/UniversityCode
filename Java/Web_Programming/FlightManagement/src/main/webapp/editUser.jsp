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
    <title>Επεξεργασία Χρήστη</title>
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
                <h2 class="fw-bold text-secondary">Ενημέρωση χρήστη</h2>
                <a href="UserServlet?action=search&query=" class="btn btn-outline-secondary fw-bold">
                    <i class="fa-solid fa-arrow-left me-2"></i>Επιστροφή στη λίστα
                </a>
            </div>

            <!-- Μηνύματα στο πάνω μέρος από servlets -->
            <c:if test="${not empty errorMessage}">
                <div class="alert alert-danger shadow-sm fw-bold mb-4">${errorMessage}</div>
            </c:if>
            <c:if test="${param.msg == 'updated'}">
                <div class="alert alert-success shadow-sm fw-bold mb-4">Οι αλλαγές αποθηκεύτηκαν.</div>
            </c:if>

            <!-- Φόρμα για επεργασία χρήστη -->
            <div class="card border-0 shadow-sm p-4 mb-5" style="max-width: 800px;">
                <form id="editUserForm" action="UserServlet?action=update" method="POST"
                      onsubmit="return validateEditForm(event)">
                    <!-- Το action είναι update-->
                    <input type="hidden" name="action" value="update">
                    <!-- To id του χρήστη -->
                    <input type="hidden" name="id" value="${userToEdit.id}">

                    <div class="row">
                        <!-- Όνομα χρήστη -->
                        <div class="col-md-6 mb-3">
                            <label class="form-label fw-semibold text-muted">Όνομα χρήστη</label>
                            <input type="text" class="form-control bg-light" value="${userToEdit.username}" readonly>
                        </div>
                        <!-- Email -->
                        <div class="col-md-6 mb-3">
                            <label for="email" class="form-label fw-semibold text-muted">Διεύθυνση email</label>
                            <input type="text" id="email" name="email" class="form-control" value="${userToEdit.email}">
                            <span id="emailError" class="text-danger small fw-bold mt-1 d-block"></span>
                        </div>
                    </div>

                    <div class="row">
                        <!-- Ονοματεπώνυμο -->
                        <div class="col-md-6 mb-3">
                            <label for="fullname" class="form-label fw-semibold text-muted">Ονοματεπώνυμο</label>
                            <input type="text" id="fullname" name="fullname" class="form-control"
                                   value="${userToEdit.fullname}">
                            <span id="fullnameError" class="text-danger small fw-bold mt-1 d-block"></span>
                        </div>
                        <!-- ΑΤ -->
                        <div class="col-md-6 mb-3">
                            <label for="idNumber" class="form-label fw-semibold text-muted">Αριθμός Ταυτότητας
                                ΑΤ</label>
                            <input type="text" id="idNumber" name="id_number" class="form-control"
                                   value="${userToEdit.idNumber}">
                            <span id="idNumberError" class="text-danger small fw-bold mt-1 d-block"></span>
                        </div>
                    </div>

                    <div class="row border-top pt-3 mt-2">
                        <!-- Κατάσταση λογαριασμού -->
                        <div class="col-md-6 mb-3">
                            <label for="accountState" class="form-label fw-semibold text-muted">Κατάσταση
                                λογαριασμού</label>
                            <select id="accountState" name="account_state" class="form-select">
                                <option value="ACTIVE"
                                        <c:if test="${userToEdit.accountState == 'ACTIVE'}">selected</c:if>>
                                    Ενεργός
                                </option>
                                <option value="DISABLED"
                                        <c:if test="${userToEdit.accountState == 'DISABLED'}">selected</c:if>>
                                    Απενεργοποιημένος
                                </option>
                            </select>
                        </div>
                    </div>

                    <!-- Τα έξτρα πεδία αν είναι πελάτης ή διαχειριστής -->
                    <div class="row border-top pt-3 mt-2">
                        <c:choose>
                            <c:when test="${userToEdit.role == 'CLIENT'}">
                                <h5 class="text-muted mb-3">Επιπλέον στοιχεία πελάτη</h5>
                                <div class="col-md-6 mb-3">
                                    <label for="afm" class="form-label fw-semibold text-muted">ΑΦΜ</label>
                                    <input type="text" id="afm" name="afm" class="form-control" maxlength="9"
                                           value="${userToEdit.afm}">
                                    <span id="afmError" class="text-danger small fw-bold mt-1 d-block"></span>
                                </div>
                                <div class="col-md-6 mb-3">
                                    <label for="homeAddress" class="form-label fw-semibold text-muted">Διεύθυνση
                                        κατοικίας</label>
                                    <input type="text" id="homeAddress" name="home_address" class="form-control"
                                           value="${userToEdit.homeAddress}">
                                    <span id="homeAddressError" class="text-danger small fw-bold mt-1 d-block"></span>
                                </div>
                            </c:when>
                            <c:otherwise>
                                <h5 class="text-muted mb-3">Επιπλέον στοιχεία υπαλλήλου</h5>
                                <div class="col-md-6 mb-3">
                                    <label for="employeeCode" class="form-label fw-semibold text-muted">Κωδικός
                                        υπαλλήλου</label>
                                    <input type="text" id="employeeCode" name="employee_code" class="form-control"
                                           value="${userToEdit.employeeCode}">
                                    <span id="employeeCodeError" class="text-danger small fw-bold mt-1 d-block"></span>
                                </div>
                            </c:otherwise>
                        </c:choose>
                    </div>

                    <div class="mt-4 text-end">
                        <button type="submit" class="btn btn-primary fw-bold px-4 py-2 shadow-sm">
                            <i class="fa-solid fa-floppy-disk me-2"></i>Αποθήκευση αλλαγών
                        </button>
                    </div>
                </form>
            </div>

        </main>
    </div>
</div>
<!-- JavaScript που χρειάζεται -->
<script src="${pageContext.request.contextPath}/js/editUser.js"></script>
</body>
</html>
