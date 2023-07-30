function logoutUser() {
    $.ajax({
        type: 'GET',
        url: APICONFIG.host + '/logout',
        xhrFields: {
            withCredentials: true
        },
        crossDomain: true,
        success: function (result) {
            $("#logoutMessageContent").text("Log out not successful.");
            window.location.href = "login.html";
        },
        error: function (xhr, status, error)
        {
            if(xhr.status == 401)
            {
                window.location.href = "login.html";
                return;
            }

            $("#logoutMessage").removeClass("alert-primary");
            $("#logoutMessage").addClass("alert-danger");
            $("#logoutMessageContent").text("An error has occurred during the log out.");
        }
    });
}