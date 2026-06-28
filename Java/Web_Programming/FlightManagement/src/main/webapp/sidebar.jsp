<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>

<nav class="col-md-3 col-lg-2 d-flex flex-column bg-dark sidebar p-3 text-white">
    <div class="d-flex align-items-center justify-content-center mb-4 border-bottom pb-3">
        <!-- λογότυπο -->
        <a href="dashboard.jsp">
            <img src="${pageContext.request.contextPath}/images/logo.png" alt="Logo" class="img-fluid"
                 style="max-height: 50px;">
        </a>
    </div>

    <ul class="nav nav-pills flex-column mb-auto">
        <li class="nav-item">
            <a href="dashboard.jsp" class="nav-link text-white-50 mb-2" id="menu-home">
                <i class="fa-solid fa-house me-2"></i>Αρχική
            </a>
        </li>

        <c:if test="${sessionScope.user.role == 'CLIENT'}">
            <li>
                <a href="BookingServlet?action=search" class="nav-link text-white-50 mb-2">
                    <i class="fa-solid fa-ticket me-2"></i>Οι κρατήσεις σου
                </a>
            </li>
            <li>
                <a href="FlightServlet?action=search" class="nav-link text-white-50 mb-2" id="menu-flights">
                    <i class="fa-solid fa-magnifying-glass me-2"></i>Αναζήτηση πτήσεων
                </a>
            </li>
        </c:if>

        <c:if test="${sessionScope.user.role == 'FLIGHT_MANAGER'}">
            <li>
                <a href="FlightServlet?action=search" class="nav-link text-white-50 mb-2" id="menu-flights-mgr">
                    <i class="fa-solid fa-plane me-2"></i>Διαχείριση πτήσεων
                </a>
            </li>
            <li>
                <a href="BookingServlet?action=search" class="nav-link text-white-50 mb-2">
                    <i class="fa-solid fa-clipboard-list me-2"></i>Διαχείριση κρατήσεων
                </a>
            </li>
        </c:if>

        <c:if test="${sessionScope.user.role == 'SYSTEM_MANAGER'}">
            <li>
                <a href="UserServlet?action=search" class="nav-link text-white-50 mb-2">
                    <i class="fa-solid fa-users me-2"></i>Διαχείριση χρηστών
                </a>
            </li>
        </c:if>

        <li class="border-top pt-2 mt-2">
            <a href="UserServlet?action=profile" class="nav-link text-white-50 mb-2">
                <i class="fa-solid fa-user-gear me-2"></i>Ο λογαριασμός σου
            </a>
        </li>
    </ul>
</nav>
