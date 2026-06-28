<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<!-- Έλεγχος session και ρόλου -->
<c:if test="${empty sessionScope.user || sessionScope.user.role != 'SYSTEM_MANAGER'}">
    <c:redirect url="login.jsp"/>
</c:if>

<!DOCTYPE html>
<html lang="el">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Διαχείριση Χρηστών</title>
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

            <!-- Μηνύματα στο πάνω μέρος από servlets -->
            <c:if test="${param.msg == 'add_success'}">
                <div class="alert alert-success shadow-sm fw-bold mb-4">
                    <i class="fa-solid fa-user-check me-2"></i>Ο νέος χρήστης καταχωρήθηκε.
                </div>
            </c:if>

            <div class="d-flex justify-content-between align-items-center mb-4">
                <h2 class="fw-bold text-secondary">Διαχείριση χρηστών</h2>
                <c:if test="${sessionScope.user.role == 'SYSTEM_MANAGER'}">
                    <a href="addUser.jsp" class="btn btn-success fw-bold">
                        <i class="fa-solid fa-user-plus me-2"></i>Νέος Χρήστης
                    </a>
                </c:if>
            </div>

            <c:if test="${not empty errorMessage}">
                <div class="alert alert-danger shadow-sm fw-bold mb-4">${errorMessage}</div>
            </c:if>
            <c:if test="${not empty param.msg && param.msg.startsWith('imported_')}">
                <div class="alert alert-success shadow-sm fw-bold mb-4">
                    Έγινε εισαγωγή ${param.msg.substring(9)} χρηστών.
                </div>
            </c:if>
            <c:if test="${param.msg == 'deletesuccess'}">
                <div class="alert alert-success shadow-sm fw-bold mb-4">Ο χρήστης διαγράφηκε.</div>
            </c:if>

            <div class="row g-4 mb-4">
                <!-- Φόρμα αναζήτησης -->
                <div class="col-md-7">
                    <div class="card border-0 shadow-sm p-4 h-100">
                        <h5 class="fw-bold text-muted mb-3">Κριτήρια Αναζήτησης</h5>
                        <form action="UserServlet" method="GET">
                            <input type="hidden" name="action" value="search">
                            <div class="row g-3">
                                <div class="col-12">
                                    <label for="query" class="form-label small fw-semibold text-muted">Αναζήτησε</label>
                                    <div class="input-group">
                                        <input type="text" id="query" name="query" class="form-control"
                                               placeholder="όνομα χρήστη ή email ή ονοματεπώνυμο ή ΑΤ ή ΑΦΜ"
                                               value="${param.query}">
                                        <button type="submit" class="btn btn-primary fw-bold">
                                            <i class="fa-solid fa-magnifying-glass me-2"></i>Αναζήτηση
                                        </button>
                                    </div>
                                </div>
                            </div>
                        </form>
                    </div>
                </div>

                <!-- Εισαγωγή πολλαπλών χρηστών -->
                <div class="col-md-5">
                    <div class="card border-0 shadow-sm p-4 h-100">
                        <h5 class="fw-bold text-muted mb-3"><i class="fa-solid fa-file-csv me-2"></i>Εισαγωγή πολλαπλών
                            χρηστών</h5>
                        <p class="text-muted small">Επέλεξε ρχείο .csv</p>
                        <form action="UserServlet" method="POST" enctype="multipart/form-data" class="mt-auto">
                            <input type="hidden" name="action" value="import">
                            <div class="mb-3">
                                <input type="file" name="csvFile" class="form-control" accept=".csv" required>
                            </div>
                            <button type="submit" class="btn btn-success fw-bold w-100 py-2 shadow-sm">
                                <i class="fa-solid fa-upload me-2"></i>Εισαγωγή
                            </button>
                        </form>
                    </div>
                </div>
            </div>

            <!-- Πίνακας με χρήστες -->
            <div class="card border-0 shadow-sm p-4 mb-5">
                <div class="table-responsive">
                    <table class="table table-striped table-hover align-middle m-0">
                        <thead class="table-dark">
                        <tr>
                            <th>Όνομα χρήστη</th>
                            <th>Ονοματεπώνυμο</th>
                            <th>Διεύθυνση Email</th>
                            <th>Ρόλος</th>
                            <th class="text-center">Ενέργειες</th>
                        </tr>
                        </thead>
                        <tbody>
                        <c:if test="${empty usersList}">
                            <tr>
                                <td colspan="5" class="text-center text-muted py-4">Δεν βρέθηκε κανένας.
                                </td>
                            </tr>
                        </c:if>
                        <c:forEach var="u" items="${usersList}">
                            <tr>
                                <td class="fw-bold">${u.username}</td>
                                <td>${u.fullname}</td>
                                <td>${u.email}</td>
                                <td>
                                        <span class="badge bg-secondary">
                                            <c:choose>
                                                <c:when test="${u.role == 'CLIENT'}">Πελάτης</c:when>
                                                <c:when test="${u.role == 'FLIGHT_MANAGER'}">Διαχειριστής Πτήσεων</c:when>
                                                <c:when test="${u.role == 'SYSTEM_MANAGER'}">Διαχειριστής Συστήματος</c:when>
                                                <c:otherwise>${u.role}</c:otherwise>
                                            </c:choose>
                                        </span>
                                </td>
                                <td class="text-center">
                                    <div class="d-flex justify-content-center gap-2">
                                        <a href="UserServlet?action=editForm&id=${u.id}"
                                           class="btn btn-sm btn-outline-primary fw-bold">
                                            <i class="fa-solid fa-user-gear me-1"></i>Διαχείριση
                                        </a>
                                        <a href="UserServlet?action=delete&id=${u.id}"
                                           class="btn btn-sm btn-outline-danger fw-bold"
                                           onclick="return confirm('Είστε βέβαιοι ότι θέλετε να διαγράψετε αυτόν τον χρήστη;');">
                                            <i class="fa-solid fa-trash me-1"></i>Διαγραφή
                                        </a>
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
    // Φωτίζει το κουμπί χρήστες στο sidebar
    const links = document.querySelectorAll('.sidebar .nav-link');
    links.forEach(link => {
        if (link.textContent.includes('Διαχείριση χρηστών')) {
            link.classList.add('active', 'fw-semibold');
            link.classList.remove('text-white-50');
        }
    });
</script>
</body>
</html>
