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
    <title>Λογαριασμός</title>
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
                <h2 class="fw-bold text-secondary">Διαχείριση λογαριασμού</h2>
            </div>

            <!-- Μηνύματα στο πλανω μέρος από servlets-->
            <c:if test="${not empty errorMessage}">
                <div class="alert alert-danger shadow-sm fw-bold mb-4">${errorMessage}</div>
            </c:if>
            <c:if test="${param.msg == 'update_success'}">
                <div class="alert alert-success shadow-sm fw-bold mb-4">Ο λογαριασμός σου ενημερώθηκε.
                </div>
            </c:if>

            <div class="row g-4">
                <!-- Πληροφορίες από όλους -->
                <div class="col-md-5">
                    <div class="card border-0 shadow-sm p-4 h-100">
                        <h5 class="fw-bold text-muted mb-4"><i class="fa-solid fa-id-card me-2"></i>Πληροφορίες
                        </h5>
                        <!-- Όνομα χρήστη -->
                        <div class="mb-3 border-bottom pb-2">
                            <span class="text-muted small d-block fw-semibold">Όνομα χρήστη:</span>
                            <span class="fs-5 fw-bold text-dark">${displayedUser.username}</span>
                        </div>
                        <!-- Ονοματεπώνυμο -->
                        <div class="mb-3 border-bottom pb-2">
                            <span class="text-muted small d-block fw-semibold">Ονοματεπώνυμο:</span>
                            <span class="fs-5 fw-bold text-dark">${displayedUser.fullname}</span>
                        </div>
                        <!-- Διεύθυνση email -->
                        <div class="mb-3 border-bottom pb-2">
                            <span class="text-muted small d-block fw-semibold">Διεύθυνση email:</span>
                            <span class="fs-5 fw-bold text-dark">${displayedUser.email}</span>
                        </div>
                        <!-- Αριθμός ταυτότητας -->
                        <div class="mb-3 border-bottom pb-2">
                            <span class="text-muted small d-block fw-semibold">Αριθμός ταυτότητας ΑΤ:</span>
                            <span class="fs-5 fw-bold text-dark">${displayedUser.idNumber}</span>
                        </div>
                        <!-- Κατάσταση λογαριασμού -->
                        <div class="mb-3 border-bottom pb-2">
                            <span class="text-muted small d-block fw-semibold">Κατάσταση Λογαριασμού:</span>
                            <c:choose>
                                <c:when test="${displayedUser.accountState == 'ACTIVE'}">
                                    <span class="badge bg-success fs-6">Ενεργοποιημένος</span>
                                </c:when>
                                <c:otherwise>
                                    <span class="badge bg-danger fs-6">Απενεργοποιημένος</span>
                                </c:otherwise>
                            </c:choose>
                        </div>
                        <!-- Ρόλος χρήστη -->
                        <div class="mb-3 border-bottom pb-2">
                            <span class="text-muted small d-block fw-semibold">Ρόλος:</span>
                            <span class="badge bg-secondary fs-6">
                                <c:choose>
                                    <c:when test="${displayedUser.role == 'CLIENT'}">Πελάτης</c:when>
                                    <c:when test="${displayedUser.role == 'FLIGHT_MANAGER'}">Διαχειριστής Πτήσεων</c:when>
                                    <c:when test="${displayedUser.role == 'SYSTEM_MANAGER'}">Διαχειριστής Συστήματος</c:when>
                                    <c:otherwise>${displayedUser.role}</c:otherwise>
                                </c:choose>
                            </span>
                        </div>

                        <!-- Ανάλογια τον ρόλο -->
                        <c:choose>
                            <c:when test="${displayedUser.role == 'CLIENT'}">
                                <div class="mb-3 border-bottom pb-2">
                                    <span class="text-muted small d-block fw-semibold">ΑΦΜ:</span>
                                    <span class="fs-5 fw-bold text-dark">${displayedUser.afm}</span>
                                </div>
                                <div class="mb-3 border-bottom pb-2">
                                    <span class="text-muted small d-block fw-semibold">Διεύθυνση κατοικίας:</span>
                                    <span class="fs-5 fw-bold text-dark">${displayedUser.homeAddress}</span>
                                </div>
                            </c:when>
                            <c:otherwise>
                                <div class="mb-3 border-bottom pb-2">
                                    <span class="text-muted small d-block fw-semibold">Κωδικός υπαλλήλου:</span>
                                    <span class="fs-5 fw-bold text-dark">${displayedUser.employeeCode}</span>
                                </div>
                            </c:otherwise>
                        </c:choose>
                    </div>
                </div>
                <!-- Επεξεργασία κάθε χρήστη τον ευατό του -->
                <div class="col-md-7">
                    <div class="card border-0 shadow-sm p-4 h-100">
                        <h5 class="fw-bold text-muted mb-4"><i class="fa-solid fa-user-pen me-2"></i>Ενημέρωση στοιχείων
                        </h5>

                        <form id="profileForm" action="UserServlet" method="POST"
                              onsubmit="return validateProfileForm(event)">
                            <!-- action update για να το πιάσει η updateUser του servlet και id και id number χρήστη -->
                            <input type="hidden" name="action" value="update">
                            <input type="hidden" name="id" value="${displayedUser.id}">
                            <input type="hidden" name="id_number" value="${displayedUser.idNumber}">

                            <!-- Ονοματεπώνυμο -->
                            <div class="mb-3">
                                <label for="fullname" class="form-label fw-semibold text-muted">Ονοματεπώνυμο</label>
                                <input type="text" id="fullname" name="fullname" class="form-control"
                                       value="${displayedUser.fullname}">
                                <span id="fullnameError" class="text-danger small fw-bold mt-1 d-block"></span>
                            </div>
                            <!-- Email -->
                            <div class="mb-3">
                                <label for="email" class="form-label fw-semibold text-muted">Διεύθυνση email</label>
                                <input type="text" id="email" name="email" class="form-control"
                                       value="${displayedUser.email}">
                                <span id="emailError" class="text-danger small fw-bold mt-1 d-block"></span>
                            </div>
                            <!-- Πεδία βάσει Ρόλου -->
                            <c:choose>
                                <c:when test="${displayedUser.role == 'CLIENT'}">
                                    <div class="mb-3">
                                        <label for="afm" class="form-label fw-semibold text-muted">ΑΦΜ</label>
                                        <input type="text" id="afm" name="afm" class="form-control" maxlength="9"
                                               value="${displayedUser.afm}">
                                        <span id="afmError" class="text-danger small fw-bold mt-1 d-block"></span>
                                    </div>
                                    <div class="mb-3">
                                        <label for="homeAddress" class="form-label fw-semibold text-muted">Διεύθυνση
                                            κατοικίας</label>
                                        <input type="text" id="homeAddress" name="home_address" class="form-control"
                                               value="${displayedUser.homeAddress}">
                                        <span id="homeAddressError"
                                              class="text-danger small fw-bold mt-1 d-block"></span>
                                    </div>
                                </c:when>
                                <c:otherwise>
                                    <div class="mb-3">
                                        <label for="employeeCode" class="form-label fw-semibold text-muted">Κωδικός
                                            υπαλλήλου</label>
                                        <input type="text" id="employeeCode" name="employee_code"
                                               class="form-control bg-light" value="${displayedUser.employeeCode}"
                                               readonly>
                                        <span id="employeeCodeError"
                                              class="text-danger small fw-bold mt-1 d-block"></span>
                                    </div>
                                </c:otherwise>
                            </c:choose>

                            <!-- Νέος κωδικός -->
                            <div class="mb-4">
                                <label for="password" class="form-label fw-semibold text-muted">Νέος κωδικός πρόσβασης
                                    (άστο κενό αν όχι)</label>
                                <input type="password" id="password" name="password" class="form-control">
                                <span id="passwordError" class="text-danger small fw-bold mt-1 d-block"></span>
                            </div>

                            <div class="text-end">
                                <button type="submit" class="btn btn-primary fw-bold px-4 py-2 shadow-sm">
                                    <i class="fa-solid fa-floppy-disk me-2"></i>Αποθήκευση αλλαγών
                                </button>
                            </div>
                        </form>
                    </div>
                </div>
            </div>
        </main>
    </div>
</div>
<!-- JavaScript που χρειάζεται -->
<script src="${pageContext.request.contextPath}/js/profile.js"></script>
</body>
</html>
