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
    <title>Κεντρικό</title>
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

        <script>
            document.getElementById("menu-home").classList.add("active", "fw-semibold");
            document.getElementById("menu-home").classList.remove("text-white-50");
        </script>

        <main class="col-md-9 ms-sm-auto col-lg-10 px-md-4 bg-light min-vh-100">
            <!-- Εισαγωγή του navigation bar -->
            <jsp:include page="navigationBar.jsp"/>

            <!-- Κάρτες για ρόλους -->
            <div class="row row-cols-1 row-cols-md-2 row-cols-lg-3 g-4 pt-3">
                <!-- Κάρτες πελάτη -->
                <c:if test="${sessionScope.user.role == 'CLIENT'}">
                    <div class="col">
                        <div class="card h-100 border-0 shadow-sm text-center">
                            <div class="card-body p-4">
                                <div class="icon-box bg-primary-subtle text-primary rounded-circle mx-auto mb-3">
                                    <i class="fa-solid fa-ticket fa-2x"></i>
                                </div>
                                <h5 class="card-title fw-bold">Οι κρατήσεις σου</h5>
                                <p class="card-text text-muted small">Δες τις πτήσεις σου και διαχειρίσου τις κρατήσεις
                                    που έχεις κάνει.</p>
                                <a href="BookingServlet?action=search" class="btn btn-primary w-100 fw-bold">Προβολή</a>
                            </div>
                        </div>
                    </div>
                    <div class="col">
                        <div class="card h-100 border-0 shadow-sm text-center">
                            <div class="card-body p-4">
                                <div class="icon-box bg-success-subtle text-success rounded-circle mx-auto mb-3">
                                    <i class="fa-solid fa-magnifying-glass fa-2x"></i>
                                </div>
                                <h5 class="card-title fw-bold">Αναζήτηση πτήσεων</h5>
                                <p class="card-text text-muted small">Δες διαθέσιμες πτήσεις και κάνε κράτηση.</p>
                                <a href="FlightServlet?action=search"
                                   class="btn btn-primary w-100 fw-bold">Αναζήτηση</a>
                            </div>
                        </div>
                    </div>
                </c:if>

                <!-- Κάρτες διαχειριστή πτήσεων -->
                <c:if test="${sessionScope.user.role == 'FLIGHT_MANAGER'}">
                    <div class="col">
                        <div class="card h-100 border-0 shadow-sm text-center">
                            <div class="card-body p-4">
                                <div class="icon-box bg-primary-subtle text-primary rounded-circle mx-auto mb-3">
                                    <i class="fa-solid fa-plane fa-2x"></i>
                                </div>
                                <h5 class="card-title fw-bold">Διαχείριση πτήσεων</h5>
                                <p class="card-text text-muted small">Διαχειρίσου πτήσεις.</p>
                                <a href="FlightServlet?action=search"
                                   class="btn btn-warning w-100 fw-bold text-dark">Διαχείριση</a>
                            </div>
                        </div>
                    </div>
                    <div class="col">
                        <div class="card h-100 border-0 shadow-sm text-center">
                            <div class="card-body p-4">
                                <div class="icon-box bg-warning-subtle text-warning rounded-circle mx-auto mb-3">
                                    <i class="fa-solid fa-clipboard-list fa-2x"></i>
                                </div>
                                <h5 class="card-title fw-bold">Διαχείριση κρατήσεων</h5>
                                <p class="card-text text-muted small">Διαχειρίσου κρατήσεις που έχουν κάνει πελάτες.</p>
                                <a href="BookingServlet?action=search" class="btn btn-warning w-100 fw-bold text-dark">Διαχείριση</a>
                            </div>
                        </div>
                    </div>
                </c:if>

                <!-- Κάρτες διαχειριστή συστήματος -->
                <c:if test="${sessionScope.user.role == 'SYSTEM_MANAGER'}">
                    <div class="col">
                        <div class="card h-100 border-0 shadow-sm text-center">
                            <div class="card-body p-4">
                                <div class="icon-box bg-danger-subtle text-danger rounded-circle mx-auto mb-3">
                                    <i class="fa-solid fa-users fa-2x"></i>
                                </div>
                                <h5 class="card-title fw-bold">Διαχείριση χρηστών</h5>
                                <p class="card-text text-muted small">Διαχειρίσου τους χρήστες της εφαρμογής.</p>
                                <a href="UserServlet?action=search" class="btn btn-warning w-100 fw-bold text-dark">Διαχείριση</a>
                            </div>
                        </div>
                    </div>
                </c:if>

                <!-- Διαχείριση λογαριασμού για όλους τους ρόλους-->
                <div class="col">
                    <div class="card h-100 border-0 shadow-sm text-center">
                        <div class="card-body p-4">
                            <div class="icon-box bg-info-subtle text-info rounded-circle mx-auto mb-3">
                                <i class="fa-solid fa-user-gear fa-2x"></i>
                            </div>
                            <h5 class="card-title fw-bold">Διαχείριση λογαριασμού</h5>
                            <p class="card-text text-muted small">Ενημέρωσε τα στοιχεία σου.</p>
                            <a href="UserServlet?action=profile" class="btn w-100 fw-bold text-white"
                               style="background-color: #6f42c1; border-color: #6f42c1;">Επεξεργασία</a>
                        </div>
                    </div>
                </div>
            </div>
        </main>
    </div>
</div>
</body>
</html>
