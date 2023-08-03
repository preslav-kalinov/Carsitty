function showElement(element) {
    if($(element).hasClass("visually-hidden")) {
        $(element).removeClass("visually-hidden");
    }
}

function hideElement(element) {
    if(!$(element).hasClass("visually-hidden")) {
        $(element).addClass("visually-hidden");
    }
}

function getBasicAuthString (sUsername, sPassword) {
    return 'Basic ' + btoa([sUsername, sPassword].join(':'));
}

function showLoggedUserInfo(result) {
    let loggedUser = result.displayName + ", " + result.role;
    $("#loggedUserInfo").append(loggedUser);
}

function getUserEndpoint(userRole) {
    let userEndpoint = "";

    if (userRole === "Manager") {
        userEndpoint = "/managers/";
    } else if (userRole === "Employee") {
        userEndpoint = "/employees/";
    }

    return userEndpoint;
}

function redirectToPageBasedOnRole(userRole) {
    let mainPage = "";
    if (userRole === "Employee") {
        mainPage = "dashboard.html";
    }

    if (userRole === "Manager") {
        mainPage = "../carsitty/manager/manager.html";
    }

    if (userRole === "Administrator") {
        mainPage = "../carsitty/administrator/administrator.html";
    }

    window.location.href = mainPage;
}

function quotemeta(text) {
    return text.replace(/[-[\]{}()*+?.,\\^$|#\s]/g, "\\$&");
}