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
        url: APICONFIG.host + '/users',
        xhrFields: {
            withCredentials: true
        },
        crossDomain: true,
        success: function (result) {
            hideElement("#loadingContainer");
            hideElement("#errorMessageContainer");
            showElement("#usersListingContainer");
            showUsersListing(result);
        },
        error: function(xhr, status, code) {
            hideElement("#loadingContainer");
            showElement("#errorMessageContainer");
            hideElement('#usersListingContainer');
            
            if(xhr.status == 404) {
                $("#errorMessageContent").append(JSON.parse(xhr.responseText).errorMessage.problem);
                return;
            }

            if(xhr.status == 401) {
                $("#errorMessageContent").append(JSON.parse(xhr.responseText).errorMessage.problem);
                return;
            }

            $("#errorMessageContent").append("Cannot connect to server");
        }
    });
}

function userSearchInputChanged() {
    const userInput = $("#userSearch").val().trim();
    const userInputRegex = new RegExp("\\b" + quotemeta(userInput), "i");
    $('#usersListingTableContent').find('tr').each(function() {
        const usernameColumnText = $(this).find('td#userName').text();
        const displayNameColumnText = $(this).find('td#displayName').text();
        const hasUsernameMatch = userInputRegex.test(usernameColumnText);
        const hasDisplayNameMatch = userInputRegex.test(displayNameColumnText);
        const emailColumnText = $(this).find('td#userEmail').text();
        const roleColumnText = $(this).find('td#userRole').text();
        const hasEmailMatch = userInputRegex.test(emailColumnText);
        const hasRoleMatch = userInputRegex.test(roleColumnText);

        if (!(hasUsernameMatch || hasDisplayNameMatch || hasEmailMatch || hasRoleMatch) && !$(this).hasClass("visually-hidden")) {
            $(this).addClass("visually-hidden");
        } else if ((hasUsernameMatch || hasDisplayNameMatch || hasEmailMatch || hasRoleMatch) && $(this).hasClass("visually-hidden")) {
            $(this).removeClass("visually-hidden");
        }
    });
}

function showUsersListing(result) {
    for(const user of result) {
        let userStatus = checkUserStatus(user);
        let tableRow = "<tr>";
        tableRow += '<td id="userName">' + user.username + "</td>";
        tableRow += '<td id="displayName">' + user.displayName + "</td>";
        tableRow += '<td id="userEmail">' + user.email + "</td>";
        tableRow += '<td id="userRole">' + user.role + "</td>";
        tableRow += "<td>" + userStatus + "</td>";
        tableRow += '<td><a href="update_user.html?username=' + user.username + '"><button type="button" class="btn btn-outline-success btn-rounded" data-mdb-ripple-color="light">Update</button></a> <button type="button" class="btn btn-outline-danger btn-rounded" data-mdb-ripple-color="light" data-mdb-toggle="modal" data-mdb-target="#deleteUserModal" onclick="changeUserDeleteModal(\'' + user.username + '\', \'' + user.role + '\')">Delete</button>';
        tableRow += "</tr>";
        $("#usersListingTableContent").append(tableRow);
    }
}

function checkUserStatus(user) {
    let userStatus = "Activated";
    if (user.enabled === false) {
        userStatus = "Deactivated";
    }

    return userStatus;
}

function changeUserDeleteModal(username, roleName) {
    $("#deleteUserModalBody").text("Are you sure you want to delete user '" +  username + "' with system role '" + roleName + "'?");
    $("#deleteUserModalYesButton").attr("onclick", 'deleteUser(\"' + username + '\", \"' + roleName + '\")');
}

function deleteUser(username, userRole) {
    let deleteUserEndpoint = "";
    if (userRole === "Manager") {
        deleteUserEndpoint = "/managers/";
    }

    if (userRole === "Employee") {
        deleteUserEndpoint = "/employees/";
    }

    $.ajax({
        type: 'DELETE',
        url: APICONFIG.host + '/users' + deleteUserEndpoint + username ,
        xhrFields: {
            withCredentials: true
        },
        crossDomain: true,
        success: function (result) {
            location.reload();
        },
        error: function (xhr, status, code) {
            $("#deleteUserModalNoButton").click();
            showElement("#errorMessageContainer");
            if(xhr.status == 404 || xhr.status == 400 || xhr.status == 401) {
                $("#errorMessageContent").append(JSON.parse(xhr.responseText).errorMessage.problem);
                return;
            }
            $("#errorMessageContent").append("Cannot connect to server");
        }
    });
}