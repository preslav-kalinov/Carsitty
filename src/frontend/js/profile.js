$.ajax({
    type: 'GET',
    url: APICONFIG.host + '/me',
    xhrFields: {
        withCredentials: true
    },
    crossDomain: true,
    success: function(result) {
        showLoggedUserInfo(result)
    },
    error: function(xhr, status, code) {
        window.location.href = "login.html";
    }
});

function onPageLoaded() {
    $.ajax({
        type: 'GET',
        url: APICONFIG.host + '/me',
        xhrFields: {
            withCredentials: true
        },
        crossDomain: true,
        success: function (result) {
            parseUserData(result);
            hideElement("#loadingContainer");
            showElement("#returnToMainMenuContainer");
            showElement("#myProfileForm");
        },
        error: function(xhr, status, code) {
            onMyProfileDataPageLoadError(xhr, status, code);
        }
    });
}

function onMyProfileDataPageLoadError(xhr, status, code) {
    hideElement("#loadingContainer");
    showElement("#returnToMainMenuContainer");
    showMyProfileDataError(xhr, status, code);
}

function showMyProfileDataError(xhr, status, code) {
    const errorMessageContent = $("#errorMessageContent");
    errorMessageContent.text("");
    showElement("#errorMessageContainer");
    hideElement("#successMessageContainer");

    if(xhr.status == 401 || xhr.status == 403) {
        errorMessageContent.append(errorMessage.problem);
        return;
    }

    errorMessageContent.append("Cannot connect to server");
}

function parseUserData(user) {
    $("#userId").val(user.id);
    $("#userName").val(user.username);
    $("#displayName").val(user.displayName);
    $("#userEmail").val(user.email);
    $("#userRole").val(user.role);
}

function partEditedSuccessfully() {
    hideElement("#errorMessageContainer");
    showElement("#successMessageContainer");
}

function onReturnToMainMenu() {
    let userRole = $("#userRole").val();
    redirectToPageBasedOnRole(userRole);
}