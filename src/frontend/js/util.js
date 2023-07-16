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

function logoutUser() {
    $.ajax({
        type: 'GET',
        url: APICONFIG.host + '/logout',
        xhrFields: {
            withCredentials: true
        },
        crossDomain: true,
        success: function (result) {
            window.location.href = "login.html";
        },
        error: function (xhr, status, error)
        {
            if(xhr.status == 401)
            {
                window.location.href = "login.html";
                return;
            }

            showElement("#errorMessageContainer");
            $("#errorMessageContent").append("An error has occurred during the log out.");
        }
    });
}

function quotemeta(text) {
    return text.replace(/[-[\]{}()*+?.,\\^$|#\s]/g, "\\$&");
}