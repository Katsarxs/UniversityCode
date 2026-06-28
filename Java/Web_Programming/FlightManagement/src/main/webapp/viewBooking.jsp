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
    <title>Προβολή Κράτησης #${booking.bookingNumber}</title>
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
                <h2 class="fw-bold text-secondary">Στοιχεία Κράτησης: ${booking.bookingNumber}</h2>
                <a href="BookingServlet?action=search" class="btn btn-outline-secondary fw-bold">
                    <i class="fa-solid fa-arrow-left me-2"></i>Επιστροφή
                </a>
            </div>

            <!-- Μηνύματα στο πάνω μέρος από servlets ajax-->
            <div id="ajaxAlert" class="alert d-none fw-bold shadow-sm mb-4"></div>
            <c:if test="${param.msg == 'insert_success'}">
                <div class="alert alert-success shadow-sm fw-bold mb-4">
                    <i class="fa-solid fa-circle-check me-2"></i>Η κράτησή σου έγινε.
                </div>
            </c:if>

            <!-- Μηνύματα στο πάνω μέρος από servlets -->
            <c:if test="${param.msg == 'update_success'}">
                <div class="alert alert-success shadow-sm fw-bold mb-4">Ο τύπος του εισιτηρίου ενημερώθηκε.
                </div>
            </c:if>
            <c:if test="${not empty errorMessage}">
                <div class="alert alert-danger shadow-sm fw-bold mb-4">${errorMessage}</div>
            </c:if>

            <div class="row g-4">
                <!-- Στοιχεία κράτησης-->
                <div class="col-lg-5">
                    <div class="card border-0 shadow-sm p-4 mb-4">
                        <h5 class="fw-bold text-muted mb-3"><i class="fa-solid fa-circle-info me-2"></i>Πληροφορίες</h5>
                        <table class="table table-sm table-borderless m-0">
                            <tr>
                                <td class="text-muted fw-semibold">Ημέρα κράτησης:</td>
                                <td class="fw-bold">${booking.bookDate}</td>
                            </tr>
                            <tr>
                                <td class="text-muted fw-semibold">Τύπος εισιτηρίου:</td>
                                <td class="fw-bold">
                                    <span class="badge bg-light text-dark border">${booking.ticketType}</span>
                                </td>
                            </tr>
                            <tr>
                                <td class="text-muted fw-semibold">Κατάσταση:</td>
                                <td id="bookingStateBadge" class="fw-bold">
                                    <span class="badge bg-secondary">${booking.state}</span>
                                </td>
                            </tr>
                            <tr>
                                <td class="text-muted fw-semibold">Θέση:</td>
                                <td id="currentSeatInfo" class="fw-bold text-primary">
                                    <c:choose>
                                        <c:when test="${not empty booking.row && not empty booking.column}">
                                            Σειρά ${booking.row}, Θέση ${booking.column}
                                        </c:when>
                                        <c:otherwise>Δεν έχεις επιλεγεί</c:otherwise>
                                    </c:choose>
                                </td>
                            </tr>
                        </table>

                        <div class="mt-4 d-flex flex-column gap-3">
                            <!-- Κουμπί αποδέσμευσης-->
                            <button id="btnUnreserve"
                                    class="btn btn-danger fw-bold w-100 <c:if test="${empty booking.row || flightState != 'STAFFED'}">d-none</c:if>"
                                    onclick="unreserveSeat(${booking.id})">
                                <i class="fa-solid fa-chair text-white me-2"></i>Αποδέσμευση Θέσης
                            </button>

                            <!-- Κουμπί ακύρωσης-->
                            <c:if test="${booking.state == 'CREATED'}">
                                <a href="BookingServlet?action=cancel&id=${booking.id}"
                                   class="btn btn-outline-danger fw-bold w-100">
                                    <i class="fa-solid fa-ban me-2"></i>Ακύρωση κράτησης
                                </a>
                            </c:if>

                            <!-- Φόρμα για αλλαγή του τύπου εισητιρίου -->
                            <c:if test="${booking.state == 'CREATED'}">
                                <div class="border-top pt-3 mt-2">
                                    <h6 class="fw-bold text-muted mb-2"><i class="fa-solid fa-arrows-rotate me-2"></i>Αλλαγή
                                        τύπου εισιτηρίου</h6>
                                    <form action="BookingServlet" method="POST" class="w-100">
                                        <input type="hidden" name="action" value="updateType">
                                        <input type="hidden" name="id" value="${booking.id}">

                                        <div class="input-group">
                                            <select name="ticket_type" class="form-select fw-semibold">
                                                <option value="ECONOMY" ${booking.ticketType == 'ECONOMY' ? 'selected' : ''}>
                                                    ECONOMY
                                                </option>
                                                <option value="NORMAL" ${booking.ticketType == 'NORMAL' ? 'selected' : ''}>
                                                    NORMAL
                                                </option>
                                                <option value="BUSINESS" ${booking.ticketType == 'BUSINESS' ? 'selected' : ''}>
                                                    BUSINESS
                                                </option>
                                            </select>
                                            <button type="submit" class="btn btn-primary fw-bold">Ενημέρωση</button>
                                        </div>
                                    </form>
                                </div>
                            </c:if>

                        </div>
                    </div>
                </div>

                <div class="col-lg-7">
                    <div class="card border-0 shadow-sm p-4 mb-4">
                        <h5 class="fw-bold text-muted mb-3"><i class="fa-solid fa-plane me-2"></i>Επιλογή Θέσης</h5>

                        <c:choose>
                            <c:when test="${booking.state == 'CANCELLED'}">
                                <div class="alert alert-danger m-0">
                                    <i class="fa-solid fa-ban me-2"></i>Η κράτηση έχει ακυρωθεί.
                                </div>
                            </c:when>
                            <c:when test="${booking.ticketType == 'ECONOMY'}">
                                <div class="alert alert-info m-0">Δεν μπορείς να επιλέξεις θέση γιατί είσαι economy.
                                </div>
                            </c:when>
                            <c:when test="${flightState != 'STAFFED'}">
                                <div class="alert alert-warning m-0">Η διαχείριση θέσεων επιτρέπεται μόνο όταν η πτήση
                                    είναι σε στελεχώμενη κατάσταση.
                                </div>
                            </c:when>
                            <c:otherwise>
                                <!-- Υπόμνημα χρωμάτων -->
                                <div class="d-flex justify-content-center gap-3 mb-4 small fw-semibold">
                                    <div><span class="badge bg-secondary me-1">&nbsp;&nbsp;</span>Διαθέσιμη NORMAL
                                    </div>
                                    <div><span class="badge bg-warning text-dark me-1">&nbsp;&nbsp;</span>Διαθέσιμη
                                        BUSINESS
                                    </div>
                                    <div><span class="badge bg-success me-1">&nbsp;&nbsp;</span>Η Θέση σου</div>
                                    <div><span class="badge bg-danger me-1">&nbsp;&nbsp;</span>Κατειλημμένες θέσεις
                                    </div>
                                </div>

                                <!-- Οπτικό πλέγμα Θέσεων -->
                                <div class="plane-grid border shadow-inner">
                                    <c:forEach var="r" begin="1" end="${flightRows}">
                                        <div class="seat-row">
                                            <div class="seat-label">Σ${r}</div>
                                            <c:forEach var="c" begin="1" end="${flightSeatsPerRow}">
                                                <c:if test="${c == 4}">
                                                    <div class="seat-aisle"></div>
                                                </c:if>
                                                <c:set var="isOccupied" value="false"/>
                                                <c:set var="isMine" value="false"/>
                                                <c:forEach var="occ" items="${occupiedSeats}">
                                                    <c:if test="${occ.row == r && occ.column == c}">
                                                        <c:set var="isOccupied" value="true"/>
                                                        <c:if test="${booking.row == r && booking.column == c}">
                                                            <c:set var="isMine" value="true"/>
                                                        </c:if>
                                                    </c:if>
                                                </c:forEach>
                                                <c:choose>
                                                    <c:when test="${isMine}">
                                                        <button class="btn btn-success seat-btn" data-row="${r}"
                                                                data-col="${c}"
                                                                onclick="selectSeat(${booking.id}, ${r}, ${c}, 'change')">${c}</button>
                                                    </c:when>
                                                    <c:when test="${isOccupied}">
                                                        <button class="btn btn-danger seat-btn" disabled>${c}</button>
                                                    </c:when>
                                                    <c:otherwise>
                                                        <c:set var="btnClass"
                                                               value="${r <= flightRowsBusiness ? 'btn-outline-warning text-dark' : 'btn-outline-secondary'}"/>
                                                        <c:set var="isDisabled"
                                                               value="${booking.ticketType == 'NORMAL' && r <= flightRowsBusiness ? 'disabled' : ''}"/>
                                                        <button class="btn ${btnClass} seat-btn" ${isDisabled}
                                                                data-row="${r}" data-col="${c}"
                                                                onclick="selectSeat(${booking.id}, ${r}, ${c}, '${empty booking.row ? 'reserve' : 'change'}')">${c}</button>
                                                    </c:otherwise>
                                                </c:choose>
                                            </c:forEach>
                                        </div>
                                    </c:forEach>
                                </div>
                            </c:otherwise>
                        </c:choose>
                    </div>
                </div>
            </div>
        </main>
    </div>
</div>
<div id="flightData"
     data-booking-id="${booking.id}"
     data-business-rows="${flightRowsBusiness}">
</div>
<!-- JavaScript που χρειάζεται -->
<script src="${pageContext.request.contextPath}/js/viewBooking.js"></script>
</body>
</html>
