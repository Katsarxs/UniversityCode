<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>

<header class="d-flex justify-content-between align-items-center bg-white p-3 shadow-sm rounded mb-4 mt-2">
    <div class="d-flex align-items-center">
        <i class="fa-solid fa-plane-departure fa-2x text-primary me-2"></i>
        <span class="fs-4 fw-bold text-dark">Εφαρμογή Πτήσεων</span>
    </div>

    <c:if test="${not empty sessionScope.user}">
        <div class="d-flex align-items-center gap-3">
            <div class="text-end d-none d-sm-block">
                <small class="text-muted">Γειά σου,</small>
                <h6 class="fw-bold text-dark m-0">${sessionScope.user.fullname}</h6>
            </div>

            <span class="badge bg-secondary px-3 py-2 fs-6">
                <i class="fa-solid fa-shield-halved me-1"></i>
                <c:choose>
                    <c:when test="${sessionScope.user.role == 'CLIENT'}">Πελάτης</c:when>
                    <c:when test="${sessionScope.user.role == 'FLIGHT_MANAGER'}">Διαχειριστής Πτήσεων</c:when>
                    <c:when test="${sessionScope.user.role == 'SYSTEM_MANAGER'}">Διαχειριστής Συστήματος</c:when>
                    <c:otherwise>${sessionScope.user.role}</c:otherwise>
                </c:choose>
            </span>

            <a href="LoginServlet?action=logout"
               class="btn btn-outline-danger fw-bold d-flex align-items-center shadow-sm">
                <i class="fa-solid fa-right-from-bracket me-2"></i>Αποσύνδεση
            </a>
        </div>
    </c:if>
</header>
