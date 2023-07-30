$.ajax({
    type: 'GET',
    url: APICONFIG.host + '/me',
    xhrFields: {
        withCredentials: true
    },
    crossDomain: true,
    success: function(result) {
        showLoggedUserInfo(result);
        hideElement("#loadingContainer");
        if (result.role === "Employee" || result.role === "Manager") {
            hideElement("#menuTiles");
            showElement("#errorMessageContainer");

            $("#errorMessageContent").append("Authorization not enough");
            return;
        }
    },
    error: function(xhr, status, code) {
        window.location.href = "../login.html";
    }
});