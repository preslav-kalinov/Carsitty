$.ajax({
    type: 'GET',
    url: APICONFIG.host + '/me',
    xhrFields: {
        withCredentials: true
    },
    crossDomain: true,
    success: function(result) {
        onLoginPageSuccess(result);
    }
});

function onPageLoaded() {
    hideElement("#loadingContainer");
}

function submitLogin() {
    showElement("#loadingContainer")

    let username = $("#usernameField").val();
    let password = $("#passwordField").val();
    if ((username == "" || username == undefined) || (password == "" || password == undefined)) {
        if (!$("#errorMessageContent").hasClass("visually-hidden")) {
            $("#errorMessageContent").text("Username and Password fields are required to login");
            hideElement("#loadingContainer");
            showElement("#errorMessageContainer");
            return;
        }
    }
    
    hideElement("#errorMessageContainer");

    if (!validateUsername() || !validatePassword()) {
        hideElement("#loadingContainer");
        return;
    }

    hideElement("#errorMessageContainer");
    hideElement("#loginBtn");

    $.ajax({
        type: 'POST',
        url: APICONFIG.host + '/login',
        xhrFields: {
            withCredentials: true            
        },
        crossDomain: true,
        beforeSend: function(xhr) {
            xhr.setRequestHeader('Authorization', getBasicAuthString(username, password));
        },
        success: function(result) {
            hideElement("#loadingContainer");
            showElement("#successMessageContainer");
            console.log("Logged in");
            onLoginPageSuccess(result);
        },
        error: function(xhr, status, code) {
            hideElement("#loadingContainer");
            onLoginPageError(xhr, status, code);
        }
    });
}

function validateUsername() {
    return validateField("usernameField", 3, 128, "The username length has to be between 3 and 128 characters");
}
  
  function validatePassword() {
    return validateField("passwordField", 6, 60, "The password length has to be between 6 and 60 characters");
}

function validateField(fieldId, minLength, maxLength, errorMessage) {
    const field = $("#" + fieldId);
    const fieldValue = field.val().trim();
    const fieldLength = fieldValue.length;
    
    field.removeClass("invalid valid").focusin();
    
    if (fieldLength < minLength || fieldLength > maxLength) {
      field.addClass("invalid");
      showElement("#errorMessageContainer");
      $("#errorMessageContent").text(errorMessage);
      return false;
    }
    
    field.addClass("valid");
    return true;
}

function onLoginPageError(xhr, status, code) {
    showElement("#loginBtn");
    showLoginError(xhr, status, code);
}

function onLoginPageSuccess(user) {
    let userRole = user.role;
    redirectToPageBasedOnRole(userRole);
}

function showLoginError(xhr, status, code) {
    showElement("#errorMessageContainer");
    hideElement("#successMessageContainer");
    if(xhr.status == 401 || xhr.status == 400) {
        $("#errorMessageContent").html(JSON.parse(xhr.responseText).errorMessage.problem);
        return;
    }
    $("#errorMessageContent").text("Cannot connect to server!");
}