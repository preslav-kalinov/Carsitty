let roleChosen

$.ajax({
    type: 'GET',
    url: APICONFIG.host + '/me',
    xhrFields: {
        withCredentials: true
    },
    crossDomain: true,
    error: function(xhr, status, code) {
        window.location.href = "../login.html";
    }
});

function onPageLoaded() {
    hideElement("#loadingContainer");
    showElement("#returnToAdminMenuContainer");
}

function onRegisterPageError(xhr, status, code) {
    showElement("#returnToAdminMenuContainer");
    showRegisterError(xhr, status, code);
}

function showRegisterError(xhr, status, code) {
    const errorMessage = JSON.parse(xhr.responseText).errorMessage;
    const errorMessageContent = $("#errorMessageContent");
    let confirmPassword = $("#confirmPasswordField").val();
    errorMessageContent.text("");

    showElement("#errorMessageContainer");
    hideElement("#successMessageContainer");

    if (xhr.status === 404 || xhr.status === 400) {
        const errorFields = ["username", "displayName", "password", "email", "enabled"];

        errorMessageContent.append(errorMessage.problem);
        errorFields.forEach((field) => {
            if (errorMessage[field] !== undefined) {
                errorMessageContent.append("<br>" + errorMessage[field]);
            }
        });

        if ((confirmPassword == "" || confirmPassword == undefined)) {
            errorMessageContent.append("<br> The confirm password field must not be empty");
        }

        return;
    }

    if(xhr.status == 401 || xhr.status == 403) {
        errorMessageContent.append(errorMessage.problem);
        return;
    }

    $("#errorMessageContent").append("Cannot connect to server");
}

function setRole(roleName) {
    roleChosen = roleName;
    $('#roleItems').find('a').each(function() {
        if($(this).attr('roleId') == roleName && !$(this).hasClass("active")) {
            $(this).addClass("active");
        } else if($(this).attr('roleId') != roleName && $(this).hasClass("active")) {
            $(this).removeClass("active");
        }
    });
}

function submitRegister() {
    hideElement("#registerBtn");
    showElement("#formSubmitLoadingContainer");

    if ((roleChosen == "" || roleChosen == undefined)) {
        if (!$("#errorMessageContent").hasClass("visually-hidden")) {
            $("#errorMessageContent").text("Specify role to register new user");
            hideElement("#loadingContainer");
            showElement("#errorMessageContainer");
            hideElement("#formSubmitLoadingContainer");
            showElement("#registerBtn");
            return;
        }
    }

    let confirmPassword = $("#confirmPasswordField").val();
    let password = $("#passwordField").val();
    if (!passwordsMatch() && (confirmPassword != "" || password != "")) {
        // Display an error message or take any other appropriate action
        if (!$("#errorMessageContent").hasClass("visually-hidden")) {
            $("#errorMessageContent").text("Passwords does not match");
            hideElement("#loadingContainer");
            showElement("#errorMessageContainer");
            hideElement("#formSubmitLoadingContainer");
            showElement("#registerBtn");
            return;
        }
    }
    
    hideElement("#errorMessageContainer");

    let endpoint = getUserEndpoint(roleChosen);
    const dataToBeSent = {
        username: $("#usernameField").val(),
        displayName: $("#displayNameField").val(),
        password: $("#passwordField").val(),
        email: $("#emailField").val(),
        enabled: "true"
    }

    $.ajax({
        type: 'POST',
        url: APICONFIG.host + '/users' + endpoint,
        xhrFields: {
            withCredentials: true            
        },
        crossDomain: true,
        data: JSON.stringify(dataToBeSent),
        contentType: "application/json",
        success: function(result) {
            hideElement("#loadingContainer");
            hideElement("#formSubmitLoadingContainer");
            showElement("#registerBtn");
            console.log("User registered");

            userRegisterSuccessful();
        },
        error: function(xhr, status, code) {
            hideElement("#loadingContainer");
            hideElement("#formSubmitLoadingContainer");
            showElement("#registerBtn");
            onRegisterPageError(xhr, status, code);
        }
    });

}

function passwordsMatch() {
    const password = document.getElementById("passwordField").value;
    const confirmPassword = document.getElementById("confirmPasswordField").value;
  
    return password === confirmPassword;
}

function userRegisterSuccessful() {
    hideElement("#errorMessageContainer");
    showElement("#successMessageContainer");
    roleChosen = undefined;
    $('#roleItems').find('a').each(function() {
        if($(this).hasClass("active")) {
            $(this).removeClass("active");
        }
        if($(this).hasClass("visually-hidden")) {
            $(this).removeClass("visually-hidden");
        }
    });

    $("#usernameField").val("");
    $("#displayNameField").val("");
    $("#emailField").val("");
    $("#passwordField").val("");
    $("#confirmPasswordField").val("");
}