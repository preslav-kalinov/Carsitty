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
    showElement("#returnToAdminMenuContainer");

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
            
            if (xhr.status == 404 || xhr.status == 401 || xhr.status == 403) {
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
        let hideUpdate=""
        let manageUserStatusBtnName="deactivate";
        let manageUserStatusBtnColor="warning"
        if (userStatus === "Deactivated") {
            hideUpdate = "visually-hidden";
            manageUserStatusBtnName="activate";
            manageUserStatusBtnColor="success"
        }

        let tableRow = "<tr>";
        tableRow += '<td id="userName">' + user.username + "</td>";
        tableRow += '<td id="displayName">' + user.displayName + "</td>";
        tableRow += '<td id="userEmail">' + user.email + "</td>";
        tableRow += '<td id="userRole">' + user.role + "</td>";
        tableRow += "<td>" + userStatus + "</td>";
        tableRow += '<td><a href="update_user.html?username=' + user.username + '"><button type="button" class="btn btn-outline-success btn-rounded ' + hideUpdate + '" data-mdb-ripple-color="light">Update</button>\
        </a> <button type="button" class="btn btn-outline-danger btn-rounded" data-mdb-ripple-color="light" data-mdb-toggle="modal" data-mdb-target="#deleteUserModal" onclick="changeUserDeleteModal(\'' + user.username + '\', \'' + user.role + '\')">Delete</button>\
        <button id="manageUserStatus" type="button" class="btn btn-outline-' + manageUserStatusBtnColor + ' btn-rounded" data-mdb-ripple-color="light" data-mdb-toggle="modal" data-mdb-target="#changeStatusModal" onclick="changeUserStatusModal(\'' + user.username + '\', \'' + user.role + '\', \'' + manageUserStatusBtnName + '\')">' + manageUserStatusBtnName + '</button></td>';
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
            if(xhr.status == 404 || xhr.status == 400 || xhr.status == 401 || xhr.status == 403) {
                $("#errorMessageContent").append(JSON.parse(xhr.responseText).errorMessage.problem);
                return;
            }
            $("#errorMessageContent").append("Cannot connect to server");
        }
    });
}

function changeUserStatusModal(username, userRole, status){
    $("#changeStatusModalBody").text("Are you sure you want to '" + status + "' user '" +  username + "' with role - '" + userRole + "' ?");
    $("#changeStatusModalYesButton").attr("onclick", 'manageUserStatus(\'' + username + '\', \'' + userRole + '\', \'' + status + '\')');
}

function manageUserStatus(username, userRole, status) {
    let endpoint = getUserEndpoint(userRole);

    let userStatusValue = true;
    if (status === "deactivate") {
        userStatusValue = false;
    }

    const dataToBeSent = {
        enabled: userStatusValue
    };

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
            location.reload();    
        },
        error: function(xhr, status, code) {
            $("#changeStatusModalNoButton").click();
            showElement("#errorMessageContainer");
            if(xhr.status == 404 || xhr.status == 400 || xhr.status == 401 || xhr.status == 403) {
                $("#errorMessageContent").append(JSON.parse(xhr.responseText).errorMessage.problem);
                return;
            }
            $("#errorMessageContent").append("Cannot connect to server");
        }  
    });
}