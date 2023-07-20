//we check if the user is logged in

$.ajax({
    type: 'GET',
    url: APICONFIG.host + '/me',
    xhrFields: {
        withCredentials: true
    },
    crossDomain: true,
    success: function(result) {
        window.location.href = "dashboard.html";
    },
    error: function(xhr) {
        window.location.href = "login.html";
    }
});