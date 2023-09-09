let roleChosen

const urlParams = new URLSearchParams(window.location.search);
const username = urlParams.get('username');

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
    showElement("#loadingContainer");
    showElement("#returnToAdminMenuContainer");

    $.ajax({
        type: 'GET',
        url: APICONFIG.host + '/users/' + username,
        xhrFields: {
            withCredentials: true
        },
        crossDomain: true,
        success: function (result) {
            parseUser(result);
            hideElement("#loadingContainer");
            showElement("#returnToAdminMenuContainer");
        },
        error: function(xhr, status, code) {
            onUpdateUserPageError(xhr, status, code);
        }
    });
}

function parseUser(user) {
    $("#usernameField").val(user.username);
    $("#displayNameField").val(user.displayName);
    $("#emailField").val(user.email);
    $('#roleItems').find('a').each(function() {
        if($(this).attr('roleId') == user.role) {
            if(!$(this).hasClass("active"))
                $(this).addClass("active");
        }
        roleChosen = user.role;
    });
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

function submitUpdate() {
    showElement("#loadingContainer");
    showElement("#returnToAdminMenuContainer");

    $.ajax({
        type: 'GET',
        url: APICONFIG.host + '/users/' + username,
        xhrFields: {
            withCredentials: true
        },
        crossDomain: true,
        success: function (result) {
            update(result);
            showElement("#returnToAdminMenuContainer");
        },
        error: function(xhr, status, code) {
            onUpdateUserPageError(xhr, status, code);
        }
    });
}

function update(user) {
    hideElement("#updateBtn");
    showElement("#formSubmitLoadingContainer");
    hideElement("#errorMessageContainer");
    
    let endpoint = getUserEndpoint(user.role);
    const dataToBeSent = {};
    
    let usernameValue = $("#usernameField").val();
    let originalUsername = user.username;
    if (usernameValue !== originalUsername) {
        dataToBeSent.username = usernameValue;
    }

    let displayNameValue = $("#displayNameField").val();
    let originalDisplayName = user.displayName;
    if (displayNameValue !== originalDisplayName) {
        dataToBeSent.displayName = displayNameValue;
    }

    let emailValue = $("#emailField").val();
    let originalEmail = user.email;
    if (emailValue !== originalEmail) {
        dataToBeSent.email = emailValue;
    }

    let passwordValue = $("#newPasswordField").val();
    if (passwordValue !== undefined && passwordValue !== "") {
        console.log("The password is not empty!")
        dataToBeSent.password = passwordValue;
    }
    else {
        console.log("The password is empty!")
    }

    dataToBeSent.role = roleChosen;

    $.ajax({
        type: 'PATCH',
        url: APICONFIG.host + '/users' + endpoint + username,
        xhrFields: {
            withCredentials: true            
        },
        crossDomain: true,
        data: JSON.stringify(dataToBeSent),
        contentType: "application/json",
        success: function(result) {
            hideElement("#loadingContainer");
            hideElement("#formSubmitLoadingContainer");
            showElement("#updateBtn");
            console.log("User updated");
            userUpdatedSuccessful();
        },
        error: function(xhr, status, code) {
            hideElement("#loadingContainer");
            hideElement("#formSubmitLoadingContainer");
            showElement("#updateBtn");
            onUpdateUserPageError(xhr, status, code);
        }
    });
}

function onUpdateUserPageError(xhr, status, code) {
    showElement("#returnToAdminMenuContainer");
    showUpdateUserError(xhr, status, code);
}

function showUpdateUserError(xhr, status, code) {
    const errorMessage = JSON.parse(xhr.responseText).errorMessage;
    const errorMessageContent = $("#errorMessageContent");
    errorMessageContent.text("");

    showElement("#errorMessageContainer");
    hideElement("#successMessageContainer");

    if (xhr.status === 404 || xhr.status === 400) {
        const errorFields = ["username", "displayName", "password", "email", "enabled"];

        errorMessageContent.append(errorMessage.problem);
        errorFields.forEach((field) => {
            if (errorMessage[field] !== undefined) {
                errorMessageContent.append("<ul><li>" + errorMessage[field] + "</li></ul>");
            }
        });

        return;
    }

    if(xhr.status == 401 || xhr.status == 403) {
        errorMessageContent.append(errorMessage.problem);
        hideElement("#loadingContainer");
        hideElement("#returnToAdminMenuContainer");
        hideElement("#updateBtn");
        hideElement("#updateForm");

        return;
    }

    $("#errorMessageContent").append("Cannot connect to server");
}

function userUpdatedSuccessful() {
    hideElement("#errorMessageContainer");
    showElement("#successMessageContainer");
}